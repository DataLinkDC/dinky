/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.dlink.service.impl;

import com.dlink.dto.APICancelDTO;
import com.dlink.dto.APIExecuteJarDTO;
import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.dto.APIExplainSqlDTO;
import com.dlink.dto.APISavePointDTO;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.result.APIJobResult;
import com.dlink.result.ExplainResult;
import com.dlink.service.APIService;
import com.dlink.utils.RunTimeUtil;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        ObjectNode objectNode = mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(planJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
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
