package org.dinky.ws;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class WebsocketFilter implements Filter {

    private static final String TOKEN_KEY = "Sec-WebSocket-Protocol";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(TOKEN_KEY);

        log.debug("Websocket connection Token security verification，Path = {}，token:{}", request.getRequestURI(), token);

        if (StpUtil.getLoginIdByToken(token)==null) {
            log.debug("Websocket connection token security verification failed, Path = {}，token:{}",request.getRequestURI(), token);
            throw NotLoginException.newInstance("","","",token);
        }

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader(TOKEN_KEY, token);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
