package org.dreamlife.hippocampus.apigateway.flowlimit.response;

import javax.servlet.http.HttpServletResponse;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/4/2
 */
public interface RestFlowLimitResponseHandler {
    void responseTo(HttpServletResponse response);
}
