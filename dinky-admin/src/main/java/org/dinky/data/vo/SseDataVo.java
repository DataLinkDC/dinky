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

package org.dinky.data.vo;

import org.dinky.ws.GlobalWebSocket;

import lombok.Data;

@Data
public class SseDataVo {
    private String sessionKey;
    private String topic;
    private Object data;
    private GlobalWebSocket.RequestDTO.EventType type;

    public SseDataVo(String sessionKey, String topic, Object data) {
        this.sessionKey = sessionKey;
        this.topic = topic;
        this.data = data;
    }

    public SseDataVo(String sessionKey, GlobalWebSocket.RequestDTO.EventType type) {
        this.sessionKey = sessionKey;
        this.type = type;
    }
}
