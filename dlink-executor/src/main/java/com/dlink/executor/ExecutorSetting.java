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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
@Setter
@Getter
public class ExecutorSetting {

    private boolean useBatchModel = false;
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private boolean useStatementSet;
    private String savePointPath;
    private String jobName;
    private Map<String, String> config;
    public static final ExecutorSetting DEFAULT = new ExecutorSetting(0, 1, true);
    private static final ObjectMapper mapper = new ObjectMapper();

    public ExecutorSetting(boolean useSqlFragment) {
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint) {
        this.checkpoint = checkpoint;
    }

    public ExecutorSetting(Integer checkpoint, boolean useSqlFragment) {
        this.checkpoint = checkpoint;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName, Map<String, String> config) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
        this.config = config;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, boolean useStatementSet,
                           boolean useBatchModel, String savePointPath, String jobName, Map<String, String> config) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.useBatchModel = useBatchModel;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
        this.config = config;
    }

    public static ExecutorSetting build(Integer checkpoint, Integer parallelism, boolean useSqlFragment, boolean useStatementSet, boolean useBatchModel, String savePointPath, String jobName, String configJson) {
        List<Map<String, String>> configList = new ArrayList<>();
        if (Asserts.isNotNullString(configJson)) {
            try {
                configList = mapper.readValue(configJson, ArrayList.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> config = new HashMap<>();
        for (Map<String, String> item : configList) {
            config.put(item.get("key"), item.get("value"));
        }
        return new ExecutorSetting(checkpoint, parallelism, useSqlFragment, useStatementSet, useBatchModel, savePointPath, jobName, config);
    }

    public static ExecutorSetting build(Map<String, String> settingMap) {
        Integer checkpoint = null;
        Integer parallelism = null;
        if (settingMap.containsKey("checkpoint") && !"".equals(settingMap.get("checkpoint"))) {
            checkpoint = Integer.valueOf(settingMap.get("checkpoint"));
        }
        if (settingMap.containsKey("parallelism") && !"".equals(settingMap.get("parallelism"))) {
            parallelism = Integer.valueOf(settingMap.get("parallelism"));
        }
        return build(checkpoint,
                parallelism,
                "1".equals(settingMap.get("useSqlFragment")),
                "1".equals(settingMap.get("useStatementSet")),
                "1".equals(settingMap.get("useBatchModel")),
                settingMap.get("savePointPath"),
                settingMap.get("jobName"),
                settingMap.get("config"));
    }

    @Override
    public String toString() {
        return "ExecutorSetting{" +
                "checkpoint=" + checkpoint +
                ", parallelism=" + parallelism +
                ", useSqlFragment=" + useSqlFragment +
                ", useStatementSet=" + useStatementSet +
                ", savePointPath='" + savePointPath + '\'' +
                ", jobName='" + jobName + '\'' +
                ", config=" + config +
                '}';
    }
}
