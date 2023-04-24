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

package com.zdpx.configure;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.dsmiley.httpproxy.ProxyServlet;

/** */
public class GrafanaProxyServlet extends ProxyServlet {
    @Override
    protected HttpResponse doExecute(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            HttpRequest proxyRequest)
            throws IOException {
        String currentUser = "admin";
        // 设置用户
        proxyRequest.setHeader("Auth", currentUser);
        return super.doExecute(servletRequest, servletResponse, proxyRequest);
    }
}
