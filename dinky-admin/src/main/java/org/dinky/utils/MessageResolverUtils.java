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

package com.dlink.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import cn.hutool.extra.spring.SpringUtil;

/**
 * desc： 获取i18n资源文件
 */
public class MessageResolverUtils {

    @Autowired
    private static MessageSource messageSource = SpringUtil.getBean(MessageSource.class);

    public MessageResolverUtils() {
    }

    /**
     * 根据 messageKey 获取国际化消息 委托给 spring messageSource
     *
     * @param code 消息key
     * @return 解析后的国际化
     */
    public static String getMessage(Object code) {
        return messageSource.getMessage(code.toString(), null, code.toString(), LocaleContextHolder.getLocale());
    }

    /**
     * 根据 messageKey 和参数 获取消息 委托给 spring messageSource
     *
     * @param code        消息key
     * @param messageArgs 参数
     * @return 解析后的国际化
     */
    public static String getMessages(Object code, Object... messageArgs) {
        Object[] objs = Arrays.stream(messageArgs).map(MessageResolverUtils::getMessage).toArray();
        String message =
                messageSource.getMessage(code.toString(), objs, code.toString(), LocaleContextHolder.getLocale());
        return message;
    }
}
