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

package org.dinky.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.annotations.ProcessStep;
import org.dinky.data.enums.ProcessStepType;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;

@Slf4j
@Setter
@Getter
public class JobManager {
    JobManagerHandler jobManagerHandler;

    public JobManager() {
    }

    private JobManager(JobConfig config, boolean isPlanMode) {
        jobManagerHandler = JobManagerHandler.build(config, isPlanMode);
    }

    public static JobManager build(JobConfig config) {
        return build(config, false);
    }

    public static JobManager build(JobConfig config, boolean isPlanMode) {
        return new JobManager(config, isPlanMode);
    }

    public boolean close() {
        return jobManagerHandler.close();
    }

    public ObjectNode getJarStreamGraphJson(String statement) {
        return jobManagerHandler.getJarStreamGraphJson(statement);
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeJarSql(String statement) throws Exception {
        return jobManagerHandler.executeJarSql(statement);
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeSql(String statement) throws Exception {
        return jobManagerHandler.executeSql(statement);
    }

    public IResult executeDDL(String statement) {
        return jobManagerHandler.executeDDL(statement);
    }

    public static SelectResult getJobData(String jobId) {
        return ResultPool.get(jobId);
    }

    public ExplainResult explainSql(String statement) {
        return jobManagerHandler.explainSql(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        return jobManagerHandler.getStreamGraph(statement);
    }

    public String getJobPlanJson(String statement) {
        return jobManagerHandler.getJobPlanJson(statement);
    }

    public boolean cancelNormal(String jobId) {
        return jobManagerHandler.cancelNormal(jobId);
    }

    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) {
        return jobManagerHandler.savepoint(jobId, savePointType, savePoint);
    }

    public String exportSql(String sql) {
        return jobManagerHandler.exportSql(sql);
    }

}
