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

package org.dinky.alert.wechat;

import static java.util.Objects.requireNonNull;

import org.dinky.alert.AlertResult;
import org.dinky.alert.AlertSendResponse;
import org.dinky.alert.wechat.params.WechatParams;
import org.dinky.utils.HttpUtils;
import org.dinky.utils.JsonUtils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONUtil;

/**
 * WeChatSender
 *
 * @since 2022/2/23 21:11
 */
public class WeChatSender {
    private static final Logger logger = LoggerFactory.getLogger(WeChatSender.class);
    private final WechatParams wechatParams;
    private final String weChatTokenUrlReplace;

    WeChatSender(Map<String, Object> config) {
        this.wechatParams = JSONUtil.toBean(JSONUtil.toJsonStr(config), WechatParams.class);
        if (wechatParams.isAtAll()) {
            wechatParams.getAtUsers().add("all");
        }
        if (wechatParams.getSendType().equals(WeChatType.CHAT.getValue())) {
            requireNonNull(wechatParams.getWebhook(), WeChatConstants.WEB_HOOK + " must not null");
        }
        weChatTokenUrlReplace = String.format(
                WeChatConstants.WECHAT_TOKEN_URL,
                wechatParams.getSendUrl(),
                wechatParams.getCorpId(),
                wechatParams.getSecret());
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
        params.put(WeChatConstants.ALERT_TEMPLATE_TITLE, title);
        params.put(WeChatConstants.ALERT_TEMPLATE_CONTENT, content);
        if (wechatParams.getSendType().equals(WeChatType.APP.getValue())) {
            params.put(WeChatConstants.ALERT_TEMPLATE_AGENT_ID, wechatParams.getAgentId());
        }
        List<String> atUsers = wechatParams.getAtUsers().isEmpty()
                ? new ArrayList<>()
                : wechatParams.getAtUsers().stream()
                        .map(u -> StrFormatter.format("<@{}>", u))
                        .collect(Collectors.toList());
        params.put(WeChatConstants.ALERT_TEMPLATE_AT_USERS, atUsers);
        return params;
    }

    public AlertResult send(String content) {
        AlertResult alertResult = new AlertResult();
        String url;
        try {
            if (WeChatType.APP.getValue().equals(wechatParams.getSendType())) {
                String token = getToken();
                assert token != null;
                url = String.format(WeChatConstants.WECHAT_PUSH_URL, wechatParams.getSendUrl(), token);
            } else {
                url = wechatParams.getWebhook();
            }
            return checkWeChatSendMsgResult(HttpUtils.post(url, content));
        } catch (Exception e) {
            logger.error("send we chat alert msg  exception : {}", e.getMessage());
            alertResult.setMessage("send we chat alert fail");
            alertResult.setSuccess(false);
        }
        return alertResult;
    }

    private String getToken() {
        try {
            String resp;
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(weChatTokenUrlReplace);
                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    HttpEntity entity = response.getEntity();
                    resp = EntityUtils.toString(entity, WeChatConstants.CHARSET);
                    EntityUtils.consume(entity);
                }
                HashMap<String, Object> map = JsonUtils.parseObject(resp, HashMap.class);
                if (map != null && null != map.get(WeChatConstants.ACCESS_TOKEN)) {
                    return map.get(WeChatConstants.ACCESS_TOKEN).toString();
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            logger.error("we chat alert get token error{}", e.getMessage());
        }
        return null;
    }

    private static AlertResult checkWeChatSendMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);
        if (null == result) {
            alertResult.setMessage("we chat send fail");
            logger.error("send we chat msg error,resp is null");
            return alertResult;
        }
        AlertSendResponse sendMsgResponse = JsonUtils.parseObject(result, AlertSendResponse.class);
        if (null == sendMsgResponse) {
            alertResult.setMessage("we chat send fail");
            logger.error("send we chat msg error,resp error");
            return alertResult;
        }
        if (sendMsgResponse.getErrcode() != 0) {
            logger.error(
                    "send we chat msg error,resp error,code:{},msg:{}",
                    sendMsgResponse.getErrcode(),
                    sendMsgResponse.getErrmsg());
            alertResult.setSuccess(false);
            alertResult.setMessage(sendMsgResponse.getErrmsg());
            return alertResult;
        }
        if (sendMsgResponse.getErrcode() == 0) {
            alertResult.setSuccess(true);
            alertResult.setMessage("we chat alert send success");
            return alertResult;
        }
        alertResult.setSuccess(false);
        alertResult.setMessage(sendMsgResponse.getErrmsg());
        return alertResult;
    }
}
