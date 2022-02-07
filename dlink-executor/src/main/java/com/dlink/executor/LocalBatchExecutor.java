package com.dlink.executor;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;

/**
 * LocalBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/4 0:04
 */
public class LocalBatchExecutor extends AbstractBatchExecutor {

    public LocalBatchExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.environment = ExecutionEnvironment.createLocalEnvironment();
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomBatchTableEnvironmentImpl.create(environment);
    }
}
