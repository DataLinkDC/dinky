package com.dlink.executor;

import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.result.SqlExplainResult;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.UserDefinedFunction;

/**
 * RemoteStreamExecutor
 *
 * @author wenmo
 * @since 2021/5/25 14:05
 **/
public class RemoteStreamExecutor extends Executor {

    private StreamExecutionEnvironment environment;
    private CustomTableEnvironmentImpl stEnvironment;
    private EnvironmentSetting environmentSetting;
    private ExecutorSetting executorSetting;


    public RemoteStreamExecutor(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting) {
        this.environmentSetting = environmentSetting;
        this.executorSetting = executorSetting;
        synchronized (RemoteStreamExecutor.class){
            this.environment = StreamExecutionEnvironment.createRemoteEnvironment(environmentSetting.getHost(), environmentSetting.getPort());
            if(executorSetting.getCheckpoint()!=null&&executorSetting.getCheckpoint()>0){
                environment.enableCheckpointing(executorSetting.getCheckpoint());
            }
            if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
                environment.setParallelism(executorSetting.getParallelism());
            }
            if(stEnvironment == null){
                stEnvironment = CustomTableEnvironmentImpl.create(environment);
            }
            if(executorSetting.getJobName()!=null&&!"".equals(executorSetting.getJobName())){
                stEnvironment.getConfig().getConfiguration().setString("pipeline.name", executorSetting.getJobName());
            }
            if(executorSetting.isUseSqlFragment()){
                stEnvironment.useSqlFragment();
            }else{
                stEnvironment.unUseSqlFragment();
            }
        }
    }

    @Override
    public StreamExecutionEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public CustomTableEnvironmentImpl getCustomTableEnvironmentImpl() {
        return this.stEnvironment;
    }

    @Override
    public ExecutorSetting getExecutorSetting() {
        return this.executorSetting;
    }

    @Override
    public EnvironmentSetting getEnvironmentSetting() {
        return this.environmentSetting;
    }

    @Override
    public JobExecutionResult execute(String statement) throws Exception {
        return stEnvironment.execute(statement);
    }

    @Override
    public TableResult executeSql(String statement){
        return stEnvironment.executeSql(statement);
    }

    @Override
    public Table sqlQuery(String statement){
        return stEnvironment.sqlQuery(statement);
    }

    @Override
    public String explainSql(String statement, ExplainDetail... extraDetails) {
        return stEnvironment.explainSql(statement,extraDetails);
    }

    @Override
    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        return stEnvironment.explainSqlRecord(statement,extraDetails);
    }

    @Override
    public String getStreamGraphString(String statement) {
        return stEnvironment.getStreamGraphString(statement);
    }

    @Override
    public ObjectNode getStreamGraph(String statement) {
        return stEnvironment.getStreamGraph(statement);
    }

    @Override
    public void registerFunction(String name, ScalarFunction function) {
        stEnvironment.registerFunction(name,function);
    }

    @Override
    public void createTemporarySystemFunction(String name, Class<? extends UserDefinedFunction> var2) {
        stEnvironment.createTemporarySystemFunction(name,var2);
    }
}
