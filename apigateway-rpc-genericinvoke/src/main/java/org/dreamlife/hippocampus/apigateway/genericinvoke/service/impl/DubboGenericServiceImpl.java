package org.dreamlife.hippocampus.apigateway.genericinvoke.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.GenericInvokeQO;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.Response;
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
    private final ReferenceConfigCache cache;
    public DubboGenericServiceImpl() {
        cache = ReferenceConfigCache.getCache();
    }

    public Response invoke(GenericInvokeQO genericInvokeQO) {
        GenericService service = null;
        ReferenceConfig<GenericService> reference = null;
        try {
            reference = new ReferenceConfig<>();
            reference.setInterface(genericInvokeQO.getInterfaceClass());
            reference.setVersion(genericInvokeQO.getVersion());
            reference.setGroup(genericInvokeQO.getGroup());
            reference.setGeneric(true);
            reference.setApplication(ConfigManager.getInstance().getApplication().get());
            reference.setRegistry(ConfigManager.getInstance().getRegistries().values().iterator().next());
            // 考虑部分RPC接口比较耗时
            reference.setTimeout(60 * 1000);
            // 不进行重试
            reference.setRetries(0);
            service = cache.get(reference);
        } catch (Exception e) {
            // 需要移除缓存，同时销毁referenceConfig，防止恶意攻击
            cache.destroy(reference);
            log.warn("RPC服务调用失败: 找不到当前RPC接口的提供者, 请求实体：{}", genericInvokeQO);
            return Response.fail(400, "RPC服务调用失败: 找不到当前RPC接口的提供者");
        }

        try {
            Object result = service.$invoke(genericInvokeQO.getMethodName(),
                    genericInvokeQO.getParameterTypes().toArray(new String[0]), genericInvokeQO.getParameters().toArray());
            return Response.ok(result);
        } catch (Exception e) {
            log.warn("RPC服务调用失败，找不到当前RPC接口的提供者或该目标方法不存在，请求实体：{}", genericInvokeQO);
            return Response.fail(400, String.format("RPC服务调用失败: 找不到当前RPC接口的提供者或该目标方法不存在"));
        }

    }


}
