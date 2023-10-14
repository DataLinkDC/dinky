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

import org.dinky.data.dto.StudioCADTO;
import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.FlinkColumn;
import org.dinky.data.model.Schema;
import org.dinky.data.result.IResult;
import org.dinky.data.result.SelectResult;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.metadata.result.JdbcSelectResult;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * StudioService
 *
 * @since 2021/5/30 11:07
 */
public interface StudioService {

    /**
     * Execute a DDL statement and return the result.
     *
     * @param studioDDLDTO A {@link StudioDDLDTO} object representing the DDL statement to execute.
     * @return An {@link IResult} object representing the result of the DDL statement execution.
     */
    IResult executeDDL(StudioDDLDTO studioDDLDTO);

    /**
     * Get common SQL data based on the specified task ID.
     *
     * @param taskId The ID of the task to get the common SQL data for.
     * @return A {@link JdbcSelectResult} object representing the common SQL data for the specified task ID.
     */
    JdbcSelectResult getCommonSqlData(Integer taskId);

    /**
     * Get job data based on the specified job ID.
     *
     * @param jobId The ID of the job to get the job data for.
     * @return A {@link SelectResult} object representing the job data for the specified job ID.
     */
    SelectResult getJobData(String jobId);

    /**
     * Get the lineage information for a specified studio CAD.
     *
     * @param studioCADTO A {@link StudioCADTO} object representing the studio CAD to get the lineage information for.
     * @return A {@link LineageResult} object representing the lineage information for the specified studio CAD.
     */
    LineageResult getLineage(StudioCADTO studioCADTO);

    /**
     * Get a list of Flink jobs based on the specified cluster ID.
     *
     * @param clusterId The ID of the cluster to get the Flink jobs for.
     * @return A list of {@link JsonNode} objects representing the Flink jobs for the specified cluster ID.
     */
    List<JsonNode> listFlinkJobs(Integer clusterId);

    /**
     * Get MS catalogs based on the specified studio meta store DTO.
     *
     * @param studioMetaStoreDTO A {@link StudioMetaStoreDTO} object representing the studio meta store DTO to get the MS catalogs for.
     * @return A list of {@link Catalog} objects representing the MS catalogs for the specified studio meta store DTO.
     */
    List<Catalog> getMSCatalogs(StudioMetaStoreDTO studioMetaStoreDTO);

    /**
     * Get the schema information for a specified MS catalog.
     *
     * @param studioMetaStoreDTO A {@link StudioMetaStoreDTO} object representing the MS catalog to get the schema information for.
     * @return A {@link Schema} object representing the schema information for the specified MS catalog.
     */
    Schema getMSSchemaInfo(StudioMetaStoreDTO studioMetaStoreDTO);

    /**
     * Get a list of Flink columns based on the specified MS catalog.
     *
     * @param studioMetaStoreDTO A {@link StudioMetaStoreDTO} object representing the MS catalog to get the Flink columns for.
     * @return A list of {@link FlinkColumn} objects representing the Flink columns for the specified MS catalog.
     */
    List<FlinkColumn> getMSFlinkColumns(StudioMetaStoreDTO studioMetaStoreDTO);
}
