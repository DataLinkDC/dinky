package org.dinky.job;

import org.apache.flink.configuration.RestOptions;
import org.dinky.gateway.GatewayType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JobConfigTest {

    @Test
    void setAddress() {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress("127.0.0.1:8888");
        jobConfig.setType(GatewayType.LOCAL.getValue());
        Map<String, String> config = new HashMap<>();
        config.put(RestOptions.PORT.key(), "9999");

        jobConfig.setConfig(config);
        jobConfig.setAddress("127.0.0.1:7777");
        assertEquals("127.0.0.1:9999",jobConfig.getAddress());

        jobConfig.setAddress("127.0.0.2");
        assertEquals("127.0.0.2:9999",jobConfig.getAddress());

        config.remove(RestOptions.PORT.key());
        jobConfig.setAddress("127.0.0.2:6666");
        assertEquals("127.0.0.2:6666", jobConfig.getAddress());

        jobConfig.setType(GatewayType.STANDALONE.getLongValue());
        jobConfig.setAddress("127.0.0.3:6666");
        assertEquals("127.0.0.3:6666", jobConfig.getAddress());
    }
}
