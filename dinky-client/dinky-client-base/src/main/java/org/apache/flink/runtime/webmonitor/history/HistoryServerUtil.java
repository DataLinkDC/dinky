package org.apache.flink.runtime.webmonitor.history;


import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HistoryServerOptions;
import org.apache.flink.util.FlinkException;
import org.dinky.data.constant.DirConstant;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.utils.ClassPathUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public final class HistoryServerUtil {

    public static void run(Consumer<String> jobIdEventListener, Map<String,String> config) {
        log.info("正在启动flink history 服务");
        HistoryServer hs;
        try {
            org.apache.flink.configuration.Configuration configuration = Configuration.fromMap(config);

            hs = new HistoryServer(configuration, (event) -> {
                if (event.getType() == HistoryServerArchiveFetcher.ArchiveEventType.CREATED) {
                    Optional.ofNullable(jobIdEventListener).ifPresent(listener -> listener.accept(event.getJobID()));
                }
            });
        } catch (IOException | FlinkException e) {
            log.error("flink history 服务启动失败，错误信息如下", e);
            throw new RuntimeException(e);
        }
        hs.run();
    }
}
