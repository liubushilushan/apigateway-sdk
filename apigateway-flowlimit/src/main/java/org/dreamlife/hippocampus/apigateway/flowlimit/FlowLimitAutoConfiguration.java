package org.dreamlife.hippocampus.apigateway.flowlimit;

import com.ptc.board.flowlimit.FlowLimiterManager;
import com.ptc.board.flowlimit.FlowLimiterService;
import com.ptc.board.flowlimit.config.FlowLimiterConfig;
import com.ptc.board.flowlimit.router.DefaultLimiterFactoryImp;
import org.dreamlife.hippocampus.apigateway.flowlimit.filter.RestApiFlowLimitFilter;
import org.dreamlife.hippocampus.apigateway.flowlimit.response.RestFlowLimitResponseHandler;
import org.dreamlife.hippocampus.apigateway.flowlimit.response.impl.SimpleRestFlowLimitResponseHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.reflect.generics.tree.Tree;

import javax.servlet.annotation.WebFilter;


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
@ConditionalOnProperty(name = "flowlimit.env.key")
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
     *  处理被限流的请求
     * @return
     */
    @Bean
    public RestFlowLimitResponseHandler restFlowLimitResponseHandler(){
        return new SimpleRestFlowLimitResponseHandler();
    }

    /**
     *  注册限流WebFilter，在Web入口处放置拦截器
     * @param limitService
     * @param restFlowLimitResponseHandler
     * @return
     */
    @Bean
    public FilterRegistrationBean webFilterRegister(FlowLimiterService limitService, RestFlowLimitResponseHandler restFlowLimitResponseHandler) {
        FilterRegistrationBean  bean = new FilterRegistrationBean(new RestApiFlowLimitFilter(limitService,restFlowLimitResponseHandler));
        bean.addUrlPatterns("/*");
        return bean;
    }
}
