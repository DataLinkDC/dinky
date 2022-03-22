package com.dlink.daemon.pool;

import com.dlink.daemon.task.DaemonTask;

/**
 * @author lcg
 * @operate
 * @date 2022/3/7 10:36
 * @return
 */
public interface ThreadPool {

    //执行任务
    void execute(DaemonTask daemonTask);

    //关闭连接池
    void shutdown();

    //增加工作数
    void addWorkers(int num);

    //减少工作数
    void removeWorker(int num);

    int getTaskSize();
}
