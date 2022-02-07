package com.dlink.executor;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.client.program.OptimizerPlanEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableException;

/**
 * AbstractBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/7 20:05
 */
public abstract class AbstractBatchExecutor extends Executor{
    protected ExecutionEnvironment environment;

    public void initEnvironment(){
        if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
            environment.setParallelism(executorSetting.getParallelism());
        }
        if(executorSetting.getConfig()!=null) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            environment.getConfig().configure(configuration, null);
        }
    }

    public void updateEnvironment(ExecutorSetting executorSetting){
        if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
            environment.setParallelism(executorSetting.getParallelism());
        }
        if(executorSetting.getConfig()!=null) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            environment.getConfig().configure(configuration, null);
        }
    }

    public JobExecutionResult execute(String jobName) throws Exception {
        return environment.execute(jobName);
    }

    public StreamGraph getStreamGraph(){
        throw new TableException("Batch model can't get StreamGraph.");
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment(){
        return null;
    }

    public ExecutionConfig getExecutionConfig(){
        return environment.getConfig();
    }

    public boolean parseAndLoadConfiguration(String statement){
        return stEnvironment.parseAndLoadConfiguration(statement,getExecutionConfig(),setConfig);
    }
}
