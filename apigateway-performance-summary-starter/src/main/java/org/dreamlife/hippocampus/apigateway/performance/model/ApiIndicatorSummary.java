package org.dreamlife.hippocampus.apigateway.performance.model;

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
public class ApiIndicatorSummary {
    // 指标名称
    private String indicatorName;
    // 单位
    private String indicatorUnit;
    // 接口名称
    private String api;
    // 汇总值
    private long summaryValue;
    // 统计次数
    private long count;
    // 上次更新时间
    private String lastSinkTime;
    // 操作
    private ApiIndicatorRecord.Operation operation;
}
