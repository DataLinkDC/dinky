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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPProvider;

/** EmailSender 邮件发送器 */
public final class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

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
    //    private final String showType;
    private final String mustNotNull = " must not be null";
    private String xlsFilePath;

    public EmailSender(Map<String, String> config) {
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

        xlsFilePath = config.get(EmailConstants.XLS_FILE_PATH);
        if (StringUtils.isBlank(xlsFilePath)) {
            xlsFilePath = EmailConstants.XLS_FILE_DEFAULT_PATH;
        }
    }

    /**
     * send mail
     *
     * @param title title
     * @param content content
     */
    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        // if there is no receivers && no receiversCc, no need to process
        if (CollectionUtils.isEmpty(receivers) && CollectionUtils.isEmpty(receiverCcs)) {
            logger.error("no receivers && no receiversCc");
            return alertResult;
        }

        receivers.removeIf(StringUtils::isEmpty);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        HtmlEmail email = new HtmlEmail();
        try {
            Session session = getSession();
            email.setMailSession(session);
            email.setFrom(mailUser, mailSenderNickName);
            email.setCharset(EmailConstants.CHARSET);
            email.setSubject(title);
            email.setMsg(content);
            email.setDebug(true);

            if (CollectionUtils.isNotEmpty(receivers)) {
                // receivers mail
                for (String receiver : receivers) {
                    email.addTo(receiver);
                }
            }

            if (CollectionUtils.isNotEmpty(receiverCcs)) {
                // cc
                for (String receiverCc : receiverCcs) {
                    email.addCc(receiverCc);
                }
            }
            // sender mail
            email.send();
            alertResult.setSuccess(true);
            return alertResult;
        } catch (Exception e) {
            handleException(alertResult, e);
        }
        return alertResult;
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

    /** handle exception */
    private void handleException(AlertResult alertResult, Exception e) {
        logger.error("Send email to {} failed", receivers, e);
        alertResult.setMessage("Send email to {" + String.join(",", receivers) + "} failed，" + e.toString());
    }
}
