package org.dinky;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorConfig;
import org.dinky.executor.ExecutorFactory;
import org.dinky.flink.ServerExecutorService;

public class ServerExecutorServiceImpl extends UnicastRemoteObject implements ServerExecutorService {
    private Executor executor;

    public ServerExecutorServiceImpl() throws RemoteException {
    }

    @Override
    public void init(ExecutorConfig executorConfig) {
        executor = ExecutorFactory.buildExecutor(executorConfig);

    }

}
