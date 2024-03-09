package org.dinky;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.dinky.flink.ServerExecutorService;

public class ServerExecutorServiceImpl extends UnicastRemoteObject implements ServerExecutorService {
    public ServerExecutorServiceImpl() throws RemoteException {
    }
}
