package com.dlink.executor;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.assertion.Asserts;

/**
 * LocalBatchExecutor
 *
 * @author wenmo
 * @since 2022/2/4 0:04
 */
public class LocalBatchExecutor extends Executor {

    public LocalBatchExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        if (Asserts.isNotNull(executorSetting.getConfig())) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            this.environment = StreamExecutionEnvironment.createLocalEnvironment(configuration);
        } else {
            this.environment = StreamExecutionEnvironment.createLocalEnvironment();
        }
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.createBatch(environment);
    }
}
