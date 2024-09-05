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

package org.apache.flink.runtime.webmonitor.history;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.plugin.PluginUtils;
import org.apache.flink.util.FlinkException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HistoryServerUtil {

    public static void run(Consumer<String> jobIdEventListener, Map<String, String> config) {
        log.info("Starting Flink History service....");
        HistoryServer hs;
        try {
            org.apache.flink.configuration.Configuration configuration = Configuration.fromMap(config);
            FileSystem.initialize(configuration, PluginUtils.createPluginManagerFromRootFolder(configuration));

            hs = new HistoryServer(configuration, (event) -> {
                if (event.getType() == HistoryServerArchiveFetcher.ArchiveEventType.CREATED) {
                    Optional.ofNullable(jobIdEventListener).ifPresent(listener -> listener.accept(event.getJobID()));
                }
            });
            log.info("Flink History service started successfully.");
        } catch (IOException | FlinkException e) {
            log.error(
                    "The Flink History service failed to start with the following error message: {}",
                    e.getMessage(),
                    e);
            throw new RuntimeException(e);
        }
        hs.run();
    }
}
