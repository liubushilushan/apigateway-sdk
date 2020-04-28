## 泛化调用网关

### 前言
- 常见的RPC调用是客户端需要依赖RPC服务的接口SDK才能去调用RPC接口。
- 那如果客户端不想依赖接口SDK，只想使用一个通用的RPC接口，通过传入不同的入参来调用RPC服务方提供的所有RPC接口，
这是可行的，这种调用方式被称为泛化调用。
- 具有泛化调用功能的网关也被称为泛化调用网关。

### 项目简介
该模块实现了泛化调用网关。网关对外提供了一个REST API接口
客户端通过调用该REST API就能调用RPC服务提供的所有RPC接口。

### 接口文档    

**请求URI：** 
- ` /api/gateway.do `
  
**请求方式：**
- POST  application/json

**请求示例**
``` 
{
  "interfaceClass": "DemoService",
  "methodName": "sayHello",
  "version": null,
  "group": null,
  "parameterTypes": ["java.lang.String"],
  "parameters":["hello, developer: 柳俊阳"]
}
```

**参数：** 

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|interfaceClass |是  |string |RPC接口类名   |
|methodName |是  |string |RPC接口方法名   |
|group |否  |string |RPC接口所在的组   |
|version |否  |string |RPC接口的版本号   |
|parameterTypes |否  |Array |RPC接口的所有入参类型的列表   |
|parameter |否  |Array |RPC接口的所有入参的值   |

 **返回示例**

``` 
{
    "data": "Hello hello, developer: 柳俊阳, response from provider: 10.0.75.1:20880",
    "msg": null,
    "code": 200
}
```

 **返回参数说明** 

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |数值   |200表示请求成功可以获取data中的数据，400表示请求参数异常，500表示服务器异常，513表示该接口当前被限流稍后再试|
|msg |字符   |错误提示信息|
|data |字符   |RPC接口返回内容|





