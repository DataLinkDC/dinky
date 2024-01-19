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

package org.dinky.alert.email;

import org.dinky.alert.AlertBaseConstant;
import org.dinky.alert.AlertResult;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EmailSenderTest {

    static EmailSender emailSender;
    private static final Map<String, Object> emailConfig = new HashMap<>();

    @Before
    public void initEmailConfig() {

        //        emailConfig.put(EmailConstants.NAME_MAIL_PROTOCOL, "smtp");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_HOST, "smtp.163.com");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_PORT, "465");
        emailConfig.put(EmailConstants.NAME_MAIL_SENDER, "zzz");
        emailConfig.put(EmailConstants.NAME_MAIL_USER, "11111111@163.com");
        emailConfig.put(EmailConstants.NAME_MAIL_PASSWD, "1111111111");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_AUTH, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_STARTTLS_ENABLE, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_SSL_ENABLE, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_SSL_TRUST, "*");
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS, Arrays.asList("111111111@qq.com"));
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERCCS, Arrays.asList("111111111111@163.com"));
        //        emailConfig.put(EmailConstants.MSG_TYPE, ShowType.TEXT.getValue());
        emailSender = new EmailSender(emailConfig);
    }

    @Ignore
    @Test
    public void testTextSendMails() throws GeneralSecurityException {
        AlertResult alertResult =
                emailSender.send(AlertBaseConstant.ALERT_TEMPLATE_TITLE, AlertBaseConstant.ALERT_TEMPLATE_MSG);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
