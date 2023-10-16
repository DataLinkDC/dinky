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

import org.dinky.data.exception.BusException;
import org.dinky.process.enums.ProcessStatus;
import org.dinky.process.enums.ProcessStepType;
import org.dinky.process.enums.ProcessType;
import org.dinky.process.model.ProcessEntity;
import org.dinky.process.model.ProcessStep;
import org.dinky.utils.LogUtil;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSONObject;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleContextHolder extends BaseSseContext<String, ProcessEntity> {
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
     * Add the SseEmitter object to the context.
     *
     * @param processName process name, which is used as an indication for the keyword list
     * @param sseEmitter  SseEmitter object
     */
    public void addSse(String processName, SseEmitter sseEmitter) {
        List<SseEmitter> emitters = sseList.getIfPresent(processName);
        if (emitters == null) {
            emitters = new ArrayList<>();
            sseList.put(processName, emitters);
        }
        emitters.add(sseEmitter);

        if (logPross.containsKey(processName)) {
            sendAsync(processName, logPross.get(processName));
        } else {
            String filePath = String.format("%s/tmp/log/%s.json", System.getProperty("user.dir"), processName);
            try {
                String string = FileUtil.readString(filePath, Charset.defaultCharset());
                ProcessEntity entity = JSONObject.parseObject(string, ProcessEntity.class);
                sendAsync(processName, entity);
            } catch (IORuntimeException e) {
                log.warn("{} have no cache files", processName);
            }
        }
    }

    @Override
    public void append(String key, ProcessEntity o) {}

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
        Map<String, ProcessStep> parentStep = getParentNode(processStep, getStepsMap(processName));
        parentStep.get(processStep.getValue()).appendLog(log);
        sendAsync(processName, logPross.get(processName));
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
     * @throws RuntimeException Throws an exception if the process does not exist
     */
    public void registerProcessStep(ProcessStepType type, String processName) throws RuntimeException {
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
        getParentNode(type, process.getStepsMap()).put(type.getValue(), processStep);
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
        Map<String, ProcessStep> processStepNode = getParentNode(type, getStepsMap(processName));
        ProcessStep processStep = processStepNode.get(type.getValue());
        processStep.setStepStatus(status);
        processStep.setEndTime(LocalDateTime.now());
        processStep.setTime(processStep.getEndTime().compareTo(processStep.getStartTime()));
        if (e != null) {
            logPross.get(processName).appendErrLog(LogUtil.getError(e));
        }
    }

    /**
     * The * method first checks if a given type has a parent step (i.e. type.getParentStep()) is null).
     * If there is a parent step, recursively call the getParentNode method, passing the parent step type and stepsMap
     * to get the child step mapping of the parent node.
     * If a given type does not have a parent step, it returns steps directly to indicate that the step of that type is a top-level step.
     * Finally, the getParentNode method returns a mapping of child steps containing the parent node for use in other methods.
     */
    private Map<String, ProcessStep> getParentNode(ProcessStepType type, Map<String, ProcessStep> stepsMap) {
        if (type.getParentStep() != null) {
            Map<String, ProcessStep> map = getParentNode(type.getParentStep(), stepsMap);
            return map.get(type.getParentStep().getValue()).getChildStepsMap();
        }
        return stepsMap;
    }

    private Map<String, ProcessStep> getStepsMap(String processName) {
        return logPross.get(processName).getStepsMap();
    }
}
