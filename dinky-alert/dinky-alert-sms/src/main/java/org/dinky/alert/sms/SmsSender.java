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
import org.dromara.sms4j.provider.config.BaseConfig;
import org.dromara.sms4j.provider.factory.BaseProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmsSender todo: https://wind.kim/doc/start/springboot.html
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

    public synchronized AlertResult send(String content) {
        SmsResponse smsResponse;
        if (supplierConfigLoader.isPresent() && supplierConfigLoader.get().getTemplateId() != null) {
            String templateId = supplierConfigLoader.get().getTemplateId();
            LinkedHashMap<String, String> smsParams = new LinkedHashMap<>();
            smsParams.put(SmsConstants.ALERT_TEMPLATE_CONTENT, content);

            smsResponse = smsSendFactory.massTexting(phoneNumbers, templateId, smsParams);
        } else {
            throw new RuntimeException("sms templateId is null");
        }
        return validateSendResult(smsResponse);
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
