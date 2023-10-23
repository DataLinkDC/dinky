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

package org.dinky.alert;

import org.dinky.context.FreeMarkerHolder;

import java.io.IOException;
import java.util.Map;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.StrFormatter;
import freemarker.template.TemplateException;
import lombok.Getter;

/**
 * AbstractAlert
 *
 * @since 2022/2/23 19:22
 */
public abstract class AbstractAlert implements Alert {

    @Getter
    private AlertConfig config;

    private final FreeMarkerHolder freeMarkerHolder = new FreeMarkerHolder();

    public Alert setConfig(AlertConfig config) {
        this.config = config;
        freeMarkerHolder.putTemplate(config.getType(), getTemplate());
        return this;
    }

    public String getTemplate() {
        return ResourceUtil.readUtf8Str(StrFormatter.format("{}.ftl", config.getType()))
                .replace("\n", "\n\n");
    }

    protected String buildContent(Map<String, Object> params) throws TemplateException, IOException {
        return freeMarkerHolder.buildWithData(getType(), params);
    }
}
