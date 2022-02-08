package com.dlink.executor;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * RemoteBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/7 22:10
 */
public class RemoteBatchExecutor extends Executor {

    public RemoteBatchExecutor(EnvironmentSetting environmentSetting, ExecutorSetting executorSetting) {
        this.environmentSetting = environmentSetting;
        this.executorSetting = executorSetting;
        this.environment = StreamExecutionEnvironment.createRemoteEnvironment(environmentSetting.getHost(), environmentSetting.getPort());
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.createBatch(environment);
    }
}
