package com.dlink.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionPool
 *
 * @author wenmo
 * @since 2021/5/25 14:32
 **/
public class SessionPool {

    // sessionId:entity
    private static volatile ConcurrentHashMap<String, ExecutorEntity> executorMap = new ConcurrentHashMap<>();

    public static boolean exist(String sessionId) {
        return executorMap.containsKey(sessionId);
    }

    public static boolean push(ExecutorEntity executorEntity) {
        // As long as the changed session is alive, it should be stored correctly
        if (!exist(executorEntity.getSessionId())) {
            executorMap.put(executorEntity.getSessionId(), executorEntity);
        }
        return true;
    }

    public static boolean remove(String sessionId) {
        if (exist(sessionId)) {
            executorMap.remove(sessionId);
        }
        return true;
    }

    public static ExecutorEntity get(String sessionId) {
        return executorMap.getOrDefault(sessionId, null);
    }

    public static List<ExecutorEntity> list() {
        return executorMap.values().stream().collect(ArrayList::new, List::add, List::addAll);
    }

    public static List<SessionInfo> filter(final String createUser) {
        return executorMap.values().stream()
                .filter(Objects::nonNull)
                .filter(entity -> {
                    return entity.getSessionConfig().getType() == SessionConfig.SessionType.PUBLIC ||
                            (entity.getCreateUser().equals(createUser));
                })
                .map(SessionInfo::build)
                .collect(ArrayList::new, List::add, List::addAll);
    }

    public static SessionInfo getInfo(String sessionId) {
        ExecutorEntity executorEntity = get(sessionId);
        if (executorEntity != null) {
            return SessionInfo.build(executorEntity);
        }
        return null;
    }
}
