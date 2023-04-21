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

package com.zdpx.coder.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

/**
 * freemarker 工具类.
 *
 * @author Licho Sun
 */
@Slf4j
public class TemplateUtils {
    private static final Configuration configuration =
            new Configuration(Configuration.VERSION_2_3_28);

    static {
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(new ClassTemplateLoader(TemplateUtils.class, "/templates"));
        configuration.setSharedVariable("instanceOf", new InstanceOfMethod());
    }

    private TemplateUtils() {}

    public static String format(String name, Object dataModel, String context) {
        final StringWriter stringWriter = new StringWriter();
        try {
            Template template = new Template(name, new StringReader(context), configuration);
            template.process(dataModel, stringWriter);
        } catch (TemplateException | IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
        return stringWriter.toString();
    }
}
