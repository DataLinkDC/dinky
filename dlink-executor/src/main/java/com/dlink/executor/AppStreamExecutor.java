package com.dlink.executor;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * AppStreamExecutor
 *
 * @author wenmo
 * @since 2021/11/18
 */
public class AppStreamExecutor extends Executor{

    public AppStreamExecutor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.environment = StreamExecutionEnvironment.getExecutionEnvironment();
        init();
    }
}
