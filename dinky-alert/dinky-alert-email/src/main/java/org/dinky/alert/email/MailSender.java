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

import static java.util.Objects.requireNonNull;

import org.dinky.alert.AlertException;
import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;
import org.dinky.alert.email.template.AlertTemplate;
import org.dinky.alert.email.template.DefaultHTMLTemplate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPProvider;

/**
 * MailSender 邮件发送器
 * @author zhumingye
 * @date: 2022/4/3
 **/
public final class MailSender {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private final List<String> receivers;
    private final List<String> receiverCcs;
    private final String mailProtocol = "SMTP";
    private final String mailSmtpHost;
    private final String mailSmtpPort;
    private final String mailSenderNickName;
    private final String enableSmtpAuth;
    private final String mailUser;
    private final String mailPasswd;
    private final String mailUseStartTLS;
    private final String mailUseSSL;
    private final String sslTrust;
    private final String showType;
    private final AlertTemplate alertTemplate;
    private final String mustNotNull = " must not be null";
    private String xlsFilePath;

    public MailSender(Map<String, String> config) {
        String receiversConfig = config.get(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS);
        if (receiversConfig == null || "".equals(receiversConfig)) {
            throw new AlertException(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS + mustNotNull);
        }

        receivers = Arrays.asList(receiversConfig.split(","));

        String receiverCcsConfig = config.get(EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERCCS);

        receiverCcs = new ArrayList<>();
        if (receiverCcsConfig != null && !"".equals(receiverCcsConfig)) {
            receiverCcs.addAll(Arrays.asList(receiverCcsConfig.split(",")));
        }

        mailSmtpHost = config.get(EmailConstants.NAME_MAIL_SMTP_HOST);
        requireNonNull(mailSmtpHost, EmailConstants.NAME_MAIL_SMTP_HOST + mustNotNull);

        mailSmtpPort = config.get(EmailConstants.NAME_MAIL_SMTP_PORT);
        requireNonNull(mailSmtpPort, EmailConstants.NAME_MAIL_SMTP_PORT + mustNotNull);

        mailSenderNickName = config.get(EmailConstants.NAME_MAIL_SENDER);
        requireNonNull(mailSenderNickName, EmailConstants.NAME_MAIL_SENDER + mustNotNull);

        enableSmtpAuth = config.get(EmailConstants.NAME_MAIL_SMTP_AUTH);

        mailUser = config.get(EmailConstants.NAME_MAIL_USER);
        requireNonNull(mailUser, EmailConstants.NAME_MAIL_USER + mustNotNull);

        mailPasswd = config.get(EmailConstants.NAME_MAIL_PASSWD);
        requireNonNull(mailPasswd, EmailConstants.NAME_MAIL_PASSWD + mustNotNull);

        mailUseStartTLS = config.get(EmailConstants.NAME_MAIL_SMTP_STARTTLS_ENABLE);

        mailUseSSL = config.get(EmailConstants.NAME_MAIL_SMTP_SSL_ENABLE);

        sslTrust = config.get(EmailConstants.NAME_MAIL_SMTP_SSL_TRUST);

        showType = config.get(EmailConstants.NAME_SHOW_TYPE);
        requireNonNull(showType, EmailConstants.NAME_SHOW_TYPE + mustNotNull);

        xlsFilePath = config.get(EmailConstants.XLS_FILE_PATH);
        if (StringUtils.isBlank(xlsFilePath)) {
            xlsFilePath = "/tmp/xls";
        }

        alertTemplate = new DefaultHTMLTemplate();
    }

    /**
     * send mail to receivers
     * @param title title
     * @param content content
     */
    public AlertResult send(String title, String content) {
        return send(this.receivers, this.receiverCcs, title, content);
    }

    /**
     * send mail
     *
     * @param receivers receivers
     * @param receiverCcs receiverCcs
     * @param title title
     * @param content content
     */
    public AlertResult send(List<String> receivers, List<String> receiverCcs, String title, String content) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        // if there is no receivers && no receiversCc, no need to process
        if (CollectionUtils.isEmpty(receivers) && CollectionUtils.isEmpty(receiverCcs)) {
            return alertResult;
        }

