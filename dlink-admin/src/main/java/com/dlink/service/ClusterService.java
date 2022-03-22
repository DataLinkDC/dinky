package com.dlink.service;

import com.dlink.cluster.FlinkClusterInfo;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Cluster;

import java.util.List;

/**
 * ClusterService
 *
 * @author wenmo
 * @since 2021/5/28 14:01
 **/
public interface ClusterService extends ISuperService<Cluster> {

    FlinkClusterInfo checkHeartBeat(String hosts, String host);

    String getJobManagerAddress(Cluster cluster);

    String buildEnvironmentAddress(boolean useRemote, Integer id);

    String buildRemoteEnvironmentAddress(Integer id);

    String buildLocalEnvironmentAddress();

    List<Cluster> listEnabledAll();

    List<Cluster> listSessionEnable();

    List<Cluster> listAutoEnable();

    Cluster registersCluster(Cluster cluster);

    boolean enableCluster(Cluster cluster);

    int clearCluster();
}
