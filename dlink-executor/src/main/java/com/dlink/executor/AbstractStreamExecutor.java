package com.dlink.executor;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;

/**
 * AbstractStreamExecutor
 *
 * @author wenmo
 * @since 2022/2/7 20:03
 */
public abstract class AbstractStreamExecutor extends Executor{
    protected StreamExecutionEnvironment environment;

    public void initEnvironment(){
        if(executorSetting.getCheckpoint()!=null&&executorSetting.getCheckpoint()>0){
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }
        if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
            environment.setParallelism(executorSetting.getParallelism());
        }
        if(executorSetting.getConfig()!=null) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            environment.getConfig().configure(configuration, null);
        }
    }

    public void updateEnvironment(ExecutorSetting executorSetting){
        if(executorSetting.getCheckpoint()!=null&&executorSetting.getCheckpoint()>0){
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }
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
        return environment.getStreamGraph();
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment(){
        return environment;
    }

    public ExecutionConfig getExecutionConfig(){
        return environment.getConfig();
    }

    public boolean parseAndLoadConfiguration(String statement){
        return stEnvironment.parseAndLoadConfiguration(statement,getExecutionConfig(),setConfig);
    }
}
