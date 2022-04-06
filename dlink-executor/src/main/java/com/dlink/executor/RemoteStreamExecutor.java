package com.dlink.executor;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.assertion.Asserts;

/**
 * RemoteStreamExecutor
 *
 * @author wenmo
 * @since 2021/5/25 14:05
 **/
public class RemoteStreamExecutor extends Executor {

    public RemoteStreamExecutor(EnvironmentSetting environmentSetting, ExecutorSetting executorSetting) {
        this.environmentSetting = environmentSetting;
        this.executorSetting = executorSetting;
        if (Asserts.isNotNull(executorSetting.getConfig())) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            this.environment = StreamExecutionEnvironment.createRemoteEnvironment(environmentSetting.getHost(), environmentSetting.getPort(), configuration);
        } else {
            this.environment = StreamExecutionEnvironment.createRemoteEnvironment(environmentSetting.getHost(), environmentSetting.getPort());
        }
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.create(environment);
    }
}
