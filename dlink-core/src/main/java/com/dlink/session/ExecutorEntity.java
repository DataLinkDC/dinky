package com.dlink.session;

import com.dlink.executor.Executor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * FlinkEntity
 *
 * @author wenmo
 * @since 2021/5/25 14:45
 **/
@Setter
@Getter
public class ExecutorEntity {
    private String sessionId;
    private SessionConfig sessionConfig;
    private String createUser;
    private LocalDateTime createTime;
    private Executor executor;

    public ExecutorEntity(String sessionId, Executor executor) {
        this.sessionId = sessionId;
        this.executor = executor;
    }

    public ExecutorEntity(String sessionId, SessionConfig sessionConfig, String createUser, LocalDateTime createTime, Executor executor) {
        this.sessionId = sessionId;
        this.sessionConfig = sessionConfig;
        this.createUser = createUser;
        this.createTime = createTime;
        this.executor = executor;
    }
}
