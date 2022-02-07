package com.dlink.executor;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * RemoteBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/7 22:10
 */
public class RemoteBatchExecutor extends AbstractBatchExecutor {

    public RemoteBatchExecutor(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting) {
        this.environmentSetting = environmentSetting;
        this.executorSetting = executorSetting;
        this.environment = ExecutionEnvironment.createRemoteEnvironment(environmentSetting.getHost(), environmentSetting.getPort());
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomBatchTableEnvironmentImpl.create(environment);
    }
}
