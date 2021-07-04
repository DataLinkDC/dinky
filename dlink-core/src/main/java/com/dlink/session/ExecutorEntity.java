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
    private SessionType type;
    private boolean useRemote;
    private String address;
    private String createUser;
    private LocalDateTime createTime;
    private Executor executor;

    public enum SessionType{
        PUBLIC,
        PRIVATE
    }

    public ExecutorEntity(String sessionId, Executor executor) {
        this.sessionId = sessionId;
        this.executor = executor;
    }

    public ExecutorEntity(String sessionId, SessionType type, boolean useRemote, String address, String createUser, LocalDateTime createTime, Executor executor) {
        this.sessionId = sessionId;
        this.type = type;
        this.useRemote = useRemote;
        this.address = address;
        this.createUser = createUser;
        this.createTime = createTime;
        this.executor = executor;
    }

}
