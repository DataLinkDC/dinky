package com.dlink.session;

import com.dlink.constant.FlinkConstant;

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
}
