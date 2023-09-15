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

import org.dinky.alert.AbstractAlert;
import org.dinky.alert.AlertResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;

/** EmailAlert */
public class EmailAlert extends AbstractAlert {
    private static final Logger log = LoggerFactory.getLogger(EmailAlert.class);

    @Override
    public String getType() {
        return EmailConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {

        Map<String, Object> params = new HashMap<>();
        params.put(EmailConstants.ALERT_TEMPLATE_TITLE, title);
        params.put(EmailConstants.ALERT_TEMPLATE_CONTENT, markdownToHtml(content));

        EmailSender emailSender = new EmailSender(getConfig().getParam());
        try {
            return emailSender.send(title, buildContent(params));
        } catch (TemplateException | IOException e) {
            log.error("{}'message send error, Reason:{}", getType(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
