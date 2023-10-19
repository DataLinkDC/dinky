package org.dinky.service.task;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.dinky.config.Dialect;
import org.dinky.data.annotation.SupportDialect;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;
import org.dinky.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

@SupportDialect(Dialect.FLINK_JAR)
public class FlinkJarSql extends FlinkSqlTask {
    public FlinkJarSql(TaskDTO task) {
        super(task);
    }

    @Override
    public List<SqlExplainResult> explain() {
        return new ArrayList<>();
    }

    @Override
    public JobResult execute() throws Exception {
        JobManager jobManager = getJobManager();

        return jobManager.executeJarSql(task.getStatement());
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public ObjectNode getJobPlan() {
        try {
            StreamGraph streamGraph = getJobManager().getJarStreamGraph(task.getStatement());
            return JsonUtils.parseObject(JsonPlanGenerator.generatePlan(streamGraph.getJobGraph()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
