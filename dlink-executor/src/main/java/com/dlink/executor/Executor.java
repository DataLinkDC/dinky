package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.interceptor.FlinkInterceptorResult;
import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.CatalogManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executor
 *
 * @author wenmo
 * @since 2021/11/17
 **/
public abstract class Executor {

    protected StreamExecutionEnvironment environment;
    protected CustomTableEnvironment stEnvironment;
    protected EnvironmentSetting environmentSetting;
    protected ExecutorSetting executorSetting;
    protected Map<String, Object> setConfig = new HashMap<>();

    protected SqlManager sqlManager = new SqlManager();
    protected boolean useSqlFragment = true;

    public SqlManager getSqlManager() {
        return sqlManager;
    }

    public boolean isUseSqlFragment() {
        return useSqlFragment;
    }

    public static Executor build() {
        return new LocalStreamExecutor(ExecutorSetting.DEFAULT);
    }

    public static Executor build(EnvironmentSetting environmentSetting, ExecutorSetting executorSetting) {
        if (environmentSetting.isUseRemote()) {
            return buildRemoteExecutor(environmentSetting, executorSetting);
        } else {
            return buildLocalExecutor(executorSetting);
        }
    }

    public static Executor buildLocalExecutor(ExecutorSetting executorSetting) {
        if (executorSetting.isUseBatchModel()) {
            return new LocalBatchExecutor(executorSetting);
        } else {
            return new LocalStreamExecutor(executorSetting);
        }
    }

    public static Executor buildAppStreamExecutor(ExecutorSetting executorSetting) {
        if (executorSetting.isUseBatchModel()) {
            return new AppBatchExecutor(executorSetting);
        } else {
            return new AppStreamExecutor(executorSetting);
        }
    }

    public static Executor buildRemoteExecutor(EnvironmentSetting environmentSetting, ExecutorSetting executorSetting) {
        environmentSetting.setUseRemote(true);
        if (executorSetting.isUseBatchModel()) {
            return new RemoteBatchExecutor(environmentSetting, executorSetting);
        } else {
            return new RemoteStreamExecutor(environmentSetting, executorSetting);
        }
    }

    public ExecutionConfig getExecutionConfig() {
        return environment.getConfig();
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return environment;
    }

    public CustomTableEnvironment getCustomTableEnvironment() {
        return stEnvironment;
    }

    public ExecutorSetting getExecutorSetting() {
        return executorSetting;
    }

    public EnvironmentSetting getEnvironmentSetting() {
        return environmentSetting;
    }

    public Map<String, Object> getSetConfig() {
        return setConfig;
    }

    public void setSetConfig(Map<String, Object> setConfig) {
        this.setConfig = setConfig;
    }

    protected void init() {
        initEnvironment();
        initStreamExecutionEnvironment();
    }

    public void update(ExecutorSetting executorSetting) {
        updateEnvironment(executorSetting);
        updateStreamExecutionEnvironment(executorSetting);
    }

