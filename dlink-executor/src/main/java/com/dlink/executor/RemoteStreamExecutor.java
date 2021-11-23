package com.dlink.executor;

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
    }

}
