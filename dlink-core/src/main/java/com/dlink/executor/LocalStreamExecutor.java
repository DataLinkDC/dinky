package com.dlink.executor;

import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * LocalStreamExecuter
 *
 * @author wenmo
 * @since 2021/5/25 13:48
 **/
public class LocalStreamExecutor extends Executor {

    public LocalStreamExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.environment = StreamExecutionEnvironment.createLocalEnvironment();
        if(executorSetting.getCheckpoint()!=null&&executorSetting.getCheckpoint()>0){
            environment.enableCheckpointing(executorSetting.getCheckpoint());
        }
        if(executorSetting.getParallelism()!=null&&executorSetting.getParallelism()>0){
            environment.setParallelism(executorSetting.getParallelism());
        }
        stEnvironment = CustomTableEnvironmentImpl.create(environment);
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
