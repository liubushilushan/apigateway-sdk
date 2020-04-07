package org.dreamlife.hippocampus.apigateway.genericinvoke.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/2
 */
@Data
@Accessors(chain = true)
@ApiModel("泛化调用返回结果")
public class Response implements Serializable {
    @ApiModelProperty("rpc泛化调用的结果")
    private Object data;
    @ApiModelProperty("错误提示")
    private String msg;
    @ApiModelProperty(value= "http请求的状态码",example = "200")
    private int code;

    private Response(){}

    public static Response fail(int status,String err){
        return new Response().setMsg(err).setCode(status);
    }
    public static Response ok(Object data){
        return new Response().setData(data).setCode(200);
    }
}
