package com.dlink.executor;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.assertion.Asserts;

/**
 * AppStreamExecutor
 *
 * @author wenmo
 * @since 2021/11/18
 */
public class AppStreamExecutor extends Executor {

    public AppStreamExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        if (Asserts.isNotNull(executorSetting.getConfig())) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            this.environment = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        } else {
            this.environment = StreamExecutionEnvironment.getExecutionEnvironment();
        }
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.create(environment);
    }
}
