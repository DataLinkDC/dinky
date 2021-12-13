package com.dlink.service.impl;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Assert;
import com.dlink.assertion.Asserts;
import com.dlink.config.Dialect;
import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.ColumnCANode;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.metadata.driver.Driver;
import com.dlink.model.Cluster;
import com.dlink.model.DataBase;
import com.dlink.model.Savepoints;
import com.dlink.model.SystemConfiguration;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.service.ClusterConfigurationService;
import com.dlink.service.ClusterService;
import com.dlink.service.DataBaseService;
import com.dlink.service.SavepointsService;
import com.dlink.service.StudioService;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.dlink.utils.RunTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private static final Logger logger = LoggerFactory.getLogger(StudioServiceImpl.class);

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private SavepointsService savepointsService;
    @Autowired
    private DataBaseService dataBaseService;

    @Override
    public JobResult executeSql(StudioExecuteDTO studioExecuteDTO) {
        if(Dialect.SQL.equalsVal(studioExecuteDTO.getDialect())){
            return executeCommonSql(studioExecuteDTO);
        }else{
            return executeFlinkSql(studioExecuteDTO);
        }
    }

    private JobResult executeFlinkSql(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        // If you are using a shared session, configure the current jobmanager address
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.build(config);
        JobResult jobResult = jobManager.executeSql(studioExecuteDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return jobResult;
    }

    private JobResult executeCommonSql(StudioExecuteDTO studioExecuteDTO) {
        JobResult result = new JobResult();
        result.setStatement(studioExecuteDTO.getStatement());
        result.setStartTime(LocalDateTime.now());
        if(Asserts.isNull(studioExecuteDTO.getDatabaseId())){
            result.setSuccess(false);
            result.setError("请指定数据源");
            result.setEndTime(LocalDateTime.now());
            return result;
        }else{
            DataBase dataBase = dataBaseService.getById(studioExecuteDTO.getDatabaseId());
            if(Asserts.isNull(dataBase)){
                result.setSuccess(false);
                result.setError("数据源不存在");
                result.setEndTime(LocalDateTime.now());
                return result;
            }
            try {
                com.dlink.metadata.result.SelectResult selectResult = Driver.build(dataBase.getDriverConfig()).connect().query(studioExecuteDTO.getStatement(),studioExecuteDTO.getMaxRowNum());
                result.setResult(selectResult);
                result.setSuccess(true);
            }catch (Exception e){
                result.setSuccess(false);
                result.setError(e.getMessage());
            }
            result.setEndTime(LocalDateTime.now());
            return result;
        }
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
        if( Dialect.SQL.equalsVal(studioExecuteDTO.getDialect())){
            return explainCommonSql(studioExecuteDTO);
        }else{
            return explainFlinkSql(studioExecuteDTO);
        }
    }

    private List<SqlExplainResult> explainFlinkSql(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.buildPlanMode(config);
        return jobManager.explainSql(studioExecuteDTO.getStatement()).getSqlExplainResults();
    }

    private List<SqlExplainResult> explainCommonSql(StudioExecuteDTO studioExecuteDTO) {
        if(Asserts.isNull(studioExecuteDTO.getDatabaseId())){
            return new ArrayList<SqlExplainResult>(){{
                add(SqlExplainResult.fail(studioExecuteDTO.getStatement(),"请指定数据源"));
            }};
        }else{
            DataBase dataBase = dataBaseService.getById(studioExecuteDTO.getDatabaseId());
            if(Asserts.isNull(dataBase)){
                return new ArrayList<SqlExplainResult>(){{
                    add(SqlExplainResult.fail(studioExecuteDTO.getStatement(),"数据源不存在"));
                }};
            }
            SqlExplainResult explainResult = Driver.build(dataBase.getDriverConfig()).connect().explain(studioExecuteDTO.getStatement());
            return new ArrayList<SqlExplainResult>(){{
                add(explainResult);
            }};
        }
    }

    @Override
    public ObjectNode getStreamGraph(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        config.setType(GatewayType.LOCAL.getLongValue());
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.buildPlanMode(config);
        return jobManager.getStreamGraph(studioExecuteDTO.getStatement());
    }

    @Override
    public ObjectNode getJobPlan(StudioExecuteDTO studioExecuteDTO) {
        JobConfig config = studioExecuteDTO.getJobConfig();
        config.setType(GatewayType.LOCAL.getLongValue());
        if(!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioExecuteDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.buildPlanMode(config);
        String planJson = jobManager.getJobPlanJson(studioExecuteDTO.getStatement());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode =mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(planJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }finally {
            return objectNode;
        }
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
        try{
            return FlinkAPI.build(cluster.getJobManagerHost()).listJobs();
        } catch (Exception e) {
            logger.info("查询作业时集群不存在");
        }
        return new ArrayList<>();
    }

    @Override
    public boolean cancel(Integer clusterId,String jobId) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster,"该集群不存在");
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress(cluster.getJobManagerHost());
        if(Asserts.isNotNull(cluster.getClusterConfigurationId())){
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
        }
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
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
            jobConfig.getGatewayConfig().getClusterConfig().setAppId(cluster.getName());
            jobConfig.setTaskId(cluster.getTaskId());
        }
        JobManager jobManager = JobManager.build(jobConfig);
        jobManager.setUseGateway(true);
        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType,null);
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
