package org.dreamlife.hippocampus.apigateway.performance.record;


import com.alibaba.dubbo.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.dreamlife.hippocampus.apigateway.performance.model.ApiIndicatorRecord;
import org.dreamlife.hippocampus.apigateway.performance.service.PerformanceSummaryService;

/**
 * 采集RPC接口每次调用时长
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Activate(group = {Constants.CONSUMER})
@Slf4j
public class RpcApiRecorder implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long time = System.currentTimeMillis();
        String api = new StringBuilder(invocation.getInvoker().getInterface().getName())
        .append(".").append(invocation.getMethodName()).toString();
        try {
            return invoker.invoke(invocation);
        } finally {
            long cost = System.currentTimeMillis() - time;
            // 统计接口平均响应时间
            PerformanceSummaryService.getInstance().submit(new ApiIndicatorRecord()
                    .setApi(api)
                    .setIndicatorName("averageTimeCost")
                    .setIndicatorUnit("ms")
                    .setOperation(ApiIndicatorRecord.Operation.AVERAGE)
                    .setIndicatorValue(cost));
            // 统计接口被调用次数
            PerformanceSummaryService.getInstance().submit(new ApiIndicatorRecord()
                    .setApi(api)
                    .setIndicatorName("invokeCount")
                    .setIndicatorUnit("")
                    .setOperation(ApiIndicatorRecord.Operation.COUNT));
        }
    }
}
