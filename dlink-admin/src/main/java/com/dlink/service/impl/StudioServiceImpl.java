package com.dlink.service.impl;

import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.model.Cluster;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StudioService;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public boolean createSession(SessionDTO sessionDTO,String createUser) {
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
}
