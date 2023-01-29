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

package org.dinky.alert.feishu;

import static java.util.Objects.requireNonNull;

import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;
import org.dinky.assertion.Asserts;
import org.dinky.utils.JSONUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @Author: zhumingye
 *
 * @date: 2022/4/2 @Description: 飞书消息发送器
 */
public final class FeiShuSender {

    private static final Logger logger = LoggerFactory.getLogger(FeiShuSender.class);
    static final String FEI_SHU_USER_REGX = "{users}";
    static final String MSG_RESULT_REGX = "{msg}";
    static final String MSG_TYPE_REGX = "{msg_type}";
    static final String FEI_SHU_KEYWORD_REGX = "{keyword}";

    private final String url;
    private final String msgType;
    private final Boolean enableProxy;
    private final String secret;
    private final String keyword;
    private String proxy;
    private Integer port;
    private String user;
    private String password;
    private final Boolean atAll;
    private String atUserIds;

    FeiShuSender(Map<String, String> config) {
        url = config.get(FeiShuConstants.WEB_HOOK);

        msgType = config.get(FeiShuConstants.MSG_TYPE);
        requireNonNull(msgType, FeiShuConstants.MSG_TYPE + " must not null");

        keyword =
                config.get(FeiShuConstants.KEYWORD) != null
                        ? config.get(FeiShuConstants.KEYWORD).replace("\r\n", "")
                        : "";
        enableProxy = Boolean.valueOf(config.get(FeiShuConstants.PROXY_ENABLE));
        secret = config.get(FeiShuConstants.SECRET);
        if (Boolean.TRUE.equals(enableProxy)) {
            proxy = config.get(FeiShuConstants.PROXY);
            port = Integer.parseInt(config.get(FeiShuConstants.PORT));
            user = config.get(FeiShuConstants.USER);
            password = config.get(FeiShuConstants.PASSWORD);
        }
        atAll = Boolean.valueOf(config.get(FeiShuConstants.AT_ALL));
        if (Boolean.FALSE.equals(atAll)) {
            atUserIds = config.get(FeiShuConstants.AT_USERS);
        }
    }

    /**
     * main send msg
     *
     * @param title
     * @param content
     * @return AlertResult
     */
    public AlertResult send(String title, String content) {
        AlertResult alertResult;
        try {
            String sendMsgOfResult = buildSendMsgAndSendOfResult(title, content);
            return checkSendMsgResult(sendMsgOfResult);
        } catch (Exception e) {
            logger.error("send fei shu alert msg  exception : {}", e.getMessage());
            alertResult = new AlertResult();
            alertResult.setSuccess(false);
            alertResult.setMessage("send fei shu alert fail.");
        }
        return alertResult;
    }

