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

    public static String getFlinkJobManagerIP(String flinkServers) {
        String res = "";
        String flinkAddress = "";
        try {
            flinkAddress = getFlinkJobMangerHost();
            res = HttpUtil.get(NetConstant.HTTP + flinkAddress + NetConstant.COLON + NetConstant.PORT +  NetConstant.SLASH + FlinkHistoryConstant.JOBS, NetConstant.SERVER_TIME_OUT_ACTIVE);
            if (!res.isEmpty()) {
                return flinkAddress;
            }
        } catch (Exception e) {
        }
        String[] servers = flinkServers.split(",");
        for (String server : servers) {
            try {
                String url = NetConstant.HTTP + server + NetConstant.COLON + NetConstant.PORT +  NetConstant.SLASH + FlinkHistoryConstant.JOBS;
                res = HttpUtil.get(url, NetConstant.SERVER_TIME_OUT_ACTIVE);
                if (!res.isEmpty()) {
                    if(server.equalsIgnoreCase(flinkAddress)){
                        setFlinkJobMangerHost(server);
                    }
                    return server;
                }
            } catch (Exception e) {
            }
        }
        return "";
    }
}
