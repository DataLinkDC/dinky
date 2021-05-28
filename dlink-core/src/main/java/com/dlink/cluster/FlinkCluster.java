package com.dlink.cluster;

import cn.hutool.http.HttpUtil;
import com.dlink.constant.FlinkConstant;
import com.dlink.constant.FlinkHistoryConstant;
import com.dlink.constant.NetConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * FlinkCluster
 *
 * @author wenmo
 * @since 2021/5/25 15:08
 **/
public class FlinkCluster {

    private static String flinkJobMangerHost;

    public static String getFlinkJobMangerHost() {
        return flinkJobMangerHost;
    }

    public static void setFlinkJobMangerHost(String flinkJobMangerHost) {
        FlinkCluster.flinkJobMangerHost = flinkJobMangerHost;
    }

    public static String getFlinkJobManagerIP(String hosts) {
        try {
            String res = HttpUtil.get(NetConstant.HTTP + getFlinkJobMangerHost() +  NetConstant.SLASH + FlinkHistoryConstant.JOBS, NetConstant.SERVER_TIME_OUT_ACTIVE);
            if (!res.isEmpty()) {
                return getFlinkJobMangerHost();
            }
        } catch (Exception e) {
        }
        String[] servers = hosts.split(",");
        for (String server : servers) {
            try {
                String url = NetConstant.HTTP + server +  NetConstant.SLASH + FlinkHistoryConstant.JOBS;
                String res = HttpUtil.get(url, NetConstant.SERVER_TIME_OUT_ACTIVE);
                if (!res.isEmpty()) {
                    setFlinkJobMangerHost(server);
                    return server;
                }
            } catch (Exception e) {
            }
        }
        return "";
    }

    public static String getFlinkJobManagerHost(String hosts) {
        String[] servers = hosts.split(",");
        for (String server : servers) {
            try {
                String res = HttpUtil.get(NetConstant.HTTP + server +  NetConstant.SLASH + FlinkHistoryConstant.JOBS, NetConstant.SERVER_TIME_OUT_ACTIVE);
                if (!res.isEmpty()) {
                    setFlinkJobMangerHost(server);
                    return server;
                }
            } catch (Exception e) {
            }
        }
        return "";
    }

    public static String testFlinkJobManagerIP(String hosts,String host) {
        try {
            String res = HttpUtil.get(NetConstant.HTTP + host +  NetConstant.SLASH + FlinkHistoryConstant.JOBS, NetConstant.SERVER_TIME_OUT_ACTIVE);
            if (!res.isEmpty()) {
                return host;
            }
        } catch (Exception e) {
        }
        String[] servers = hosts.split(",");
        for (String server : servers) {
            try {
                String url = NetConstant.HTTP + server +  NetConstant.SLASH + FlinkHistoryConstant.JOBS;
                String res = HttpUtil.get(url, NetConstant.SERVER_TIME_OUT_ACTIVE);
                if (!res.isEmpty()) {
                    setFlinkJobMangerHost(server);
                    return server;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
