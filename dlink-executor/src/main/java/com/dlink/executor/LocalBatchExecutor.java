package com.dlink.executor;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * LocalBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/4 0:04
 */
public class LocalBatchExecutor extends Executor {

    public LocalBatchExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.environment = StreamExecutionEnvironment.createLocalEnvironment();
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.createBatch(environment);
    }
}
