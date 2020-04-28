package com.liuapi.apigateway.performance.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/5
 */
@Data
@Accessors(chain = true)
public class ApiIndicatorRecord {
    // 指标名称
    private String indicatorName;
    // 单位
    private String indicatorUnit;

    // key
    private String api;
    // 指标值
    private long indicatorValue;
    // 操作,默认为计数
    private Operation operation = Operation.COUNT;

    public static enum Operation{
        SUMMARY,AVERAGE,COUNT;
    }
}
