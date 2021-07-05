package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.cluster.FlinkCluster;
import com.dlink.constant.FlinkConstant;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.exception.BusException;
import com.dlink.exception.JobException;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.model.Cluster;
import com.dlink.result.IResult;
import com.dlink.result.RunResult;
import com.dlink.result.SelectResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StudioService;
import com.dlink.session.SessionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

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
    public JobResult executeSql(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        buildEnvironmentAddress(config,studioExecuteDTO.getClusterId());
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeSql(studioExecuteDTO.getStatement());
    }

    private void buildEnvironmentAddress(JobConfig config,Integer clusterId){
        if(config.isUseRemote()) {
            config.setAddress(clusterService.getJobManagerAddress(
                    clusterService.getById(clusterId)
            ));
        }else{
            try {
                InetAddress address = InetAddress.getLocalHost();
                if(address!=null) {
                    config.setAddress(address.getHostAddress());
                }else{
                    config.setAddress(FlinkConstant.LOCAL_HOST);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IResult executeDDL(StudioDDLDTO studioDDLDTO) {
        JobConfig config = studioDDLDTO.getJobConfig();
        buildEnvironmentAddress(config,studioDDLDTO.getClusterId());
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeDDL(studioDDLDTO.getStatement());
    }

    @Override
    public SelectResult getJobData(String jobId) {
        return JobManager.getJobData(jobId);
    }

    @Override
    public boolean clearSession(String session) {
        if(SessionPool.remove(session)>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<TableCANode> getOneTableCAByStatement(String statement) {
        return CABuilder.getOneTableCAByStatement(statement);
    }

    @Override
    public List<TableCANode> getOneTableColumnCAByStatement(String statement) {
        return CABuilder.getOneTableColumnCAByStatement(statement);
    }
}
