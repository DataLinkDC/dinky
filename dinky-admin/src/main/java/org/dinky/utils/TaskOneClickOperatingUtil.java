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

package org.dinky.utils;

import org.dinky.common.result.Result;
import org.dinky.context.SpringContextUtils;
import org.dinky.model.CodeEnum;
import org.dinky.model.JobLifeCycle;
import org.dinky.model.JobStatus;
import org.dinky.model.Task;
import org.dinky.model.TaskOperatingSavepointSelect;
import org.dinky.model.TaskOperatingStatus;
import org.dinky.result.TaskOperatingResult;
import org.dinky.service.TaskService;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.exceptions.ExceptionUtil;

/**
 * @author mydq
 * @version 1.0
 */
public class TaskOneClickOperatingUtil {

    private static List<TaskOperatingResult> oneClickOnlineCache = new ArrayList<>(0);

    private static List<TaskOperatingResult> oneClickOfflineCache = new ArrayList<>(0);

    private static final AtomicBoolean oneClickOnlineThreadStatus = new AtomicBoolean(false);

    private static final AtomicBoolean oneClickOfflineThreadStatus = new AtomicBoolean(false);

    public static synchronized Result oneClickOnline(
            List<Task> tasks, TaskOperatingSavepointSelect taskOperatingSavepointSelect) {
        if (oneClickOnlineThreadStatus.get() || oneClickOfflineThreadStatus.get()) {
            return Result.failed("存在一键上线或者下线操作，请稍后重试");
        }
        final TaskService taskService = SpringContextUtils.getBeanByClass(TaskService.class);
        if (CollectionUtils.isEmpty(tasks)) {
            final Result<List<Task>> listResult =
                    taskService.queryOnLineTaskByDoneStatus(
                            Arrays.asList(JobLifeCycle.RELEASE),
                            JobStatus.getAllDoneStatus(),
                            true,
                            0);
            if (CollectionUtils.isEmpty(listResult.getDatas())) {
                return Result.succeed("没有需要上线的任务");
            }
            tasks = listResult.getDatas();
        }
        oneClickOnlineCache =
                tasks.stream()
                        .map(task -> new TaskOperatingResult(task, taskOperatingSavepointSelect))
                        .collect(Collectors.toList());
        new OneClickOperatingThread(
                        "oneClickOnlineThread",
                        oneClickOnlineCache,
                        oneClickOnlineThreadStatus,
                        taskService::selectSavepointOnLineTask)
                .start();
        return Result.succeed("success");
    }

    public static synchronized Result onClickOffline(List<Task> tasks) {
        if (oneClickOnlineThreadStatus.get() || oneClickOfflineThreadStatus.get()) {
            return Result.failed("存在一键上线或者下线操作，请稍后重试");
        }
        final TaskService taskService = SpringContextUtils.getBeanByClass(TaskService.class);
        if (CollectionUtils.isEmpty(tasks)) {
            final Result<List<Task>> listResult =
                    taskService.queryOnLineTaskByDoneStatus(
                            Arrays.asList(JobLifeCycle.ONLINE),
                            Collections.singletonList(JobStatus.RUNNING),
                            false,
                            0);
            if (CollectionUtils.isEmpty(listResult.getDatas())) {
                return Result.succeed("没有需要下线的任务");
            }
            tasks = listResult.getDatas();
        }
        oneClickOfflineCache =
                tasks.stream().map(TaskOperatingResult::new).collect(Collectors.toList());
        new OneClickOperatingThread(
                        "oneClickOfflineThread",
                        oneClickOfflineCache,
                        oneClickOfflineThreadStatus,
                        taskService::selectSavepointOffLineTask)
                .start();
        return Result.succeed("success");
    }

    public static Result<Map<String, Object>> queryOneClickOperatingTaskStatus() {
        final Map<String, Object> map = new HashMap<>(4);
        map.put("online", oneClickOnlineCache);
        map.put("onlineStatus", oneClickOnlineThreadStatus.get());
        map.put("offline", oneClickOfflineCache);
        map.put("offlineStatus", oneClickOfflineThreadStatus.get());
        return Result.succeed(map);
    }

    public static List<Task> parseJsonNode(JsonNode operating) {
        final JsonNode tasksJsonNode = operating.withArray("tasks");
        if (tasksJsonNode == null || tasksJsonNode.isEmpty()) {
            return null;
        }
        final List<Task> result = new ArrayList<>(tasksJsonNode.size());
        for (JsonNode node : tasksJsonNode) {
            final Task task = new Task();
            task.setId(node.get("id").asInt());
            task.setName(node.get("name").asText());
            result.add(task);
        }
        return result;
    }

    private static class OneClickOperatingThread extends Thread {

        private static final Logger LOGGER = LoggerFactory.getLogger(OneClickOperatingThread.class);

        private final String threadName;

        private final List<TaskOperatingResult> taskOperatingResults;

        private final AtomicBoolean threadStatus;

        private final Consumer<TaskOperatingResult> consumer;

        public OneClickOperatingThread(
                String threadName,
                List<TaskOperatingResult> taskOperatingResults,
                AtomicBoolean threadStatus,
                Consumer<TaskOperatingResult> consumer) {
            super(threadName);
            this.threadName = threadName;
            this.threadStatus = threadStatus;
            this.threadStatus.set(true);
            this.taskOperatingResults = taskOperatingResults;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                if (CollectionUtils.isEmpty(taskOperatingResults)) {
                    return;
                }
                for (TaskOperatingResult taskOperatingResult : taskOperatingResults) {
                    try {
                        taskOperatingResult.setStatus(TaskOperatingStatus.OPERATING_BEFORE);
                        consumer.accept(taskOperatingResult);
                    } catch (Throwable e) {
                        exceptionDealWith(taskOperatingResult, e);
                    }
                }
            } finally {
                this.threadStatus.set(false);
            }
        }

        private void exceptionDealWith(TaskOperatingResult taskOperatingResult, Throwable e) {
            taskOperatingResult.setStatus(TaskOperatingStatus.EXCEPTION);
            taskOperatingResult.setCode(CodeEnum.EXCEPTION.getCode());
            taskOperatingResult.setMessage(ExceptionUtil.stacktraceToString(e));
            LOGGER.error(
                    "[{}],  taskId={}, taskName={}, operating exception",
                    threadName,
                    taskOperatingResult.getTask().getId(),
                    taskOperatingResult.getTask().getName(),
                    e);
        }
    }
}
