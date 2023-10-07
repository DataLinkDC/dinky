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

package org.dinky.data.model.flink.exceptions;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *     "root-exception": null,
 *     "timestamp": null,
 *     "all-exceptions": [],
 *     "truncated": false,
 *     "exceptionHistory": {
 *         "entries": [],
 *         "truncated": false
 *     }
 * }
 */
@ApiModel(value = "FlinkJobExceptionsDetail", description = "Flink Job Exceptions Detail Info")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlinkJobExceptionsDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "All Exceptions",
            required = true,
            notes = "All Exceptions",
            dataType = "List",
            example = "All Exceptions")
    @JSONField(name = "all-exceptions")
    private List<Object> allExceptions;

    @ApiModelProperty(
            value = "Root Exception",
            required = true,
            notes = "Root Exception",
            dataType = "String",
            example = "Root Exception")
    @JSONField(name = "root-exception")
    private String rootException = "";

    @ApiModelProperty(
            value = "Timestamp",
            required = true,
            notes = "Timestamp",
            dataType = "Object",
            example = "Timestamp")
    @JSONField(name = "timestamp")
    private Long timestamp;

    @ApiModelProperty(
            value = "Truncated",
            required = true,
            notes = "Truncated",
            dataType = "Boolean",
            example = "Truncated")
    @JSONField(name = "truncated")
    private Boolean truncated;

    @ApiModelProperty(
            value = "Exception History",
            required = true,
            notes = "Exception History",
            dataType = "Object",
            example = "Exception History")
    @JSONField(name = "exceptionHistory")
    private ExceptionHistory exceptionHistory;

    @ApiModel(value = "ExceptionHistory", description = "Exception History Info")
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExceptionHistory {

        @ApiModelProperty(value = "Entries", required = true, notes = "Entries", dataType = "List", example = "Entries")
        @JSONField(name = "entries")
        private List<Object> entries;

        @ApiModelProperty(
                value = "Truncated",
                required = true,
                notes = "Truncated",
                dataType = "Boolean",
                example = "Truncated")
        @JSONField(name = "truncated")
        private Boolean truncated;
    }
}
