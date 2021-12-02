package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Assert;
import com.dlink.cluster.FlinkCluster;
import com.dlink.cluster.FlinkClusterInfo;
import com.dlink.constant.FlinkConstant;
import com.dlink.constant.NetConstant;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.ClusterMapper;
import com.dlink.model.Cluster;
import com.dlink.service.ClusterService;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    public FlinkClusterInfo checkHeartBeat(String hosts, String host) {
        return FlinkCluster.testFlinkJobManagerIP(hosts,host);
    }

    @Override
    public String getJobManagerAddress(Cluster cluster) {
        Assert.check(cluster);
        FlinkClusterInfo info = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
        String host = null;
        if(info.isEffective()){
            host = info.getJobManagerAddress();
        }
        Assert.checkHost(host);
        if(!host.equals(cluster.getJobManagerHost())){
            cluster.setJobManagerHost(host);
            updateById(cluster);
        }
        return host;
    }

    @Override
    public String buildEnvironmentAddress(boolean useRemote, Integer id) {
        if(useRemote&&id!=0) {
            return buildRemoteEnvironmentAddress(id);
        }else{
            return buildLocalEnvironmentAddress();
        }
    }

    @Override
    public String buildRemoteEnvironmentAddress(Integer id) {
        return getJobManagerAddress(getById(id));
    }

    @Override
    public String buildLocalEnvironmentAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            if(inetAddress!=null) {
                return inetAddress.getHostAddress()+ NetConstant.COLON+FlinkConstant.FLINK_REST_DEFAULT_PORT;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return FlinkConstant.LOCAL_HOST;
    }

    @Override
    public List<Cluster> listEnabledAll() {
        return this.list(new QueryWrapper<Cluster>().eq("enabled",1));
    }

    @Override
    public List<Cluster> listSessionEnable() {
        return baseMapper.listSessionEnable();
    }

    @Override
    public List<Cluster> listAutoEnable() {
        return list(new QueryWrapper<Cluster>().eq("enabled",1).eq("auto_registers",1));
    }

    @Override
    public Cluster registersCluster(Cluster cluster) {
        checkHealth(cluster);
        saveOrUpdate(cluster);
        return cluster;
    }

    @Override
    public int clearCluster() {
        List<Cluster> clusters = listAutoEnable();
        int count = 0;
        for(Cluster item : clusters){
            if((!checkHealth(item))&&removeById(item)){
                count ++;
            }
        }
        return count;
    }

    private boolean checkHealth(Cluster cluster){
        FlinkClusterInfo info = checkHeartBeat(cluster.getHosts(), cluster.getJobManagerHost());
        if(!info.isEffective()){
            cluster.setJobManagerHost("");
            cluster.setStatus(0);
            return false;
        }else{
            cluster.setJobManagerHost(info.getJobManagerAddress());
            cluster.setStatus(1);
            cluster.setVersion(info.getVersion());
            return true;
        }
    }
}
