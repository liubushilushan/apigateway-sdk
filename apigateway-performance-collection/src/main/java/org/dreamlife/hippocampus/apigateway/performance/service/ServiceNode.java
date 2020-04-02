package org.dreamlife.hippocampus.apigateway.performance.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.apigateway.performance.model.PerformanceRecord;
import org.dreamlife.hippocampus.apigateway.performance.model.PerformanceSummary;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 服务节点
 *
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/31
 */
@Slf4j
public class ServiceNode {
    private final Map<String, PerformanceSummary> segment;
    private final ExecutorService executor;

    public ServiceNode(ThreadFactory factory){
        executor = new ThreadPoolExecutor(
                1, 1,
                0, TimeUnit.MILLISECONDS
                , Queues.newLinkedBlockingQueue(1024)
                , factory);
        segment = Maps.newHashMap();
    }

    public void submit(PerformanceRecord record){
        executor.submit(() -> {
            final String api = record.getApi();
            final long responseMills = record.getResponseMills();
            // 简单累加性能值
            PerformanceSummary reference = segment.get(api);
            if (reference == null) {
                reference = new PerformanceSummary();
                segment.put(api, reference);
            }
            reference.setTotalInvokeCount(reference.getTotalInvokeCount() + 1);
            reference.setTotalResponseTime(reference.getTotalResponseTime() + responseMills);
        });
    }

    public void sink(){
        Runnable sink = () -> {
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            segment.keySet().stream()
                    .forEach(
                            api -> {
                                // 获取性能值，并执行sink操作
                                PerformanceSummary value = segment.get(api);
                                long totalInvokeCount = value.getTotalInvokeCount();
                                if (totalInvokeCount <= 0) {
                                    return;
                                }
                                double averageTimeCost = value.getTotalResponseTime() / totalInvokeCount;
                                // 打印出每个被请求接口的平均响应时间
                                log.info("API: {}, averageCostTime: {} ms, totalInvokeCount: {}, during {}, {}",
                                        api, averageTimeCost, totalInvokeCount, value.getLastSinkTime(),currentTime);
                                // 性能值清空
                                segment.put(api,
                                        value.setTotalResponseTime(0)
                                                .setTotalInvokeCount(0))
                                        .setLastSinkTime(currentTime);
                                ;
                            }
                    );
        };
        // 给每个线程服务都提交一个sink任务
        executor.submit(sink);
    }

}
