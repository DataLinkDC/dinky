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

package com.dlink.session;

import com.dlink.executor.Executor;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkEntity
 *
 * @author wenmo
 * @since 2021/5/25 14:45
 **/
@Setter
@Getter
public class ExecutorEntity {
    private String sessionId;
    private SessionConfig sessionConfig;
    private String createUser;
    private LocalDateTime createTime;
    private Executor executor;

    public ExecutorEntity(String sessionId, Executor executor) {
        this.sessionId = sessionId;
        this.executor = executor;
    }

    public ExecutorEntity(String sessionId, SessionConfig sessionConfig, String createUser, LocalDateTime createTime, Executor executor) {
        this.sessionId = sessionId;
        this.sessionConfig = sessionConfig;
        this.createUser = createUser;
        this.createTime = createTime;
        this.executor = executor;
    }
}
