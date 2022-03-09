package com.dlink.daemon.entity;

import com.dlink.daemon.task.DaemonTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TaskWorker.class);

    private volatile boolean running = true;

    private TaskQueue<DaemonTask> queue;

    public TaskWorker(TaskQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
//        log.info("TaskWorker run");
        while (running) {
            DaemonTask daemonTask = queue.dequeue();
            if (daemonTask != null) {
                try {
                    daemonTask.dealTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
//        log.info(Thread.currentThread().getName() + "TaskWorker shutdown");
        running = false;
    }
}
