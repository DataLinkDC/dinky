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

package org.dinky.init;

import org.dinky.assertion.Asserts;
import org.dinky.context.TenantContextHolder;
import org.dinky.daemon.task.DaemonFactory;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.Tenant;
import org.dinky.function.constant.PathConstant;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.job.FlinkJobTask;
import org.dinky.process.exception.DinkyException;
import org.dinky.scheduler.client.ProjectClient;
import org.dinky.scheduler.exception.SchedulerException;
import org.dinky.scheduler.model.Project;
import org.dinky.service.GitProjectService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.SysConfigService;
import org.dinky.service.TaskService;
import org.dinky.service.TenantService;
import org.dinky.utils.JSONUtil;
import org.dinky.utils.UDFUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * SystemInit
 *
 * @since 2021/11/18
 */
@Component
@Order(value = 1)
@RequiredArgsConstructor
public class SystemInit implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemInit.class);
    private final ProjectClient projectClient;
    private final SysConfigService sysConfigService;
    private final JobInstanceService jobInstanceService;
    private final TaskService taskService;
    private final TenantService tenantService;
    private final GitProjectService gitProjectService;
    private static Project project;

    @Override
    public void run(ApplicationArguments args) {
        List<Tenant> tenants = tenantService.list();
        sysConfigService.initSysConfig();
        for (Tenant tenant : tenants) {
            taskService.initDefaultFlinkSQLEnv(tenant.getId());
        }
        initTaskMonitor();
        initDolphinScheduler();
        registerUDF();
        updateGitBuildState();
    }

    /** init task monitor */
    private void initTaskMonitor() {
        List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
        List<DaemonTaskConfig> configList = new ArrayList<>();
        for (JobInstance jobInstance : jobInstances) {
            configList.add(new DaemonTaskConfig(FlinkJobTask.TYPE, jobInstance.getId()));
        }
        log.info("Number of tasks started: " + configList.size());
        DaemonFactory.start(configList);
    }

    /** init DolphinScheduler */
    private void initDolphinScheduler() {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        systemConfiguration.setInitMethod(
                c -> {
                    if (c == systemConfiguration.getDolphinschedulerEnable()) {
                        if (systemConfiguration.getDolphinschedulerEnable().getValue()) {
                            if (StrUtil.hasBlank(
                                    systemConfiguration.getDolphinschedulerUrl().getValue(),
                                    systemConfiguration.getDolphinschedulerProjectName().getValue(),
                                    systemConfiguration.getDolphinschedulerToken().getValue())) {
                                sysConfigService.updateSysConfigByKv(
                                        systemConfiguration.getDolphinschedulerEnable().getKey(),
                                        "false");
                                throw new DinkyException(
                                        "Before starting dolphinscheduler docking, please fill in the relevant configuration");
                            }
                            try {
                                project = projectClient.getDinkyProject();
                                if (Asserts.isNull(project)) {
                                    project = projectClient.createDinkyProject();
                                }
                            } catch (Exception e) {
                                log.error("Error in DolphinScheduler: ", e);
                                throw new DinkyException(e);
                            }
                        }
                    }
                });
    }

    /**
     * get dolphinscheduler's project
     *
     * @return {@link Project}
     */
    public static Project getProject() {
        if (Asserts.isNull(project)) {
            throw new SchedulerException("Please complete the dolphinscheduler configuration.");
        }
        return project;
    }

    public void registerUDF() {
        // 设置admin用户 ，获取全部的udf代码，此地方没有租户隔离
        TenantContextHolder.set(1);
        UdfCodePool.registerPool(
                taskService.getAllUDF().stream()
                        .map(UDFUtils::taskToUDF)
                        .collect(Collectors.toList()));
        UdfCodePool.updateGitPool(gitProjectService.getGitPool());
        TenantContextHolder.set(null);
    }

    public void updateGitBuildState() {
        String path = PathConstant.TMP_PATH + "/build.list";
        if (FileUtil.exist(path)) {
            List<Integer> runningList =
                    JSONUtil.toList(FileUtil.readUtf8String(path), Integer.class);
            gitProjectService.list().stream()
                    .filter(x -> x.getBuildState().equals(1))
                    .filter(x -> runningList.contains(x.getId()))
                    .peek(x -> x.setBuildState(2))
                    .forEach(Model::updateById);
            FileUtil.del(path);
        }
    }
}
