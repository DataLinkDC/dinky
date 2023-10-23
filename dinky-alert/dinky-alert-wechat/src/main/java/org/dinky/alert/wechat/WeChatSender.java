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
import org.dinky.utils.HttpUtils;
import org.dinky.utils.JsonUtils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.text.StrFormatter;

/**
 * WeChatSender
 *
 * @since 2022/2/23 21:11
 */
public class WeChatSender {
    private static final Logger logger = LoggerFactory.getLogger(WeChatSender.class);
    private static final String CORP_ID_REGEX = "{corpId}";
    private static final String SECRET_REGEX = "{secret}";
    private static final String TOKEN_REGEX = "{token}";
    private final String weChatAgentId;
    private String weChatUsers;
    private final String weChatTokenUrlReplace;
    private final String sendType;
    private final String webhookUrl;

    WeChatSender(Map<String, String> config) {
        weChatAgentId = config.getOrDefault(WeChatConstants.AGENT_ID, "");
        weChatUsers = config.getOrDefault(WeChatConstants.AT_USERS, "");
        String isAtAll = config.getOrDefault(WeChatConstants.AT_ALL, "");
        if (Boolean.parseBoolean(isAtAll)) {
            weChatUsers = "all";
        }

        webhookUrl = config.get(WeChatConstants.WEB_HOOK);

        sendType = config.get(WeChatConstants.SEND_TYPE);
        if (sendType.equals(WeChatType.CHAT.getValue())) {
            requireNonNull(webhookUrl, WeChatConstants.WEB_HOOK + " must not null");
        }

        String weChatCorpId = config.getOrDefault(WeChatConstants.CORP_ID, "");
        String weChatSecret = config.getOrDefault(WeChatConstants.SECRET, "");

        weChatTokenUrlReplace = WeChatConstants.WECHAT_TOKEN_URL
                .replace(CORP_ID_REGEX, weChatCorpId)
                .replace(SECRET_REGEX, weChatSecret);
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
        params.put(WeChatConstants.ALERT_TEMPLATE_AGENT_ID, weChatAgentId);
        List<String> atUsers = Arrays.stream(weChatUsers.split(","))
                .map(u -> StrFormatter.format("<@{}>", u))
                .collect(Collectors.toList());
        params.put(WeChatConstants.ALERT_TEMPLATE_AT_USERS, atUsers);
        return params;
    }

    public AlertResult send(String content) {
        AlertResult alertResult = new AlertResult();
        String url;
        try {
            if (sendType.equals(WeChatType.APP.getValue())) {
                String token = getToken();
                assert token != null;
                url = WeChatConstants.WECHAT_PUSH_URL.replace(TOKEN_REGEX, token);
            } else {
                url = webhookUrl;
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
