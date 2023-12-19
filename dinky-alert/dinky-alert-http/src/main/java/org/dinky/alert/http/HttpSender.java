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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * DingTalkSender
 */
public class HttpSender {
    private static final Logger logger = LoggerFactory.getLogger(HttpSender.class);

    private final HttpParams httpParams;

    private HttpRequestBase httpRequest;

    HttpSender(Map<String, Object> config) {
        this.httpParams = JSONUtil.toBean(JSONUtil.toJsonStr(config), HttpParams.class);
        Asserts.checkNotNull(httpParams, "dingTalkParams is null");
    }

    /**
     * build template params
     *
     * @param title
     * @param content
     * @return
     */
    public Map<String, Object> buildTemplateParams(String title, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put(HttpConstants.ALERT_TEMPLATE_TITLE, title);
        params.put(HttpConstants.ALERT_TEMPLATE_MSG, content);
        return params;
    }

    /**
     * send msg of main
     *
     * @param content： send msg content
     * @return AlertResult
     */
    public AlertResult send(String content) {
        AlertResult alertResult = new AlertResult();

        try {
            createHttpRequest(content);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (httpParams.getMethod() == null) {
            alertResult.setSuccess(false);
            alertResult.setMessage("Request types are not supported");
            return alertResult;
        }

        try {
            String resp = this.getResponseString(httpRequest);
            alertResult.setSuccess(true);
            alertResult.setMessage(resp);
        } catch (Exception e) {
            logger.error("send http alert msg  exception : {}", e.getMessage());
            alertResult.setSuccess(false);
            alertResult.setMessage("send http request  alert fail.");
        }

        return alertResult;
    }

    private void createHttpRequest(String msg) throws MalformedURLException, URISyntaxException {
        if (HttpConstants.REQUEST_TYPE_POST.equals(httpParams.getMethod())) {
            httpRequest = new HttpPost(httpParams.getUrl());
            buildRequestHeader();
            buildMsgToRequestBody(msg);
        } else if (HttpConstants.REQUEST_TYPE_GET.equals(httpParams.getMethod())) {
            buildMsgToUrl(msg);
            URL unencodeUrl = new URL(httpParams.getUrl());
            URI uri = new URI(
                    unencodeUrl.getProtocol(),
                    unencodeUrl.getHost(),
                    unencodeUrl.getPath(),
                    unencodeUrl.getQuery(),
                    null);

            httpRequest = new HttpGet(uri);
            buildRequestHeader();
        }
    }

    public String getResponseString(HttpRequestBase httpRequest) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, HttpConstants.DEFAULT_CHARSET);
    }

    /**
     * add msg param in url
     */
    private void buildMsgToUrl(String msg) {

        String line = "&";
        // check splice char is & or ?
        if (!httpParams.getUrl().contains("?")) {
            line = "?";
        }
        httpParams.setUrl(String.format("%s%smsg=%s", httpParams.getUrl(), line, msg));
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
    private void buildMsgToRequestBody(String msg) {
        try {
            JSONObject jsonObject = JSONUtil.createObj().set(HttpConstants.ALERT_TEMPLATE_MSG, msg);
            StringEntity entity = new StringEntity(JSONUtil.toJsonStr(jsonObject), HttpConstants.DEFAULT_CHARSET);
            ((HttpPost) httpRequest).setEntity(entity);
        } catch (Exception e) {
            logger.error("send http alert msg  exception : {}", e.getMessage());
        }
    }
}
