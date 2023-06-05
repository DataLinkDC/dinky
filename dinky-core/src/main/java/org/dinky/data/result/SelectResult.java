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
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * SelectResult
 *
 * @since 2021/5/25 16:01
 */
@Setter
@Getter
public class SelectResult extends AbstractResult implements IResult {

    private String jobID;
    private List<Map<String, Object>> rowData;
    private Integer total;
    private Integer currentCount;
    private Set<String> columns;
    private boolean isDestroyed;

    public SelectResult(
            List<Map<String, Object>> rowData,
            Integer total,
            Integer currentCount,
            Set<String> columns,
            String jobID,
            boolean success) {
        this.rowData = rowData;
        this.total = total;
        this.currentCount = currentCount;
        this.columns = columns;
        this.jobID = jobID;
        this.success = success;
        // this.endTime = LocalDateTime.now();
        this.isDestroyed = false;
    }

    public SelectResult(String jobID, List<Map<String, Object>> rowData, Set<String> columns) {
        this.jobID = jobID;
        this.rowData = rowData;
        this.total = rowData.size();
        this.columns = columns;
        this.success = true;
        this.isDestroyed = false;
    }

    public SelectResult(String jobID, boolean isDestroyed, boolean success) {
        this.jobID = jobID;
        this.isDestroyed = isDestroyed;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String getJobId() {
        return jobID;
    }

    public static SelectResult buildDestruction(String jobID) {
        return new SelectResult(jobID, true, false);
    }

    public static SelectResult buildSuccess(String jobID) {
        return new SelectResult(jobID, false, true);
    }

    public static SelectResult buildFailed() {
        return new SelectResult(null, false, false);
    }
}
