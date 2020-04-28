package com.liuapi.apigateway.performance.record;

import com.liuapi.apigateway.performance.model.ApiIndicatorRecord;
import lombok.extern.slf4j.Slf4j;
import com.liuapi.apigateway.performance.service.PerformanceSummaryService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 采集restApi的接口响应时间与调用次数
 * <p>
 * 这里使用Write Back策略：先缓存请求的统计指标，再异步将汇总结果输出
 * 如果每个请求都打日志的话，对磁盘IO会有很大压力
 */
@Slf4j
public class RestApiRecorder extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        long time = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String api = request.getRequestURI();
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            if(404 == httpServletResponse.getStatus()){
                // 不统计404的接口，否则会有内存泄露的风险
                return;
            }
            if("/error".equals(api)||"/favicon.ico".equals(api)){
                // 不统计error或图标的接口，没有意义
                return;
            }
            // 通过消息队列异步处理
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
