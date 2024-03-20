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

package org.dinky.flink;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobConfig;
import org.dinky.job.JobResult;

public class LocalExecutorService implements DinkyExecutor {
    @Override
    public void init(JobConfig config, boolean isPlanMode) {

    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public ObjectNode getJarStreamGraphJson(String statement) {
        return null;
    }

    @Override
    public JobResult executeJarSql(String statement) throws Exception {
        return null;
    }

    @Override
    public JobResult executeSql(String statement) throws Exception {
        return null;
    }

    @Override
    public IResult executeDDL(String statement) {
        return null;
    }

    @Override
    public ExplainResult explainSql(String statement) {
        return null;
    }

    @Override
    public ObjectNode getStreamGraph(String statement) {
        return null;
    }

    @Override
    public String getJobPlanJson(String statement) {
        return null;
    }

    @Override
    public boolean cancelNormal(String jobId) {
        return false;
    }

    @Override
    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) {
        return null;
    }

    @Override
    public String exportSql(String sql) {
        return null;
    }
}
