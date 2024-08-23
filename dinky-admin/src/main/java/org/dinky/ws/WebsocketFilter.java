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

package org.dinky.ws;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketFilter implements Filter {

    private static final String TOKEN_KEY = "Sec-WebSocket-Protocol";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(TOKEN_KEY);

        log.debug(
                "Websocket connection Token security verification，Path = {}，token:{}", request.getRequestURI(), token);

        if (StpUtil.getLoginIdByToken(token) == null) {
            log.debug(
                    "Websocket connection token security verification failed, Path = {}，token:{}",
                    request.getRequestURI(),
                    token);
            throw NotLoginException.newInstance("", "", "", token);
        }

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader(TOKEN_KEY, token);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
