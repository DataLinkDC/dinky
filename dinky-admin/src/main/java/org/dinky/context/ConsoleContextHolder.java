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

package org.dinky.context;

import org.dinky.aop.ProcessAspect;
import org.dinky.data.enums.SseTopic;
import org.dinky.data.exception.BusException;
import org.dinky.process.enums.ProcessStatus;
import org.dinky.process.enums.ProcessStepType;
import org.dinky.process.enums.ProcessType;
import org.dinky.process.exception.DinkyException;
import org.dinky.process.model.ProcessEntity;
import org.dinky.process.model.ProcessStep;
import org.dinky.utils.LogUtil;

import org.apache.http.util.TextUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.MDC;

import com.alibaba.fastjson2.JSONObject;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleContextHolder {
    protected static final ConsoleContextHolder instance = new ConsoleContextHolder();

    /**
     * Get an instance of ConsoleContextHolder.
     *
     * @return ConsoleContextHolder instance
     */
    public static ConsoleContextHolder getInstances() {
        return instance;
    }

    private final Map<String, ProcessEntity> logPross = new ConcurrentHashMap<>();

    /**
     * Get a list of all processes
     * */
    public List<ProcessEntity> list() {
        return new ArrayList<>(logPross.values());
    }

    /**
     * Add log messages to specific processes and process steps.
     *
     * @param processName process name
     * @param processStep process step type
     * @param log         messages
     * @throws BusException Throws an exception if the process does not exist
     */
    public void appendLog(String processName, ProcessStepType processStep, String log) {
        if (!logPross.containsKey(processName)) {
            throw new BusException(StrFormatter.format("process {} does not exist", processName));
        }
        logPross.get(processName).appendLog(log);
        ProcessStep stepNode = getStepNode(processStep.getValue(), getStepsMap(processName));
        stepNode.appendLog(log);
        String topic = StrFormatter.format("{}/{}", SseTopic.PROCESS_CONSOLE.getValue(), processName);
        CompletableFuture.runAsync(() -> {
            SseSessionContextHolder.sendTopic(topic, logPross.get(processName));
        });
    }

    /**
     * Register a new process.
     *
     * @param type        process type
     * @param processName process name
     * @throws RuntimeException Throws an exception if the process already exists
     */
    public void registerProcess(ProcessType type, String processName) throws RuntimeException {
        if (logPross.containsKey(processName)) {
            throw new BusException("Another user is running an action to suppress this request");
        }
        ProcessEntity entity = ProcessEntity.builder()
                .pid(processName)
                .log(new StringBuilder())
                .errLog(new StringBuilder())
                .status(ProcessStatus.INITIALIZING)
                .type(type)
                .name(processName)
                .startTime(LocalDateTime.now())
                .stepsMap(new LinkedHashMap<>())
                .build();
        logPross.put(processName, entity);
    }

    /**
     * Register a new process step.
     *
     * @param type        process step type
     * @param processName process name
     * @param parentStep  parent step
     * @throws RuntimeException Throws an exception if the process does not exist
     */
    public void registerProcessStep(ProcessStepType type, String processName, String parentStep)
            throws RuntimeException {
        if (!logPross.containsKey(processName)) {
            throw new BusException(StrFormatter.format("Process {} does not exist", processName));
        }
        ProcessEntity process = logPross.get(processName);
        process.setStatus(ProcessStatus.RUNNING);
        ProcessStep processStep = ProcessStep.builder()
                .stepStatus(ProcessStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .type(type)
                .name(type.getDesc().getMessage())
                .log(new StringBuilder())
                .errLog(new StringBuilder())
                .childStepsMap(new LinkedHashMap<>())
                .build();

        if (TextUtils.isEmpty(parentStep)) {
            // parentStep为空表示为顶级节点
            process.getStepsMap().put(type.getValue(), processStep);
        } else {
            ProcessStep stepNode = getStepNode(parentStep, process.getStepsMap());
            stepNode.getChildStepsMap().put(type.getValue(), processStep);
        }
    }

    /**
     * Mark the process as completed.
     *
     * @param processName process name
     * @param status      Process status
     * @param e           exception object, optional
     */
    public void finishedProcess(String processName, ProcessStatus status, Throwable e) {
        if (!logPross.containsKey(processName)) {
            return;
        }
        ProcessEntity process = logPross.get(processName);
        process.setStatus(status);
        process.setEndTime(LocalDateTime.now());
        process.setTime(process.getEndTime().compareTo(process.getStartTime()));
        if (e != null) {
            process.appendErrLog(LogUtil.getError(e));
        }
        String filePath = String.format("%s/tmp/log/%s.json", System.getProperty("user.dir"), process.getName());
        if (FileUtil.exist(filePath)) {
            Assert.isTrue(FileUtil.del(filePath));
        }
        FileUtil.writeUtf8String(JSONObject.toJSONString(process), filePath);
        logPross.remove(processName);
    }

    /**
     * Mark process step as completed.
     *
     * @param processName process name
     * @param type        process step type
     * @param status      Process step status
     * @param e           exception object, optional
     */
    public void finishedStep(String processName, ProcessStepType type, ProcessStatus status, Throwable e) {
        if (!logPross.containsKey(processName)) {
            return;
        }
        ProcessStep processStep = getStepNode(type.getValue(), getStepsMap(processName));
        processStep.setStepStatus(status);
        processStep.setEndTime(LocalDateTime.now());
        processStep.setTime(processStep.getEndTime().compareTo(processStep.getStartTime()));
        if (e != null) {
            logPross.get(processName).appendErrLog(LogUtil.getError(e));
        }
    }

    private ProcessStep getStepNode(String stepType, Map<String, ProcessStep> stepsMap) {
        ProcessStep stepNode = findStepNode(stepType, stepsMap);
        if (stepNode != null) {
            return stepNode;
        }
        String errorStr = StrFormatter.format(
                "Get Parent Node Failed, This is most likely a Dinky bug, "
                        + "please report the following information back to the community：\nProcess:{},\nstep:{},\nprocessNam:{}",
                JSONObject.toJSONString(logPross),
                stepType,
                MDC.get(ProcessAspect.PROCESS_NAME));
        throw new DinkyException(errorStr);
    }

    /**
     * 递归查找节点
     * */
    private ProcessStep findStepNode(String stepType, Map<String, ProcessStep> stepsMap) {
        for (Map.Entry<String, ProcessStep> entry : stepsMap.entrySet()) {
            if (entry.getKey().equals(stepType)) {
                return entry.getValue();
            } else {
                ProcessStep stepNode = findStepNode(stepType, entry.getValue().getChildStepsMap());
                if (stepNode != null) {
                    return stepNode;
                }
            }
        }
        return null;
    }

    private Map<String, ProcessStep> getStepsMap(String processName) {
        return logPross.get(processName).getStepsMap();
    }
}
