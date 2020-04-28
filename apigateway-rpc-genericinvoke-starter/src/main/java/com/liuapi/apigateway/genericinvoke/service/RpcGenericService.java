package com.liuapi.apigateway.genericinvoke.service;

import com.liuapi.apigateway.genericinvoke.model.GenericInvokeQO;
import com.liuapi.apigateway.genericinvoke.model.Response;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/1
 */
public interface RpcGenericService {
    Response invoke(GenericInvokeQO genericInvokeQO);
}
