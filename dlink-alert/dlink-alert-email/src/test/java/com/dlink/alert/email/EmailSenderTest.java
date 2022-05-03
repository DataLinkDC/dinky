package com.dlink.alert.email;

import com.dlink.alert.AlertResult;
import com.dlink.alert.ShowType;
import com.dlink.alert.email.template.AlertTemplate;
import com.dlink.alert.email.template.DefaultHTMLTemplate;
import com.dlink.utils.JSONUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EmailSenderTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderTest.class);
    static MailSender mailSender;
    private static Map<String, String> emailConfig = new HashMap<>();
    private static AlertTemplate alertTemplate;

    String title = "Dinky Email Alert";
    String content = "[{\"id\":\"69\","
            + "\"name\":\"UserBehavior-0--1193959466\","
            + "\"Job name\": \"Start workflow\","
            + "\"State\": \"SUCCESS\","
            + "\"Recovery\":\"NO\","
            + "\"Run time\": \"1\","
            + "\"Start time\": \"2018-08-06 10:31:34.0\","
            + "\"End time\": \"2018-08-06 10:31:49.0\","
            + "\"Host\": \"192.168.xx.xx\","
            + "\"Notify group\" :\"4\"}]";

    @BeforeClass
    public static void initEmailConfig() {
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
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS, "user1@qq.com,user2@163.com");
        emailConfig.put(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERCCS, "user3@qq.com");
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.TEXT.getValue());
        alertTemplate = new DefaultHTMLTemplate();
        mailSender = new MailSender(emailConfig);
    }

    @Test
    public void testTextSendMails() {
        AlertResult alertResult = mailSender.send(title, content);
        Assert.assertEquals(true, alertResult.getSuccess()); // 格式需要调整
    }


    @Test
    public void testSendTableMail() {
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.TABLE.getValue());
        mailSender = new MailSender(emailConfig);
        AlertResult alertResult = mailSender.send(title, content);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testAttachmentFile() {
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.ATTACHMENT.getValue());
        mailSender = new MailSender(emailConfig);
        AlertResult alertResult = mailSender.send(title, content);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testTableAttachmentFile() {
        emailConfig.put(EmailConstants.NAME_SHOW_TYPE, ShowType.TABLE_ATTACHMENT.getValue());
        mailSender = new MailSender(emailConfig);
        AlertResult alertResult = mailSender.send(title, content);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testGenTextEmail() {
        List<LinkedHashMap> linkedHashMaps = JSONUtil.toList(content, LinkedHashMap.class);
        if (linkedHashMaps.size() > EmailConstants.NUMBER_1000) {
            linkedHashMaps = linkedHashMaps.subList(0, EmailConstants.NUMBER_1000);
        }
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append(EmailConstants.TR).append(EmailConstants.TH_COLSPAN).append(title).append(EmailConstants.TH_END).append(EmailConstants.TR_END);
        for (LinkedHashMap<String, Object> mapItems : linkedHashMaps) {
            Set<Map.Entry<String, Object>> entries = mapItems.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                stringBuilder.append(EmailConstants.TR);
                stringBuilder.append(EmailConstants.TD).append(entry.getKey()).append(EmailConstants.TD_END);
                stringBuilder.append(EmailConstants.TD).append(entry.getValue()).append(EmailConstants.TD_END);
                stringBuilder.append(EmailConstants.TR_END);
            }

            System.out.println(stringBuilder.toString());

        }
    }
}