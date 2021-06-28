package com.dlink.job;

import cn.hutool.extra.spring.SpringUtil;
import com.dlink.model.History;
import com.dlink.service.HistoryService;

/**
 * Job2MysqlHandler
 *
 * @author wenmo
 * @since 2021/6/27 0:04
 */
public class Job2MysqlHandler implements JobHandler {

    @Override
    public boolean init() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setClusterId(job.getJobConfig().getClusterId());
        history.setJobManagerAddress(job.getJobManagerAddress());
        history.setJobName(job.getJobConfig().getJobName());
        history.setSession(job.getJobConfig().getSession());
        history.setStatus(job.getStatus().ordinal());
        history.setStartTime(job.getStartTime());
        history.setType(job.getType().ordinal());
        history.setTaskId(job.getJobConfig().getTaskId());
        HistoryService historyService = SpringUtil.getBean(HistoryService.class);
        historyService.save(history);
        return true;
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public boolean running() {
        return true;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public boolean failed() {
        return true;
    }

    @Override
    public boolean callback() {
        return true;
    }

    @Override
    public boolean close() {
        return true;
    }
}
