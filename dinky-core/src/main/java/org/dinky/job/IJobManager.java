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

import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IJobManager {
    void init(JobConfig config, boolean isPlanMode);

    void prepare(String statement);

    boolean close();

    ObjectNode getJarStreamGraphJson(String statement);

    JobResult executeJarSql(String statement) throws Exception;

    JobResult executeSql(String statement) throws Exception;

    IResult executeDDL(String statement);

    ExplainResult explainSql(String statement);

    ObjectNode getStreamGraph(String statement);

    String getJobPlanJson(String statement);

    boolean cancelNormal(String jobId);

    SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint);

    String exportSql(String sql);
}
