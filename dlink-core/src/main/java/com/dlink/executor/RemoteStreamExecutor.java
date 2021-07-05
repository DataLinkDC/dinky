package com.dlink.executor;

import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * RemoteStreamExecutor
 *
 * @author wenmo
 * @since 2021/5/25 14:05
 **/
public class RemoteStreamExecutor extends Executor {

    public RemoteStreamExecutor(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting) {
        this.environmentSetting = environmentSetting;
        this.executorSetting = executorSetting;
        this.environment = StreamExecutionEnvironment.createRemoteEnvironment(environmentSetting.getHost(), environmentSetting.getPort());
        init();
        /*synchronized (RemoteStreamExecutor.class){
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
        }*/
    }

}
