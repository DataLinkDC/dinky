package org.dinky.service;

public interface WatchTableService {
    void registerListenEntry(Integer userId, String table);

    void unRegisterListenEntry(Integer userId, String table);
}
