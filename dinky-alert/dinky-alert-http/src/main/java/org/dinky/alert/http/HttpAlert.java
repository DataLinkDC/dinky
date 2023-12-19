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

package org.dinky.alert.http;

import java.util.Map;
import org.dinky.alert.AbstractAlert;
import org.dinky.alert.AlertResult;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;

/**
 * DingTalkAlert
 *
 * @since 2022/2/23 19:28
 */
public class HttpAlert extends AbstractAlert {
    private static final Logger log = LoggerFactory.getLogger(HttpAlert.class);

    @Override
    public String getType() {
        return HttpConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        HttpSender sender = new HttpSender(getConfig().getParam());
        Map<String, Object> templateParams = sender.buildTemplateParams(title, content);
        return sender.send(templateParams);
    }
}
