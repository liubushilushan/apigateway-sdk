## API网关设计

### （一）性能数据采集功能
模块：apigateway-performance-collection

### （二）泛化调用网关
模块：apigateway-rpc-genericinvoke

> 模块实现了泛化调用网关。
网关对外提供了一个REST API接口，客户端通过调用该REST API就能调用RPC服务方提供的所有RPC接口。

### （三）限流功能
模块：apigateway-flowlimit
