package com.liuapi.apigateway.performance.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.liuapi.apigateway.performance.model.ApiIndicatorRecord;
import com.liuapi.apigateway.performance.model.ApiIndicatorReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * 性能统计服务
 *
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Slf4j
public class PerformanceSummaryService implements InitializingBean {
    private static volatile PerformanceSummaryService INSTANCE;
    /**
     * 并行度
     */
    private final int concurrencyLevel;


    private final List<PerformanceSummaryServiceNode> nodes;

    public PerformanceSummaryService(int concurrencyLevel) {
        // 设置并发度
        this.concurrencyLevel = concurrencyLevel;

        nodes = new ArrayList<>(concurrencyLevel);
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("performance-summary-%s").build();
        IntStream.range(0, concurrencyLevel)
                .forEach(
                        (offset) -> {
                            nodes.add(new PerformanceSummaryServiceNode(factory));
                        }
                );
    }

    /**
     * 通过该方法可获取Spring Ioc容器中的PerformanceSummaryService对象的引用
     */
    public static PerformanceSummaryService getInstance() {
        return INSTANCE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }


    /**
     * 路由
     * 使用简单的取模算法进行路由
     *
     * @param key
     * @return
     */
    private PerformanceSummaryServiceNode route(String key) {
        int offset = 0;
        if (key != null) {
            offset = Math.floorMod(key.hashCode(),concurrencyLevel);
        }
        return nodes.get(offset);
    }
    public void submit(ApiIndicatorRecord record) {
        PerformanceSummaryServiceNode node = route(record.getApi());
        try{
            node.submit(record);
        }catch (RejectedExecutionException t){
            // 任务过载时，不能影响业务线程的执行
            log.error("PerformanceSummaryThreadPool is exhausted, detail msg: {}",t.getMessage());
        }
    }

    /**
     * 获取并重置性能指标
     */
    public void sinkAndReset() {
        nodes.stream()
                .map(
                        node -> {
                            List<ApiIndicatorReport> apiIndicatorReports = null;
                            try {
                                apiIndicatorReports = node.getAndReset().get();
                            } catch (RejectedExecutionException e) {
                                // 任务过载时，不能影响计时器的执行
                                log.error("PerformanceSummaryThreadPool is exhausted, can't get summary report, detail msg: {}",e.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return apiIndicatorReports;
                        }
                )
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(report -> {
                    // 打日志的行为只能交给定时器来完成
                    // 如果交给线程池来完成，则会造成当前任务执行时间过长，堆积其他任务
                    log.info(report.getResult());
                });
    }


}
