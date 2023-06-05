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

import lombok.Getter;
import lombok.Setter;

/**
 * InsertResult
 *
 * @since 2021/5/25 19:08
 */
@Getter
@Setter
public class InsertResult extends AbstractResult implements IResult {

    private String jobID;

    public InsertResult(String jobID, boolean success) {
        this.jobID = jobID;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    public static InsertResult success(String jobID) {
        return new InsertResult(jobID, true);
    }

    @Override
    public String getJobId() {
        return jobID;
    }
}
