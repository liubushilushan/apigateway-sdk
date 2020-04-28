package com.liuapi.apigateway.server;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/2
 */
@EnableScheduling
@EnableDubbo
@SpringBootApplication
public class ApiGatewayServer {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServer.class,args);
    }
}
