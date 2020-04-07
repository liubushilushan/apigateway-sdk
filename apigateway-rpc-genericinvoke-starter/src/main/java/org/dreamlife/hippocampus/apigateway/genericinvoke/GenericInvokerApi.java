package org.dreamlife.hippocampus.apigateway.genericinvoke;

import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.GenericInvokeQO;
import org.dreamlife.hippocampus.apigateway.genericinvoke.model.Response;
import org.dreamlife.hippocampus.apigateway.genericinvoke.service.RpcGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/1
 */
@RestController
@Api(value = "GenericInvokerApi", tags = {"泛化调用"})
public class GenericInvokerApi {
    @Autowired
    private RpcGenericService rpcGenericService;
    @ApiOperation("泛化调用接口")
    @PostMapping("/api/gateway.do")
    public Response invoke(@RequestBody GenericInvokeQO genericInvokeQO) {
        String interfaceClass = genericInvokeQO.getInterfaceClass();
        if(Strings.isNullOrEmpty(interfaceClass)){
            return Response.fail(400,"参数错误： interfaceClass为空");
        }
        String methodName = genericInvokeQO.getMethodName();
        if(Strings.isNullOrEmpty(methodName)){
            return Response.fail(400,"参数错误： method为空");
        }

        List<String> parameterTypes = genericInvokeQO.getParameterTypes();
        List<Object> parameters = genericInvokeQO.getParameters();
        if(null == parameterTypes){
            parameterTypes = Collections.emptyList();
            genericInvokeQO.setParameterTypes(parameterTypes);
        }
        if(null == parameters){
            parameters = Collections.emptyList();
            genericInvokeQO.setParameters(parameters);
        }
        if(parameters.size()!=parameterTypes.size()){
            return  Response.fail(400,
                    "参数错误： parameter为所有参数的列表，parameterTypes为对应参数的全限类名列表, 两个列表的元素个数必须相等");
        }
        return rpcGenericService.invoke(genericInvokeQO);
    }
}
