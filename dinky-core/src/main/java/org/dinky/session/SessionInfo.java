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

package org.dinky.session;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * SessionInfo
 *
 * @author wenmo
 * @since 2021/7/6 22:22
 */
@Setter
@Getter
public class SessionInfo {
    private String session;
    private SessionConfig sessionConfig;
    private String createUser;
    private LocalDateTime createTime;

    public SessionInfo(String session, SessionConfig sessionConfig, String createUser, LocalDateTime createTime) {
        this.session = session;
        this.sessionConfig = sessionConfig;
        this.createUser = createUser;
        this.createTime = createTime;
    }

    public static SessionInfo build(ExecutorEntity executorEntity) {
        return new SessionInfo(executorEntity.getSessionId(), executorEntity.getSessionConfig(), executorEntity.getCreateUser(), executorEntity.getCreateTime());
    }

}
