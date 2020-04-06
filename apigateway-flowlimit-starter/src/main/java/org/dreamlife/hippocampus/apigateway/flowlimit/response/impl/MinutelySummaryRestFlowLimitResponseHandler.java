package org.dreamlife.hippocampus.apigateway.flowlimit.response.impl;

import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.apigateway.flowlimit.response.RestFlowLimitResponseHandler;
import org.dreamlife.hippocampus.apigateway.performance.model.ApiIndicatorRecord;
import org.dreamlife.hippocampus.apigateway.performance.service.PerformanceSummaryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/6
 */
@Slf4j
public class MinutelySummaryRestFlowLimitResponseHandler implements RestFlowLimitResponseHandler {

    private static final String SYSTEM_BUSY_WORD = "{\"code\":\"system.busy\",\"status\":513}";
    @Override
    public void responseTo(HttpServletRequest request,HttpServletResponse response) {
        response.setStatus(513);
        try {
            response.getWriter().print(SYSTEM_BUSY_WORD);
        } catch (IOException e) {
            log.error("response write error ,msg :{}",e.getMessage(),e);
        } finally {
            // 记录接口被限流的次数
            PerformanceSummaryService.getInstance().submit(
                    new ApiIndicatorRecord()
                            .setApi(request.getRequestURI())
                            .setIndicatorName("flowLimitCount")
                            .setIndicatorUnit("")
                            .setOperation(ApiIndicatorRecord.Operation.COUNT)
            );
        }
    }
}
