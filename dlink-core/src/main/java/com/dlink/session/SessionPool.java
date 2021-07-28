package com.dlink.session;

import com.dlink.constant.FlinkConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * SessionPool
 *
 * @author wenmo
 * @since 2021/5/25 14:32
 **/
public class SessionPool {

    private static volatile List<ExecutorEntity> executorList = new Vector<>(FlinkConstant.DEFAULT_SESSION_COUNT);

    public static boolean exist(String sessionId) {
        for (ExecutorEntity executorEntity : executorList) {
            if (executorEntity.getSessionId().equals(sessionId)) {
                return true;
            }
        }
        return false;
    }

    public static Integer push(ExecutorEntity executorEntity){
        if (executorList.size() >= FlinkConstant.DEFAULT_SESSION_COUNT * FlinkConstant.DEFAULT_FACTOR) {
            executorList.remove(0);
        }else if(executorList.size() >= FlinkConstant.DEFAULT_SESSION_COUNT){
            executorList.clear();
        }
        executorList.add(executorEntity);
        return executorList.size();
    }

    public static Integer remove(String sessionId) {
        int count = executorList.size();
        for (int i = 0; i < executorList.size(); i++) {
            if (sessionId.equals(executorList.get(i).getSessionId())) {
                executorList.remove(i);
                break;
            }
        }
        return count - executorList.size();
    }

    public static ExecutorEntity get(String sessionId) {
        for (ExecutorEntity executorEntity : executorList) {
            if (executorEntity.getSessionId().equals(sessionId)) {
                return executorEntity;
            }
        }
        return null;
    }

    public static List<ExecutorEntity> list(){
        return executorList;
    }

    public static List<SessionInfo> filter(String createUser){
        List<SessionInfo> sessionInfos = new ArrayList<>();
        for (ExecutorEntity item : executorList) {
            if(item.getSessionConfig().getType()== SessionConfig.SessionType.PUBLIC){
                sessionInfos.add(SessionInfo.build(item));
            }else{
                if(createUser!=null&&createUser.equals(item.getCreateUser())){
                    sessionInfos.add(SessionInfo.build(item));
                }
            }
        }
        return sessionInfos;
    }

    public static SessionInfo getInfo(String sessionId){
        ExecutorEntity executorEntity = get(sessionId);
        if(executorEntity!=null){
            return SessionInfo.build(executorEntity);
        }else {
            return null;
        }
    }
}
