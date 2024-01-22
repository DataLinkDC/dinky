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
import org.dinky.daemon.pool.FlinkJobThreadPool;
import org.dinky.daemon.pool.ScheduleThreadPool;
import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.Configuration;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.Task;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.function.constant.PathConstant;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.job.ClearJobHistoryTask;
import org.dinky.job.FlinkJobTask;
import org.dinky.job.SystemMetricsTask;
import org.dinky.scheduler.client.ProjectClient;
import org.dinky.scheduler.exception.SchedulerException;
import org.dinky.scheduler.model.Project;
import org.dinky.service.GitProjectService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.SysConfigService;
import org.dinky.service.TaskService;
import org.dinky.service.TenantService;
import org.dinky.service.resource.BaseResourceManager;
import org.dinky.url.RsURLStreamHandlerFactory;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.UDFUtils;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SystemInit
 *
 * @since 2021/11/18
 */
@Component
@Order(value = 1)
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class SystemInit implements ApplicationRunner {
    private final SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();

    private final ProjectClient projectClient;
    private final SysConfigService sysConfigService;
    private final JobInstanceService jobInstanceService;
    private final TaskService taskService;
    private final TenantService tenantService;
    private final GitProjectService gitProjectService;
    private final ScheduleThreadPool schedule;

    private static Project project;

    @Override
    public void run(ApplicationArguments args) {
        TenantContextHolder.ignoreTenant();
        initResources();
        List<Tenant> tenants = tenantService.list();
        sysConfigService.initSysConfig();

        for (Tenant tenant : tenants) {
            taskService.initDefaultFlinkSQLEnv(tenant.getId());
        }
        initDaemon();
        initDolphinScheduler();
        registerUDF();
        updateGitBuildState();
        registerURL();
    }

    private void registerURL() {
        TomcatURLStreamHandlerFactory.getInstance().addUserFactory(new RsURLStreamHandlerFactory());
    }

    private void initResources() {
        CollUtil.newArrayList(
                        systemConfiguration.getResourcesEnable(),
                        systemConfiguration.getResourcesModel(),
                        systemConfiguration.getResourcesOssSecretKey(),
                        systemConfiguration.getResourcesOssEndpoint(),
                        systemConfiguration.getResourcesHdfsUser(),
                        systemConfiguration.getResourcesHdfsDefaultFS(),
                        systemConfiguration.getResourcesOssAccessKey(),
                        systemConfiguration.getResourcesOssRegion(),
                        systemConfiguration.getResourcesPathStyleAccess())
                .forEach(x -> x.addParameterCheck(y -> {
                    if (Boolean.TRUE.equals(
                            systemConfiguration.getResourcesEnable().getValue())) {
                        try {
                            BaseResourceManager.initResourceManager();
                        } catch (Exception e) {
                            log.error("Init resource error: ", e);
                        }
                    }
                }));
    }

    /**
     * init task monitor
     */
    private void initDaemon() {
        SystemConfiguration sysConfig = SystemConfiguration.getInstances();

        // Init system metrics task
        DaemonTask sysMetricsTask = DaemonTask.build(new DaemonTaskConfig(SystemMetricsTask.TYPE));
        Configuration<Boolean> metricsSysEnable = sysConfig.getMetricsSysEnable();
        Configuration<Integer> sysGatherTiming = sysConfig.getMetricsSysGatherTiming();
        Consumer<Configuration<?>> metricsListener = c -> {
            c.addChangeEvent(x -> {
                schedule.removeSchedule(sysMetricsTask);
                PeriodicTrigger trigger = new PeriodicTrigger(sysGatherTiming.getValue());
                if (metricsSysEnable.getValue()) schedule.addSchedule(sysMetricsTask, trigger);
            });
        };
        metricsListener.accept(metricsSysEnable);
        metricsListener.accept(sysGatherTiming);
        metricsSysEnable.runChangeEvent();

        // Init clear job history task
        DaemonTask clearJobHistoryTask = DaemonTask.build(new DaemonTaskConfig(ClearJobHistoryTask.TYPE));
        schedule.addSchedule(clearJobHistoryTask, new PeriodicTrigger(1, TimeUnit.HOURS));

        // Add flink running job task to flink job thread pool
        List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
        FlinkJobThreadPool flinkJobThreadPool = FlinkJobThreadPool.getInstance();
        for (JobInstance jobInstance : jobInstances) {
            DaemonTaskConfig config = new DaemonTaskConfig(FlinkJobTask.TYPE, jobInstance.getId());
            DaemonTask daemonTask = DaemonTask.build(config);
            flinkJobThreadPool.execute(daemonTask);
        }
    }

    /**
     * init DolphinScheduler
     */
    private void initDolphinScheduler() {
        List<Configuration<?>> configurationList =
                systemConfiguration.getAllConfiguration().get("dolphinscheduler");
        configurationList.forEach(c -> c.addParameterCheck(this::aboutDolphinSchedulerInitOperation));
        // init call for once
        aboutDolphinSchedulerInitOperation("init");
    }

    private void aboutDolphinSchedulerInitOperation(Object v) {
        if (Boolean.TRUE.equals(systemConfiguration.getDolphinschedulerEnable().getValue())) {
            if (StrUtil.isEmpty(Convert.toStr(v))) {
                sysConfigService.updateSysConfigByKv(
                        systemConfiguration.getDolphinschedulerEnable().getKey(), "false");
                throw new DinkyException("Before starting DolphinScheduler"
                        + " docking, please fill in the"
                        + " relevant configuration");
            }
            try {
                project = projectClient.getDinkyProject();
                if (project == null) {
                    project = projectClient.createDinkyProject();
                }
            } catch (Exception e) {
                log.warn("Get or create DolphinScheduler project failed, please check the config of DolphinScheduler!");
            }
        }
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
        List<Task> allUDF = taskService.getAllUDF();
        if (CollUtil.isNotEmpty(allUDF)) {
            UdfCodePool.registerPool(allUDF.stream().map(UDFUtils::taskToUDF).collect(Collectors.toList()));
        }
        UdfCodePool.updateGitPool(gitProjectService.getGitPool());
    }

    public void updateGitBuildState() {
        String path = PathConstant.TMP_PATH + "/build.list";
        if (FileUtil.exist(path)) {
            List<Integer> runningList = JsonUtils.toList(FileUtil.readUtf8String(path), Integer.class);
            gitProjectService.list().stream()
                    .filter(x -> x.getBuildState().equals(1))
                    .filter(x -> runningList.contains(x.getId()))
                    .peek(x -> x.setBuildState(2))
                    .forEach(Model::updateById);
            FileUtil.del(path);
        }
    }
}
