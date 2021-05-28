package com.dlink.session;

import com.dlink.executor.Executor;

/**
 * FlinkEntity
 *
 * @author wenmo
 * @since 2021/5/25 14:45
 **/
public class ExecutorEntity {
    private String sessionId;
    private Executor executor;

    public ExecutorEntity(String sessionId, Executor executor) {
        this.sessionId = sessionId;
        this.executor = executor;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
