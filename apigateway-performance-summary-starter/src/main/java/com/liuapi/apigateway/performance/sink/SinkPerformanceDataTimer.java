package com.liuapi.apigateway.performance.sink;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import com.liuapi.apigateway.performance.service.PerformanceSummaryService;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Slf4j
public class SinkPerformanceDataTimer implements InitializingBean {
    private final ScheduledExecutorService timer;

    public SinkPerformanceDataTimer() {
        timer = new ScheduledThreadPoolExecutor(1,
                new ThreadFactoryBuilder().setNameFormat("performance-timer-sink-%s").build()
                );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        int initialDelayInSecond = 60 - now.getSecond();
        timer.scheduleAtFixedRate(() ->
                {
                    PerformanceSummaryService.getInstance().sinkAndReset();
                }, initialDelayInSecond, 60, TimeUnit.SECONDS
        );
        log.info("Thread [performance-timer-sinkAndReset] begin to sinkAndReset every minute...");
    }
}
