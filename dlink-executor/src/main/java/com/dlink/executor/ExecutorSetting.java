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

package com.dlink.executor;

import com.dlink.assertion.Asserts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
@Setter
@Getter
public class ExecutorSetting {

    private static final Logger log = LoggerFactory.getLogger(ExecutorSetting.class);
    public static final ExecutorSetting DEFAULT = new ExecutorSetting(0, 1, true);

    public static final String CHECKPOINT_CONST = "checkpoint";
    public static final String PARALLELISM_CONST = "parallelism";
    public static final String USE_SQL_FRAGMENT = "useSqlFragment";
    public static final String USE_STATEMENT_SET = "useStatementSet";
    public static final String USE_BATCH_MODEL = "useBatchModel";
    public static final String SAVE_POINT_PATH = "savePointPath";
    public static final String JOB_NAME = "jobName";
    public static final String CONFIG_CONST = "config";

    private static final ObjectMapper mapper = new ObjectMapper();
    private boolean useBatchModel;
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private boolean useStatementSet;
    private String savePointPath;
    private String jobName;
    private Map<String, String> config;

    public ExecutorSetting(boolean useSqlFragment) {
        this(null, useSqlFragment);
    }

    public ExecutorSetting(Integer checkpoint) {
        this(checkpoint, false);
    }

    public ExecutorSetting(Integer checkpoint, boolean useSqlFragment) {
        this(checkpoint, null, useSqlFragment, null, null);
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment) {
        this(checkpoint, parallelism, useSqlFragment, null, null);
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName) {
        this(checkpoint, parallelism, useSqlFragment, savePointPath, jobName, null);
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath) {
        this(checkpoint, parallelism, useSqlFragment, savePointPath, null, null);
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName, Map<String, String> config) {
        this(checkpoint, parallelism, useSqlFragment, false, false, savePointPath, jobName, config);
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, boolean useStatementSet, boolean useBatchModel, String savePointPath, String jobName,
                           Map<String, String> config) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.useBatchModel = useBatchModel;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
        this.config = config;
    }

    public static ExecutorSetting build(Integer checkpoint, Integer parallelism, boolean useSqlFragment, boolean useStatementSet, boolean useBatchModel, String savePointPath, String jobName,
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
            config.put(item.get("key"), item.get("value"));
        }
        return new ExecutorSetting(checkpoint, parallelism, useSqlFragment, useStatementSet, useBatchModel,
                savePointPath, jobName, config);
    }

    public static ExecutorSetting build(Map<String, String> settingMap) {
        Integer checkpoint = Integer.valueOf(settingMap.get(CHECKPOINT_CONST));
        Integer parallelism = Integer.valueOf(settingMap.get(PARALLELISM_CONST));

        return build(checkpoint, parallelism, "1".equals(settingMap.get(USE_SQL_FRAGMENT)), "1".equals(settingMap.get(USE_STATEMENT_SET)), "1".equals(settingMap.get(USE_BATCH_MODEL)),
            settingMap.get(SAVE_POINT_PATH), settingMap.get(JOB_NAME), settingMap.get(CONFIG_CONST));
    }

    public boolean isValidParallelism() {
        return this.getParallelism() != null && this.getParallelism() > 0;
    }

    public boolean isValidJobName() {
        return this.getJobName() != null && !"".equals(this.getJobName());
    }

    @Override
    public String toString() {
        return String.format("ExecutorSetting{checkpoint=%d, parallelism=%d, useSqlFragment=%s, useStatementSet=%s, savePointPath='%s', jobName='%s', config=%s}", checkpoint, parallelism,
            useSqlFragment, useStatementSet, savePointPath, jobName, config);
    }
}
