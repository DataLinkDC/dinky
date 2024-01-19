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
import org.dinky.alert.dingtalk.params.DingTalkParams;
import org.dinky.assertion.Asserts;
import org.dinky.data.model.ProxyConfig;
import org.dinky.utils.HttpUtils;
import org.dinky.utils.JsonUtils;

import org.apache.commons.codec.binary.Base64;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.json.JSONUtil;

/**
 * DingTalkSender
 */
public class DingTalkSender {
    private static final Logger logger = LoggerFactory.getLogger(DingTalkSender.class);

    private final DingTalkParams dingTalkParams;
    private ProxyConfig proxyConfig = null;

    DingTalkSender(Map<String, Object> config) {
        this.dingTalkParams = JSONUtil.toBean(JSONUtil.toJsonStr(config), DingTalkParams.class);
        Asserts.checkNotNull(dingTalkParams, "dingTalkParams is null");
        if (Boolean.TRUE.equals(dingTalkParams.isEnableProxy())) {
            proxyConfig = new ProxyConfig(
                    dingTalkParams.getHostname(),
                    dingTalkParams.getPort(),
                    dingTalkParams.getUser(),
                    dingTalkParams.getPassword());
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
        params.put(DingTalkConstants.ALERT_TEMPLATE_TITLE, title);
        params.put(DingTalkConstants.ALERT_TEMPLATE_CONTENT, content);
        params.put(DingTalkConstants.ALERT_TEMPLATE_KEYWORD, dingTalkParams.getKeyword());
        params.put(DingTalkConstants.ALERT_TEMPLATE_AT_MOBILES, dingTalkParams.getAtMobiles());
        params.put(DingTalkConstants.ALERT_TEMPLATE_AT_ALL, dingTalkParams.isAtAll());
        return params;
    }

    /**
     * send msg of main
     *
     * @param content： send msg content
     * @return AlertResult
     */
    public AlertResult send(String content) {
        AlertResult alertResult;
        try {
            String httpUrl = Asserts.isNotNullString(dingTalkParams.getSecret())
                    ? generateSignedUrl()
                    : dingTalkParams.getWebhook();
            return checkMsgResult(HttpUtils.post(httpUrl, content, proxyConfig));
        } catch (Exception e) {
            logger.error("send ding talk alert msg  exception : {}", e.getMessage());
            alertResult = new AlertResult();
            alertResult.setSuccess(false);
            alertResult.setMessage("send ding talk alert fail.");
        }
        return alertResult;
    }

    /**
     * generate Signed Url of SHA256
     *
     * @return String
     */
    private String generateSignedUrl() {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + DingTalkConstants.ENTER_LINE + dingTalkParams.getSecret();
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(dingTalkParams.getSecret().getBytes(DingTalkConstants.CHARSET), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(DingTalkConstants.CHARSET));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), DingTalkConstants.CHARSET);
        } catch (Exception e) {
            logger.error("generate sign error, message:{}", e.getMessage());
        }
        return dingTalkParams.getWebhook() + "&timestamp=" + timestamp + "&sign=" + sign;
    }

    /**
     * Check Msg Result
     *
     * @param result
     * @return
     */
    private AlertResult checkMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        if (null == result) {
            alertResult.setMessage("send ding talk msg error");
            logger.info("send ding talk msg error,ding talk server resp is null");
            return alertResult;
        }
        AlertSendResponse response = JsonUtils.parseObject(result, AlertSendResponse.class);
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
