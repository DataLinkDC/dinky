package com.dlink.cluster;

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkClusterInfo
 *
 * @author wenmo
 * @since 2021/10/20 9:10
 **/
@Getter
@Setter
public class FlinkClusterInfo {
    private boolean isEffective;
    private String jobManagerAddress;
    private String version;

    public static final FlinkClusterInfo INEFFECTIVE = new FlinkClusterInfo(false);

    public FlinkClusterInfo(boolean isEffective) {
        this.isEffective = isEffective;
    }

    public FlinkClusterInfo(boolean isEffective, String jobManagerAddress, String version) {
        this.isEffective = isEffective;
        this.jobManagerAddress = jobManagerAddress;
        this.version = version;
    }

    public static FlinkClusterInfo build(String jobManagerAddress, String version){
        return new FlinkClusterInfo(true, jobManagerAddress, version);
    }
}
