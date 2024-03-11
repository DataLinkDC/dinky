package org.dinky.executor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableResult;
import org.dinky.classloader.DinkyClassLoader;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.SqlExplainResult;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Executor {
    // return dinkyClassLoader
    DinkyClassLoader getDinkyClassLoader();

    VariableManager getVariableManager();

    boolean isUseSqlFragment();

    ExecutionConfig getExecutionConfig();

    StreamExecutionEnvironment getStreamExecutionEnvironment();

    void setStreamExecutionEnvironment(StreamExecutionEnvironment environment);

    CustomTableEnvironment getCustomTableEnvironment();

    ExecutorConfig getExecutorConfig();

    Map<String, String> getSetConfig();

    TableConfig getTableConfig();

    String getTimeZone();

    String pretreatStatement(String statement);

    JobExecutionResult execute(String jobName) throws Exception;

    JobClient executeAsync(String jobName) throws Exception;

    TableResult executeSql(String statement);

    void initUDF(String... udfFilePath);

    void initPyUDF(String executable, String... udfPyFilePath);

    void addJar(File... jarPath);

    SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails);

    ObjectNode getStreamGraph(List<String> statements);

    StreamGraph getStreamGraph();

    ObjectNode getStreamGraphFromDataStream(List<String> statements);

    JobPlanInfo getJobPlanInfo(List<String> statements);

    JobPlanInfo getJobPlanInfoFromDataStream(List<String> statements);

    JobGraph getJobGraphFromInserts(List<String> statements);

    TableResult executeStatementSet(List<String> statements);

    String explainStatementSet(List<String> statements);

    List<LineageRel> getLineage(String statement);
}
