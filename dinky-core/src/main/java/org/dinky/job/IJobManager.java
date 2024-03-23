package org.dinky.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;

public interface IJobManager {
    void init(JobConfig config, boolean isPlanMode);

    void prepare(String statement);

    boolean close();

    ObjectNode getJarStreamGraphJson(String statement);

    JobResult executeJarSql(String statement) throws Exception;

    JobResult executeSql(String statement) throws Exception;

    IResult executeDDL(String statement);

    ExplainResult explainSql(String statement);

    ObjectNode getStreamGraph(String statement);

    String getJobPlanJson(String statement);

    boolean cancelNormal(String jobId);

    SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint);

    String exportSql(String sql);
}
