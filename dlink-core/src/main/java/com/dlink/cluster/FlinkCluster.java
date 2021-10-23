package com.dlink.cluster;

import cn.hutool.core.io.IORuntimeException;
import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;

/**
 * FlinkCluster
 *
 * @author wenmo
 * @since 2021/5/25 15:08
 **/
public class FlinkCluster {

    private static Logger logger = LoggerFactory.getLogger(FlinkCluster.class);

    public static FlinkClusterInfo testFlinkJobManagerIP(String hosts,String host) {
        if(Asserts.isNotNullString(host)) {
            FlinkClusterInfo info = executeSocketTest(host);
            if(info.isEffective()){
                return info;
            }
        }
        String[] servers = hosts.split(",");
        for (String server : servers) {
            FlinkClusterInfo info = executeSocketTest(server);
            if(info.isEffective()){
                return info;
            }
        }
        return FlinkClusterInfo.INEFFECTIVE;
    }
    
    private static FlinkClusterInfo executeSocketTest(String host){
        try {
            String res = FlinkAPI.build(host).getVersion();
            if (Asserts.isNotNullString(res)) {
                return FlinkClusterInfo.build(host,res);
            }
        } catch (IORuntimeException e) {
            logger.info("Flink jobManager 地址排除 -- "+ host);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return FlinkClusterInfo.INEFFECTIVE;
    }

}
