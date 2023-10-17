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

package org.dinky.data.flink.exceptions;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@NoArgsConstructor
public class FlinkJobExceptionsDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "All Exceptions",
            required = true,
            notes = "All Exceptions",
            dataType = "List",
            example = "All Exceptions")
    @JsonProperty("all-exceptions")
    private List<Object> allExceptions;

    @ApiModelProperty(
            value = "Root Exception",
            required = true,
            notes = "Root Exception",
            dataType = "String",
            example = "Root Exception")
    @JsonProperty("root-exception")
    private String rootException = "";

    @ApiModelProperty(
            value = "Timestamp",
            required = true,
            notes = "Timestamp",
            dataType = "Object",
            example = "Timestamp")
    @JsonProperty("timestamp")
    private Long timestamp;

    @ApiModelProperty(
            value = "Truncated",
            required = true,
            notes = "Truncated",
            dataType = "Boolean",
            example = "Truncated")
    @JsonProperty("truncated")
    private Boolean truncated;

    @ApiModelProperty(
            value = "Exception History",
            required = true,
            notes = "Exception History",
            dataType = "Object",
            example = "Exception History")
    @JsonProperty("exceptionHistory")
    private ExceptionHistory exceptionHistory;

    @JsonCreator
    public FlinkJobExceptionsDetail(
            @JsonProperty("all-exceptions") List<Object> allExceptions,
            @JsonProperty("root-exception") String rootException,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("truncated") Boolean truncated,
            @JsonProperty("exceptionHistory") ExceptionHistory exceptionHistory) {
        this.allExceptions = allExceptions;
        this.rootException = rootException;
        this.timestamp = timestamp;
        this.truncated = truncated;
        this.exceptionHistory = exceptionHistory;
    }

    @ApiModel(value = "ExceptionHistory", description = "Exception History Info")
    @Builder
    @Data
    @NoArgsConstructor
    public static class ExceptionHistory {

        @ApiModelProperty(value = "Entries", required = true, notes = "Entries", dataType = "List", example = "Entries")
        @JsonProperty("entries")
        private List<Object> entries;

        @ApiModelProperty(
                value = "Truncated",
                required = true,
                notes = "Truncated",
                dataType = "Boolean",
                example = "Truncated")
        @JsonProperty("truncated")
        private Boolean truncated;

        @JsonCreator
        public ExceptionHistory(
                @JsonProperty("entries") List<Object> entries, @JsonProperty("truncated") Boolean truncated) {
            this.entries = entries;
            this.truncated = truncated;
        }
    }
}
