package org.dreamlife.hippocampus.apigateway.genericinvoke.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/1
 */
@Data
@ApiModel("泛化调用请求实体")
public class GenericInvokeQO {
    @ApiModelProperty(value = "接口的全限类名", required = true,example = "com.dtdream.gamma.user.api.v3.GammaV3OrganizationService")
    private String interfaceClass;
    @ApiModelProperty(value = "接口的方法名", required = true,example = "getNode")
    private String methodName;
    @ApiModelProperty(value = "版本号",example = "1.0.0")
    private String version;
    @ApiModelProperty(value = "组",example = "DEFAULT")
    private String group;
    @ApiModelProperty(value = "所有参数的全限类名列表",example = "[\"java.lang.Long\"]")
    private List<String> parameterTypes;
    @ApiModelProperty(value = "所有参数的列表",example = "[\"21\"]")
    private List<Object> parameters;

    @Override
    public String toString() {
        return "{" +
                "\"interfaceClass\":'" + interfaceClass + '\'' +
                ", \"methodName\":'" + methodName + '\'' +
                ", \"version\":'" + version + '\'' +
                ", \"group\":'" + group + '\'' +
                ", \"parameterTypes\":" + parameterTypes +
                ", \"parameters\":" + parameters +
                '}';
    }
}
