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

package org.dinky.ws.handler;

import org.dinky.data.metrics.Jvm;
import org.dinky.ws.GlobalWebSocketTopic;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import cn.hutool.core.map.MapUtil;

@Service
public class JvmInfo extends ScheduleMessageEventHandler {

    @Override
    protected long scheduleDelay() {

        return TimeUnit.SECONDS.toMillis(3);
    }

    @Override
    public Map<String, Object> autoMessageSend() {
        return MapUtil.<String, Object>builder().put(NONE_PARAMS, Jvm.of()).build();
    }

    @Override
    public Map<String, Object> firstSubscribe(Set<String> allParams) {
        return autoMessageSend();
    }

    @Override
    public GlobalWebSocketTopic getTopic() {
        return GlobalWebSocketTopic.JVM_INFO;
    }
}
