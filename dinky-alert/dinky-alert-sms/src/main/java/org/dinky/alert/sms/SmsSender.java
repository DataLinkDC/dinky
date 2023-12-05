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

package org.dinky.alert.sms;

import org.dinky.alert.AlertResult;
import org.dinky.alert.sms.config.SmsConfigLoader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.comm.constant.SupplierConstant;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.dromara.sms4j.provider.factory.BaseProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmsSender
 * <p>
 *    SMS sending service, use SMS4J <<a href="https://sms4j.com/doc3/supplierConfig.html">sms4j</a>> implement, currently only implements Aliyun and Tencent Cloud SMS alert
 */
public class SmsSender {
    private static final Logger logger = LoggerFactory.getLogger(SmsSender.class);
    private final Optional<BaseConfig> supplierConfigLoader;
    private final BaseProviderFactory baseProviderFactory;
    private final SmsBlend smsSendFactory;
    private final List<String> phoneNumbers;

    SmsSender(Map<String, Object> config) {
        this.supplierConfigLoader = Optional.of(SmsConfigLoader.getConfigSupplierConfig(config));
        this.baseProviderFactory = SmsConfigLoader.getBaseProviderFactory(
                supplierConfigLoader.get().getSupplier());
        this.smsSendFactory = baseProviderFactory.createSms(supplierConfigLoader.get());
        this.phoneNumbers = SmsConfigLoader.getPhoneNumberList(config);
    }

    /**
     * Sends the specified content via SMS and returns the result.
     *
     * @param  content    the content to be sent
     * @return            the result of the SMS send operation
     */
    public synchronized AlertResult send(String content) {
        SmsResponse smsResponse;
        if (supplierConfigLoader.isPresent() && supplierConfigLoader.get().getTemplateId() != null) {
            String templateId = supplierConfigLoader.get().getTemplateId();
            smsResponse = smsSendFactory.massTexting(phoneNumbers, templateId, buildPlatFormVariableOfContent(content));
        } else {
            throw new RuntimeException("sms templateId is null");
        }
        return validateSendResult(smsResponse);
    }

    /**
     * Builds a platform variable of the content for an SMS alert template.
     * <p>
     *     zh-CN:因为不同的厂商有不同的模板变量，所以这里需要根据不同的厂商来构建不同的模板变量
     *     en-US:Because different manufacturers have different template variables, so here you need to build different template variables according to different manufacturers
     *
     * @param  content  the content of the alert template
     * @return          a LinkedHashMap containing the platform variable for the content
     */
    private LinkedHashMap<String, String> buildPlatFormVariableOfContent(String content) {
        LinkedHashMap<String, String> smsParams = new LinkedHashMap<>();
        String templateVariableKey = "";
        switch (baseProviderFactory.getSupplier()) {
            case SupplierConstant.ALIBABA:
                templateVariableKey = SmsConstants.ALIYUNM_SMS_TEMPLATE_VARIABLES;
                break;
            case SupplierConstant.TENCENT:
                templateVariableKey = SmsConstants.TENCENT_SMS_TEMPLATE_VARIABLES_1;
                break;
        }

        smsParams.put(templateVariableKey, content);
        return smsParams;
    }

    private AlertResult validateSendResult(SmsResponse smsResponse) {
        AlertResult alertResult = new AlertResult();
        if (smsResponse.isSuccess()) {
            logger.info(
                    "sms send success, phoneNumbers: {}, message: {}",
                    phoneNumbers,
                    smsResponse.getData().toString());
            alertResult.setSuccess(true);
            alertResult.setMessage("sms send success");
        } else {
            String errorMsg = String.format(
                    "sms send fail, reason: %s", smsResponse.getData().toString());
            alertResult.setSuccess(false);
            alertResult.setMessage(errorMsg);
            logger.error(errorMsg);
        }
        return alertResult;
    }
}
