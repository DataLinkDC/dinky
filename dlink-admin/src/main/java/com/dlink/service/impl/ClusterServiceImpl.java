package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Assert;
import com.dlink.cluster.FlinkCluster;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.ClusterMapper;
import com.dlink.model.Cluster;
import com.dlink.service.ClusterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClusterServiceImpl
 *
 * @author wenmo
 * @since 2021/5/28 14:02
 **/
@Service
public class ClusterServiceImpl extends SuperServiceImpl<ClusterMapper, Cluster> implements ClusterService {

    @Override
    public String checkHeartBeat(String hosts,String host) {
        return FlinkCluster.testFlinkJobManagerIP(hosts,host);
    }

    @Override
    public String getJobManagerAddress(Cluster cluster) {
        Assert.check(cluster);
        String host = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
        Assert.checkHost(host);
        if(!host.equals(cluster.getJobManagerHost())){
            cluster.setJobManagerHost(host);
            updateById(cluster);
        }
        return host;
    }

    @Override
    public List<Cluster> listEnabledAll() {
        return this.list(new QueryWrapper<Cluster>().eq("enabled",1));
    }
}
