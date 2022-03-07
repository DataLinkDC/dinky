package com.dlink.daemon.task;

import com.dlink.assertion.Asserts;
import com.dlink.daemon.exception.DaemonTaskException;
import sun.misc.Service;
import java.util.Iterator;
import java.util.Optional;

public interface DaemonTask {
    
    static Optional<DaemonTask> get(DaemonTaskConfig config) {
        Asserts.checkNotNull(config, "线程任务配置不能为空");
        Iterator<DaemonTask> providers = Service.providers(DaemonTask.class);
        while (providers.hasNext()) {
            DaemonTask daemonTask = providers.next();
            if (daemonTask.canHandle(config.getType())) {
                return Optional.of(daemonTask.setConfig(config));
            }
        }
        return Optional.empty();
    }

    static DaemonTask build(DaemonTaskConfig config) {
        Optional<DaemonTask> optionalDriver = DaemonTask.get(config);
        if (!optionalDriver.isPresent()) {
            throw new DaemonTaskException("不支持线程任务类型【" + config.getType() + "】");
        }
        DaemonTask daemonTask = optionalDriver.get();
        return daemonTask;
    }
    
    DaemonTask setConfig(DaemonTaskConfig config);

    default boolean canHandle(String type){
        return Asserts.isEqualsIgnoreCase(getType(),type);
    }

    String getType();

    void dealTask();
}
