package org.dinky.service.task;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.dinky.config.Dialect;
import org.dinky.data.annotation.SupportDialect;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;
import org.dinky.service.impl.TaskServiceImpl;
import org.dinky.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Slf4j
@SupportDialect(Dialect.FLINK_SQL)
public class FlinkSqlTask extends BaseTask {
    public FlinkSqlTask(TaskDTO task) {
        super(task);
    }

    @Override
    public List<SqlExplainResult> explain()  {
        return getJobManager().explainSql(task.getStatement()).getSqlExplainResults();
    }

    public ObjectNode getJobPlan(){
        String planJson = getJobManager().getJobPlanJson(task.getStatement());
        return JsonUtils.parseObject(planJson);
    }

    @Override
    public JobResult execute() throws Exception {
        log.info("Initializing Flink job config...");
        return getJobManager().executeSql(task.getStatement());
    }

    @NotNull
    protected JobManager getJobManager() {
        TaskServiceImpl taskService = SpringUtil.getBean(TaskServiceImpl.class);
        return JobManager.build(
                taskService.buildJobConfig(task));
    }

    @Override
    public boolean stop() {
        return false;
    }


}