    public void initEnvironment() {
        /*if (executorSetting.getCheckpoint() != null && executorSetting.getCheckpoint() > 0) {
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }*/
        if (executorSetting.getParallelism() != null && executorSetting.getParallelism() > 0) {
            environment.setParallelism(executorSetting.getParallelism());
        }
        if (executorSetting.getConfig() != null) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            environment.getConfig().configure(configuration, null);
        }
    }

    public void updateEnvironment(ExecutorSetting executorSetting) {
        /*if (executorSetting.getCheckpoint() != null && executorSetting.getCheckpoint() > 0) {
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }*/
        if (executorSetting.getParallelism() != null && executorSetting.getParallelism() > 0) {
            environment.setParallelism(executorSetting.getParallelism());
        }
        if (executorSetting.getConfig() != null) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            environment.getConfig().configure(configuration, null);
        }
    }

    abstract CustomTableEnvironment createCustomTableEnvironment();

    private void initStreamExecutionEnvironment() {
        useSqlFragment = executorSetting.isUseSqlFragment();
        stEnvironment = createCustomTableEnvironment();
        if (executorSetting.getJobName() != null && !"".equals(executorSetting.getJobName())) {
            stEnvironment.getConfig().getConfiguration().setString(PipelineOptions.NAME.key(), executorSetting.getJobName());
        }
        setConfig.put(PipelineOptions.NAME.key(), executorSetting.getJobName());
        if (executorSetting.getConfig() != null) {
            for (Map.Entry<String, String> entry : executorSetting.getConfig().entrySet()) {
                stEnvironment.getConfig().getConfiguration().setString(entry.getKey(), entry.getValue());
            }
        }
    }

    private void updateStreamExecutionEnvironment(ExecutorSetting executorSetting) {
        useSqlFragment = executorSetting.isUseSqlFragment();
        copyCatalog();
        if (executorSetting.getJobName() != null && !"".equals(executorSetting.getJobName())) {
            stEnvironment.getConfig().getConfiguration().setString(PipelineOptions.NAME.key(), executorSetting.getJobName());
        }
        setConfig.put(PipelineOptions.NAME.key(), executorSetting.getJobName());
        if (executorSetting.getConfig() != null) {
            for (Map.Entry<String, String> entry : executorSetting.getConfig().entrySet()) {
                stEnvironment.getConfig().getConfiguration().setString(entry.getKey(), entry.getValue());
            }
        }
    }

    private void copyCatalog() {
        String[] catalogs = stEnvironment.listCatalogs();
        CustomTableEnvironment newstEnvironment = createCustomTableEnvironment();
        for (int i = 0; i < catalogs.length; i++) {
            if (stEnvironment.getCatalog(catalogs[i]).isPresent()) {
                newstEnvironment.getCatalogManager().unregisterCatalog(catalogs[i], true);
                newstEnvironment.registerCatalog(catalogs[i], stEnvironment.getCatalog(catalogs[i]).get());
            }
        }
        stEnvironment = newstEnvironment;
    }

    public String pretreatStatement(String statement) {
        return FlinkInterceptor.pretreatStatement(this, statement);
    }

    private FlinkInterceptorResult pretreatExecute(String statement) {
        return FlinkInterceptor.build(this, statement);
    }

    public JobExecutionResult execute(String jobName) throws Exception {
        return environment.execute(jobName);
    }

    public TableResult executeSql(String statement) {
        statement = pretreatStatement(statement);
        FlinkInterceptorResult flinkInterceptorResult = pretreatExecute(statement);
        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
            return flinkInterceptorResult.getTableResult();
        }
        if (!flinkInterceptorResult.isNoExecute()) {
            return stEnvironment.executeSql(statement);
        } else {
            return CustomTableResultImpl.TABLE_RESULT_OK;
        }
    }

    public String explainSql(String statement, ExplainDetail... extraDetails) {
        statement = pretreatStatement(statement);
        if (!pretreatExecute(statement).isNoExecute()) {
            return stEnvironment.explainSql(statement, extraDetails);
        } else {
            return "";
        }
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        statement = pretreatStatement(statement);
        if (Asserts.isNotNullString(statement) && !pretreatExecute(statement).isNoExecute()) {
            return stEnvironment.explainSqlRecord(statement, extraDetails);
        } else {
            return null;
        }
    }

    public ObjectNode getStreamGraph(String statement) {
        statement = pretreatStatement(statement);
        if (!pretreatExecute(statement).isNoExecute()) {
            return stEnvironment.getStreamGraph(statement);
        } else {
            return null;
        }
    }

    public ObjectNode getStreamGraph(List<String> statements) {
        StreamGraph streamGraph = stEnvironment.getStreamGraphFromInserts(statements);
        JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
        String json = jsonGenerator.getJSON();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            return objectNode;
        }
    }

    public StreamGraph getStreamGraph() {
        return environment.getStreamGraph();
    }

    public ObjectNode getStreamGraphFromDataStream(List<String> statements) {
        for (String statement : statements) {
            executeSql(statement);
        }
        StreamGraph streamGraph = getStreamGraph();
        JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
        String json = jsonGenerator.getJSON();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            return objectNode;
        }
    }

    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return stEnvironment.getJobPlanInfo(statements);
    }

    public JobPlanInfo getJobPlanInfoFromDataStream(List<String> statements) {
        for (String statement : statements) {
            executeSql(statement);
        }
        StreamGraph streamGraph = getStreamGraph();
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(streamGraph.getJobGraph()));
    }

    /*public void registerFunction(String name, ScalarFunction function){
        stEnvironment.registerFunction(name,function);
    }

    public void createTemporarySystemFunction(String name, Class<? extends UserDefinedFunction> var2){
        stEnvironment.createTemporarySystemFunction(name,var2);
    }*/

    public CatalogManager getCatalogManager() {
        return stEnvironment.getCatalogManager();
    }

    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return stEnvironment.getJobGraphFromInserts(statements);
    }

    public StatementSet createStatementSet() {
        return stEnvironment.createStatementSet();
    }

    public TableResult executeStatementSet(List<String> statements) {
        StatementSet statementSet = stEnvironment.createStatementSet();
        for (String item : statements) {
            statementSet.addInsertSql(item);
        }
        return statementSet.execute();
    }

    public String explainStatementSet(List<String> statements) {
        StatementSet statementSet = stEnvironment.createStatementSet();
        for (String item : statements) {
            statementSet.addInsertSql(item);
        }
        return statementSet.explain();
    }

    public void submitSql(String statements) {
        executeSql(statements);
    }

    public void submitStatementSet(List<String> statements) {
        executeStatementSet(statements);
    }

    public boolean parseAndLoadConfiguration(String statement) {
        return stEnvironment.parseAndLoadConfiguration(statement, environment, setConfig);
    }
}
