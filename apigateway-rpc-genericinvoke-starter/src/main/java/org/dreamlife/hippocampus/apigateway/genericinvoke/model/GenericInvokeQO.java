package org.dreamlife.hippocampus.apigateway.genericinvoke.model;

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
public class GenericInvokeQO {
    private String interfaceClass;
    private String methodName;
    private String version;
    private String group;
    private List<String> parameterTypes;
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
