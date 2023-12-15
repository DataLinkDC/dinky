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

import org.dinky.alert.AlertResult;
import org.dinky.alert.feishu.params.FeiShuParams;
import org.dinky.assertion.Asserts;
import org.dinky.data.model.ProxyConfig;
import org.dinky.utils.HttpUtils;
import org.dinky.utils.JsonUtils;

import org.apache.commons.codec.binary.Base64;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.hutool.json.JSONUtil;

/**
 * fei shu sender
 */
public final class FeiShuSender {
    private static final Logger logger = LoggerFactory.getLogger(FeiShuSender.class);

    private final FeiShuParams feiShuParams;
    private ProxyConfig proxyConfig = null;

    FeiShuSender(Map<String, Object> config) {

        this.feiShuParams = JSONUtil.toBean(JSONUtil.toJsonStr(config), FeiShuParams.class);

        if (Boolean.TRUE.equals(feiShuParams.isEnableProxy())) {
            proxyConfig = new ProxyConfig(
                    feiShuParams.getHostname(),
                    feiShuParams.getPort(),
                    feiShuParams.getUser(),
                    feiShuParams.getPassword());
        }
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
        params.put(FeiShuConstants.ALERT_TEMPLATE_TITLE, title);
        params.put(FeiShuConstants.ALERT_TEMPLATE_CONTENT, content);
        params.put(FeiShuConstants.ALERT_TEMPLATE_KEYWORD, feiShuParams.getKeyword());
        if (Asserts.isNotNullString(feiShuParams.getSecret())) {
            Integer currentTimeMillis = Math.toIntExact(System.currentTimeMillis() / 1000) - 1800;
            params.put(FeiShuConstants.SIGN_TMESTAMP, currentTimeMillis);
            params.put(FeiShuConstants.SIGN, getSign(feiShuParams.getSecret(), currentTimeMillis));
        }
        List<String> atUsers =
                CollectionUtils.isEmpty(feiShuParams.getAtUsers()) ? Arrays.asList("all") : feiShuParams.getAtUsers();
        params.put(FeiShuConstants.ALERT_TEMPLATE_AT_USERS, atUsers);
        return params;
    }

    /**
     * main send msg
     *
     * @param content
     * @return AlertResult
     */
    public AlertResult send(String content) {
        try {
            return checkSendMsgResult(HttpUtils.post(feiShuParams.getWebhook(), content, proxyConfig));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("send fei shu alert msg  exception : {}", e.getMessage(), e);
            AlertResult alertResult = new AlertResult();
            alertResult.setSuccess(false);
            alertResult.setMessage("send fei shu alert fail.");
            return alertResult;
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
        if (Math.abs(System.currentTimeMillis() / 1000 - timestamp) > 3600) {
            throw new IllegalArgumentException("timestamp is invalid, must be within 1 hour of current time");
        }
        String stringToSign = timestamp + FeiShuConstants.ENTER_LINE + secretKey;
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stringToSign.getBytes(FeiShuConstants.CHARSET), "HmacSHA256"));
            byte[] signData = mac.doFinal(new byte[] {});
            sign = new String(Base64.encodeBase64(signData));
        } catch (Exception e) {
            logger.error("generate sign error, message:{}", e.getMessage());
        }
        return sign;
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
            logger.error("send fei shu msg error,fei shu server resp is null");
            return alertResult;
        }
        FeiShuSendMsgResponse sendMsgResponse = JsonUtils.parseObject(result, FeiShuSendMsgResponse.class);

        if (null == sendMsgResponse) {
            alertResult.setMessage("send fei shu msg fail");
            logger.error("send fei shu msg error,resp error");
            return alertResult;
        }
        if (sendMsgResponse.code != 0) {
            alertResult.setMessage("send fei shu msg fail" + sendMsgResponse.getStatusMessage());
            logger.error("send fei shu msg error,resp error : {}  ", sendMsgResponse.getStatusMessage());
            return alertResult;
        }
        alertResult.setSuccess(true);
        alertResult.setMessage("send fei shu msg success" + sendMsgResponse.getStatusMessage());
        return alertResult;
    }

    static final class FeiShuSendMsgResponse {

        @JsonProperty("Extra")
        private String extra;

        @JsonProperty("code")
        private Integer code;

        @JsonProperty("msg")
        private String msg;

        public FeiShuSendMsgResponse() {}

        public String getExtra() {
            return this.extra;
        }

        @JsonProperty("Extra")
        public void setExtra(String extra) {
            this.extra = extra;
        }

        public Integer getStatusCode() {
            return this.code;
        }

        @JsonProperty("code")
        public void setStatusCode(Integer code) {
            this.code = code;
        }

        public String getStatusMessage() {
            return this.msg;
        }

        @JsonProperty("msg")
        public void setStatusMessage(String statusMessage) {
            this.msg = statusMessage;
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
            if (!Objects.equals(this$extra, other$extra)) {
                return false;
            }
            final Object this$statusCode = this.getStatusCode();
            final Object other$statusCode = other.getStatusCode();
            if (!Objects.equals(this$statusCode, other$statusCode)) {
                return false;
            }
            final Object this$statusMessage = this.getStatusMessage();
            final Object other$statusMessage = other.getStatusMessage();
            return Objects.equals(this$statusMessage, other$statusMessage);
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
