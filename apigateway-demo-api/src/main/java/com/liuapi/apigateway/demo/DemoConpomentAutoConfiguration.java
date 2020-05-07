package com.liuapi.apigateway.demo;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/5
 */
@Configuration
@ComponentScan("com.liuapi.apigateway.demo")
@DubboComponentScan("com.liuapi.apigateway.demo")
public class DemoConpomentAutoConfiguration {
}
