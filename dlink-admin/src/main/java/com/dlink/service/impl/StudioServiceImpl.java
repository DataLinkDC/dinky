package com.dlink.service.impl;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.ColumnCANode;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.model.Cluster;
import com.dlink.parser.SqlType;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StudioService;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.dlink.trans.Operations;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        if(Operations.getSqlTypeFromStatements(statement)== SqlType.INSERT) {
            return CABuilder.getOneTableCAByStatement(statement);
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public List<TableCANode> getOneTableColumnCAByStatement(String statement) {
        if(Operations.getSqlTypeFromStatements(statement)== SqlType.INSERT) {
            return CABuilder.getOneTableColumnCAByStatement(statement);
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public List<ColumnCANode> getColumnCAByStatement(String statement) {
        if(Operations.getSqlTypeFromStatements(statement)== SqlType.INSERT) {
            return CABuilder.getColumnCAByStatement(statement);
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public List<JsonNode> listJobs(Integer clusterId) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster,"该集群不存在");
        return FlinkAPI.build(cluster.getJobManagerHost()).listJobs();
    }
}
