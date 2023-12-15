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

package org.dinky.alert.sms;

import org.dinky.alert.AbstractAlert;
import org.dinky.alert.AlertException;
import org.dinky.alert.AlertResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;

/**
 * SmsAlert
 */
public class SmsAlert extends AbstractAlert {
    private static final Logger logger = LoggerFactory.getLogger(SmsAlert.class);

    @Override
    public String getType() {
        return SmsConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        SmsSender sender = new SmsSender(getConfig().getParam());
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(SmsConstants.ALERT_TEMPLATE_TITLE, title);
            params.put(SmsConstants.ALERT_TEMPLATE_CONTENT, removeHTMLTagsAndUrls(markdownToText(content)));
            String built = buildContent(params);
            return sender.send(built);
        } catch (TemplateException | IOException e) {
            logger.error("{}'message send error, Reason:{}", getType(), e.getMessage());
            throw new AlertException(e.getMessage(), e);
        }
    }

    /**
     * remove line from a string by line number
     *
     * @param inputString      input string with line breaks (new lines)
     * @param headerLineNumber number of lines to remove from the beginning of the string
     * @param keepLineNumber   number of lines to keep from the beginning of the string
     * @return string with lines removed
     */
    public static String removeLine(String inputString, int headerLineNumber, int keepLineNumber) {
        String[] lines = inputString.split("\\r?\\n");
        if (keepLineNumber >= lines.length) {
            return inputString;
        }
        if (headerLineNumber >= keepLineNumber) {
            throw new IllegalArgumentException(String.format(
                    "headerLineNumber: %s must be less than keepLineNumber: %s", headerLineNumber, keepLineNumber));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = headerLineNumber - 1; i < keepLineNumber; i++) {
            sb.append(lines[i]);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * remove html tags from a string
     *
     * @param text html text
     * @return text without html tags
     */
    public static String removeHTMLTagsAndUrls(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // remove all html tags  and urls
        String pattern = "<[^>]+>|\\-\\s+|\\»|\\«|\"";
        text = text.replaceAll(pattern, "").replaceAll(": ", ": ");
        return removeLine(text, 1, 5);
    }

    /**
     * Convert markdown to text
     *
     * @param markdown Markdown text
     * @return text without Markdown tags
     */
    public static String markdownToText(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        return renderer.render(document);
    }
}
