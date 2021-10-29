package com.dlink.gateway;

import org.junit.Test;

/**
 * GatewayTest
 *
 * @author qiwenkai
 * @since 2021/10/29 17:06
 **/
public class GatewayTest {

    @Test
    public void getTest(){
        GatewayConfig config = new GatewayConfig();
        config.setJobName("apptest");
        config.setType(GatewayType.get("yarn-application"));
        config.setConfigDir("/opt/src/flink-1.12.2_pj/conf");
        config.setUserJarPath("hdfs:///flink12/jar/currencyAppJar.jar");
        config.setUserJarParas("--id 2410,2412,2411".split("\\s+"));
        config.setUserJarMainAppClass("com.app.MainApp");
        String longValue = Gateway.build(config).getType().getLongValue();
        System.out.println(longValue);
    }
}
