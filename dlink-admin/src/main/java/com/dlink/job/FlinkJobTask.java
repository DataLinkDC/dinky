package com.dlink.job;

import com.dlink.assertion.Asserts;
import com.dlink.context.SpringContextUtils;
import com.dlink.daemon.constant.FlinkTaskConstant;
import com.dlink.daemon.pool.DefaultThreadPool;
import com.dlink.daemon.task.DaemonTask;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.model.JobInstance;
import com.dlink.model.JobStatus;
import com.dlink.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;

import java.time.Duration;
import java.time.LocalDateTime;

@DependsOn("springContextUtils")
public class FlinkJobTask implements DaemonTask {

    private static final Logger log = LoggerFactory.getLogger(FlinkJobTask.class);

    private DaemonTaskConfig config;
    public final static String TYPE = "jobInstance";
    private static TaskService taskService;
    private long preDealTime;

    static {
        taskService = SpringContextUtils.getBean("taskServiceImpl", TaskService.class);
    }

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void dealTask() {
        long gap = System.currentTimeMillis() - this.preDealTime;
        if (gap < FlinkTaskConstant.TIME_SLEEP) {
            try {
                Thread.sleep(FlinkTaskConstant.TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        preDealTime = System.currentTimeMillis();
        JobInstance jobInstance = taskService.refreshJobInstance(config.getId(), false);
        if ((!JobStatus.isDone(jobInstance.getStatus())) || (Asserts.isNotNull(jobInstance.getFinishTime())
                && Duration.between(jobInstance.getFinishTime(), LocalDateTime.now()).toMinutes() < 1)) {
            DefaultThreadPool.getInstance().execute(this);
        }
    }
}
