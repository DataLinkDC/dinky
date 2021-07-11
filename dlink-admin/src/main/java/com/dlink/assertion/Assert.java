package com.dlink.assertion;

import com.dlink.exception.BusException;
import com.dlink.model.Cluster;
import com.dlink.model.Statement;
import com.dlink.model.Task;

/**
 * Assert
 *
 * @author wenmo
 * @since 2021/5/30 11:13
 */
public interface Assert {

    static boolean checkNotNull(Object object){
        return object!=null;
    }

    static void check(Cluster cluster) {
        if (cluster.getId() == null) {
            throw new BusException("Flink 集群【" + cluster.getId() + "】不存在");
        }
    }

    static void check(Task task) {
        if (task == null) {
            throw new BusException("作业不存在");
        }
    }

    static void check(Statement statement) {
        if (statement == null) {
            throw new BusException("FlinkSql语句不存在");
        }
    }

    static void checkHost(String host) {
        if (host == null || "".equals(host)) {
            throw new BusException("集群地址暂不可用");
        }
    }
}
