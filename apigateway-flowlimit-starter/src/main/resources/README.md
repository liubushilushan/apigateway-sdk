### 限流功能
**模块：apigateway-flowlimit-starter**

**模块简介：**
- 该模块实现REST接口的限流服务，
- 当流量未达到阈值时，限流服务会放行接口。
- 当流量超过阈值后，限流服务会拦截该接口，并给客户端返回513错误码以及友好提示。

**设计细节：**
该限流服务是在guava rateLimiter类上做了一层封装。
- 这里带来一个接口组的概念，一个接口组对应一个正则，匹配这个正则的uri都属于该组，限流服务为一个组创建一个rateLimiter对象，实现组之间的隔离。
- 由于服务基于rateLimiter，所以每次限流的间隔是1秒，限流的算法是令牌桶算法。

**接入方式：**
用户需要添加对该SDK的依赖，同时添加一些配置。
- 如果项目中包含了apigateway-performance-summary-starter的依赖，则每分钟统计并打印被限流的接口以及相应的限流次数。
- 如果项目中不包含apigateway-performance-summary-starter的依赖，则接口每一次被限流就会打印一次日志。

**配置demo：** 
```yaml
flowlimit:
  rules:
  - pattern: ".*"
    key: group1
    tps: 200
```
