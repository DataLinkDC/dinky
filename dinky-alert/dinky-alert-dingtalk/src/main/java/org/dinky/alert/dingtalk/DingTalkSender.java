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

package org.dinky.alert.dingtalk;

import org.dinky.alert.AlertResult;
import org.dinky.alert.AlertSendResponse;
import org.dinky.alert.ShowType;
import org.dinky.assertion.Asserts;
import org.dinky.utils.JSONUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DingTalkSender
 *
 * @author wenmo
 * @since 2022/2/23 19:34
 **/
public class DingTalkSender {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkSender.class);
    private final String url;
    private final String keyword;
    private final String secret;
    private String msgType;
    private final String atMobiles;
    private final String atUserIds;
    private final Boolean atAll;
    private final Boolean enableProxy;
    private String proxy;
    private Integer port;
    private String user;
    private String password;

    DingTalkSender(Map<String, String> config) {
        url = config.get(DingTalkConstants.WEB_HOOK);
        keyword = config.get(DingTalkConstants.KEYWORD);
        secret = config.get(DingTalkConstants.SECRET);
        msgType = config.get(DingTalkConstants.MSG_TYPE);
        atMobiles = config.get(DingTalkConstants.AT_MOBILES);
        atUserIds = config.get(DingTalkConstants.AT_USERIDS);
        atAll = Boolean.valueOf(config.get(DingTalkConstants.AT_ALL));
        enableProxy = Boolean.valueOf(config.get(DingTalkConstants.PROXY_ENABLE));
        if (Boolean.TRUE.equals(enableProxy)) {
            port = Integer.parseInt(config.get(DingTalkConstants.PORT));
            proxy = config.get(DingTalkConstants.PROXY);
            user = config.get(DingTalkConstants.USER);
            password = config.get(DingTalkConstants.PASSWORD);
        }
    }

    public AlertResult send(String title, String content) {
        AlertResult alertResult;
        try {
            String resp = sendMsg(title, content);
            return checkMsgResult(resp);
        } catch (Exception e) {
            logger.info("send ding talk alert msg  exception : {}", e.getMessage());
            alertResult = new AlertResult();
            alertResult.setSuccess(false);
            alertResult.setMessage("send ding talk alert fail.");
        }
        return alertResult;
    }

    private String sendMsg(String title, String content) throws IOException {
        String msg = generateMsgJson(title, content);
        String httpUrl = url;
        if (Asserts.isNotNullString(secret)) {
            httpUrl = generateSignedUrl();
        }
        HttpPost httpPost = new HttpPost(httpUrl);
        StringEntity stringEntity = new StringEntity(msg, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        CloseableHttpClient httpClient;
        if (Boolean.TRUE.equals(enableProxy)) {
            HttpHost httpProxy = new HttpHost(proxy, port);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(new AuthScope(httpProxy), new UsernamePasswordCredentials(user, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
            RequestConfig rcf = RequestConfig.custom().setProxy(httpProxy).build();
            httpPost.setConfig(rcf);
        } else {
            httpClient = HttpClients.createDefault();
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String resp;
            try {
                HttpEntity httpEntity = response.getEntity();
                resp = EntityUtils.toString(httpEntity, "UTF-8");
                EntityUtils.consume(httpEntity);
            } finally {
                response.close();
            }
            return resp;
        } finally {
            httpClient.close();
        }
    }

    private String generateMsgJson(String title, String content) {
        if (Asserts.isNullString(msgType)) {
            msgType = ShowType.TEXT.getValue();
        }
        Map<String, Object> items = new HashMap<>();
        items.put("msgtype", msgType);
        Map<String, Object> text = new HashMap<>();
        items.put(msgType, text);
        if (ShowType.MARKDOWN.getValue().equals(msgType)) {
            generateMarkdownMsg(title, content, text);
        } else {
            generateTextMsg(title, content, text);
        }
        setMsgAt(items);
        return JSONUtil.toJsonString(items);
    }

    private void generateTextMsg(String title, String content, Map<String, Object> text) {
        StringBuilder builder = new StringBuilder();
        if (Asserts.isNotNullString(keyword)) {
            builder.append(keyword);
            builder.append("\n");
        }
        String txt = genrateResultMsg(title, content, builder);
        text.put("content", txt);
    }

    private void generateMarkdownMsg(String title, String content, Map<String, Object> text) {
        StringBuilder builder = new StringBuilder("# ");
        if (Asserts.isNotNullString(keyword)) {
            builder.append(" ");
            builder.append(keyword);
        }
        builder.append("\n\n");
        if (Asserts.isNotNullString(atMobiles)) {
            Arrays.stream(atMobiles.split(",")).forEach(value -> {
                builder.append("@");
                builder.append(value);
                builder.append(" ");
            });
        }
        if (Asserts.isNotNullString(atUserIds)) {
            Arrays.stream(atUserIds.split(",")).forEach(value -> {
                builder.append("@");
                builder.append(value);
                builder.append(" ");
            });
        }
        builder.append("\n\n");
        String txt = genrateResultMsg(title, content, builder);
        text.put("title", title);
        text.put("text", txt);
    }

    /**
     * 公共生成 markdown 和 text 消息
     *
     * @param title   标题
     * @param content 内容
     * @param builder 拼接字符串
     * @return
     */
    private String genrateResultMsg(String title, String content, StringBuilder builder) {
        List<LinkedHashMap> mapSendResultItemsList = JSONUtil.toList(content, LinkedHashMap.class);
        if (null == mapSendResultItemsList || mapSendResultItemsList.isEmpty()) {
            logger.error("itemsList is null");
            throw new RuntimeException("itemsList is null");
        }
        for (LinkedHashMap mapItems : mapSendResultItemsList) {
            Set<Map.Entry<String, Object>> entries = mapItems.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
            StringBuilder t = new StringBuilder(String.format("`%s`%s", title, DingTalkConstants.MARKDOWN_ENTER));

            while (iterator.hasNext()) {

                Map.Entry<String, Object> entry = iterator.next();
                t.append(DingTalkConstants.MARKDOWN_QUOTE);
                t.append(entry.getKey()).append("：").append(entry.getValue());
                t.append(DingTalkConstants.MARKDOWN_ENTER);
            }
            builder.append(t);
        }
        byte[] byt = StringUtils.getBytesUtf8(builder.toString());
        String txt = StringUtils.newStringUtf8(byt);
        return txt;
    }

    private String generateSignedUrl() {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            logger.error("generate sign error, message:{}", e);
        }
        return url + "&timestamp=" + timestamp + "&sign=" + sign;
    }

    private void setMsgAt(Map<String, Object> items) {
        Map<String, Object> at = new HashMap<>();
        String[] atMobileArray = Asserts.isNotNullString(atMobiles) ? atMobiles.split(",") : new String[0];
        String[] atUserArray = Asserts.isNotNullString(atUserIds) ? atUserIds.split(",") : new String[0];
        boolean isAtAll = Objects.isNull(atAll) ? false : atAll;
        at.put("isAtAll", isAtAll);
        if (atMobileArray.length > 0) {
            at.put("atMobiles", atMobileArray);
        }
        if (atMobileArray.length > 0) {
            at.put("atUserIds", atUserArray);
        }
        items.put("at", at);
    }

    private AlertResult checkMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        if (null == result) {
            alertResult.setMessage("send ding talk msg error");
            logger.info("send ding talk msg error,ding talk server resp is null");
            return alertResult;
        }
        AlertSendResponse response = JSONUtil.parseObject(result, AlertSendResponse.class);
        if (null == response) {
            alertResult.setMessage("send ding talk msg fail");
            logger.info("send ding talk msg error,resp error");
            return alertResult;
        }
        if (response.getErrcode() == 0) {
            alertResult.setSuccess(true);
            alertResult.setMessage("send ding talk msg success");
            return alertResult;
        }
        alertResult.setMessage(String.format("alert send ding talk msg error : %s", response.getErrmsg()));
        logger.info("alert send ding talk msg error : {}", response.getErrmsg());
        return alertResult;
    }
}
