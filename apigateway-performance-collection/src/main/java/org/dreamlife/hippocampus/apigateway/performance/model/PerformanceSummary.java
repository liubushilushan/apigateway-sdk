package org.dreamlife.hippocampus.apigateway.performance.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Data
@Accessors(chain = true)
public class PerformanceSummary {
    private long totalInvokeCount;
    private long totalResponseTime;
    private String lastSinkTime;
}
