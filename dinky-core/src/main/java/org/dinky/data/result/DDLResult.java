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
 * DDLResult
 *
 * @since 2021/6/29 22:06
 */
@Setter
@Getter
public class DDLResult extends AbstractResult implements IResult {

    private List<Map<String, Object>> rowData;
    private Integer total;
    private Set<String> columns;

    public DDLResult(boolean success) {
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    public DDLResult(List<Map<String, Object>> rowData, Integer total, Set<String> columns) {
        this.rowData = rowData;
        this.total = total;
        this.columns = columns;
        this.success = true;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String getJobId() {
        return null;
    }
}
