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

package org.dinky.ws.topic;

import static org.dinky.ws.GlobalWebSocket.sendTopic;

import org.dinky.service.impl.PrintTableServiceImpl;
import org.dinky.ws.GlobalWebSocketTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintTable extends BaseTopic {
    public static final PrintTable INSTANCE = new PrintTable();

    private PrintTable() {
        PrintTableServiceImpl.PrintTableListener printer = new PrintTableServiceImpl.PrintTableListener(this::send);
        printer.start();
    }

    public void send(String message) {
        try {
            String[] data = message.split("\n", 2);

            Map<String, Object> result =
                    MapUtil.<String, Object>builder().put(data[0], data[1]).build();
            sendTopic(GlobalWebSocketTopic.PRINT_TABLE, result);
        } catch (Exception e) {
            log.error("send message failed: {}", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> autoDataSend(Set<String> allParams) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> firstDataSend(Set<String> allParams) {
        return new HashMap<>();
    }
}
