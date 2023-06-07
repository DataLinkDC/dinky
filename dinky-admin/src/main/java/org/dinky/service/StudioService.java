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

package org.dinky.service;

import org.dinky.data.dto.SqlDTO;
import org.dinky.data.dto.StudioCADTO;
import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioExecuteDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.FlinkColumn;
import org.dinky.data.model.Schema;
import org.dinky.data.result.IResult;
import org.dinky.data.result.SelectResult;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.job.JobResult;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * StudioService
 *
 * @since 2021/5/30 11:07
 */
public interface StudioService {

    JobResult executeSql(StudioExecuteDTO studioExecuteDTO);

    JobResult executeCommonSql(SqlDTO sqlDTO);

    IResult executeDDL(StudioDDLDTO studioDDLDTO);

    List<SqlExplainResult> explainSql(StudioExecuteDTO studioExecuteDTO);

    ObjectNode getStreamGraph(StudioExecuteDTO studioExecuteDTO);

    ObjectNode getJobPlan(StudioExecuteDTO studioExecuteDTO);

    SelectResult getJobData(String jobId);

    LineageResult getLineage(StudioCADTO studioCADTO);

    List<JsonNode> listJobs(Integer clusterId);

    boolean cancel(Integer clusterId, String jobId);

    boolean savepoint(
            Integer taskId, Integer clusterId, String jobId, String savePointType, String name);

    List<Catalog> getMSCatalogs(StudioMetaStoreDTO studioMetaStoreDTO);

    Schema getMSSchemaInfo(StudioMetaStoreDTO studioMetaStoreDTO);

    List<FlinkColumn> getMSFlinkColumns(StudioMetaStoreDTO studioMetaStoreDTO);
}
