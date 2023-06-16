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

package org.dinky.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

@RestController()
@RequestMapping(FlinkProxyController.API)
public class FlinkProxyController {
    public static final String API = "/api/flink/";

    @RequestMapping("/**")
    public void proxyUba(HttpServletRequest request, HttpServletResponse resp)
            throws IOException, URISyntaxException {
        // String url = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
        URI uri = new URI(request.getRequestURI());
        String path = uri.getPath();
        if (!StrUtil.contains(path, API)) {
            return;
        }
        path = path.replace(API, "");

        String query = request.getQueryString();
        if (StrUtil.isNotBlank(query)) {
            path = HttpUtil.urlWithForm(path, URLUtil.decode(query), StandardCharsets.UTF_8, true);
        }
        HttpRequest httpRequest = HttpUtil.createRequest(Method.valueOf(request.getMethod()), path);
        HttpResponse execute = httpRequest.execute();
        if (execute.body() != null) {
            execute.headers()
                    .forEach(
                            (k, v) -> {
                                resp.addHeader(k, v.get(0));
                            });
            execute.writeBody(resp.getOutputStream(), true, null);
        }

        //        URI newUri = new URI(target);
        //        // 执行代理查询
        //        String methodName = request.getMethod();
        //        HttpMethod httpMethod = HttpMethod.resolve(methodName);
        //        if (httpMethod == null) {
        //            return;
        //        }
        //        ClientHttpRequest delegate = new
        // SimpleClientHttpRequestFactory().createRequest(newUri, httpMethod);
        //        Enumeration<String> headerNames = request.getHeaderNames();
        //        // 设置请求头
        //        while (headerNames.hasMoreElements()) {
        //            String headerName = headerNames.nextElement();
        //            Enumeration<String> v = request.getHeaders(headerName);
        //            List<String> arr = new ArrayList<>();
        //            while (v.hasMoreElements()) {
        //                arr.add(v.nextElement());
        //            }
        //            delegate.getHeaders().addAll(headerName, arr);
        //        }
        //        List<String[]> stringsList = new LinkedList<>();
    }
}
