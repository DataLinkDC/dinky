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

package org.dinky.service.impl;

import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.init.SystemInit;
import org.dinky.scheduler.client.ProcessClient;
import org.dinky.scheduler.client.TaskClient;
import org.dinky.scheduler.enums.ReleaseState;
import org.dinky.scheduler.exception.SchedulerException;
import org.dinky.scheduler.model.DagData;
import org.dinky.scheduler.model.DagNodeLocation;
import org.dinky.scheduler.model.DinkyTaskParams;
import org.dinky.scheduler.model.DinkyTaskRequest;
import org.dinky.scheduler.model.ProcessDefinition;
import org.dinky.scheduler.model.ProcessTaskRelation;
import org.dinky.scheduler.model.Project;
import org.dinky.scheduler.model.TaskDefinition;
import org.dinky.scheduler.model.TaskMainInfo;
import org.dinky.scheduler.model.TaskRequest;
import org.dinky.service.CatalogueService;
import org.dinky.service.SchedulerService;
import org.dinky.utils.JsonUtils;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Strings;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    public static final String TASK_TYPE = "DINKY";
    private final ProcessClient processClient;
    private final TaskClient taskClient;
    private final CatalogueService catalogueService;

    /**
     * Pushes the specified DinkyTaskRequest to the task queue.
     *
     * @param  dinkyTaskRequest  the DinkyTaskRequest to be added to the task queue
     * @return                  true if the task was successfully added, false otherwise
     */
    @Override
    public boolean pushAddTask(DinkyTaskRequest dinkyTaskRequest) {
        // Use root catalog as process (workflow) name.
        Catalogue catalogue = catalogueService.getOne(
                new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskRequest.getTaskId()));
        if (catalogue == null) {
            log.error(Status.DS_GET_NODE_LIST_ERROR.getMessage());
            throw new BusException(Status.DS_GET_NODE_LIST_ERROR);
        }

        DinkyTaskParams dinkyTaskParams = new DinkyTaskParams();
        dinkyTaskParams.setTaskId(dinkyTaskRequest.getTaskId());
        dinkyTaskParams.setAddress(
                SystemConfiguration.getInstances().getDinkyAddr().getValue());
        dinkyTaskRequest.setTaskParams(JSONUtil.parseObj(dinkyTaskParams).toString());
        dinkyTaskRequest.setTaskType(TASK_TYPE);

        String processName = getDinkyNames(catalogue, 0);
        long projectCode = SystemInit.getProject().getCode();
        // Get process from dolphin scheduler
        ProcessDefinition process = processClient.getProcessDefinitionInfo(projectCode, processName);

        String taskName = catalogue.getName() + ":" + catalogue.getId();
        dinkyTaskRequest.setName(taskName);

        TaskRequest taskRequest = new TaskRequest();
        Long taskCode = taskClient.genTaskCode(projectCode);

        // If the process does not exist, a process needs to be created.
        if (process == null) {
            dinkyTaskRequest.setCode(taskCode);
            BeanUtil.copyProperties(dinkyTaskRequest, taskRequest);
            taskRequest.setTimeoutFlag(dinkyTaskRequest.getTimeoutFlag());
            taskRequest.setFlag(dinkyTaskRequest.getFlag());
            taskRequest.setIsCache(dinkyTaskRequest.getIsCache());
            JSONObject jsonObject = JSONUtil.parseObj(taskRequest);
            JSONArray taskArray = new JSONArray();
            taskArray.set(jsonObject);
            log.info(Status.DS_ADD_WORK_FLOW_DEFINITION_SUCCESS.getMessage());

            DagNodeLocation dagNodeLocation = new DagNodeLocation();
            dagNodeLocation.setTaskCode(taskCode);
            dagNodeLocation.setX(RandomUtil.randomLong(200, 500));
            dagNodeLocation.setY(RandomUtil.randomLong(100, 400));
            log.info("DagNodeLocation Info: {}", dagNodeLocation);

            ProcessTaskRelation processTaskRelation = ProcessTaskRelation.generateProcessTaskRelation(taskCode);
            JSONObject processTaskRelationJSONObject = JSONUtil.parseObj(processTaskRelation);
            JSONArray taskRelationArray = new JSONArray();
            taskRelationArray.set(processTaskRelationJSONObject);

            processClient.createOrUpdateProcessDefinition(
                    projectCode,
                    null,
                    processName,
                    taskCode,
                    taskRelationArray.toString(),
                    taskArray.toString(),
                    Arrays.asList(dagNodeLocation),
                    false);
            return true;
        }

        // If the workflow is in an online state, it cannot be updated.
        if (process.getReleaseState() == ReleaseState.ONLINE) {
            log.error(Status.DS_WORK_FLOW_DEFINITION_ONLINE.getMessage(), processName);
        }

        TaskMainInfo taskMainInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName, TASK_TYPE);
        // If task name exist, update task definition.
        if (taskMainInfo != null) {
            log.warn(Status.DS_WORK_FLOW_DEFINITION_TASK_NAME_EXIST.getMessage(), processName, taskName);
            return pushUpdateTask(
                    projectCode, taskMainInfo.getProcessDefinitionCode(), taskMainInfo.getTaskCode(), dinkyTaskRequest);
        }
        // If the task does not exist, a dinky task needs to be created.
        dinkyTaskRequest.setCode(taskCode);
        BeanUtil.copyProperties(dinkyTaskRequest, taskRequest);
        taskRequest.setTimeoutFlag(dinkyTaskRequest.getTimeoutFlag());
        taskRequest.setFlag(dinkyTaskRequest.getFlag());
        taskRequest.setIsCache(dinkyTaskRequest.getIsCache());

        String taskDefinitionJsonObj = JSONUtil.toJsonStr(taskRequest);
        taskClient.createTaskDefinition(
                projectCode, process.getCode(), dinkyTaskRequest.getUpstreamCodes(), taskDefinitionJsonObj);
        // update the location of process
        updateProcessDefinition(process, taskCode, taskRequest, projectCode);

        log.info(Status.DS_ADD_TASK_DEFINITION_SUCCESS.getMessage());
        return true;
    }

    private void updateProcessDefinition(ProcessDefinition process, Long taskCode, TaskRequest task, long projectCode) {

        DagData dagData = processClient.getProcessDefinitionInfo(projectCode, process.getCode());
        if (dagData == null) {
            log.error(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST.getMessage());
            throw new BusException(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST);
        }
        List<ProcessTaskRelation> processTaskRelationList = dagData.getProcessTaskRelationList();
        List<TaskDefinition> taskDefinitionList = dagData.getTaskDefinitionList();
        List<DagNodeLocation> locations = process.getLocations();

        if (CollUtil.isNotEmpty(process.getLocations())) {
            boolean matched = process.getLocations().stream().anyMatch(location -> location.getTaskCode() == taskCode);
            // if not matched, add a new location
            if (!matched) {
                // 获取最大的 x y 坐标
                long xMax = process.getLocations().stream()
                        .mapToLong(DagNodeLocation::getX)
                        .max()
                        .getAsLong();
                long xMin = process.getLocations().stream()
                        .mapToLong(DagNodeLocation::getX)
                        .min()
                        .getAsLong();
                long yMax = process.getLocations().stream()
                        .mapToLong(DagNodeLocation::getY)
                        .max()
                        .getAsLong();
                long yMin = process.getLocations().stream()
                        .mapToLong(DagNodeLocation::getY)
                        .min()
                        .getAsLong();
                // 随机出一个 x y 坐标
                DagNodeLocation dagNodeLocation = new DagNodeLocation();
                dagNodeLocation.setTaskCode(taskCode);
                dagNodeLocation.setX(RandomUtil.randomLong(xMin == xMax ? 0 : xMin, xMax));
                dagNodeLocation.setY(RandomUtil.randomLong(yMin == yMax ? 0 : yMin, yMax));
                locations = process.getLocations();
                locations.add(dagNodeLocation);
            }
        } else {
            // 随机出一个 x y 坐标
            DagNodeLocation dagNodeLocation = new DagNodeLocation();
            dagNodeLocation.setTaskCode(taskCode);
            dagNodeLocation.setX(RandomUtil.randomLong(200, 500));
            dagNodeLocation.setY(RandomUtil.randomLong(100, 400));
            locations.add(dagNodeLocation);
        }

        JSONArray taskArray = new JSONArray();
        taskDefinitionList.removeIf(taskDefinition -> (task.getName()).equalsIgnoreCase(taskDefinition.getName()));

        taskArray.addAll(taskDefinitionList);
        taskArray.add(task);
        String processTaskRelationListJson = JsonUtils.toJsonString(processTaskRelationList);

        processClient.createOrUpdateProcessDefinition(
                projectCode,
                process.getCode(),
                process.getName(),
                taskCode,
                processTaskRelationListJson,
                taskArray.toString(),
                locations,
                true);
        log.info(
                Status.DS_PROCESS_DEFINITION_UPDATE.getMessage(),
                process.getName(),
                taskCode,
                taskArray.toString(),
                locations);
    }

    /**
     * Pushes an update task to the API.
     *
     * @param  projectCode           the project code
     * @param  processCode           the process code
     * @param  taskCode              the task code
     * @param  dinkyTaskRequest      the DinkyTaskRequest object containing task details
     * @return                       true if the task is successfully updated, false otherwise
     */
    @Override
    public boolean pushUpdateTask(
            long projectCode, long processCode, long taskCode, DinkyTaskRequest dinkyTaskRequest) {
        TaskDefinition taskDefinition = taskClient.getTaskDefinition(projectCode, taskCode);
        if (taskDefinition == null) {
            log.error(Status.DS_TASK_NOT_EXIST.getMessage());
            throw new BusException(Status.DS_TASK_NOT_EXIST);
        }

        if (!TASK_TYPE.equals(taskDefinition.getTaskType())) {
            log.error(Status.DS_TASK_TYPE_NOT_SUPPORT.getMessage(), taskDefinition.getTaskType());
            throw new BusException(Status.DS_TASK_TYPE_NOT_SUPPORT, taskDefinition.getTaskType());
        }

        DagData dagData = processClient.getProcessDefinitionInfo(projectCode, processCode);
        if (dagData == null) {
            log.error(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST.getMessage());
            throw new BusException(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST);
        }

        ProcessDefinition process = dagData.getProcessDefinition();
        if (process == null) {
            log.error(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST.getMessage());
            throw new BusException(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST);
        }

        if (process.getReleaseState() == ReleaseState.ONLINE) {
            log.error(Status.DS_WORK_FLOW_DEFINITION_ONLINE.getMessage(), process.getName());
            throw new BusException(Status.DS_WORK_FLOW_DEFINITION_ONLINE, process.getName());
        }
        TaskRequest taskRequest = new TaskRequest();

        dinkyTaskRequest.setName(taskDefinition.getName());
        dinkyTaskRequest.setTaskParams(taskDefinition.getTaskParams());
        dinkyTaskRequest.setTaskType(TASK_TYPE);
        BeanUtil.copyProperties(dinkyTaskRequest, taskRequest);
        taskRequest.setTimeoutFlag(dinkyTaskRequest.getTimeoutFlag());
        taskRequest.setFlag(dinkyTaskRequest.getFlag());
        taskRequest.setIsCache(dinkyTaskRequest.getIsCache());

        String taskDefinitionJsonObj = JSONUtil.toJsonStr(taskRequest);
        Long updatedTaskDefinition = taskClient.updateTaskDefinition(
                projectCode, taskCode, dinkyTaskRequest.getUpstreamCodes(), taskDefinitionJsonObj);

        updateProcessDefinition(process, taskCode, taskRequest, projectCode);
        if (updatedTaskDefinition != null && updatedTaskDefinition > 0) {
            log.info(Status.MODIFY_SUCCESS.getMessage());
            return true;
        }
        log.error(Status.MODIFY_FAILED.getMessage());
        return false;
    }

    /**
     * Retrieves the list of TaskMainInfo objects for a given dinkyTaskId.
     *
     * @param  dinkyTaskId   the id of the dinky task
     * @return               the list of TaskMainInfo objects
     */
    @Override
    public List<TaskMainInfo> getTaskMainInfos(long dinkyTaskId) {
        Catalogue catalogue =
                catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            log.error(Status.DS_GET_NODE_LIST_ERROR.getMessage());
            throw new BusException(Status.DS_GET_NODE_LIST_ERROR);
        }
        long projectCode = SystemInit.getProject().getCode();
        List<TaskMainInfo> taskMainInfos = taskClient.getTaskMainInfos(projectCode, "", "", "");
        // 去掉本身
        taskMainInfos.removeIf(taskMainInfo ->
                (catalogue.getName() + ":" + catalogue.getId()).equalsIgnoreCase(taskMainInfo.getTaskName()));
        return taskMainInfos;
    }

    /**
     * Retrieves the task definition information for a given dinkyTaskId.
     *
     * @param  dinkyTaskId   the ID of the dinky task
     * @return               the task definition information
     */
    @Override
    public TaskDefinition getTaskDefinitionInfo(long dinkyTaskId) {
        Catalogue catalogue =
                catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            log.error(Status.DS_GET_NODE_LIST_ERROR.getMessage());
            throw new BusException(Status.DS_GET_NODE_LIST_ERROR);
        }

        Project dinkyProject = SystemInit.getProject();
        long projectCode = dinkyProject.getCode();

        String processName = getDinkyNames(catalogue, 0);
        String taskName = catalogue.getName() + ":" + catalogue.getId();
        TaskMainInfo taskMainInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName, TASK_TYPE);
        TaskDefinition taskDefinition = null;
        if (taskMainInfo == null) {
            log.error(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST.getMessage(), processName, taskName);
            throw new BusException(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST, processName, taskName);
        }

        taskDefinition = taskClient.getTaskDefinition(projectCode, taskMainInfo.getTaskCode());
        if (taskDefinition == null) {
            log.error(Status.DS_WORK_FLOW_NOT_SAVE.getMessage());
            throw new BusException(Status.DS_WORK_FLOW_NOT_SAVE);
        }

        taskDefinition.setProcessDefinitionCode(taskMainInfo.getProcessDefinitionCode());
        taskDefinition.setProcessDefinitionName(taskMainInfo.getProcessDefinitionName());
        taskDefinition.setProcessDefinitionVersion(taskMainInfo.getProcessDefinitionVersion());
        taskDefinition.setUpstreamTaskMap(taskMainInfo.getUpstreamTaskMap());
        return taskDefinition;
    }

    /**
     * Retrieves the dinky names from the given catalogue and index.
     *
     * @param  catalogue    the catalogue object to retrieve the names from
     * @param  i            the index to start retrieving the names from
     * @return              the dinky names retrieved from the catalogue
     */
    private String getDinkyNames(Catalogue catalogue, int i) {
        if (i == 3 || catalogue.getParentId().equals(0)) {
            return "";
        }

        catalogue = catalogueService.getById(catalogue.getParentId());
        if (catalogue == null) {
            throw new SchedulerException("Get Node List Error");
        }

        String name = i == 0 ? catalogue.getName() + ":" + catalogue.getId() : catalogue.getName();
        String next = getDinkyNames(catalogue, ++i);

        if (Strings.isNullOrEmpty(next)) {
            return name;
        }
        return name + "_" + next;
    }
}
