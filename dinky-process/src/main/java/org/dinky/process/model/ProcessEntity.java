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

package com.dlink.process.model;

import com.dlink.assertion.Asserts;
import com.dlink.process.pool.ConsolePool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * Process
 *
 * @author wenmo
 * @since 2022/10/16 16:30
 */
public class ProcessEntity {

    private String pid;
    private String name;
    private Integer taskId;
    private ProcessType type;
    private ProcessStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long time;
    private int stepIndex = 0;
    private List<ProcessStep> steps;
    private Integer userId;

    public static final ProcessEntity NULL_PROCESS = new ProcessEntity();

    public ProcessEntity() {
    }

    public ProcessEntity(String pid, String name, Integer taskId, ProcessType type, Integer userId) {
        this.pid = pid;
        this.name = name;
        this.taskId = taskId;
        this.type = type;
        this.userId = userId;
    }

    public ProcessEntity(String name, Integer taskId, ProcessType type, ProcessStatus status, LocalDateTime startTime,
            LocalDateTime endTime, long time,
            List<ProcessStep> steps, Integer userId) {
        this.name = name;
        this.taskId = taskId;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.time = time;
        this.steps = steps;
        this.userId = userId;
    }

    public static ProcessEntity init(ProcessType type, Integer userId) {
        return init(type.getValue() + "_TEMP", null, type, userId);
    }

    public static ProcessEntity init(Integer taskId, ProcessType type, Integer userId) {
        return init(type.getValue() + taskId, taskId, type, userId);
    }

    public static ProcessEntity init(String name, Integer taskId, ProcessType type, Integer userId) {
        ProcessEntity process = new ProcessEntity(UUID.randomUUID().toString(), name, taskId, type, userId);
        process.setStatus(ProcessStatus.INITIALIZING);
        process.setStartTime(LocalDateTime.now());
        process.setSteps(new ArrayList<>());
        process.getSteps().add(ProcessStep.init());
        process.nextStep();
        return process;
    }

    public void start() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).setEndTime(LocalDateTime.now());
        setStatus(ProcessStatus.RUNNING);
        steps.add(ProcessStep.run());
        nextStep();
    }

    public void finish() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).setEndTime(LocalDateTime.now());
        setStatus(ProcessStatus.FINISHED);
        setEndTime(LocalDateTime.now());
        setTime(getEndTime().compareTo(getStartTime()));
    }

    public void finish(String str) {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).setEndTime(LocalDateTime.now());
        String message = CharSequenceUtil.format("\n[{}] {} INFO: {}", type.getValue(), LocalDateTime.now(), str);
        steps.get(stepIndex - 1).appendInfo(message);
        setStatus(ProcessStatus.FINISHED);
        setEndTime(LocalDateTime.now());
        setTime(getEndTime().compareTo(getStartTime()));
        ConsolePool.write(message, userId);
    }

    public void config(String str) {
        if (isNullProcess()) {
            return;
        }
        String message = CharSequenceUtil.format("\n[{}] {} CONFIG: {}", type.getValue(), LocalDateTime.now(), str);
        steps.get(stepIndex - 1).appendInfo(message);
        ConsolePool.write(message, userId);
    }

    public void info(String str) {
        if (isNullProcess()) {
            return;
        }
        String message = CharSequenceUtil.format("\n[{}] {} INFO: {}", type.getValue(), LocalDateTime.now(), str);
        steps.get(stepIndex - 1).appendInfo(message);
        ConsolePool.write(message, userId);
    }

    public void infoSuccess() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).appendInfo("Success.");
        ConsolePool.write("Success.", userId);
    }

    public void infoFail() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).appendInfo("Fail.");
        ConsolePool.write("Fail.", userId);
    }

    public void error(String str) {
        if (isNullProcess()) {
            return;
        }
        String message = CharSequenceUtil.format("\n[{}] {} ERROR: {}", type.getValue(), LocalDateTime.now(), str);
        steps.get(stepIndex - 1).appendInfo(message);
        steps.get(stepIndex - 1).appendError(message);
        ConsolePool.write(message, userId);
    }

    public void nextStep() {
        if (isNullProcess()) {
            return;
        }
        stepIndex++;
    }

    public boolean isNullProcess() {
        return Asserts.isNullString(pid);
    }

    public boolean isActiveProcess() {
        return status.isActiveStatus();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<ProcessStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProcessStep> steps) {
        this.steps = steps;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }
}
