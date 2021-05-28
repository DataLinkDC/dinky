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

    public abstract StreamExecutionEnvironment getEnvironment();

    public abstract CustomTableEnvironmentImpl getCustomTableEnvironmentImpl();

    public abstract ExecutorSetting getExecutorSetting();

    public abstract EnvironmentSetting getEnvironmentSetting();

    public abstract JobExecutionResult execute(String statement) throws Exception;

    public abstract TableResult executeSql(String statement);

    public abstract Table sqlQuery(String statement);

    public abstract String explainSql(String statement, ExplainDetail... extraDetails);

    public abstract SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails);

    public abstract String getStreamGraphString(String statement);

    public abstract ObjectNode getStreamGraph(String statement);

    public abstract void registerFunction(String name, ScalarFunction function);

    public abstract void createTemporarySystemFunction(String name, Class<? extends UserDefinedFunction> var2);
}
