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

import java.security.GeneralSecurityException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.util.MailSSLSocketFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
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
     * Sends an email with the given title and content.
     *
     * @param  title   the title of the email
     * @param  content the content of the email
     * @return         an AlertResult object representing the result of the email sending
     */
    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();
        // if there is no receivers && no receiversCc, no need to process
        if (CollectionUtils.isEmpty(emailParams.getReceivers())) {
            logger.error("no receivers , you must set receivers");
            return alertResult;
        }

        try {
            String sendResult = MailUtil.send(
                    getMailAccount(),
                    emailParams.getReceivers(),
                    emailParams.getReceiverCcs(),
                    null,
                    title,
                    content,
                    true);
            if (StringUtils.isNotBlank(sendResult)) {
                logger.info("Send email info: {}", sendResult);
                alertResult.setSuccess(true);
                alertResult.setMessage(sendResult);
                return alertResult;
            }
        } catch (GeneralSecurityException e) {
            handleException(alertResult, e);
        }

        return alertResult;
    }

    /**
     * Retrieves the MailAccount object for sending emails.
     *
     * @return          the MailAccount object containing the email server configuration
     * @throws GeneralSecurityException    if there is a security exception during the process
     */
    private MailAccount getMailAccount() throws GeneralSecurityException {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(emailParams.getServerHost());
        mailAccount.setPort(emailParams.getServerPort());
        String userNameFrom = emailParams.getUser();
        if (emailParams.getUser() != null && emailParams.getSender() != null) {
            userNameFrom = emailParams.getSender() + "<" + emailParams.getUser() + ">";
        }
        mailAccount.setFrom(userNameFrom);
        if (emailParams.isEnableSmtpAuth()) {
            mailAccount.setAuth(emailParams.isEnableSmtpAuth());
            mailAccount.setUser(emailParams.getUser());
            mailAccount.setPass(emailParams.getPassword());
        }
        mailAccount.setStarttlsEnable(emailParams.isStarttlsEnable());
        mailAccount.setSslEnable(emailParams.isSslEnable());
        mailAccount.setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustedHosts(emailParams.getSmtpSslTrust().split(","));
        mailAccount.setCustomProperty("mail.smtp.ssl.socketFactory", sf);
        return mailAccount;
    }

    /**
     * handle exception
     */
    private void handleException(AlertResult alertResult, Exception e) {
        logger.error("Send email to {} failed", emailParams.getReceivers(), e);
        alertResult.setSuccess(false);
        alertResult.setMessage(
                "Send email to {" + String.join(",", emailParams.getReceivers()) + "} failed，" + e.toString());
    }
}
