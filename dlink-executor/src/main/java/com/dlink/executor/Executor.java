package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.exception.FlinkException;
import sun.misc.Service;

import java.util.Iterator;
import java.util.Optional;

/**
 * Executor
 *
 * @author wenmo
 * @since 2021/10/22 11:01
 **/
public interface Executor {
    static Optional<Executor> get(EnvironmentSetting setting) {
        Asserts.checkNotNull(setting, "Flink 执行配置不能为空");
        Iterator<Executor> providers = Service.providers(Executor.class);
        while (providers.hasNext()) {
            Executor executor = providers.next();
            if (executor.canHandle(setting.getVersion())) {
                return Optional.of(executor.setEnvironmentSetting(setting));
            }
        }
        return Optional.empty();
    }

    static Executor build(EnvironmentSetting config) {
        Optional<Executor> optionalExecutor = Executor.get(config);
        if (!optionalExecutor.isPresent()) {
            throw new FlinkException("不支持 Flink 版本【" + config.getVersion() + "】");
        }
        return optionalExecutor.get();
    }

    Executor setEnvironmentSetting(EnvironmentSetting setting);

    EnvironmentSetting getEnvironmentSetting();

    boolean canHandle(String type);

    String getVersion();

    Executor build();

    Executor build(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting);

    Executor buildLocalExecutor();

    Executor buildRemoteExecutor(EnvironmentSetting environmentSetting,ExecutorSetting executorSetting);

}
