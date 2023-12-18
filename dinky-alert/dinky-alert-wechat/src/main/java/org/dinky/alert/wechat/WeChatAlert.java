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

package org.dinky.alert.wechat;

import org.dinky.alert.AbstractAlert;
import org.dinky.alert.AlertResult;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.StrFormatter;
import freemarker.template.TemplateException;

/**
 * WeChatAlert
 *
 * @since 2022/2/23 21:09
 */
public class WeChatAlert extends AbstractAlert {
    private static final Logger logger = LoggerFactory.getLogger(WeChatAlert.class);

    @Override
    public String getType() {
        return WeChatConstants.TYPE;
    }

    @Override
    public String getTemplate() {
        String sendType = (String) getConfig().getParam().get(WeChatConstants.SEND_TYPE);
        return ResourceUtil.readUtf8Str(
                StrFormatter.format("{}-{}.ftl", getConfig().getType(), sendType));
    }

    @Override
    public AlertResult send(String title, String content) {
        WeChatSender sender = new WeChatSender(getConfig().getParam());
        try {
            String built = buildContent(sender.buildTemplateParams(title, replaceContent(content)));
            return sender.send(built);
        } catch (TemplateException | IOException e) {
            logger.error("{}'message send error, Reason:{}", getType(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * remove - from  string
     *
     * @param  content      input string with line breaks (new lines)
     * @return string with lines removed
     */
    private String replaceContent(String content) {
        return content.replaceAll("\\n-\\s", "\n");
    }
}
