package com.dlink.executor;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * AppBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/7 22:14
 */
public class AppBatchExecutor extends Executor {

    public AppBatchExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.environment = StreamExecutionEnvironment.createLocalEnvironment();
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.createBatch(environment);
    }
}
