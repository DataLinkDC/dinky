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

package org.dinky.utils;

import org.dinky.data.enums.Status;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import cn.hutool.extra.spring.SpringUtil;

/** desc： 获取i18n资源文件 */
public class I18nMsgUtils {

    private static MessageSource messageSource = SpringUtil.getBean(MessageSource.class);

    public I18nMsgUtils() {}

    /**
     * According to messageKey and parameters, get the message and delegate to spring messageSource
     *
     * @param code msg key
     * @return {@link String} internationalization information
     */
    public static String getMsg(Object code) {
        return messageSource.getMessage(
                code.toString(), null, code.toString(), LocaleContextHolder.getLocale());
    }

    /**
     * According to messageKey and parameters, get the message and delegate to spring messageSource
     *
     * @param code msg key
     * @param messageArgs msg parameters
     * @return {@link String} internationalization information
     */
    public static String getMsg(Object code, Object... messageArgs) {
        Object[] objs = Arrays.stream(messageArgs).map(I18nMsgUtils::getMsg).toArray();
        return messageSource.getMessage(
                code.toString(), objs, code.toString(), LocaleContextHolder.getLocale());
    }

    /**
     * Get internationalization information according to status
     *
     * @param status {@link Status}
     * @return {@link String} internationalization information
     */
    public static String getMsg(Status status) {
        Optional<Status> statusByCode = Status.findStatusByCode(status.getCode());
        if (statusByCode.isPresent()) {
            return messageSource.getMessage(
                    "", null, status.getMsg(), LocaleContextHolder.getLocale());
        }
        return messageSource.getMessage(
                "unknown.i18n",
                null,
                "Unknown i18n information, please check. . .",
                LocaleContextHolder.getLocale());
    }
}
