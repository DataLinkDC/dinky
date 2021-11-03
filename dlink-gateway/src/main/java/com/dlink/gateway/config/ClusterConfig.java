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
    private String flinkLibs;
    private String yarnConfigPath;

    public ClusterConfig() {
    }

    public ClusterConfig(String flinkConfigPath, String flinkLibs, String yarnConfigPath) {
        this.flinkConfigPath = flinkConfigPath;
        this.flinkLibs = flinkLibs;
        this.yarnConfigPath = yarnConfigPath;
    }

    public static ClusterConfig build(String flinkConfigPath, String flinkLibs, String yarnConfigPath){
        return new ClusterConfig(flinkConfigPath,flinkLibs,yarnConfigPath);
    }
}
