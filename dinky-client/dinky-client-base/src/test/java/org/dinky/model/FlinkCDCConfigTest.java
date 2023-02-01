package org.dinky.model;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 *
 */
class FlinkCDCConfigTest {

    @Test
    void getSinkConfigurationString() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put(FlinkCDCConfig.SINK_DB, "sink_db");
        sinkConfig.put("propertyOne", "propertyOneValue");
        sinkConfig.put("propertyTwo", "propertyTwoValue");

        FlinkCDCConfig flinkCDCConfig = new FlinkCDCConfig(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                sinkConfig,
                null);
        String sinkConfigureStr = flinkCDCConfig.getSinkConfigurationString();
        assertThat(sinkConfigureStr, equalTo("'propertyOne' = 'propertyOneValue',\n" +
                "'propertyTwo' = 'propertyTwoValue'"));
    }
}
