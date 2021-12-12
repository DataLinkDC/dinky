package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.dto.*;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.result.APIJobResult;
import com.dlink.result.ExplainResult;
import com.dlink.service.APIService;
import com.dlink.utils.RunTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * APIServiceImpl
 *
 * @author wenmo
 * @since 2021/12/11 21:46
 */
@Service
public class APIServiceImpl implements APIService {

    @Override
    public APIJobResult executeSql(APIExecuteSqlDTO apiExecuteSqlDTO) {
        JobConfig config = apiExecuteSqlDTO.getJobConfig();
        JobManager jobManager = JobManager.build(config);
        JobResult jobResult = jobManager.executeSql(apiExecuteSqlDTO.getStatement());
        APIJobResult apiJobResult = APIJobResult.build(jobResult);
        RunTimeUtil.recovery(jobManager);
        return apiJobResult;
    }

    @Override
    public ExplainResult explainSql(APIExplainSqlDTO apiExplainSqlDTO) {
        JobConfig config = apiExplainSqlDTO.getJobConfig();
        JobManager jobManager = JobManager.buildPlanMode(config);
        ExplainResult explainResult = jobManager.explainSql(apiExplainSqlDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return explainResult;
    }

    @Override
    public ObjectNode getJobPlan(APIExplainSqlDTO apiExplainSqlDTO) {
        JobConfig config = apiExplainSqlDTO.getJobConfig();
        JobManager jobManager = JobManager.buildPlanMode(config);
        String planJson = jobManager.getJobPlanJson(apiExplainSqlDTO.getStatement());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode =mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(planJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }finally {
            RunTimeUtil.recovery(jobManager);
            return objectNode;
        }
    }

    @Override
    public ObjectNode getStreamGraph(APIExplainSqlDTO apiExplainSqlDTO) {
        JobConfig config = apiExplainSqlDTO.getJobConfig();
        JobManager jobManager = JobManager.buildPlanMode(config);
        ObjectNode streamGraph = jobManager.getStreamGraph(apiExplainSqlDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return streamGraph;
    }

    @Override
    public boolean cancel(APICancelDTO apiCancelDTO) {
        JobConfig jobConfig = apiCancelDTO.getJobConfig();
        JobManager jobManager = JobManager.build(jobConfig);
        boolean cancel = jobManager.cancel(apiCancelDTO.getJobId());
        RunTimeUtil.recovery(jobManager);
        return cancel;
    }

    @Override
    public SavePointResult savepoint(APISavePointDTO apiSavePointDTO) {
        JobConfig jobConfig = apiSavePointDTO.getJobConfig();
        JobManager jobManager = JobManager.build(jobConfig);
        SavePointResult savepoint = jobManager.savepoint(apiSavePointDTO.getJobId(), apiSavePointDTO.getSavePointType(), apiSavePointDTO.getSavePoint());
        RunTimeUtil.recovery(jobManager);
        return savepoint;
    }

    @Override
    public APIJobResult executeJar(APIExecuteJarDTO apiExecuteJarDTO) {
        JobConfig config = apiExecuteJarDTO.getJobConfig();
        JobManager jobManager = JobManager.build(config);
        JobResult jobResult = jobManager.executeJar();
        APIJobResult apiJobResult = APIJobResult.build(jobResult);
        RunTimeUtil.recovery(jobManager);
        return apiJobResult;
    }
}