        receivers.removeIf(StringUtils::isEmpty);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        if (showType.equals(ShowType.TABLE.getValue()) || showType.equals(ShowType.TEXT.getValue())) {
            // send email
            HtmlEmail email = new HtmlEmail();

            try {
                Session session = getSession();
                email.setMailSession(session);
                email.setFrom(mailUser, mailSenderNickName);
                email.setCharset(EmailConstants.UTF_8);
                if (CollectionUtils.isNotEmpty(receivers)) {
                    // receivers mail
                    for (String receiver : receivers) {
                        email.addTo(receiver);
                    }
                }

                if (CollectionUtils.isNotEmpty(receiverCcs)) {
                    //cc
                    for (String receiverCc : receiverCcs) {
                        email.addCc(receiverCc);
                    }
                }
                // sender mail
                return getStringObjectMap(title, content, alertResult, email);
            } catch (Exception e) {
                handleException(alertResult, e);
            }
        } else if (showType.equals(ShowType.ATTACHMENT.getValue()) || showType.equals(ShowType.TABLE_ATTACHMENT.getValue())) {
            try {

                String partContent = (showType.equals(ShowType.ATTACHMENT.getValue())
                    ? "Please see the attachment " + title + EmailConstants.EXCEL_SUFFIX_XLSX
                    : htmlTable(title, content, false));

                attachment(title, content, partContent);

                alertResult.setSuccess(true);
                return alertResult;
            } catch (Exception e) {
                handleException(alertResult, e);
                return alertResult;
            }
        }
        return alertResult;

    }

    /**
     * html table content
     *
     * @param content the content
     * @param showAll if show the whole content
     * @return the html table form
     */
    private String htmlTable(String title, String content, boolean showAll) {
        return alertTemplate.getMessageFromTemplate(title, content, ShowType.TABLE, showAll);
    }

    /**
     * html table content
     *
     * @param content the content
     * @return the html table form
     */
    private String htmlTable(String title, String content) {
        return htmlTable(title,content, true);
    }

    /**
     * html text content
     *
     * @param content the content
     * @return text in html form
     */
    private String htmlText(String title, String content) {
        return alertTemplate.getMessageFromTemplate(title, content, ShowType.TEXT);
    }

    /**
     * send mail as Excel attachment
     */
    private void attachment(String title, String content, String partContent) throws Exception {
        MimeMessage msg = getMimeMessage();

        attachContent(title, content, partContent, msg);
    }

    /**
     * get MimeMessage
     */
    private MimeMessage getMimeMessage() throws MessagingException {

        // 1. The first step in creating mail: creating session
        Session session = getSession();
        // Setting debug mode, can be turned off
        session.setDebug(false);

        // 2. creating mail: Creating a MimeMessage
        MimeMessage msg = new MimeMessage(session);
        // 3. set sender
        msg.setFrom(new InternetAddress(mailUser));
        // 4. set receivers
        for (String receiver : receivers) {
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        }
        return msg;
    }

    /**
     * get session
     *
     * @return the new Session
     */
    private Session getSession() {
        // support multilple email format
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        Properties props = new Properties();
        props.setProperty(EmailConstants.MAIL_SMTP_HOST, mailSmtpHost);
        props.setProperty(EmailConstants.MAIL_SMTP_PORT, mailSmtpPort);

        if (StringUtils.isNotEmpty(enableSmtpAuth)) {
            props.setProperty(EmailConstants.MAIL_SMTP_AUTH, enableSmtpAuth);
        }
        if (StringUtils.isNotEmpty(mailProtocol)) {
            props.setProperty(EmailConstants.MAIL_TRANSPORT_PROTOCOL, mailProtocol);
        }

        if (StringUtils.isNotEmpty(mailUseSSL)) {
            props.setProperty(EmailConstants.MAIL_SMTP_SSL_ENABLE, mailUseSSL);
        }

        if (StringUtils.isNotEmpty(mailUseStartTLS)) {
            props.setProperty(EmailConstants.MAIL_SMTP_STARTTLS_ENABLE, mailUseStartTLS);
        }

        if (StringUtils.isNotEmpty(sslTrust)) {
            props.setProperty(EmailConstants.MAIL_SMTP_SSL_TRUST, sslTrust);
        }

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // mail username and password
                return new PasswordAuthentication(mailUser, mailPasswd);
            }
        };

        Session session = Session.getInstance(props, auth);
        session.addProvider(new SMTPProvider());
        return session;
    }

    /**
     * attach content
     */
    private void attachContent(String title, String content, String partContent, MimeMessage msg) throws MessagingException, IOException {
        /*
         * set receiverCc
         */
        if (CollectionUtils.isNotEmpty(receiverCcs)) {
            for (String receiverCc : receiverCcs) {
                msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(receiverCc));
            }
        }

        // set subject
        msg.setSubject(title);
        MimeMultipart partList = new MimeMultipart();
        // set signature
        MimeBodyPart part1 = new MimeBodyPart();
        part1.setContent(partContent, EmailConstants.TEXT_HTML_CHARSET_UTF_8);
        // set attach file
        MimeBodyPart part2 = new MimeBodyPart();
        File file = new File(xlsFilePath + EmailConstants.SINGLE_SLASH + title + EmailConstants.EXCEL_SUFFIX_XLSX);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // make excel file

        ExcelUtils.genExcelFile(content, title, xlsFilePath);

        part2.attachFile(file);
        part2.setFileName(MimeUtility.encodeText(title + EmailConstants.EXCEL_SUFFIX_XLSX, EmailConstants.UTF_8, "B"));
        // add components to collection
        partList.addBodyPart(part1);
        partList.addBodyPart(part2);
        msg.setContent(partList);
        // 5. send Transport
        Transport.send(msg);
        // 6. delete saved file
        deleteFile(file);
    }

    /**
     * the string object map
     */
    private AlertResult getStringObjectMap(String title, String content, AlertResult alertResult, HtmlEmail email) throws EmailException {

        /*
         * the subject of the message to be sent
         */
        email.setSubject(title);
        /*
         * to send information, you can use HTML tags in mail content because of the use of HtmlEmail
         */
        if (showType.equals(ShowType.TABLE.getValue())) {
            email.setMsg(htmlTable(title, content));
        } else if (showType.equals(ShowType.TEXT.getValue())) {
            email.setMsg(htmlText(title, content));
        }

        // send
        email.setDebug(true);
        email.send();

        alertResult.setSuccess(true);

        return alertResult;
    }

    /**
     * file delete
     *
     * @param file the file to delete
     */
    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.delete()) {
                logger.info("delete success: {}", file.getAbsolutePath());
            } else {
                logger.info("delete fail: {}", file.getAbsolutePath());
            }
        } else {
            logger.info("file not exists: {}", file.getAbsolutePath());
        }
    }

    /**
     * handle exception
     */
    private void handleException(AlertResult alertResult, Exception e) {
        logger.error("Send email to {} failed", receivers, e);
        alertResult.setMessage("Send email to {" + String.join(",", receivers) + "} failed，" + e.toString());
    }

}
