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

package org.dinky.data.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class MockSinkResult extends AbstractResult implements IResult {

    private String taskId;
    private Map<String, List<Map<String, String>>> tableRowData;
    private boolean truncationFlag = false;
    private boolean isDestroyed;

    public MockSinkResult(String taskId, Map<String, List<Map<String, String>>> tableRowData) {
        this.taskId = taskId;
        this.tableRowData = tableRowData;
    }

    public MockSinkResult(String taskId, boolean isDestroyed, boolean success) {
        this.taskId = taskId;
        this.isDestroyed = isDestroyed;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    public static MockSinkResult buildSuccess(String taskId) {
        return new MockSinkResult(taskId, false, true);
    }

    public static MockSinkResult buildFailed() {
        return new MockSinkResult(null, false, false);
    }

    @Override
    public String getJobId() {
        return this.taskId;
    }
}
