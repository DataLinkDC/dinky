package com.dlink.gateway.config;

import com.dlink.gateway.ConfigPara;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * FlinkConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:56
 */
@Getter
@Setter
public class FlinkConfig {
    private String jobName;
    private String jobId;
    private ActionType action;
    private SavePointType savePointType;
    private String savePoint;
    private List<ConfigPara> configParas;

    public static final String DEFAULT_SAVEPOINT_PREFIX = "hdfs:///flink/savepoints/";
    public FlinkConfig() {
    }
}
