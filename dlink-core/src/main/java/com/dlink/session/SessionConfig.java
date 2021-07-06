package com.dlink.session;

import com.dlink.executor.ExecutorSetting;
import lombok.Getter;
import lombok.Setter;

/**
 * SessionConfig
 *
 * @author wenmo
 * @since 2021/7/6 21:59
 */
@Getter
@Setter
public class SessionConfig {
    private SessionType type;
    private boolean useRemote;
    private Integer clusterId;
    private String clusterName;
    private String address;

    public enum SessionType{
        PUBLIC,
        PRIVATE
    }

    public SessionConfig(SessionType type, boolean useRemote, Integer clusterId, String clusterName, String address) {
        this.type = type;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.address = address;
    }

    public static SessionConfig build(String type, boolean useRemote, Integer clusterId, String clusterName, String address){
        return new SessionConfig(SessionType.valueOf(type),useRemote,clusterId,clusterName,address);
    }

    public ExecutorSetting getExecutorSetting(){
        return new ExecutorSetting(true);
    }

}
