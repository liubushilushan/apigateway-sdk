package org.dreamlife.hippocampus.apigateway.genericinvoke.service;

import org.dreamlife.hippocampus.apigateway.genericinvoke.model.GenericInvokeQO;

import java.util.List;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/1
 */
public interface RpcGenericService {
    Object invoke(GenericInvokeQO genericInvokeQO) throws Exception;
}
