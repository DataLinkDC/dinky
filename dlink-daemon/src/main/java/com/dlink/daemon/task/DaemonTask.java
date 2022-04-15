package com.dlink.daemon.task;

import java.util.Optional;
import java.util.ServiceLoader;

import com.dlink.assertion.Asserts;
import com.dlink.daemon.exception.DaemonTaskException;

public interface DaemonTask {

    static Optional<DaemonTask> get(DaemonTaskConfig config) {
        Asserts.checkNotNull(config, "线程任务配置不能为空");
        ServiceLoader<DaemonTask> daemonTasks = ServiceLoader.load(DaemonTask.class);
        for (DaemonTask daemonTask : daemonTasks) {
            if (daemonTask.canHandle(config.getType())) {
                return Optional.of(daemonTask.setConfig(config));
            }
        }
        return Optional.empty();
    }

    static DaemonTask build(DaemonTaskConfig config) {
        Optional<DaemonTask> optionalDaemonTask = DaemonTask.get(config);
        if (!optionalDaemonTask.isPresent()) {
            throw new DaemonTaskException("不支持线程任务类型【" + config.getType() + "】");
        }
        DaemonTask daemonTask = optionalDaemonTask.get();
        return daemonTask;
    }

    DaemonTask setConfig(DaemonTaskConfig config);

    default boolean canHandle(String type) {
        return Asserts.isEqualsIgnoreCase(getType(), type);
    }

    String getType();

    void dealTask();
}
