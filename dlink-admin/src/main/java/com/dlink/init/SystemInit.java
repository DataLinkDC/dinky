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

package com.dlink.init;

import com.dlink.assertion.Asserts;
import com.dlink.daemon.task.DaemonFactory;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.job.FlinkJobTask;
import com.dlink.model.JobInstance;
import com.dlink.scheduler.client.ProjectClient;
import com.dlink.scheduler.config.DolphinSchedulerProperties;
import com.dlink.scheduler.exception.SchedulerException;
import com.dlink.scheduler.model.Project;
import com.dlink.service.JobInstanceService;
import com.dlink.service.SysConfigService;
import com.dlink.service.TaskService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SystemInit
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Component
@Order(value = 1)
public class SystemInit implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemInit.class);
    @Autowired
    private ProjectClient projectClient;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private JobInstanceService jobInstanceService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DolphinSchedulerProperties dolphinSchedulerProperties;
    private static Project project;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sysConfigService.initSysConfig();
        taskService.initDefaultFlinkSQLEnv();
        initTaskMonitor();
        initDolphinScheduler();
    }

    /**
     * init task monitor
     */
    private void initTaskMonitor() {
        List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
        List<DaemonTaskConfig> configList = new ArrayList<>();
        for (JobInstance jobInstance : jobInstances) {
            configList.add(new DaemonTaskConfig(FlinkJobTask.TYPE, jobInstance.getId()));
        }
        log.info("Number of tasks started: " + configList.size());
        DaemonFactory.start(configList);
    }

    /**
     * init DolphinScheduler
     */
    private void initDolphinScheduler() {
        if (dolphinSchedulerProperties.isEnabled()) {
            try {
                project = projectClient.getDinkyProject();
                if (Asserts.isNull(project)) {
                    project = projectClient.createDinkyProject();
                }
            } catch (Exception e) {
                log.error("Error in DolphinScheduler: {}", e);
            }
        }
    }

    /**
     * get dolphinscheduler's project
     *
     * @return: com.dlink.scheduler.model.Project
     */
    public static Project getProject() {
        if (Asserts.isNull(project)) {
            throw new SchedulerException("Please complete the dolphinscheduler configuration.");
        }
        return project;
    }
}
