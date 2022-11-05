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

package com.dlink.utils;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.core.execution.SavepointFormatType;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.ContextResolvedTable;
import org.apache.flink.table.catalog.ObjectIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * FlinkUtil
 *
 * @author wenmo
 * @since 2022/05/08
 */
public class FlinkUtil {

    public static List<String> getFieldNamesFromCatalogManager(CatalogManager catalogManager, String catalog, String database, String table) {
        Optional<ContextResolvedTable> tableOpt = catalogManager.getTable(
                ObjectIdentifier.of(catalog, database, table)
        );
        if (tableOpt.isPresent()) {
            return tableOpt.get().getResolvedSchema().getColumnNames();
        } else {
            return new ArrayList<String>();
        }
    }

    public static List<String> catchColumn(TableResult tableResult) {
        return tableResult.getResolvedSchema().getColumnNames();
    }

    public static String triggerSavepoint(ClusterClient clusterClient, String jobId, String savePoint) throws ExecutionException, InterruptedException {
        return clusterClient.triggerSavepoint(JobID.fromHexString(jobId), savePoint, SavepointFormatType.DEFAULT).get().toString();
    }

    public static String stopWithSavepoint(ClusterClient clusterClient, String jobId, String savePoint) throws ExecutionException, InterruptedException {
        return clusterClient.stopWithSavepoint(JobID.fromHexString(jobId), true, savePoint, SavepointFormatType.DEFAULT).get().toString();
    }

    public static String cancelWithSavepoint(ClusterClient clusterClient, String jobId, String savePoint) throws ExecutionException, InterruptedException {
        return clusterClient.cancelWithSavepoint(JobID.fromHexString(jobId), savePoint, SavepointFormatType.DEFAULT).get().toString();
    }
}
