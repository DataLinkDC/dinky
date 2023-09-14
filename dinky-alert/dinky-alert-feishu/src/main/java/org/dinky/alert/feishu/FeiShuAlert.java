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

package org.dinky.alert.feishu;

import org.dinky.alert.AbstractAlert;
import org.dinky.alert.AlertResult;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;

/** FeiShuAlert */
public class FeiShuAlert extends AbstractAlert {

    private static final Logger logger = LoggerFactory.getLogger(FeiShuAlert.class);

    @Override
    public String getType() {
        return FeiShuConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        FeiShuSender sender = new FeiShuSender(getConfig().getParam());
        try {
            String built = buildContent(sender.buildTemplateParams(title, content));
            logger.info("Send FeiShu alert title: {}", title);
            return sender.send(built);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
