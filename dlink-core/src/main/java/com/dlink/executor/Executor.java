package com.dlink.executor;

import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.UserDefinedFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executor
 * @author  wenmo
 * @since  2021/5/25 13:39
 **/
public abstract class Executor {

    protected StreamExecutionEnvironment environment;
    protected CustomTableEnvironmentImpl stEnvironment;
    protected EnvironmentSetting environmentSetting;
    protected ExecutorSetting executorSetting;

    public static Executor build(){
        return new LocalStreamExecutor(ExecutorSetting.DEFAULT);
    }

    public static Executor build(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting){
        if(environmentSetting.isUseRemote()){
            return buildRemoteExecutor(environmentSetting,executorSetting);
        }else{
            return buildLocalExecutor(executorSetting);
        }
    }

    public static Executor buildLocalExecutor(ExecutorSetting executorSetting){
        return new LocalStreamExecutor(executorSetting);
    }

    public static Executor buildRemoteExecutor(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting){
        environmentSetting.setUseRemote(true);
        return new RemoteStreamExecutor(environmentSetting,executorSetting);
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

    protected void init(){
        initEnvironment();
        initStreamExecutionEnvironment();
    }

    public void update(ExecutorSetting executorSetting){
        updateEnvironment(executorSetting);
        updateStreamExecutionEnvironment(executorSetting);
    }

    private void initEnvironment(){
        if(executorSetting.getCheckpoint()!=null&&executorSetting.getCheckpoint()>0){
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }
        if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
            environment.setParallelism(executorSetting.getParallelism());
        }
    }

    private void updateEnvironment(ExecutorSetting executorSetting){
        if(executorSetting.getCheckpoint()!=null&&executorSetting.getCheckpoint()>0){
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }
        if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
            environment.setParallelism(executorSetting.getParallelism());
        }
    }

    private void initStreamExecutionEnvironment(){
        stEnvironment = CustomTableEnvironmentImpl.create(environment);
        if(executorSetting.isUseSqlFragment()){
            stEnvironment.useSqlFragment();
        }else{
            stEnvironment.unUseSqlFragment();
        }
        if(executorSetting.getJobName()!=null&&!"".equals(executorSetting.getJobName())){
            stEnvironment.getConfig().getConfiguration().setString("pipeline.name", executorSetting.getJobName());
        }
        if(executorSetting.getConfig()!=null){
            for (Map.Entry<String, String> entry : executorSetting.getConfig().entrySet()) {
                stEnvironment.getConfig().getConfiguration().setString(entry.getKey(), entry.getValue());
            }
        }
    }

    private void updateStreamExecutionEnvironment(ExecutorSetting executorSetting){
        copyCatalog();
        if(executorSetting.isUseSqlFragment()){
            stEnvironment.useSqlFragment();
        }else{
            stEnvironment.unUseSqlFragment();
        }
        if(executorSetting.getJobName()!=null&&!"".equals(executorSetting.getJobName())){
            stEnvironment.getConfig().getConfiguration().setString("pipeline.name", executorSetting.getJobName());
        }
        if(executorSetting.getConfig()!=null){
            for (Map.Entry<String, String> entry : executorSetting.getConfig().entrySet()) {
                stEnvironment.getConfig().getConfiguration().setString(entry.getKey(), entry.getValue());
            }
        }
    }

    private void copyCatalog(){
        String[] catalogs = stEnvironment.listCatalogs();
        CustomTableEnvironmentImpl newstEnvironment = CustomTableEnvironmentImpl.create(environment);
        for (int i = 0; i < catalogs.length; i++) {
            if(stEnvironment.getCatalog(catalogs[i]).isPresent()) {
                newstEnvironment.getCatalogManager().unregisterCatalog(catalogs[i],true);
                newstEnvironment.registerCatalog(catalogs[i], stEnvironment.getCatalog(catalogs[i]).get());
            }
        }
        stEnvironment = newstEnvironment;
    }

    public JobExecutionResult execute(String jobName) throws Exception{
        return stEnvironment.execute(jobName);
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

    public ObjectNode getStreamGraph(String statement){
        return stEnvironment.getStreamGraph(statement);
    }

    public void registerFunction(String name, ScalarFunction function){
        stEnvironment.registerFunction(name,function);
    }

    public void createTemporarySystemFunction(String name, Class<? extends UserDefinedFunction> var2){
        stEnvironment.createTemporarySystemFunction(name,var2);
    }

    public CatalogManager getCatalogManager(){
        return stEnvironment.getCatalogManager();
    }

    public JobGraph getJobGraphFromInserts(List<String> statements){
        return stEnvironment.getJobGraphFromInserts(statements);
    }
}
