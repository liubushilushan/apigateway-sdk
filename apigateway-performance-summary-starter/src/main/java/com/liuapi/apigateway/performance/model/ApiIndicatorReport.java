package com.liuapi.apigateway.performance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/6
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ApiIndicatorReport {
    private String api;
    private String indicatorName;
    private String result;
}
