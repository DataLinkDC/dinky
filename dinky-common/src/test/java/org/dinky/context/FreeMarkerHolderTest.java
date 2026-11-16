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

package org.dinky.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import freemarker.template.TemplateException;

class FreeMarkerHolderTest {

    @Test
    void rendersProvidedTemplateData() throws Exception {
        FreeMarkerHolder holder = new FreeMarkerHolder();
        holder.putTemplate(
                "alert",
                "- **Job Name :** ${jobName}\n"
                        + "- **Job Status :** ${jobStatus}\n"
                        + "- **Alert Time :** ${alertTime}\n"
                        + "- **Start Time :** ${jobStartTime}\n"
                        + "- **End Time :** ${jobEndTime}\n"
                        + "- ${errorMsg}\n"
                        + "[Go toTask Web](http://${taskUrl})");

        Map<String, Object> data = new HashMap<>();
        data.put("jobName", "demo");
        data.put("jobStatus", "FAILED");
        data.put("alertTime", "2023-01-02 00:00:00");
        data.put("jobStartTime", "2023-01-01 00:00:00");
        data.put("jobEndTime", "2023-01-02 00:00:00");
        data.put("errorMsg", "task failed");
        data.put("taskUrl", "localhost:8000/#/devops/job-detail?id=29");

        String rendered = holder.buildWithData("alert", data);

        assertTrue(rendered.contains("demo"));
        assertTrue(rendered.contains("FAILED"));
        assertTrue(rendered.contains("2023-01-02 00:00:00"));
        assertTrue(rendered.contains("task failed"));
        assertTrue(rendered.contains("localhost:8000/#/devops/job-detail?id=29"));
    }

    @Test
    void rendersAlertSenderFormatting() throws Exception {
        FreeMarkerHolder holder = new FreeMarkerHolder();
        holder.putTemplate(
                "sender",
                "title=${title}; content=${content?json_string}; atAll=${isAtAll?c};"
                        + " mobiles=<#list atMobiles as key>${key}<#if key_has_next>,</#if></#list>");

        Map<String, Object> data = new HashMap<>();
        data.put("title", "Job failed");
        data.put("content", "line1\nline2");
        data.put("isAtAll", false);
        data.put("atMobiles", Arrays.asList("13800000000", "13900000000"));

        String rendered = holder.buildWithData("sender", data);

        assertTrue(rendered.contains("title=Job failed"));
        assertTrue(rendered.contains("atAll=false"));
        assertTrue(rendered.contains("13800000000,13900000000"));
        assertTrue(rendered.contains("\\n") || rendered.contains("line1"));
    }

    @Test
    void rendersProvidedTemplateDataFromString() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("className", "MyUdf");
        data.put("package", "com.example");

        String rendered = FreeMarkerHolder.render(
                "${(package=='')?string('', 'package '+package+';')}\npublic class ${className} {}", data);

        assertEquals("package com.example;\npublic class MyUdf {}", rendered);
    }

    @Test
    void rejectsArbitraryTypeConstruction() {
        FreeMarkerHolder holder = new FreeMarkerHolder();
        holder.putTemplate("construct", "${'java.util.ArrayList'?new()}");

        Map<String, Object> data = new HashMap<>();
        assertThrows(TemplateException.class, () -> holder.buildWithData("construct", data));
    }

    @Test
    void rejectsApiBuiltin() {
        FreeMarkerHolder holder = new FreeMarkerHolder();
        holder.putTemplate("api", "${value?api}");

        Map<String, Object> data = new HashMap<>();
        data.put("value", "demo");
        Exception ex = assertThrows(Exception.class, () -> holder.buildWithData("api", data));
        assertFalse(ex.getMessage() == null && ex.getCause() == null);
    }
}
