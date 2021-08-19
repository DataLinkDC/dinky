package com.dlink.cluster;

import cn.hutool.http.HttpUtil;
import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkConstant;
import com.dlink.constant.FlinkHistoryConstant;
import com.dlink.constant.NetConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * FlinkCluster
 *
 * @author wenmo
 * @since 2021/5/25 15:08
 **/
public class FlinkCluster {

    private static Logger logger = LoggerFactory.getLogger(FlinkCluster.class);

    public static String testFlinkJobManagerIP(String hosts,String host) {
        try {
            String res = FlinkAPI.build(host).getVersion();
            if (Asserts.isNotNullString(res)) {
                return host;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        String[] servers = hosts.split(",");
        for (String server : servers) {
            try {
                String res = FlinkAPI.build(server).getVersion();
                if (Asserts.isNotNullString(res)) {
                    return server;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }

}
