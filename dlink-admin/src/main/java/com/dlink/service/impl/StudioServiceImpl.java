package com.dlink.service.impl;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.ColumnCANode;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.model.Cluster;
import com.dlink.model.Savepoints;
import com.dlink.model.SystemConfiguration;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.service.ClusterConfigurationService;
import com.dlink.service.ClusterService;
import com.dlink.service.SavepointsService;
import com.dlink.service.StudioService;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private SavepointsService savepointsService;

    @Override
    public JobResult executeSql(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeSql(studioExecuteDTO.getStatement());
    }

    @Override
    public IResult executeDDL(StudioDDLDTO studioDDLDTO) {
        JobConfig config = studioDDLDTO.getJobConfig();
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioDDLDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeDDL(studioDDLDTO.getStatement());
    }

    @Override
    public List<SqlExplainResult> explainSql(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.build(config);
        return jobManager.explainSql(studioExecuteDTO.getStatement());
    }

    @Override
    public ObjectNode getStreamGraph(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.build(config);
        return jobManager.getStreamGraph(studioExecuteDTO.getStatement());
    }

    @Override
    public SelectResult getJobData(String jobId) {
        return JobManager.getJobData(jobId);
    }

    @Override
    public SessionInfo createSession(SessionDTO sessionDTO, String createUser) {
        if(sessionDTO.isUseRemote()) {
            Cluster cluster = clusterService.getById(sessionDTO.getClusterId());
            SessionConfig sessionConfig = SessionConfig.build(
                    sessionDTO.getType(), true,
                    cluster.getId(), cluster.getAlias(),
                    clusterService.buildEnvironmentAddress(true, sessionDTO.getClusterId()));
            return JobManager.createSession(sessionDTO.getSession(), sessionConfig, createUser);
        }else{
            SessionConfig sessionConfig = SessionConfig.build(
                    sessionDTO.getType(), false,
                    null, null,
                    clusterService.buildEnvironmentAddress(false, null));
            return JobManager.createSession(sessionDTO.getSession(), sessionConfig, createUser);
        }
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
    public List<SessionInfo> listSession(String createUser) {
        return JobManager.listSession(createUser);
    }

    @Override
    public List<TableCANode> getOneTableCAByStatement(String statement) {
        return CABuilder.getOneTableCAByStatement(statement);
    }

    @Override
    public List<TableCANode> getOneTableColumnCAByStatement(String statement) {
        return CABuilder.getOneTableColumnCAByStatement(statement);
    }

    @Override
    public List<ColumnCANode> getColumnCAByStatement(String statement) {
        return CABuilder.getColumnCAByStatement(statement);
    }

    @Override
    public List<JsonNode> listJobs(Integer clusterId) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster,"该集群不存在");
        return FlinkAPI.build(cluster.getJobManagerHost()).listJobs();
    }

    @Override
    public boolean cancel(Integer clusterId,String jobId) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster,"该集群不存在");
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress(cluster.getJobManagerHost());
        if(Asserts.isNotNull(cluster.getClusterConfigurationId())){
            Map<String, String> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
        }
        jobConfig.setUseRestAPI(SystemConfiguration.getInstances().isUseRestAPI());
        JobManager jobManager = JobManager.build(jobConfig);
        return jobManager.cancel(jobId);
    }

    @Override
    public boolean savepoint(Integer clusterId, String jobId, String savePointType,String name) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster,"该集群不存在");
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress(cluster.getJobManagerHost());
        jobConfig.setType(cluster.getType());
        if(Asserts.isNotNull(cluster.getClusterConfigurationId())){
            Map<String, String> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
        }
        jobConfig.setUseRestAPI(SystemConfiguration.getInstances().isUseRestAPI());
        JobManager jobManager = JobManager.build(jobConfig);
        jobManager.setUseGateway(true);
        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType);
        if(Asserts.isNotNull(savePointResult)){
            for(JobInfo item : savePointResult.getJobInfos()){
                if(Asserts.isEqualsIgnoreCase(jobId,item.getJobId())){
                    Savepoints savepoints = new Savepoints();
                    savepoints.setName(name);
                    savepoints.setType(savePointType);
                    savepoints.setPath(item.getSavePoint());
                    savepoints.setTaskId(cluster.getTaskId());
                    savepointsService.save(savepoints);
                }
            }
            return true;
        }
        return false;
    }
}
