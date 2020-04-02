package org.dreamlife.hippocampus.apigateway.performance.record;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dreamlife.hippocampus.apigateway.performance.model.PerformanceRecord;
import org.dreamlife.hippocampus.apigateway.performance.service.PerformanceSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 采集restApi的接口响应时间与调用次数
 * <p>
 * 这里使用Write Back策略：先缓存请求的统计指标，再异步将汇总结果输出
 * 如果每个请求都打日志的话，对磁盘IO会有很大压力
 */
@Slf4j
@Aspect
public class RestApiRecorder {

    @Autowired
    private PerformanceSummaryService performanceSummaryService;

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void controllerLog() {
    }

    /**
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("controllerLog()")
    public Object record(ProceedingJoinPoint pjp) throws Throwable {
        long time = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String api = request.getRequestURL().toString();
        try {
            return pjp.proceed();
        } finally {
            // 通过消息队列异步处理
            long cost = System.currentTimeMillis() - time;
            performanceSummaryService.submit(new PerformanceRecord().setResponseMills(cost).setApi(api));
        }
    }





}
