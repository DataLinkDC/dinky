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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import lombok.SneakyThrows;

@Controller
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
        String web = "web/";
        List<String> pathSplit = StrUtil.split(path, "/");
        if (path.contains(web)) {
            CollUtil.removeBlank(pathSplit);
            if (pathSplit.size() < 2) {
                return;
            }
            String host = pathSplit.get(1);
            path = path.replace(web + host, "");
            if ("/".equals(path)) {
                ServletUtil.write(
                        resp,
                        ResourceUtil.getStream("classpath:/static/flink/index.html"),
                        MediaType.TEXT_HTML_VALUE);
                return;
            }
            String mimeType = FileUtil.getMimeType(path);
            if (StrUtil.isBlank(mimeType)) {
                HttpRequest httpRequest =
                        HttpUtil.createRequest(Method.valueOf(request.getMethod()), host + path);
                writeToHttpServletResponse(httpRequest.execute(), resp);
            } else {
                ServletUtil.write(
                        resp, ResourceUtil.getStream("classpath:/static/flink/" + path), mimeType);
            }
            return;
        }
        if (StrUtil.isBlank(path)) {
            return;
        }
        String query = request.getQueryString();
        if (StrUtil.isNotBlank(query)) {
            path = HttpUtil.urlWithForm(path, URLUtil.decode(query), StandardCharsets.UTF_8, true);
        }
        List<String> urls = StrUtil.split(pathSplit.remove(0), ",");
        pathSplit.add(0, urls.get(RandomUtil.randomInt(urls.size())));
        HttpRequest httpRequest =
                HttpUtil.createRequest(
                        Method.valueOf(request.getMethod()), StrUtil.join("/", pathSplit));
        try (HttpResponse httpResponse = httpRequest.execute(); ) {
            writeToHttpServletResponse(httpResponse, resp);
        }
    }

    @SneakyThrows
    public void writeToHttpServletResponse(HttpResponse httpResponse, HttpServletResponse resp) {
        if (httpResponse.body() != null) {
            httpResponse
                    .headers()
                    .forEach(
                            (k, v) -> {
                                resp.addHeader(k, v.get(0));
                            });
            httpResponse.writeBody(resp.getOutputStream(), true, null);
        }
    }
}
