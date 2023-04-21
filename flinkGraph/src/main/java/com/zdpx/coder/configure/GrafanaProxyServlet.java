package com.zdpx.coder.configure;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class GrafanaProxyServlet extends ProxyServlet {
    @Override
    protected HttpResponse doExecute(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                     HttpRequest proxyRequest) throws IOException {
        String currentUser = "admin";
        // 设置用户
        proxyRequest.setHeader("Auth", currentUser);
        return super.doExecute(servletRequest, servletResponse, proxyRequest);
    }
}
