package com.liuapi.apigateway.flowlimit.filter;


import com.ptc.board.flowlimit.FlowLimiterService;
import com.liuapi.apigateway.flowlimit.response.RestFlowLimitResponseHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 在WEB入口处对REST API进行限流
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/2
 */
public class RestApiFlowLimitFilter extends OncePerRequestFilter {
    private final FlowLimiterService limiterService;

    private final RestFlowLimitResponseHandler restFlowLimitResponseHandler;

    public RestApiFlowLimitFilter(FlowLimiterService limiterService,RestFlowLimitResponseHandler restFlowLimitResponseHandler){
        this.limiterService = limiterService;
        this.restFlowLimitResponseHandler = restFlowLimitResponseHandler;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String api = httpServletRequest.getServletPath();
        if (limiterService.tryAcquire(api, 1)) {
            // 在阈值内的流量接着往下执行
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        } else {
            // 超过阈值的流量在这里被拦截
            // 返回友好信息
            restFlowLimitResponseHandler.responseTo(httpServletRequest,httpServletResponse);
        }
    }
}
