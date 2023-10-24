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

package org.dinky.sse;

import org.dinky.aop.ProcessAspect;
import org.dinky.context.ConsoleContextHolder;
import org.dinky.process.enums.ProcessStepType;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.io.Serializable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Plugin(name = "LogSseAppender", category = "Core", elementType = "appender", printObject = true)
public class LogSseAppender extends AbstractAppender {
    protected LogSseAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout,
            boolean ignoreExceptions,
            Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    /**
     * This method is called when a new log comes over, contextData is the data in the MDC,
     * set in {@link ProcessAspect} If contextData contains PROCESS_NAME and PROCESS_STEP,
     * it is represented as buried point data and sent to {@link ConsoleContextHolder}
     * Otherwise, it is a normal log and is ignored
     */
    @Override
    public void append(LogEvent event) {
        ReadOnlyStringMap contextData = event.getContextData();
        if (contextData.containsKey(ProcessAspect.PROCESS_NAME)
                && contextData.containsKey(ProcessAspect.PROCESS_STEP)) {
            String processName = contextData.getValue(ProcessAspect.PROCESS_NAME);
            String processStep = contextData.getValue(ProcessAspect.PROCESS_STEP);
            ConsoleContextHolder.getInstances()
                    .appendLog(processName, ProcessStepType.get(processStep), event.toString());
        }
    }

    /**
     * createAppender
     *
     * */
    @PluginFactory
    public static LogSseAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") final Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout) {
        if (name == null) {
            log.error("No name provided for LogSseAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new LogSseAppender(name, filter, layout, false, null);
    }
}
