package com.dlink.executor;

import org.apache.flink.api.java.ExecutionEnvironment;

/**
 * AppBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/7 22:14
 */
public class AppBatchExecutor extends AbstractBatchExecutor {

    public AppBatchExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.environment = ExecutionEnvironment.createLocalEnvironment();
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomBatchTableEnvironmentImpl.create(environment);
    }
}
