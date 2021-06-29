package com.dlink.job;

import cn.hutool.json.JSONUtil;
import com.dlink.context.SpringContextUtils;
import com.dlink.model.History;
import com.dlink.service.HistoryService;
import org.springframework.context.annotation.DependsOn;

/**
 * Job2MysqlHandler
 *
 * @author wenmo
 * @since 2021/6/27 0:04
 */
@DependsOn("springContextUtils")
public class Job2MysqlHandler implements JobHandler {

    private static HistoryService historyService;

    static {
        historyService = SpringContextUtils.getBean("historyServiceImpl",HistoryService.class);
    }

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
        history.setTaskId(job.getJobConfig().getTaskId());
        history.setConfig(JSONUtil.toJsonStr(job.getJobConfig()));
        historyService.save(history);
        job.setId(history.getId());
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
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setId(job.getId());
        history.setJobId(job.getJobId());
        history.setStatus(job.getStatus().ordinal());
        history.setEndTime(job.getEndTime());
        history.setResult(JSONUtil.toJsonStr(job.getResult()));
        historyService.updateById(history);
        return true;
    }

    @Override
    public boolean failed() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setId(job.getId());
        history.setJobId(job.getJobId());
        history.setStatus(job.getStatus().ordinal());
        history.setEndTime(job.getEndTime());
        history.setError(job.getError());
        historyService.updateById(history);
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
