package com.dlink.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.dlink.common.result.Result;
import com.dlink.context.SpringContextUtils;
import com.dlink.model.CodeEnum;
import com.dlink.model.JobLifeCycle;
import com.dlink.model.JobStatus;
import com.dlink.model.Task;
import com.dlink.model.TaskOperatingStatus;
import com.dlink.result.TaskOperatingResult;
import com.dlink.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author mydq
 * @version 1.0
 * @date 2022/7/16 20:26
 **/
public class TaskOneClickOperatingUtil {

    private static List<TaskOperatingResult> oneClickOnlineCache = new ArrayList<>(0);

    private static List<TaskOperatingResult> oneClickOfflineCache = new ArrayList<>(0);

    private final static AtomicBoolean oneClickOnlineThreadStatus = new AtomicBoolean(false);

    private final static AtomicBoolean oneClickOfflineThreadStatus = new AtomicBoolean(false);

    public static synchronized Result oneClickOnline(List<Task> tasks, boolean selectLatestSavepoint) {
        if (oneClickOnlineThreadStatus.get() || oneClickOfflineThreadStatus.get()) {
            return Result.failed("存在一键上线或者下线操作，请稍后重试");
        }
        final TaskService taskService = SpringContextUtils.getBeanByClass(TaskService.class);
        if (CollectionUtils.isEmpty(tasks)) {
            final Result<List<Task>> listResult = taskService.queryOnLineTaskByDoneStatus(Arrays.asList(JobLifeCycle.RELEASE), JobStatus.getAllDoneStatus(), true, 0);
            if (CollectionUtils.isEmpty(listResult.getDatas())) {
                return Result.succeed("没有需要上线的任务");
            }
            tasks = listResult.getDatas();
        }
        oneClickOnlineCache = tasks.stream().map(task -> new TaskOperatingResult(task, selectLatestSavepoint)).collect(Collectors.toList());
        new OneClickOperatingThread("oneClickOnlineThread", oneClickOnlineCache, oneClickOnlineThreadStatus, taskService::selectSavepointOnLineTask).start();
        return Result.succeed("success");
    }

    public static synchronized Result onClickOffline(List<Task> tasks) {
        if (oneClickOnlineThreadStatus.get() || oneClickOfflineThreadStatus.get()) {
            return Result.failed("存在一键上线或者下线操作，请稍后重试");
        }
        final TaskService taskService = SpringContextUtils.getBeanByClass(TaskService.class);
        if (CollectionUtils.isEmpty(tasks)) {
            final Result<List<Task>> listResult = taskService.queryOnLineTaskByDoneStatus(Arrays.asList(JobLifeCycle.ONLINE), Collections.singletonList(JobStatus.RUNNING), false, 0);
            if (CollectionUtils.isEmpty(listResult.getDatas())) {
                return Result.succeed("没有需要下线的任务");
            }
            tasks = listResult.getDatas();
        }
        oneClickOfflineCache = tasks.stream().map(TaskOperatingResult::new).collect(Collectors.toList());
        new OneClickOperatingThread("oneClickOfflineThread", oneClickOfflineCache, oneClickOfflineThreadStatus, taskService::selectSavepointOffLineTask).start();
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

        public OneClickOperatingThread(String threadName, List<TaskOperatingResult> taskOperatingResults, AtomicBoolean threadStatus, Consumer<TaskOperatingResult> consumer) {
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
            LOGGER.error("[{}],  taskId={}, taskName={}, operating exception", threadName
                    , taskOperatingResult.getTask().getId(), taskOperatingResult.getTask().getName(), e);
        }


    }


}
