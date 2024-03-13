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

package org.dinky.executor;

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.GatewayType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * ExecutorSetting
 *
 * @since 2021/5/25 13:43
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@ApiModel(value = "ExecutorConfig", description = "Executor config for a job")
public class ExecutorConfig {

    private static final Logger log = LoggerFactory.getLogger(ExecutorConfig.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final ExecutorConfig DEFAULT = ExecutorConfig.builder()
            .checkpoint(0)
            .parallelism(1)
            .useSqlFragment(true)
            .build();

    // after unique all run model to remote, this field could discard
    @ApiModelProperty(
            value = "Flink run mode",
            dataType = "String",
            example = "local standalone",
            notes = "Flink run mode")
    private String type;

    @ApiModelProperty(
            value = "Job manager rest host",
            dataType = "String",
            example = "127.0.0.1",
            notes = "Remote environment need a job manager rest host")
    private String host;

    @ApiModelProperty(
            value = "Job manager rest port",
            dataType = "Integer",
            example = "8081",
            notes = "Remote environment need a job manager rest port")
    private Integer port;

    @ApiModelProperty(
            value = "Flag indicating whether to use batch model",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use batch model")
    private boolean useBatchModel;

    @ApiModelProperty(
            value = "Whether to only build plans",
            dataType = "boolean",
            example = "true",
            notes = "Build plan only")
    private boolean isPlan;

    @ApiModelProperty(
            value = "Checkpoint interval",
            dataType = "Integer",
            example = "5000",
            notes = "Checkpoint interval")
    private Integer checkpoint;

    @ApiModelProperty(value = "Parallelism level", dataType = "Integer", example = "4", notes = "Parallelism level")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Flag indicating whether to use SQL fragment",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use SQL fragment")
    private boolean useSqlFragment;

    @ApiModelProperty(
            value = "Flag indicating whether to use statement set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use statement set")
    private boolean useStatementSet;

    @ApiModelProperty(
            value = "Save point path",
            dataType = "String",
            example = "/savepoints",
            notes = "Save point path")
    private String savePointPath;

    @ApiModelProperty(value = "Name of the job", dataType = "String", example = "MyJob", notes = "Name of the job")
    private String jobName;

    @ApiModelProperty(
            value = "Map of configuration properties",
            dataType = "Map<String, String>",
            example = "{\"config1\": \"value1\", \"config2\": \"value2\"}",
            notes = "Map of configuration properties")
    private Map<String, String> config;

    @ApiModelProperty(
            value = "Map of variables",
            dataType = "Map<String, String>",
            example = "{\"var1\": \"value1\", \"var2\": \"value2\"}",
            notes = "Map of variables")
    private Map<String, String> variables;

    @ApiModelProperty(
            value = "List of JAR files",
            dataType = "String[]",
            example = "[\"file1.jar\", \"file2.jar\"]",
            notes = "List of JAR files")
    private String[] jarFiles;

    public static ExecutorConfig build(
            String type,
            String address,
            Integer checkpoint,
            Integer parallelism,
            boolean useSqlFragment,
            boolean useStatementSet,
            boolean useBatchModel,
            String savePointPath,
            String jobName,
            Map<String, String> config,
            Map<String, String> variables) {

        String host = null;
        Integer port = null;
        if (Asserts.isNotNullString(address)) {
            String[] strings = address.split(":");
            if (strings.length > 1) {
                host = strings[0];
                port = Integer.parseInt(strings[1]);
            } else {
                host = strings[0];
                port = 8081;
            }
        }

        return ExecutorConfig.builder()
                .type(type)
                .host(host)
                .port(port)
                .checkpoint(checkpoint)
                .parallelism(parallelism)
                .useSqlFragment(useSqlFragment)
                .useStatementSet(useStatementSet)
                .useBatchModel(useBatchModel)
                .savePointPath(savePointPath)
                .jobName(jobName)
                .config(config)
                .variables(variables)
                .build();
    }

    public static ExecutorConfig build(
            String type,
            Integer checkpoint,
            Integer parallelism,
            boolean useSqlFragment,
            boolean useStatementSet,
            boolean useBatchModel,
            String savePointPath,
            String jobName,
            String configJson) {
        List<Map<String, String>> configList = new ArrayList<>();
        if (Asserts.isNotNullString(configJson)) {
            try {
                configList = mapper.readValue(configJson, ArrayList.class);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }

        Map<String, String> config = new HashMap<>();
        for (Map<String, String> item : configList) {
            if (Asserts.isNotNull(item) && Asserts.isAllNotNullString(item.get("key"), item.get("value"))) {
                config.put(item.get("key"), item.get("value"));
            }
        }

        return build(
                type,
                null,
                checkpoint,
                parallelism,
                useSqlFragment,
                useStatementSet,
                useBatchModel,
                savePointPath,
                jobName,
                config,
                null);
    }

    public String getJobManagerAddress() {
        return host + ":" + port;
    }

    public boolean isRemote() {
        return !GatewayType.get(type).isLocalExecute();
    }

    public boolean isValidParallelism() {
        return Asserts.isNotNull(this.getParallelism()) && this.getParallelism() > 0;
    }

    public boolean isValidJobName() {
        return Asserts.isNotNullString(this.getJobName());
    }

    public boolean isValidHost() {
        return Asserts.isNotNullString(this.getHost());
    }

    public boolean isValidPort() {
        return Asserts.isNotNull(this.getPort());
    }

    public boolean isValidConfig() {
        return Asserts.isNotNullMap(this.getConfig());
    }

    public boolean isValidVariables() {
        return Asserts.isNotNullMap(this.getVariables());
    }

    public boolean isValidJarFiles() {
        return Asserts.isNotNull(this.getJarFiles());
    }

    public boolean isPlan() {
        return isPlan;
    }

    public void setPlan(boolean plan) {
        isPlan = plan;
    }

    @Override
    public String toString() {
        return String.format(
                "ExecutorSetting{checkpoint=%d, parallelism=%d, useSqlFragment=%s,"
                        + " useStatementSet=%s, savePointPath='%s', jobName='%s', config=%s}",
                checkpoint, parallelism, useSqlFragment, useStatementSet, savePointPath, jobName, config);
    }
}
