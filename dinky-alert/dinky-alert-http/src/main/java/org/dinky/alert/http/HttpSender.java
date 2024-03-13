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

package org.dinky.alert.http;

import org.dinky.alert.AlertResult;
import org.dinky.alert.http.params.HttpParams;
import org.dinky.assertion.Asserts;
import org.dinky.data.ext.ConfigItem;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * Http Sender
 */
public class HttpSender {
    private static final Logger logger = LoggerFactory.getLogger(HttpSender.class);

    private final HttpParams httpParams;
    private HttpRequestBase httpRequest;

    HttpSender(Map<String, Object> config) {
        this.httpParams = JSONUtil.toBean(JSONUtil.toJsonStr(config), HttpParams.class);
        Asserts.checkNotNull(httpParams, "httpParams is null");
    }

    /**
     * send msg of main
     *
     * @param content： send msg content
     * @return AlertResult
     */
    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();

        if (httpParams.getMethod() == null) {
            alertResult.setSuccess(false);
            alertResult.setMessage("Request types are not supported");
            return alertResult;
        }

        try {
            createHttpRequest(title, content);
            String resp = this.getResponseString(httpRequest);
            alertResult.setSuccess(true);
            alertResult.setMessage(resp);
        } catch (Exception e) {
            logger.error("send http alert msg  failed", e);
            alertResult.setSuccess(false);
            alertResult.setMessage("send http request  alert fail.");
        }

        return alertResult;
    }

    private void createHttpRequest(String title, String content) {
        if (HttpConstants.REQUEST_TYPE_POST.equals(httpParams.getMethod())) {
            httpRequest = new HttpPost(httpParams.getUrl());
            buildRequestHeader();
            buildMsgToRequestBody(title, content);
        } else if (HttpConstants.REQUEST_TYPE_GET.equals(httpParams.getMethod())) {
            // TODO Get implementation
        }
    }

    public String getResponseString(HttpRequestBase httpRequest) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, HttpConstants.DEFAULT_CHARSET);
        }
    }

    /**
     * add msg param in url
     */
    private void buildMsgToUrl(Map<String, Object> templateParams) {

        String line;
        // check splice char is & or ?
        if (!httpParams.getUrl().contains("?")) {
            line = "?";
        } else {
            line = "&";
        }
        templateParams.forEach((k, v) -> {
            httpParams.setUrl(String.format("%s%s%s=%s", httpParams.getUrl(), line, k, v));
        });
    }

    private void buildRequestHeader() {
        List<ConfigItem> paramsHeaders = httpParams.getHeaders();
        if (CollUtil.isNotEmpty(paramsHeaders)) {
            paramsHeaders.forEach(configItem -> {
                httpRequest.setHeader(configItem.getKey(), configItem.getValue());
            });
        }
    }

    /**
     * set body params
     */
    private void buildMsgToRequestBody(String title, String content) {
        try {
            JSONObject body = JSONUtil.parseObj(httpParams.getBody());
            if (TextUtils.isEmpty(httpParams.getTitleFiled())) {
                content = StrFormatter.format("{}\n{}", title, content);
            } else {
                body.putByPath(httpParams.getTitleFiled(), title);
            }
            body.putByPath(httpParams.getContentFiled(), content);
            StringEntity entity = new StringEntity(JSONUtil.toJsonStr(body), HttpConstants.DEFAULT_CHARSET);
            ((HttpPost) httpRequest).setEntity(entity);
        } catch (Exception e) {
            logger.error("send http alert msg  exception", e);
        }
    }
}
