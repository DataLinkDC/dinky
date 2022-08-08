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

import com.dlink.context.RequestContext;
import com.dlink.daemon.task.DaemonFactory;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.job.FlinkJobTask;
import com.dlink.model.JobInstance;
import com.dlink.model.Tenant;
import com.dlink.service.JobInstanceService;
import com.dlink.service.SysConfigService;
import com.dlink.service.TaskService;
import com.dlink.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    private SysConfigService sysConfigService;
    @Autowired
    private JobInstanceService jobInstanceService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TenantService tenantService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sysConfigService.initSysConfig();
//        taskService.initDefaultFlinkSQLEnv(1);
//        List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
//        List<DaemonTaskConfig> configList = new ArrayList<>();
//        for (JobInstance jobInstance : jobInstances) {
//            configList.add(new DaemonTaskConfig(FlinkJobTask.TYPE, jobInstance.getId()));
//        }
        List<Tenant> tenants = tenantService.list();
        List<DaemonTaskConfig> configList = new ArrayList<>();
        sysConfigService.initSysConfig();
        for (Tenant tenant : tenants) {
            RequestContext.set(tenant.getId());
            taskService.initDefaultFlinkSQLEnv(tenant.getId());
            List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
            for (JobInstance jobInstance : jobInstances) {
                configList.add(new DaemonTaskConfig(FlinkJobTask.TYPE, jobInstance.getId()));
            }
        }
        log.info("启动的任务数量:" + configList.size());
        DaemonFactory.start(configList);
    }
}
