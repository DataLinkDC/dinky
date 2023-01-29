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
import org.dinky.alert.ShowType;
import org.dinky.assertion.Asserts;
import org.dinky.utils.JSONUtil;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WeChatSender
 *
 * @author wenmo
 * @since 2022/2/23 21:11
 */
public class WeChatSender {

    private static final Logger logger = LoggerFactory.getLogger(WeChatSender.class);
    private static final String ALERT_STATUS = "false";
    private static final String AGENT_ID_REG_EXP = "{agentId}";
    private static final String MSG_REG_EXP = "{msg}";
    private static final String USER_REG_EXP = "{toUser}";
    private static final String CORP_ID_REGEX = "{corpId}";
    private static final String SECRET_REGEX = "{secret}";
    private static final String TOKEN_REGEX = "{token}";
    private static final String SHOW_TYPE_REGEX = "{msgtype}";
    private final String weChatAgentId;
    private final String weChatUsers;
    private final String weChatUserSendMsg;
    private final String weChatTokenUrlReplace;
    private final String weChatToken;
    private final String sendType;
    private static String showType;
    private final String webhookUrl;
    private final String keyWord;
    private final Boolean atAll;

    WeChatSender(Map<String, String> config) {
        sendType = config.get(WeChatConstants.SEND_TYPE);
        weChatAgentId =
                sendType.equals(WeChatType.CHAT.getValue())
                        ? ""
                        : config.get(WeChatConstants.AGENT_ID);
        atAll = Boolean.valueOf(config.get(WeChatConstants.AT_ALL));
        weChatUsers =
                sendType.equals(WeChatType.CHAT.getValue())
                        ? (atAll && config.get(WeChatConstants.AT_USERS) == null
                                ? ""
                                : config.get(WeChatConstants.AT_USERS))
                        : config.get(WeChatConstants.AT_USERS);
        String weChatCorpId =
                sendType.equals(WeChatType.CHAT.getValue())
                        ? ""
                        : config.get(WeChatConstants.CORP_ID);
        String weChatSecret =
                sendType.equals(WeChatType.CHAT.getValue())
                        ? ""
                        : config.get(WeChatConstants.SECRET);
        String weChatTokenUrl =
                sendType.equals(WeChatType.CHAT.getValue()) ? "" : WeChatConstants.WECHAT_TOKEN_URL;
        weChatUserSendMsg = WeChatConstants.WECHAT_APP_TEMPLATE;
        showType = config.get(WeChatConstants.MSG_TYPE);
        requireNonNull(showType, WeChatConstants.MSG_TYPE + " must not null");
        webhookUrl = config.get(WeChatConstants.WEB_HOOK);
        keyWord = config.get(WeChatConstants.KEYWORD);
        if (sendType.equals(WeChatType.CHAT.getValue())) {
            requireNonNull(webhookUrl, WeChatConstants.WEB_HOOK + " must not null");
        }
        weChatTokenUrlReplace =
                weChatTokenUrl
                        .replace(CORP_ID_REGEX, weChatCorpId)
                        .replace(SECRET_REGEX, weChatSecret);
        weChatToken = getToken();
    }

    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();
        List<String> userList = new ArrayList<>();
        if (Asserts.isNotNullString(weChatUsers)) {
            userList = Arrays.asList(weChatUsers.split(","));
        }
        if (atAll) {
            userList.add("all");
        }

        String data = markdownByAlert(title, content, userList);

        String msg = "";
        if (sendType.equals(WeChatType.APP.getValue())) {
            msg =
                    weChatUserSendMsg
                            .replace(USER_REG_EXP, generateUserList(userList))
                            .replace(AGENT_ID_REG_EXP, weChatAgentId)
                            .replace(MSG_REG_EXP, data)
                            .replace(SHOW_TYPE_REGEX, showType);
        } else {
            msg =
                    WeChatConstants.WECHAT_WEBHOOK_TEMPLATE
                            .replace(SHOW_TYPE_REGEX, showType)
                            .replace(MSG_REG_EXP, data);
        }

