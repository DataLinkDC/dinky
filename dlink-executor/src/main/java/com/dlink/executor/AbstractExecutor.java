package com.dlink.executor;

import com.dlink.assertion.Asserts;

/**
 * AbstractExecutor
 *
 * @author wenmo
 * @since 2021/10/22 11:19
 **/
public abstract class AbstractExecutor implements Executor {

    protected EnvironmentSetting environmentSetting;
    protected ExecutorSetting executorSetting;

    public Executor setEnvironmentSetting(EnvironmentSetting setting) {
        this.environmentSetting=setting;
        return this;
    }

    public EnvironmentSetting getEnvironmentSetting() {
        return environmentSetting;
    }

    public ExecutorSetting getExecutorSetting() {
        return executorSetting;
    }

    public void setExecutorSetting(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
    }

    public boolean canHandle(String version) {
        return Asserts.isEqualsIgnoreCase(getVersion(),version);
    }
}
