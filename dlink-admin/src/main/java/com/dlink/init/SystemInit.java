package com.dlink.init;

import com.dlink.daemon.task.DaemonFactory;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.job.FlinkJobTask;
import com.dlink.model.JobInstance;
import com.dlink.service.JobInstanceService;
import com.dlink.service.SysConfigService;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sysConfigService.initSysConfig();
        List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
        List<DaemonTaskConfig> configList = new ArrayList<>();
        for (JobInstance jobInstance : jobInstances) {
            configList.add(new DaemonTaskConfig(FlinkJobTask.TYPE, jobInstance.getId()));
        }
        log.info("启动的任务数量:" + configList.size());
        DaemonFactory.start(configList);
    }
}
