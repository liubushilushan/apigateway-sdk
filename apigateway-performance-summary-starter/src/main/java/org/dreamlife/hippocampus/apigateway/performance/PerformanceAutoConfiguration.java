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
     * 定时器
     * 每分钟持久化并重置接口的性能指标
     * @return
     */
    @Bean
    public SinkPerformanceDataTimer sink(){
        return new SinkPerformanceDataTimer();
    }

    /**
     *  WEB入口设置Filter采集rest接口
     * @return
     */
    @Bean
    @ConditionalOnClass(DispatcherServlet.class)
    public FilterRegistrationBean restApiRecorderFilterRegister() {
        FilterRegistrationBean  bean = new FilterRegistrationBean(new RestApiRecorder());
        bean.addUrlPatterns("/*");
        /**
         * 此处说明：
         * 由于设计上只想去统计正常接口的响应时长，考虑到部分拦截器会直接拦截掉OPTION请求、限流部分接口。
         * 若将接口性能统计拦截器位于其他拦截器之前，那除了统计正常接口的响应时长，该拦截器还统计了被拦截或限流的接口的响应时长，造成与实际结果的偏差
         * 因此此处将拦截器置于拦截器链的结尾
          */
        bean.setOrder(Integer.MAX_VALUE);
        return bean;
    }

}
