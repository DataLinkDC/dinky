package com.dlink.session;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * SessionInfo
 *
 * @author wenmo
 * @since 2021/7/6 22:22
 */
@Setter
@Getter
public class SessionInfo {
    private String session;
    private SessionConfig sessionConfig;
    private String createUser;
    private LocalDateTime createTime;

    public SessionInfo(String session, SessionConfig sessionConfig, String createUser, LocalDateTime createTime) {
        this.session = session;
        this.sessionConfig = sessionConfig;
        this.createUser = createUser;
        this.createTime = createTime;
    }

    public static SessionInfo build(ExecutorEntity executorEntity){
        return new SessionInfo(executorEntity.getSessionId(),executorEntity.getSessionConfig(),executorEntity.getCreateUser(),executorEntity.getCreateTime());
    }

}
