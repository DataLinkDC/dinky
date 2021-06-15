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
 * Executor
 * @author  wenmo
 * @since  2021/5/25 13:39
 **/
public abstract class Executor {

    public static final String LOCAL = "LOCAL";
    public static final String REMOTE = "REMOTE";

    protected StreamExecutionEnvironment environment;
    protected CustomTableEnvironmentImpl stEnvironment;
    protected EnvironmentSetting environmentSetting;
    protected ExecutorSetting executorSetting;


    public static Executor build(){
        return new LocalStreamExecutor(new ExecutorSetting(LOCAL));
    }

    public static Executor build(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting){
        if(LOCAL.equals(executorSetting.getType())){
            return new LocalStreamExecutor(executorSetting);
        }else if(REMOTE.equals(executorSetting.getType())){
            return new RemoteStreamExecutor(environmentSetting,executorSetting);
        }else{
            return new LocalStreamExecutor(executorSetting);
        }
    }

    public StreamExecutionEnvironment getEnvironment(){
        return environment;
    }

    public CustomTableEnvironmentImpl getCustomTableEnvironmentImpl(){
        return stEnvironment;
    }

    public ExecutorSetting getExecutorSetting(){
        return executorSetting;
    }

    public EnvironmentSetting getEnvironmentSetting(){
        return environmentSetting;
    }

    public JobExecutionResult execute(String statement) throws Exception{
        return stEnvironment.execute(statement);
    }

    public TableResult executeSql(String statement){
        return stEnvironment.executeSql(statement);
    }

    public Table sqlQuery(String statement){
        return stEnvironment.sqlQuery(statement);
    }

    public String explainSql(String statement, ExplainDetail... extraDetails){
        return stEnvironment.explainSql(statement,extraDetails);
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails){
        return stEnvironment.explainSqlRecord(statement,extraDetails);
    }

    public String getStreamGraphString(String statement){
        return stEnvironment.getStreamGraphString(statement);
    }

    public ObjectNode getStreamGraph(String statement){
        return stEnvironment.getStreamGraph(statement);
    }

    public void registerFunction(String name, ScalarFunction function){
        stEnvironment.registerFunction(name,function);
    }

    public void createTemporarySystemFunction(String name, Class<? extends UserDefinedFunction> var2){
        stEnvironment.createTemporarySystemFunction(name,var2);
    }
}
