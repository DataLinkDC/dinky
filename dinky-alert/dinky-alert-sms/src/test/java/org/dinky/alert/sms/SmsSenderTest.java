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

import org.dinky.alert.AlertBaseConstant;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.dromara.sms4j.comm.constant.SupplierConstant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** SmsSenderTest */
@Ignore
public class SmsSenderTest {

    private static final Map<String, Object> SMS_CONFIG = new HashMap<>();

    /** init WeChatConfig */
    @Before
    public void initSmsConfig() {
        // test alibaba sms
        SMS_CONFIG.put(SmsConstants.SUPPLIERS, SupplierConstant.ALIBABA);
        SMS_CONFIG.put(SmsConstants.ACCESS_KEY_ID, "xxxxxxxxxxxxx");
        SMS_CONFIG.put(SmsConstants.REGION_ID, "cn-shanghai");
        SMS_CONFIG.put(SmsConstants.ACCESS_KEY_SECRET, "xxxxxxxxxxx");
        SMS_CONFIG.put(SmsConstants.SIGNATURE, "xxxxxxxxx");
        SMS_CONFIG.put(SmsConstants.TEMPLATE_ID, "xxxxxx");
        SMS_CONFIG.put(SmsConstants.TEMPLATE_NAME, "content");
        SMS_CONFIG.put(SmsConstants.PHONE_NUMBERS, Arrays.asList("xxxxxx"));
    }

    @Ignore
    @Test
    public void testSendMsg() {

        SmsAlert weChatAlert = new SmsAlert();
        AlertConfig alertConfig = new AlertConfig();

        alertConfig.setType(SmsConstants.TYPE);
        alertConfig.setParam(SMS_CONFIG);
        weChatAlert.setConfig(alertConfig);

        AlertResult alertResult =
                weChatAlert.send(AlertBaseConstant.ALERT_TEMPLATE_TITLE, AlertBaseConstant.ALERT_TEMPLATE_MSG);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