    /**
     * send msg
     *
     * @param title
     * @param content
     * @return
     * @throws IOException
     */
    private String buildSendMsgAndSendOfResult(String title, String content) throws IOException {

        String msg = replaceParamsToBuildSendMsgTemplate(title, content);

        HttpPost httpPost = buildHttpPost(url, msg);

        CloseableHttpClient httpClient;
        if (Boolean.TRUE.equals(enableProxy)) {
            httpClient = getCloseableHttpClientOfProxy(httpPost);
        } else {
            httpClient = HttpClients.createDefault();
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("send feishu message error, return http status code: {} ", statusCode);
            }
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, FeiShuConstants.CHARSET);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            logger.info("Fei Shu send title :{} ,content :{}, resp: {}", title, content, resp);
            return resp;
        } finally {
            httpClient.close();
        }
    }

    /**
     * parse json to template
     *
     * @param title
     * @param content
     * @return
     */
    private String replaceParamsToBuildSendMsgTemplate(String title, String content) {

        Integer currentTimeMillis = Math.toIntExact(System.currentTimeMillis() / 1000);

        String jsonResult = "";
        byte[] byt = StringUtils.getBytesUtf8(buildFinalResultMsgBody(title, content));
        String contentResult = StringUtils.newStringUtf8(byt);
        String userIdsToText = buildAtUserList(Asserts.isNullString(atUserIds) ? "all" : atUserIds);
        if (StringUtils.equals(ShowType.TEXT.getValue(), msgType)) {
            jsonResult =
                    FeiShuConstants.FEI_SHU_TEXT_TEMPLATE
                            .replace(MSG_TYPE_REGX, msgType)
                            .replace(
                                    MSG_RESULT_REGX,
                                    keyword
                                            + FeiShuConstants.MARKDOWN_ENTER_BACK_SLASH
                                            + contentResult)
                            .replace(FEI_SHU_USER_REGX, userIdsToText)
                            .replaceAll("/n", "\\\\n");
        } else {
            jsonResult =
                    FeiShuConstants.FEI_SHU_POST_TEMPLATE
                            .replace(MSG_TYPE_REGX, msgType)
                            .replace(FEI_SHU_KEYWORD_REGX, keyword)
                            .replace(MSG_RESULT_REGX, contentResult)
                            .replace(FEI_SHU_USER_REGX, userIdsToText)
                            .replaceAll("/n", "\\\\n");
        }

        if (Asserts.isNotNullString(secret)) {
            ObjectNode jsonNodes = JSONUtil.parseObject(jsonResult);
            jsonNodes.put(FeiShuConstants.SIGN_TMESTAMP, currentTimeMillis);
            jsonNodes.put(FeiShuConstants.SIGN, getSign(secret, currentTimeMillis));
            return jsonNodes.toString();
        } else {
            return jsonResult;
        }
    }

    /**
     * generate sign
     *
     * @param secretKey
     * @param timestamp
     * @return
     */
    private String getSign(String secretKey, Integer timestamp) {
        String stringToSign = timestamp + FeiShuConstants.ENTER_LINE + secretKey;
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(
                    new SecretKeySpec(
                            stringToSign.getBytes(FeiShuConstants.CHARSET), "HmacSHA256"));
            byte[] signData = mac.doFinal(new byte[] {});
            sign = new String(Base64.encodeBase64(signData));
        } catch (Exception e) {
            logger.error("generate sign error, message:{}", e.getMessage());
        }
        return sign;
    }

    /**
     * Generate AtUsers
     *
     * @param users
     * @return
     */
    private String buildAtUserList(String users) {
        String[] userList = users.split(",");

        StringBuilder atUserList = new StringBuilder();
        if (msgType.equals(ShowType.TEXT.getValue())) {
            for (String user : userList) {
                atUserList.append("<at user_id=\\\"").append(user).append("\\\"></at>");
            }
        } else {
            for (String user : userList) {
                atUserList.append("{\"tag\":\"at\",\"user_id\":\"").append(user).append("\"},");
            }
            atUserList.deleteCharAt(atUserList.length() - 1);
        }
        return atUserList.toString();
    }

    /**
     * Generate send msg
     *
     * @param title
     * @param content
     * @return
     */
    public static String buildFinalResultMsgBody(String title, String content) {
        List<LinkedHashMap> mapSendResultItemsList = JSONUtil.toList(content, LinkedHashMap.class);
        if (null == mapSendResultItemsList || mapSendResultItemsList.isEmpty()) {
            logger.error("itemsList is null");
            throw new RuntimeException("itemsList is null");
        }
        StringBuilder contents = new StringBuilder(100);
        contents.append(String.format("`%s` %s", title, FeiShuConstants.MARKDOWN_ENTER_BACK_SLASH));
        for (LinkedHashMap mapItems : mapSendResultItemsList) {
            Set<Entry<String, Object>> entries = mapItems.entrySet();
            Iterator<Entry<String, Object>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                String key = entry.getKey();
                String value = entry.getValue().toString();
                contents.append(key + "：" + value)
                        .append(FeiShuConstants.MARKDOWN_ENTER_BACK_SLASH);
            }
            return contents.toString();
        }
        return null;
    }

    private static HttpPost buildHttpPost(String httpUrl, String msg) {
        HttpPost httpPost = new HttpPost(httpUrl);
        StringEntity stringEntity = new StringEntity(msg, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        return httpPost;
    }

    /**
     * checkSendFeiShuSendMsgResult
     *
     * @param result
     * @return
     */
    public static AlertResult checkSendMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        if (Asserts.isNull(result)) {
            alertResult.setMessage("send fei shu msg error");
            logger.info("send fei shu msg error,fei shu server resp is null");
            return alertResult;
        }
        FeiShuSendMsgResponse sendMsgResponse =
                JSONUtil.parseObject(result, FeiShuSendMsgResponse.class);

        if (null == sendMsgResponse) {
            alertResult.setMessage("send fei shu msg fail");
            logger.info("send fei shu msg error,resp error");
            return alertResult;
        }
        if (sendMsgResponse.statusCode == 0) {
            alertResult.setSuccess(true);
            alertResult.setMessage("send fei shu msg success");
            return alertResult;
        }
        alertResult.setMessage(
                String.format(
                        "alert send fei shu msg error : %s", sendMsgResponse.getStatusMessage()));
        logger.info(
                "alert send fei shu msg error : {} ,Extra : {} ",
                sendMsgResponse.getStatusMessage(),
                sendMsgResponse.getExtra());
        return alertResult;
    }

    /**
     * get CloseableHttpClient Of Proxy
     *
     * @param httpPost
     * @return
     */
    private CloseableHttpClient getCloseableHttpClientOfProxy(HttpPost httpPost) {
        CloseableHttpClient httpClient;
        HttpHost httpProxy = new HttpHost(proxy, port);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(httpProxy), new UsernamePasswordCredentials(user, password));
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
        RequestConfig rcf = RequestConfig.custom().setProxy(httpProxy).build();
        httpPost.setConfig(rcf);
        return httpClient;
    }

    static final class FeiShuSendMsgResponse {

        @JsonProperty("Extra")
        private String extra;

        @JsonProperty("StatusCode")
        private Integer statusCode;

        @JsonProperty("StatusMessage")
        private String statusMessage;

        public FeiShuSendMsgResponse() {}

        public String getExtra() {
            return this.extra;
        }

        @JsonProperty("Extra")
        public void setExtra(String extra) {
            this.extra = extra;
        }

        public Integer getStatusCode() {
            return this.statusCode;
        }

        @JsonProperty("StatusCode")
        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMessage() {
            return this.statusMessage;
        }

        @JsonProperty("StatusMessage")
        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FeiShuSendMsgResponse)) {
                return false;
            }
            final FeiShuSendMsgResponse other = (FeiShuSendMsgResponse) o;
            final Object this$extra = this.getExtra();
            final Object other$extra = other.getExtra();
            if (this$extra == null ? other$extra != null : !this$extra.equals(other$extra)) {
                return false;
            }
            final Object this$statusCode = this.getStatusCode();
            final Object other$statusCode = other.getStatusCode();
            if (this$statusCode == null
                    ? other$statusCode != null
                    : !this$statusCode.equals(other$statusCode)) {
                return false;
            }
            final Object this$statusMessage = this.getStatusMessage();
            final Object other$statusMessage = other.getStatusMessage();
            if (this$statusMessage == null
                    ? other$statusMessage != null
                    : !this$statusMessage.equals(other$statusMessage)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $extra = this.getExtra();
            result = result * PRIME + ($extra == null ? 43 : $extra.hashCode());
            final Object $statusCode = this.getStatusCode();
            result = result * PRIME + ($statusCode == null ? 43 : $statusCode.hashCode());
            final Object $statusMessage = this.getStatusMessage();
            result = result * PRIME + ($statusMessage == null ? 43 : $statusMessage.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "FeiShuSender.FeiShuSendMsgResponse(extra="
                    + this.getExtra()
                    + ", statusCode="
                    + this.getStatusCode()
                    + ", statusMessage="
                    + this.getStatusMessage()
                    + ")";
        }
    }
}
