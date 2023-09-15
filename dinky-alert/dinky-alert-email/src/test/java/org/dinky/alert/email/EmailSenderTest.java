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

import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EmailSenderTest {

    static EmailSender emailSender;
    private static final Map<String, String> emailConfig = new HashMap<>();

    String title = "Dinky Email Alert";

    @Before
    public void initEmailConfig() {

        emailConfig.put(EmailConstants.NAME_MAIL_PROTOCOL, "smtp");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_HOST, "alertMsg");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_PORT, "465");
        emailConfig.put(EmailConstants.NAME_MAIL_SENDER, "xxx");
        emailConfig.put(EmailConstants.NAME_MAIL_USER, "xxxx@163.com");
        emailConfig.put(EmailConstants.NAME_MAIL_PASSWD, "xxxxx");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_AUTH, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_STARTTLS_ENABLE, "false");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_SSL_ENABLE, "false");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_SSL_TRUST, "smtp.mxhichina.com");
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS, "xxxxx@126.com");
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERCCS, "user3@qq.com");
        emailConfig.put(EmailConstants.MSG_TYPE, ShowType.TEXT.getValue());
        emailSender = new EmailSender(emailConfig);
    }

    @Ignore
    @Test
    public void testTextSendMails() {
        String alertMsg = "{}";
        AlertResult alertResult = emailSender.send(title, alertMsg);
        Assert.assertEquals(true, alertResult.getSuccess()); // 格式需要调整
    }
}
