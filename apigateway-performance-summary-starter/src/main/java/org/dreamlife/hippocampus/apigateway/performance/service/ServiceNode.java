package org.dreamlife.hippocampus.apigateway.performance.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.apigateway.performance.model.ApiIndicatorRecord;
import org.dreamlife.hippocampus.apigateway.performance.model.ApiIndicatorReport;
import org.dreamlife.hippocampus.apigateway.performance.model.ApiIndicatorSummary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
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
    // eg. api -> [{"summary":10,"invokeCount":5}]
    private final Map<String, Map<String, ApiIndicatorSummary>> segment;

    private final ExecutorService executor;

    public ServiceNode(ThreadFactory factory) {
        executor = new ThreadPoolExecutor(
                1, 1,
                0, TimeUnit.MILLISECONDS
                , Queues.newLinkedBlockingQueue(1024)
                , factory);
        segment = Maps.newHashMap();
    }

    public void submit(ApiIndicatorRecord record) {
        executor.submit(() -> {
            final String api = record.getApi();
            // 简单累加性能值
            Map<String, ApiIndicatorSummary> indicators = segment.get(record.getApi());
            if (null == indicators) {
                indicators = Maps.newHashMap();
                segment.put(record.getApi(), indicators);
            }
            ApiIndicatorSummary summary = indicators.get(record.getIndicatorName());
            if (summary == null) {
                summary = new ApiIndicatorSummary().setApi(record.getApi())
                        .setCount(0)
                        .setIndicatorName(record.getIndicatorName())
                        .setIndicatorUnit(record.getIndicatorUnit())
                        .setOperation(record.getOperation())
                        .setSummaryValue(0);
                indicators.put(record.getIndicatorName(), summary);
            }

            switch (record.getOperation()) {
                case SUMMARY:
                case AVERAGE:
                    summary.setSummaryValue(summary.getSummaryValue() + record.getIndicatorValue());
                    break;
                default:
                    break;
            }
            summary.setCount(summary.getCount() + 1);
        });
    }

    public Future<List<ApiIndicatorReport>> report() {
        Callable sink = () -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<ApiIndicatorReport> reports = segment.keySet()
                    .stream()
                    .map(
                            api -> {
                                Map<String, ApiIndicatorSummary> indicators = segment.get(api);
                                return indicators.values().stream()
                                        .filter(summary -> summary.getCount() > 0)
                                        .map(summary -> assemble(summary, now))
                                        .collect(Collectors.toList())
                                        ;
                            }
                    )
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            // 清空累计值
            reports.stream()
                    .forEach(
                            report -> {
                                ApiIndicatorSummary summary = segment.get(report.getApi()).get(report.getIndicatorName());
                                summary.setCount(0)
                                        .setSummaryValue(0)
                                        .setLastSinkTime(now);
                            }
                    );

            return reports;
        };
        // 给每个线程服务都提交一个sink任务
        return executor.submit(sink);
    }

    private ApiIndicatorReport assemble(ApiIndicatorSummary summary, String currentTime) {
        long count = summary.getCount();
        if (count <= 0) {
            return null;
        }
        String displayValue = null;
        switch (summary.getOperation()) {
            case AVERAGE:
                displayValue = summary.getSummaryValue() / count + "";
                break;
            case SUMMARY:
                displayValue = summary.getSummaryValue() + "";
                break;
            case COUNT:
                displayValue = summary.getCount() + "";
                break;
        }
        String result = String.format("API: %s, %s: %s %s, during %s , %s",
                summary.getApi(), summary.getIndicatorName(), displayValue, summary.getIndicatorUnit(), summary.getLastSinkTime(), currentTime);

        return new ApiIndicatorReport()
                .setApi(summary.getApi())
                .setIndicatorName(summary.getIndicatorName())
                .setResult(result);
    }


}
