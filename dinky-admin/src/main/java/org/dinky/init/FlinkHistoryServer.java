package org.dinky.init;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.runtime.webmonitor.history.HistoryServerUtil;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.service.JobInstanceService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class FlinkHistoryServer implements ApplicationRunner {
    public static final Set<String> HISTORY_JOBID_SET = new LinkedHashSet<>();
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 20, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.DiscardOldestPolicy());

    private final Runnable historyRunnable;

    public FlinkHistoryServer(JobInstanceService jobInstanceService) {
        this.historyRunnable = () -> {
            HistoryServerUtil.run((jobId) -> {
                HISTORY_JOBID_SET.add(jobId);
                threadPoolExecutor.execute(() -> {
                    jobInstanceService.hookJobDoneByHistory(jobId);
                });
            }, SystemConfiguration.getInstances().getFlinkHistoryServerConfiguration());
        };
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        AtomicReference<Thread> historyThread = new AtomicReference<>(new Thread(historyRunnable));
        Runnable closeHistory = ()->{
            if (historyThread.get().isAlive()) {
                historyThread.get().interrupt();
                HISTORY_JOBID_SET.clear();
            }
        };
        CollUtil.newArrayList(systemConfiguration.getUseFlinkHistoryServer(), systemConfiguration.getFlinkHistoryServerPort(), systemConfiguration.getFlinkHistoryServerArchiveRefreshInterval()).forEach(
                x -> x.addChangeEvent(d -> {
                    if (systemConfiguration.getUseFlinkHistoryServer().getValue()) {
                        closeHistory.run();
                        historyThread.updateAndGet((t)->new Thread(historyRunnable)).start();
                    } else {
                        closeHistory.run();
                    }
                })
        );
        if (systemConfiguration.getUseFlinkHistoryServer().getValue()) {
            historyThread.get().start();
        }
    }
}
