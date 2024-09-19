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

package org.dinky.ws;

import org.dinky.ws.topic.BaseTopic;
import org.dinky.ws.topic.JvmInfo;
import org.dinky.ws.topic.Metrics;
import org.dinky.ws.topic.PrintTable;
import org.dinky.ws.topic.ProcessConsole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalWebSocketTopic {
    JVM_INFO("jvmInfo", JvmInfo.INSTANCE, 5000),
    PROCESS_CONSOLE("PROCESS_CONSOLE", ProcessConsole.INSTANCE, Integer.MAX_VALUE),
    PRINT_TABLE("PRINT_TABLE", PrintTable.INSTANCE, Integer.MAX_VALUE),
    METRICS("METRICS", Metrics.INSTANCE, Integer.MAX_VALUE),
    ;
    private final String topic;
    private final BaseTopic instance;
    private final int delaySend;
}
