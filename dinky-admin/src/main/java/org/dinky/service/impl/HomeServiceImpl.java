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

import org.dinky.data.model.HomeResource;
import org.dinky.data.model.JobModelOverview;
import org.dinky.data.model.JobStatusOverView;
import org.dinky.data.model.JobTypeOverView;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertInstanceService;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.DataBaseService;
import org.dinky.service.FragmentVariableService;
import org.dinky.service.GitProjectService;
import org.dinky.service.HomeService;
import org.dinky.service.TaskService;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final ClusterInstanceService clusterInstanceService;
    private final ClusterConfigurationService clusterConfigurationService;
    private final DataBaseService dataBaseService;
    private final FragmentVariableService fragmentVariableService;
    private final GitProjectService gitProjectService;
    private final AlertGroupService alertGroupService;
    private final AlertInstanceService alertInstanceService;
    private final TaskService taskService;

    @Override
    public HomeResource getResourceOverview() {
        HomeResource homeResource = new HomeResource();
        homeResource.setFlinkClusterCount(clusterInstanceService.list().size());
        homeResource.setFlinkConfigCount(clusterConfigurationService.list().size());
        homeResource.setDbSourceCount(dataBaseService.list().size());
        homeResource.setGlobalVarCount(fragmentVariableService.list().size());
        homeResource.setGitProjectCount(gitProjectService.list().size());
        homeResource.setAlertGroupCount(alertGroupService.list().size());
        homeResource.setAlertInstanceCount(alertInstanceService.list().size());
        return homeResource;
    }

    @Override
    public JobStatusOverView getJobStatusOverView() {
        JobStatusOverView jobStatusOverView = new JobStatusOverView();
        // todo: query job status
        return jobStatusOverView;
    }

    @Override
    public List<JobTypeOverView> getJobTypeOverView() {
        return taskService.getTaskOnlineRate();
    }

    @Override
    public JobModelOverview getJobModelOverview() {
        return taskService.getJobStreamingOrBatchModelOverview();
    }
}
