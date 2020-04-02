package org.dreamlife.hippocampus.apigateway.performance.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.apigateway.performance.model.PerformanceRecord;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
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


    private final List<ServiceNode> nodes;

    public PerformanceSummaryService(int concurrencyLevel) {
        // 设置并发度
        this.concurrencyLevel = concurrencyLevel;

        nodes = new ArrayList<>(concurrencyLevel);
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("performance-summary-%s").build();
        IntStream.range(0, concurrencyLevel)
                .forEach(
                        (offset) -> {
                            nodes.add(new ServiceNode(factory));
                        }
                );
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    /**
     * 路由
     * 使用简单的取余算法进行路由
     * @param key
     * @return
     */
    private ServiceNode route(String key) {
        int offset = 0;
        if (key != null) {
            offset = Math.abs(key.hashCode()) % concurrencyLevel;
        }
        return nodes.get(offset);
    }

    /**
     * 当调用方没有注入Spring IoC容器中的PerformanceSummaryService实例
     * 需要调用该兼容性的静态方法才能提交任务
     * @param record
     */
    public static void compatibleSubmit(PerformanceRecord record){
        INSTANCE.submit(record);
    }

    public void submit(PerformanceRecord record) {
        ServiceNode node = route(record.getApi());
        node.submit(record);
    }

    /**
     * 持久化性能指标
     */
    public void sink() {
        IntStream.range(0, concurrencyLevel)
                .forEach(
                        (offset) -> {
                            nodes.get(offset).sink();
                        }
                );
    }


}
