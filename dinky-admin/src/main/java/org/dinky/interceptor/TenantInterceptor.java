/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.interceptor;

import org.dinky.assertion.Asserts;
import org.dinky.context.TenantContextHolder;
import org.dinky.data.annotation.PublicInterface;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.lang.Opt;
import lombok.extern.slf4j.Slf4j;

/** tenant interceptor */
@Slf4j
public class TenantInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        boolean isPass = false;
        Cookie[] cookies = request.getCookies();
        Opt<String> token = Opt.empty();
        if (Asserts.isNotNull(cookies)) {
            for (Cookie cookie : cookies) {
                switch (cookie.getName()) {
                    case "satoken":
                        token = Opt.ofBlankAble(cookie.getValue());
                        if (SaManager.getSaTokenDao().get("satoken:login:token:" + token.get())
                                != null) {
                            isPass = true;
                        }
                        break;
                    case "tenantId":
                        TenantContextHolder.set(Integer.valueOf(cookie.getValue()));
                        break;
                }
            }
        }
        if (!isPass) {
            if (handler instanceof HandlerMethod) {
                if (((HandlerMethod) handler).getMethodAnnotation(PublicInterface.class) != null) {
                    return true;
                }
            }
            throw NotLoginException.newInstance("", "-1", token.get());
        }
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }
}
