package org.dreamlife.hippocampus.apigateway.genericinvoke.service.impl;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.rpc.service.GenericService;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.GenericInvokeQO;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.Response;
import org.dreamlife.hippocampus.apigateway.genericinvoke.service.RpcGenericService;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    private final LoadingCache<String, FutureTask<GenericService>> cache;

    public DubboGenericServiceImpl() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60))
                .build(CacheLoader.from(
                        (cacheKey) -> new FutureTask<>(() -> {
                            String[] param = cacheKey.split(".");
                            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                            reference.setInterface(param[0]);
                            reference.setGroup(param[1]);
                            reference.setVersion(param[2]);
                            reference.setGeneric(true);
                            reference.setApplication(ConfigManager.getInstance().getApplication().get());
                            reference.setRegistry(ConfigManager.getInstance().getRegistries().values().iterator().next());
                            // 考虑部分RPC接口比较耗时
                            reference.setTimeout(60 * 1000);
                            // 不进行重试
                            reference.setRetries(0);
                            GenericService genericService = reference.get();
                            return genericService;
                        })
                ));
    }

    private String generateKey(String interfaceClass, String group, String version) {
        return interfaceClass + "." + group + "." + version;
    }

    private GenericService getService(String interfaceClass, String group, String version) throws ExecutionException, InterruptedException {
        String cacheKey = generateKey(interfaceClass, group, version);
        FutureTask<GenericService> futureTask = cache.get(cacheKey);
        return futureTask.get();
    }

    @Override
    public Response invoke(GenericInvokeQO genericInvokeQO) {
        GenericService service = null;
        try {
            service = getService(genericInvokeQO.getInterfaceClass(),
                    genericInvokeQO.getGroup(), genericInvokeQO.getVersion());
        } catch (Exception e) {
            log.warn("RPC服务调用失败: 找不到当前RPC接口的提供者, 请求实体：{}", genericInvokeQO);
            return Response.fail(400, "RPC服务调用失败: 找不到当前RPC接口的提供者");
        }
        try {
            Object result = service.$invoke(genericInvokeQO.getMethodName(),
                    genericInvokeQO.getParameterTypes().toArray(new String[0]), genericInvokeQO.getParameters().toArray());
            return Response.ok(result);
        } catch (Exception e) {
            log.warn("当前接口调用失败，不存在目标方法，请求实体：{}", genericInvokeQO);
            return Response.fail(400, String.format("RPC服务调用失败: 不存在目标方法（ %s ）", genericInvokeQO.getMethodName()));
        }

    }


}
