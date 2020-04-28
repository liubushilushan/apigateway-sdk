package com.liuapi.apigateway.flowlimit;

import com.liuapi.apigateway.flowlimit.filter.RestApiFlowLimitFilter;
import com.liuapi.apigateway.flowlimit.response.impl.DefaultRestFlowLimitResponseHandler;
import com.liuapi.apigateway.flowlimit.response.impl.MinutelySummaryRestFlowLimitResponseHandler;
import com.ptc.board.flowlimit.FlowLimiterManager;
import com.ptc.board.flowlimit.FlowLimiterService;
import com.ptc.board.flowlimit.config.FlowLimiterConfig;
import com.ptc.board.flowlimit.router.DefaultLimiterFactoryImp;
import com.liuapi.apigateway.flowlimit.response.RestFlowLimitResponseHandler;
import com.liuapi.apigateway.performance.service.PerformanceSummaryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 * 限流策略
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/2
 */
@Configuration
public class FlowLimitAutoConfiguration {
    /**
     * 配置信息
     * API 根据KEY来分组, 一个KEY一个限流策略
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "flowlimit")
    public FlowLimiterConfig flowLimiterConfig() {
        return new FlowLimiterConfig();
    }

    /**
     *  限流策略核心服务
     *  一个key，对应一个Guava RateLimiter对象
     * @param flowLimiterConfig
     * @return
     */
    @Bean
    public FlowLimiterService limitService(FlowLimiterConfig flowLimiterConfig) {
        return new FlowLimiterManager().create(flowLimiterConfig, new DefaultLimiterFactoryImp());
    }

    /**
     * 当包含 apigateway-performance-collection 包时,选择MinutelySummaryRestFlowLimitResponseHandler作为被限流接口的处理器
     * @return
     */
    @Bean
    @ConditionalOnClass(PerformanceSummaryService.class)
    public RestFlowLimitResponseHandler summaryRestFlowLimitResponseHandler(){
        return new MinutelySummaryRestFlowLimitResponseHandler();
    }

    /**
     * 当项目缺少RestFlowLimitResponseHandler时，会初始化缺省的限流处理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public RestFlowLimitResponseHandler restFlowLimitResponseHandler(){
        return new DefaultRestFlowLimitResponseHandler();
    }

    /**
     *  注册限流WebFilter，在Web入口处放置拦截器
     * @param limitService
     * @return
     */
    @Bean
    public FilterRegistrationBean restApiFlowLimitFilterRegister(FlowLimiterService limitService,RestFlowLimitResponseHandler restFlowLimitResponseHandler) {
        FilterRegistrationBean  bean = new FilterRegistrationBean(new RestApiFlowLimitFilter(limitService,restFlowLimitResponseHandler));
        bean.addUrlPatterns("/*");
        return bean;
    }
}
