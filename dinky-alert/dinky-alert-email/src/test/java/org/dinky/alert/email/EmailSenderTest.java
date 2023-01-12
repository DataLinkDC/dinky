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

import org.dinky.alert.AlertMsg;
import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;
import org.dinky.alert.email.template.AlertTemplate;
import org.dinky.alert.email.template.DefaultHTMLTemplate;
import org.dinky.utils.JSONUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSenderTest {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderTest.class);
    static MailSender mailSender;
    private static Map<String, String> emailConfig = new HashMap<>();
    private static AlertTemplate alertTemplate;

    String title = "Dinky Email Alert";
    private AlertMsg alertMsg = new AlertMsg();

    @Before
    public void initEmailConfig() {

        String uuid = UUID.randomUUID().toString();

        alertMsg.setAlertType("实时告警监控");
        alertMsg.setAlertTime("2018-08-06 10:31:34.0");
        alertMsg.setJobID(uuid);
        alertMsg.setJobName("测试任务");
        alertMsg.setJobType("SQL");
        alertMsg.setJobStatus("FAILED");
        alertMsg.setJobStartTime("2018-08-06 10:31:34.0");
        alertMsg.setJobEndTime("2018-08-06 10:31:49.0");
        alertMsg.setJobDuration("23 Seconds");
        String linkUrl = "[跳转至该任务的FlinkWeb](http://cdh1:8081/#/job/" + uuid + "/overview)";
        alertMsg.setLinkUrl(linkUrl);
        String exceptionUrl = "[点击查看该任务的异常日志](http://cdh1:8081/#/job/" + uuid + "/exceptions)";
        alertMsg.setExceptionUrl(exceptionUrl);

        emailConfig.put(EmailConstants.NAME_MAIL_PROTOCOL, "smtp");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_HOST, "smtp.mxhichina.com");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_PORT, "465");
        emailConfig.put(EmailConstants.NAME_MAIL_SENDER, "aliyun.sdaDXZxDFSDFasa.cn");
        emailConfig.put(EmailConstants.NAME_MAIL_USER, "aliyun.sdaDXZxDFSDFasay.cn");
        emailConfig.put(EmailConstants.NAME_MAIL_PASSWD, "5vffgdsf123132q8");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_AUTH, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_STARTTLS_ENABLE, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_SSL_ENABLE, "true");
        emailConfig.put(EmailConstants.NAME_MAIL_SMTP_SSL_TRUST, "smtp.mxhichina.com");
        emailConfig.put(
                EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS, "user1@qq.com,user2@163.com");
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERCCS, "user3@qq.com");
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.TEXT.getValue());
        alertTemplate = new DefaultHTMLTemplate();
        mailSender = new MailSender(emailConfig);
    }

    @Test
    public void testTextSendMails() {
        AlertResult alertResult = mailSender.send(title, alertMsg.toString());
        Assert.assertEquals(true, alertResult.getSuccess()); // 格式需要调整
    }

    @Test
    public void testSendTableMail() {
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.TABLE.getValue());
        mailSender = new MailSender(emailConfig);
        AlertResult alertResult = mailSender.send(title, alertMsg.toString());
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testAttachmentFile() {
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.ATTACHMENT.getValue());
        mailSender = new MailSender(emailConfig);
        AlertResult alertResult = mailSender.send(title, alertMsg.toString());
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testTableAttachmentFile() {
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.TABLE_ATTACHMENT.getValue());
        mailSender = new MailSender(emailConfig);
        AlertResult alertResult = mailSender.send(title, alertMsg.toString());
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testGenTextEmail() {
        List<LinkedHashMap> linkedHashMaps =
                JSONUtil.toList(alertMsg.toString(), LinkedHashMap.class);
        if (linkedHashMaps.size() > EmailConstants.NUMBER_1000) {
            linkedHashMaps = linkedHashMaps.subList(0, EmailConstants.NUMBER_1000);
        }
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder
                .append(EmailConstants.TR)
                .append(EmailConstants.TH_COLSPAN)
                .append(title)
                .append(EmailConstants.TH_END)
                .append(EmailConstants.TR_END);
        for (LinkedHashMap<String, Object> mapItems : linkedHashMaps) {
            Set<Map.Entry<String, Object>> entries = mapItems.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                stringBuilder.append(EmailConstants.TR);
                stringBuilder
                        .append(EmailConstants.TD)
                        .append(entry.getKey())
                        .append(EmailConstants.TD_END);
                stringBuilder
                        .append(EmailConstants.TD)
                        .append(entry.getValue())
                        .append(EmailConstants.TD_END);
                stringBuilder.append(EmailConstants.TR_END);
            }
        }
    }
}
