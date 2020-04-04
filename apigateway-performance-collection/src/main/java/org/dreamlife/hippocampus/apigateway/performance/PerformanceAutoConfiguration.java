package org.dreamlife.hippocampus.apigateway.performance;

import org.dreamlife.hippocampus.apigateway.performance.record.RestApiRecorder;
import org.dreamlife.hippocampus.apigateway.performance.service.PerformanceSummaryService;
import org.dreamlife.hippocampus.apigateway.performance.sink.SinkPerformanceDataTimer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 接口性能统计服务
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Configuration
public class PerformanceAutoConfiguration {

    /**
     * 接口性能统计的核心服务
     * @return
     */
    @Bean
    public PerformanceSummaryService performanceSummaryService(){
        /**
         * 配置并行度参数
         * 该参数决定计算节点的个数
         */
        int concurrencyLevel = 2;
        return new PerformanceSummaryService(concurrencyLevel);
    }

    /**
     * 定时持久化各个API的性能指标
     * @return
     */
    @Bean
    public SinkPerformanceDataTimer sink(){
        return new SinkPerformanceDataTimer();
    }

    /**
     *
     * 设置拦截器来采集RestApi的接口响应时间与调用次数
     * @return
     */
    @Bean
    @ConditionalOnClass(DispatcherServlet.class)
    public RestApiRecorder restApiRecord(){
        return new RestApiRecorder();
    }
    /**
     *  WEB入口采集rest接口
     * @return
     */
    @Bean
    @ConditionalOnClass(DispatcherServlet.class)
    public FilterRegistrationBean webFilterRegister() {
        FilterRegistrationBean  bean = new FilterRegistrationBean(new RestApiRecorder());
        bean.addUrlPatterns("/*");
        return bean;
    }

}
