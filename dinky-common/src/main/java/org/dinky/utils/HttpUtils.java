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

package org.dinky.utils;

import org.dinky.data.model.ProxyConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static String post(String url, String jsonParam) throws IOException {
        return post(url, jsonParam, null);
    }

    /**
     * post json data
     *
     * @param url
     * @param jsonParam
     * @return
     * @throws IOException
     */
    public static String post(String url, String jsonParam, ProxyConfig proxyConfig) throws IOException {

        HttpPost httpPost = buildHttpPost(url, jsonParam);

        CloseableHttpClient httpClient;
        if (proxyConfig != null) {
            httpClient = getCloseableHttpClientOfProxy(httpPost, proxyConfig);
        } else {
            httpClient = HttpClients.createDefault();
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                logger.debug(
                        "post data success, return http status code: {} , msg: {}",
                        statusCode,
                        response.getStatusLine().getReasonPhrase());
            } else {
                logger.warn(
                        "post data error, return http status code: {}, msg: {} ",
                        statusCode,
                        response.getStatusLine().getReasonPhrase());
            }
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            return resp;
        } finally {
            httpClient.close();
        }
    }

    /**
     * build HttpPost
     *
     * @param httpUrl
     * @param msg
     * @return
     */
    private static HttpPost buildHttpPost(String httpUrl, String msg) {
        HttpPost httpPost = new HttpPost(httpUrl);
        StringEntity stringEntity = new StringEntity(msg, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        return httpPost;
    }

    /**
     * get CloseableHttpClient Of Proxy
     *
     * @param httpPost
     * @return
     */
    private static CloseableHttpClient getCloseableHttpClientOfProxy(HttpPost httpPost, ProxyConfig proxyConfig) {
        CloseableHttpClient httpClient;
        HttpHost httpProxy = new HttpHost(proxyConfig.getHostname(), proxyConfig.getPort());
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(httpProxy),
                new UsernamePasswordCredentials(proxyConfig.getUser(), proxyConfig.getPassword()));
        httpClient =
                HttpClients.custom().setDefaultCredentialsProvider(provider).build();
        RequestConfig rcf = RequestConfig.custom().setProxy(httpProxy).build();
        httpPost.setConfig(rcf);
        return httpClient;
    }

    /**
     * asyncRequest
     *
     * @param addressList
     * @param urlParams
     * @param timeout
     * @param consumer
     */
    public static void asyncRequest(
            List<String> addressList, String urlParams, int timeout, Consumer<HttpResponse> consumer) {
        if (CollUtil.isEmpty(addressList)) {
            return;
        }
        int index = RandomUtil.randomInt(addressList.size());
        String url = addressList.get(index);
        try {
            HttpUtil.createGet(url + urlParams).disableCache().timeout(timeout).then(consumer);
        } catch (Exception e) {
            logger.error("url-timeout :{} ", url);
            addressList.remove(index);
            asyncRequest(addressList, urlParams, timeout, consumer);
        }
    }
}
