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

import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioLineageDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.Column;
import org.dinky.data.model.Schema;
import org.dinky.data.result.IResult;
import org.dinky.data.result.SelectResult;
import org.dinky.explainer.lineage.LineageResult;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * StudioService
 *
 * @since 2021/5/30 11:07
 */
public interface StudioService {

    IResult executeDDL(StudioDDLDTO studioDDLDTO);

    SelectResult getJobData(String jobId);

    LineageResult getLineage(StudioLineageDTO studioCADTO);

    List<JsonNode> listFlinkJobs(Integer clusterId);

    List<Catalog> getMSCatalogs(StudioMetaStoreDTO studioMetaStoreDTO);

    Schema getMSSchemaInfo(StudioMetaStoreDTO studioMetaStoreDTO);

    List<Column> getMSColumns(StudioMetaStoreDTO studioMetaStoreDTO);
}
