package org.dreamlife.hippocampus.apigateway.genericinvoke.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.rpc.service.GenericService;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.GenericInvokeQO;
import org.dreamlife.hippocampus.apigateway.genericinvoke.service.RpcGenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Dubbo泛化服务
 *
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/31
 */
@Slf4j
@Service
public class DubboGenericServiceImpl implements RpcGenericService {
    private final Map<String, FutureTask<GenericService>> cache;


    public DubboGenericServiceImpl() {
        cache = new ConcurrentHashMap<>();
    }

    private String generateKey(String interfaceClass, String group, String version) {
        return new StringBuilder(interfaceClass)
                .append(".")
                .append(group)
                .append(".")
                .append(version).toString();

    }

    private GenericService getService(String interfaceClass, String group, String version) throws ExecutionException, InterruptedException {
        String cacheKey = generateKey(interfaceClass,group,version);

        FutureTask<GenericService> futureTask = cache.get(cacheKey);

        if(null == futureTask){
            // 没有命中缓存
            // 为了解决多程序并发穿透缓存造成的危害，这里设置只允许一个线程去加载结果，其余线程阻塞等待
            futureTask = new FutureTask(
                    () ->{
                        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                        reference.setInterface(interfaceClass);
                        reference.setVersion(version);
                        reference.setGeneric(true);
                        reference.setGroup(group);
                        reference.setApplication(ConfigManager.getInstance().getApplication().get());
                        reference.setRegistry(ConfigManager.getInstance().getRegistries().values().iterator().next());
                        // 考虑部分RPC接口比较耗时
                        reference.setTimeout(60 * 1000);
                        // 不进行重试
                        reference.setRetries(0);
                        // 根据ReferenceConfig对象获取到GenericService对象的操作很重，需要进行底层通信
                        // 因此需要缓存创建的GenericService对象
                        GenericService genericService = reference.get();
                        return genericService;
                    }
            );
            FutureTask previousTask = cache.putIfAbsent(cacheKey, futureTask);
            if(null == previousTask){
                // 写成功的线程执行任务
                futureTask.run();
            }else{
                // 没有写成功的线程便获取写入成功的futureTask
                futureTask = cache.get(cacheKey);
            }
        }
        try {
            return futureTask.get();
        } catch (Exception e) {
            log.error("get futureTask fail cause {}, task key is {} ",e.getMessage(),cacheKey,e);
            throw e;
        }
    }

    public Object invoke(GenericInvokeQO genericInvokeQO)
            throws Exception {
        GenericService service = getService(genericInvokeQO.getInterfaceClass(),
                genericInvokeQO.getGroup(), genericInvokeQO.getVersion());
        if(null == service){
            return "rpc.invoke.fail on";
        }
        Object result = service.$invoke(genericInvokeQO.getMethodName(),
                genericInvokeQO.getParameterTypes().toArray(new String[0]), genericInvokeQO.getParameters().toArray());
        return result;
    }


}
