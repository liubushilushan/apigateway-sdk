package org.dreamlife.hippocampus.apigateway.performance.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * 封装一次请求相关的性能指标
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Data
@Accessors(chain = true)
public class PerformanceRecord {
    private String api;
    private long responseMills;
}
