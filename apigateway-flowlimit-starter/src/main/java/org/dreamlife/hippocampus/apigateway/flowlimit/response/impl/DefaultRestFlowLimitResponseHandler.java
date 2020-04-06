package org.dreamlife.hippocampus.apigateway.flowlimit.response.impl;

import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.apigateway.flowlimit.response.RestFlowLimitResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/2
 */
@Slf4j
public class DefaultRestFlowLimitResponseHandler implements RestFlowLimitResponseHandler {
    private static final String SYSTEM_BUSY_WORD = "{\"code\":\"system.busy\",\"status\":513}";
    @Override
    public void responseTo(HttpServletRequest request, HttpServletResponse response) {
        // 每条被限流的接口都会打印一条日志
        log.error("system busy");
        response.setStatus(513);
        try {
            response.getWriter().print(SYSTEM_BUSY_WORD);
        } catch (IOException e) {
           log.error("response write error ,msg :{}",e.getMessage(),e);
        }
    }
}
