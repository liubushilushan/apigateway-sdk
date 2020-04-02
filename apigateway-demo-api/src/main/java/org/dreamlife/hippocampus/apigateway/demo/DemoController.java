package org.dreamlife.hippocampus.apigateway.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Slf4j
@RestController
public class DemoController {
    @Reference
    private DemoService demoService;

    @GetMapping("/ping/{randomSuffix}")
    public String ping(@PathVariable("randomSuffix") String randomSuffix){
        int sleep = new Random().nextInt(100);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String flag = String.format("invoke %s, cost time: %s ms","/ping/"+randomSuffix,sleep);
        log.info(flag);
        return flag;
    }
    @GetMapping("/hello/{randomSuffix}")
    public String sayHello(@PathVariable("randomSuffix") String randomSuffix){
        return demoService.sayHello(randomSuffix);
    }
}
