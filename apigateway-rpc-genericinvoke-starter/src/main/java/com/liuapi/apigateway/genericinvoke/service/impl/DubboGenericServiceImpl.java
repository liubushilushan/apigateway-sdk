package com.liuapi.apigateway.genericinvoke.service.impl;


import com.liuapi.apigateway.genericinvoke.model.GenericInvokeQO;
import com.liuapi.apigateway.genericinvoke.model.Response;
import com.liuapi.apigateway.genericinvoke.service.RpcGenericService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;

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
public class DubboGenericServiceImpl implements RpcGenericService, InitializingBean, ApplicationContextAware {
    private final ReferenceConfigCache cache;
    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;
    private ApplicationContext applicationContext;

    public DubboGenericServiceImpl() {
        cache = ReferenceConfigCache.getCache();
    }

    @Override
    public Response invoke(GenericInvokeQO genericInvokeQO) {
        if(applicationConfig == null || registryConfig == null){
            log.error("RPC服务调用失败: 缺少dubbo配置");
            return Response.fail(400, "RPC服务调用失败: 当前服务缺少dubbo配置");
        }

        GenericService service = null;
        ReferenceConfig<GenericService> reference = null;
        try {
            reference = new ReferenceConfig<>();
            reference.setInterface(genericInvokeQO.getInterfaceClass());
            reference.setVersion(genericInvokeQO.getVersion());
            reference.setGroup(genericInvokeQO.getGroup());
            reference.setGeneric(true);
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
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


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ApplicationConfig> applicationConfigMap = applicationContext.getBeansOfType(ApplicationConfig.class, false, false);
        if(applicationConfigMap.size()==1){
            ApplicationConfig applicationConfig = applicationConfigMap.values().iterator().next();
            if(applicationConfig.isValid()){
                this.applicationConfig = applicationConfig;
            }
        }
        if(this.applicationConfig==null){
            log.error("缺少配置： dubbo.application.name, 这将会导致泛化调用失败");
        }

        Map<String, RegistryConfig> registryConfigMap = applicationContext.getBeansOfType(RegistryConfig.class, false, false);
        if(registryConfigMap.size()>=1){
            RegistryConfig registryConfig = registryConfigMap.values().iterator().next();
            if(registryConfig.isValid()){
                this.registryConfig = registryConfig;
            }
        }
        if(this.registryConfig==null){
            log.error("缺少配置： dubbo.registry.address, 这将会导致泛化调用失败");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}