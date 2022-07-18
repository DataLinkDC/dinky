package com.dlink.interceptor;


import com.dlink.context.RequestContext;
import com.mysql.cj.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.HandlerInterceptor;


/**
 * tenant interceptor
 */
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("tenant interceptor preHandle execute ");
        String tenantId = request.getHeader("tenantId");
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            RequestContext.set(Integer.valueOf(tenantId));
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}