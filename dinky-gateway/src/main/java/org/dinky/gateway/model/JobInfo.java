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

package org.dinky.gateway.model;

import lombok.Getter;
import lombok.Setter;

/**
 * JobInfo
 *
 * @since 2021/11/3 21:45
 */
@Getter
@Setter
public class JobInfo {

    private String jobId;
    private String savePoint;
    private JobStatus status;

    public JobInfo(String jobId) {
        this.jobId = jobId;
    }

    public JobInfo(String jobId, JobStatus status) {
        this.jobId = jobId;
        this.status = status;
    }

    public enum JobStatus {
        RUN("run"),
        STOP("stop"),
        CANCEL("cancel"),
        FAIL("fail");

        private String value;

        JobStatus(String value) {
            this.value = value;
        }
    }
}