        if (sendType.equals(WeChatType.APP.getValue()) && Asserts.isNullString(weChatToken)) {
            alertResult.setMessage("send we chat alert fail,get weChat token error");
            alertResult.setSuccess(false);
            return alertResult;
        }
        String enterpriseWeChatPushUrlReplace = "";
        if (sendType.equals(WeChatType.APP.getValue())) {
            enterpriseWeChatPushUrlReplace =
                    WeChatConstants.WECHAT_PUSH_URL.replace(TOKEN_REGEX, weChatToken);
        } else if (sendType.equals(WeChatType.CHAT.getValue())) {
            enterpriseWeChatPushUrlReplace = webhookUrl;
        }
        try {
            return checkWeChatSendMsgResult(post(enterpriseWeChatPushUrlReplace, msg));
        } catch (Exception e) {
            logger.info("send we chat alert msg  exception : {}", e.getMessage());
            alertResult.setMessage("send we chat alert fail");
            alertResult.setSuccess(false);
        }
        return alertResult;
    }

    private static String post(String url, String data) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(data, WeChatConstants.CHARSET));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, WeChatConstants.CHARSET);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            return resp;
        }
    }

    private static String mkUserList(Iterable<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (String name : list) {
            sb.append("\"").append(name).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    /**
     * generate AtUser List
     *
     * @param list
     * @return
     */
    private static String generateUserList(Iterable<String> list) {
        if (Asserts.isNull(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append("|");
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * @Author: zhumingye
     *
     * @date: 2022/3/26 @Description: 将用户列表转换为 <@用户名> 的格式
     * @param userList
     * @return java.lang.String
     */
    private static String generateMarkDownAtUsers(List<String> userList) {

        StringBuilder builder = new StringBuilder("\n");
        if (Asserts.isNotNull(userList)) {
            userList.forEach(
                    value -> {
                        if ("all".equals(value) && showType.equals(ShowType.TEXT.getValue())) {
                            builder.append("@all ");
                        } else {
                            builder.append("<@").append(value).append("> ");
                        }
                    });
        }
        return builder.toString();
    }

    private String markdownByAlert(String title, String content, List<String> userList) {
        String result = "";
        if (showType.equals(ShowType.MARKDOWN.getValue())) {
            result = markdownTable(title, content, userList, sendType);
        } else if (showType.equals(ShowType.TEXT.getValue())) {
            result = markdownText(title, content, userList, sendType);
        }
        return result;
    }

    private static String markdownTable(
            String title, String content, List<String> userList, String sendType) {
        return getMsgResult(title, content, userList, sendType);
    }

    private static String markdownText(
            String title, String content, List<String> userList, String sendType) {
        return getMsgResult(title, content, userList, sendType);
    }

    /**
     * @Author: zhumingye
     *
     * @date: 2022/3/25 @Description: 创建公共方法 用于创建发送消息文本
     * @param title 发送标题
     * @param content 发送内容
     * @param sendType
     * @return java.lang.String
     * @throws
     */
    private static String getMsgResult(
            String title, String content, List<String> userList, String sendType) {

        List<LinkedHashMap> mapItemsList = JSONUtil.toList(content, LinkedHashMap.class);
        if (null == mapItemsList || mapItemsList.isEmpty()) {
            logger.error("itemsList is null");
            throw new RuntimeException("itemsList is null");
        }
        String markDownAtUsers = generateMarkDownAtUsers(userList);
        StringBuilder contents = new StringBuilder(200);
        for (LinkedHashMap mapItems : mapItemsList) {
            Set<Map.Entry<String, Object>> entries = mapItems.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
            StringBuilder t =
                    new StringBuilder(
                            String.format(
                                    "`%s`%s", title, WeChatConstants.MARKDOWN_QUOTE_RIGHT_TAG));

            while (iterator.hasNext()) {

                Map.Entry<String, Object> entry = iterator.next();
                t.append(WeChatConstants.MARKDOWN_QUOTE_RIGHT_TAG);
                t.append(entry.getKey()).append("：").append(entry.getValue());
                t.append(WeChatConstants.ENTER_LINE);
            }
            contents.append(t);
        }
        if (sendType.equals(WeChatType.CHAT.getValue())) {
            contents.append(markDownAtUsers);
        }
        return contents.toString();
    }

    private String getToken() {
        try {
            return getAccessToken(weChatTokenUrlReplace);
        } catch (IOException e) {
            logger.info("we chat alert get token error{}", e.getMessage());
        }
        return null;
    }

    private static String getAccessToken(String url) throws IOException {
        String resp;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, WeChatConstants.CHARSET);
                EntityUtils.consume(entity);
            }
            HashMap<String, Object> map = JSONUtil.parseObject(resp, HashMap.class);
            if (map != null && null != map.get(WeChatConstants.ACCESS_TOKEN)) {
                return map.get(WeChatConstants.ACCESS_TOKEN).toString();
            } else {
                return null;
            }
        }
    }

    private static AlertResult checkWeChatSendMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);
        if (null == result) {
            alertResult.setMessage("we chat send fail");
            logger.info("send we chat msg error,resp is null");
            return alertResult;
        }
        AlertSendResponse sendMsgResponse = JSONUtil.parseObject(result, AlertSendResponse.class);
        if (null == sendMsgResponse) {
            alertResult.setMessage("we chat send fail");
            logger.info("send we chat msg error,resp error");
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
