package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.cluster.FlinkCluster;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.exception.BusException;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.job.JobManager;
import com.dlink.model.Cluster;
import com.dlink.result.RunResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StudioService;
import com.dlink.session.SessionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * StudioServiceImpl
 *
 * @author wenmo
 * @since 2021/5/30 11:08
 */
@Service
public class StudioServiceImpl implements StudioService {

    @Autowired
    private ClusterService clusterService;

    @Override
    public RunResult executeSql(StudioExecuteDTO studioExecuteDTO) {
        studioExecuteDTO.setSession(studioExecuteDTO.getClusterId()+"_"+studioExecuteDTO.getSession());
        String ExecuteType = Executor.REMOTE;
        String host =null;
        Cluster cluster = clusterService.getById(studioExecuteDTO.getClusterId());
        if(studioExecuteDTO.getClusterId()==0&&cluster==null){
            ExecuteType = Executor.LOCAL;
        }else if(cluster==null){
            throw new BusException("未获取到集群信息");
        }else {
            Assert.check(cluster);
            host = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
            Assert.checkHost(host);
            if(!host.equals(cluster.getJobManagerHost())){
                cluster.setJobManagerHost(host);
                clusterService.updateById(cluster);
            }
        }
        JobManager jobManager = new JobManager(host,studioExecuteDTO.getSession(),studioExecuteDTO.getMaxRowNum());
        return jobManager.execute(studioExecuteDTO.getStatement(), new ExecutorSetting(
                ExecuteType,studioExecuteDTO.getCheckPoint(),studioExecuteDTO.getParallelism(),
                studioExecuteDTO.isFragment(),studioExecuteDTO.getSavePointPath(),studioExecuteDTO.getJobName()));
    }

    @Override
    public RunResult executeDDL(StudioDDLDTO studioDDLDTO) {
        studioDDLDTO.setSession(studioDDLDTO.getClusterId()+"_"+studioDDLDTO.getSession());
        String ExecuteType = Executor.REMOTE;
        String host =null;
        Cluster cluster = clusterService.getById(studioDDLDTO.getClusterId());
        if(studioDDLDTO.getClusterId()==0&&cluster==null){
            ExecuteType = Executor.LOCAL;
        }else {
            Assert.check(cluster);
            host = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
            Assert.checkHost(host);
            if(!host.equals(cluster.getJobManagerHost())){
                cluster.setJobManagerHost(host);
                clusterService.updateById(cluster);
            }
        }
        JobManager jobManager = new JobManager(host,studioDDLDTO.getSession(),1000);
        return jobManager.execute(studioDDLDTO.getStatement(), new ExecutorSetting(
                ExecuteType));
    }

    @Override
    public boolean clearSession(String session) {
        if(SessionPool.remove(session)>0){
            return true;
        }else{
            return false;
        }
    }
}
