package com.dlink.gateway.config;

import lombok.Getter;
import lombok.Setter;

/**
 * ClusterConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:52
 */
@Getter
@Setter
public class ClusterConfig {
    private String flinkConfigPath;
    private String flinkLibPath;
    private String yarnConfigPath;
    private String appId;

    public ClusterConfig() {
    }

    public ClusterConfig(String flinkConfigPath, String flinkLibPath, String yarnConfigPath) {
        this.flinkConfigPath = flinkConfigPath;
        this.flinkLibPath = flinkLibPath;
        this.yarnConfigPath = yarnConfigPath;
    }

    public static ClusterConfig build(String flinkConfigPath, String flinkLibPath, String yarnConfigPath){
        return new ClusterConfig(flinkConfigPath,flinkLibPath,yarnConfigPath);
    }

    @Override
    public String toString() {
        return "ClusterConfig{" +
                "flinkConfigPath='" + flinkConfigPath + '\'' +
                ", flinkLibPath='" + flinkLibPath + '\'' +
                ", yarnConfigPath='" + yarnConfigPath + '\'' +
                ", appId='" + appId + '\'' +
                '}';
    }
}
