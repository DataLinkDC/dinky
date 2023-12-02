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
import org.dinky.alert.email.params.EmailParams;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;

import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPSSLProvider;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;

/**
 * EmailSender 邮件发送器
 */
public final class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    private final EmailParams emailParams;

    public EmailSender(Map<String, Object> config) {
        this.emailParams = JSONUtil.toBean(JSONUtil.toJsonStr(config), EmailParams.class);
        if (CollUtil.isEmpty(emailParams.getReceivers())) {
            throw new AlertException("receivers is empty, please check config");
        }
        String mustNotNull = " must not be null";
        requireNonNull(emailParams.getServerHost(), EmailConstants.NAME_MAIL_SMTP_HOST + mustNotNull);
        requireNonNull(emailParams.getServerPort(), EmailConstants.NAME_MAIL_SMTP_PORT + mustNotNull);
        requireNonNull(emailParams.getReceivers(), EmailConstants.NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS + mustNotNull);
        requireNonNull(emailParams.getSender(), EmailConstants.NAME_MAIL_SENDER + mustNotNull);
        requireNonNull(emailParams.getUser(), EmailConstants.NAME_MAIL_USER + mustNotNull);
        requireNonNull(emailParams.getPassword(), EmailConstants.NAME_MAIL_PASSWD + mustNotNull);
    }

    /**
     * send mail
     *
     * @param title   title
     * @param content content
     */
    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();
        alertResult.setSuccess(false);

        // if there is no receivers && no receiversCc, no need to process
        if (CollectionUtils.isEmpty(emailParams.getReceivers())
                && CollectionUtils.isEmpty(emailParams.getReceiverCcs())) {
            logger.error("no receivers && no receiversCc");
            return alertResult;
        }

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        HtmlEmail email = new HtmlEmail();
        try {
            Session session = getSession();
            email.setHostName(emailParams.getServerHost());
            email.setSmtpPort(emailParams.getServerPort());
            email.setSSLOnConnect(emailParams.isSslEnable());
            email.setSslSmtpPort(String.valueOf(emailParams.getServerPort()));
            email.setStartTLSEnabled(emailParams.isStarttlsEnable());
            email.setSSLCheckServerIdentity(emailParams.isSslEnable());
            if (emailParams.isEnableSmtpAuth()) {
                email.setAuthentication(emailParams.getUser(), emailParams.getPassword());
            }
            email.setMailSession(session);
            email.setFrom(emailParams.getUser(), emailParams.getSender());
            email.setCharset(EmailConstants.CHARSET);
            email.setSubject(title);
            email.setMsg(content);
            email.setDebug(true);

            if (CollectionUtils.isNotEmpty(emailParams.getReceivers())) {
                // receivers mail
                for (String receiver : emailParams.getReceivers()) {
                    email.addTo(receiver);
                }
            }

            if (CollectionUtils.isNotEmpty(emailParams.getReceiverCcs())) {
                // cc
                for (String receiverCc : emailParams.getReceiverCcs()) {
                    email.addCc(receiverCc);
                }
            }
            // sender mail
            String sendResult = email.send();
            logger.info("Send email info: {}", sendResult);
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
        props.put(EmailConstants.NAME_MAIL_PROTOCOL, "smtp");
        props.setProperty(EmailConstants.MAIL_SMTP_HOST, emailParams.getServerHost());
        props.setProperty(EmailConstants.MAIL_SMTP_PORT, String.valueOf(emailParams.getServerPort()));
        props.setProperty(EmailConstants.MAIL_SMTP_AUTH, String.valueOf(emailParams.isEnableSmtpAuth()));
        if (StringUtils.isNotEmpty(emailParams.getMailProtocol())) {
            props.setProperty(EmailConstants.MAIL_TRANSPORT_PROTOCOL, emailParams.getMailProtocol());
        }
        props.setProperty(EmailConstants.MAIL_SMTP_SSL_ENABLE, String.valueOf(emailParams.isSslEnable()));
        props.setProperty(EmailConstants.MAIL_SMTP_STARTTLS_ENABLE, String.valueOf(emailParams.isStarttlsEnable()));
        props.setProperty(EmailConstants.MAIL_SENDER, emailParams.getSender());
        props.setProperty(EmailConstants.MAIL_USER, emailParams.getUser());
        props.setProperty(EmailConstants.MAIL_PASSWD, emailParams.getPassword());

        if (StringUtils.isNotEmpty(emailParams.getSmtpSslTrust())) {
            props.setProperty(EmailConstants.MAIL_SMTP_SSL_TRUST, emailParams.getSmtpSslTrust());
        }

        Authenticator auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // mail username and password
                return new PasswordAuthentication(emailParams.getUser(), emailParams.getPassword());
            }
        };

        Session session = Session.getInstance(props, auth);
        session.addProvider(new SMTPSSLProvider());
        return session;
    }

    /**
     * handle exception
     */
    private void handleException(AlertResult alertResult, Exception e) {
        logger.error("Send email to {} failed", emailParams.getReceivers(), e);
        alertResult.setMessage(
                "Send email to {" + String.join(",", emailParams.getReceivers()) + "} failed，" + e.toString());
    }
}
