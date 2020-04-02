package org.dreamlife.hippocampus.apigateway.performance.sink;

import org.dreamlife.hippocampus.apigateway.performance.service.PerformanceSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
public class SinkPerformanceScheduler {
    @Autowired
    private PerformanceSummaryService performanceSummaryService;

    /**
     * 每隔一分钟打印一次各个API的性能数据
     */
    @Scheduled(cron = "0 * * * * ?")
    private void logPerformanceData(){
        performanceSummaryService.sink();
    }
}
