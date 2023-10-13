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

package org.dinky.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.VarCharType;
import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.config.Dialect;
import org.dinky.data.dto.StudioCADTO;
import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.Cluster;
import org.dinky.data.model.Column;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.FlinkColumn;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.DDLResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.explainer.lineage.LineageBuilder;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.process.context.ProcessContextHolder;
import org.dinky.process.enums.ProcessType;
import org.dinky.process.model.ProcessEntity;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.DataBaseService;
import org.dinky.service.StudioService;
import org.dinky.service.TaskService;
import org.dinky.sql.FlinkQuery;
import org.dinky.utils.RunTimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** StudioServiceImpl */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudioServiceImpl implements StudioService {

    private final ClusterInstanceService clusterInstanceService;
    private final DataBaseService dataBaseService;
    private final TaskService taskService;
    private final Cache<String, JobManager> jobManagerCache = CacheUtil.newTimedCache(1000 * 60 * 2);

    private IResult executeMSFlinkSql(StudioMetaStoreDTO studioMetaStoreDTO) {
        String envSql = taskService.buildEnvSql(studioMetaStoreDTO);
        studioMetaStoreDTO.setStatement(studioMetaStoreDTO.getStatement() + envSql);
        JobConfig config = studioMetaStoreDTO.getJobConfig();
        JobManager jobManager = JobManager.build(config);
        IResult jobResult = jobManager.executeDDL(studioMetaStoreDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return jobResult;
    }

    @Override
    public JdbcSelectResult getCommonSqlData(Integer taskId) {
        return ResultPool.getCommonSqlCache(taskId);
    }

    @Override
    public IResult executeDDL(StudioDDLDTO studioDDLDTO) {
        JobConfig config = studioDDLDTO.getJobConfig();
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeDDL(studioDDLDTO.getStatement());
    }

    @Override
    public SelectResult getJobData(String jobId) {
        return JobManager.getJobData(jobId);
    }

    @Override
    public LineageResult getLineage(StudioCADTO studioCADTO) {
        ProcessEntity process = ProcessContextHolder.registerProcess(
                ProcessEntity.init(ProcessType.LINEAGE, StpUtil.getLoginIdAsInt()));
        if (Asserts.isNotNullString(studioCADTO.getDialect())
                && !Dialect.FLINK_SQL.equalsVal(studioCADTO.getDialect())) {
            if (Asserts.isNull(studioCADTO.getDatabaseId())) {
                process.error("Job's data source not selected!");
                return null;
            }
            DataBase dataBase = dataBaseService.getById(studioCADTO.getDatabaseId());
            if (Asserts.isNull(dataBase)) {
                process.error("Job's data source does not exist!");
                return null;
            }
            if (Dialect.DORIS.equalsVal(studioCADTO.getDialect())) {
                return org.dinky.explainer.sqllineage.LineageBuilder.getSqlLineage(
                        studioCADTO.getStatement(), "mysql", dataBase.getDriverConfig());
            } else {
                return org.dinky.explainer.sqllineage.LineageBuilder.getSqlLineage(
                        studioCADTO.getStatement(), studioCADTO.getDialect().toLowerCase(), dataBase.getDriverConfig());
            }
        } else {
            String envSql = taskService.buildEnvSql(studioCADTO);
            studioCADTO.setStatement(studioCADTO.getStatement() + envSql);
            return LineageBuilder.getColumnLineageByLogicalPlan(studioCADTO.getStatement());
        }
    }

    @Override
    public List<JsonNode> listFlinkJobs(Integer clusterId) {
        Cluster cluster = clusterInstanceService.getById(clusterId);
        Asserts.checkNotNull(cluster, "该集群不存在");
        try {
            return FlinkAPI.build(cluster.getJobManagerHost()).listJobs();
        } catch (Exception e) {
            log.info("查询作业时集群不存在");
        }
        return new ArrayList<>();
    }
    @Override
    public List<Catalog> getMSCatalogs(StudioMetaStoreDTO studioMetaStoreDTO) {
        List<Catalog> catalogs = new ArrayList<>();
        if (Dialect.isCommonSql(studioMetaStoreDTO.getDialect())) {
            DataBase dataBase = dataBaseService.getById(studioMetaStoreDTO.getDatabaseId());
            if (!Asserts.isNull(dataBase)) {
                Catalog defaultCatalog = Catalog.build(FlinkQuery.defaultCatalog());
                Driver driver = Driver.build(dataBase.getDriverConfig());
                defaultCatalog.setSchemas(driver.listSchemas());
                catalogs.add(defaultCatalog);
            }
        } else {
            String envSql = taskService.buildEnvSql(studioMetaStoreDTO);
            JobManager jobManager = getJobManager(studioMetaStoreDTO, envSql);
            CustomTableEnvironment customTableEnvironment = jobManager.getExecutor().getCustomTableEnvironment();
            for (String catalogName : customTableEnvironment.listCatalogs()) {
                Catalog catalog = Catalog.build(catalogName);
                List<Schema> schemas = new ArrayList<>();
                customTableEnvironment.useCatalog(catalogName);
                for (String database : customTableEnvironment.listDatabases()) {
                    Schema schema = Schema.build(database);
                    schemas.add(schema);
                }
                catalog.setSchemas(schemas);
                catalogs.add(catalog);
            }
        }
        return catalogs;
    }

    @Override
    public Schema getMSSchemaInfo(StudioMetaStoreDTO studioMetaStoreDTO) {
        String database = studioMetaStoreDTO.getDatabase();
        Schema schema = Schema.build(database);
        List<Table> tables = new ArrayList<>();
        if (Dialect.isCommonSql(studioMetaStoreDTO.getDialect())) {
            DataBase dataBase = dataBaseService.getById(studioMetaStoreDTO.getDatabaseId());
            if (Asserts.isNotNull(dataBase)) {
                Driver driver = Driver.build(dataBase.getDriverConfig());
                tables.addAll(driver.listTables(database));
            }
        } else {
            String envSql = taskService.buildEnvSql(studioMetaStoreDTO);
            JobManager jobManager = getJobManager(studioMetaStoreDTO, envSql);
            CustomTableEnvironment customTableEnvironment = jobManager.getExecutor().getCustomTableEnvironment();
            String catalogName = studioMetaStoreDTO.getCatalog();
            customTableEnvironment.useCatalog(catalogName);
            customTableEnvironment.useDatabase(database);
            for (String tableName : customTableEnvironment.listTables(catalogName, database)) {
                Table table = Table.build(tableName, catalogName);
                tables.add(table);
            }
            schema.setTables(tables);

            // show views
            schema.setViews(Arrays.asList(customTableEnvironment.listViews()));
            // show functions
            schema.setFunctions(Arrays.asList(customTableEnvironment.listFunctions()));
            // show user functions
            schema.setUserFunctions(Arrays.asList(customTableEnvironment.listUserDefinedFunctions()));
            // show modules
            schema.setModules(Arrays.asList(customTableEnvironment.listModules()));
        }
        schema.setTables(tables);
        return schema;
    }

    @Override
    public List<Column> getMSFlinkColumns(StudioMetaStoreDTO studioMetaStoreDTO) {
        List<Column> columns = new ArrayList<>();
        if (!Dialect.isCommonSql(studioMetaStoreDTO.getDialect())) {
            String catalogName = studioMetaStoreDTO.getCatalog();
            String database = studioMetaStoreDTO.getDatabase();
            String tableName = studioMetaStoreDTO.getTable();
            String envSql = taskService.buildEnvSql(studioMetaStoreDTO);
            JobManager jobManager = getJobManager(studioMetaStoreDTO, envSql);
            CustomTableEnvironment customTableEnvironment = jobManager.getExecutor().getCustomTableEnvironment();

            customTableEnvironment.getCatalogManager().getTable(ObjectIdentifier.of(catalogName, database, tableName))
                    .ifPresent(t -> {
                        for (int i = 0; i < t.getResolvedSchema().getColumns().size(); i++) {
                            org.apache.flink.table.catalog.Column flinkColumn = t.getResolvedSchema().getColumns().get(i);
                            AtomicBoolean isPrimaryKey = new AtomicBoolean(false);
                            t.getResolvedSchema().getPrimaryKey().ifPresent(k -> {
                                isPrimaryKey.set(k.getColumns().contains(flinkColumn.getName()));
                            });
                            LogicalType logicalType = flinkColumn.getDataType().getLogicalType();
                            Column column = Column.builder().name(flinkColumn.getName()).type(logicalType.getTypeRoot().name())
                                    .comment(flinkColumn.getComment().orElse("")).keyFlag(isPrimaryKey.get())
                                    .isNullable(logicalType.isNullable())
                                    .position(i)
                                    .build();
                            if (logicalType instanceof VarCharType) {
                                column.setLength(((VarCharType) logicalType).getLength());
                            } else if (logicalType instanceof TimestampType) {
                                column.setLength(((TimestampType) logicalType).getPrecision());
                            } else if (logicalType instanceof DecimalType) {
                                column.setLength(((DecimalType) logicalType).getPrecision());
                                column.setScale(((DecimalType) logicalType).getScale());
                            }

                            for (ColumnType columnType : ColumnType.values()) {
                                if (columnType.getJavaType().equals(flinkColumn.getDataType().getConversionClass().getName())) {
                                    column.setJavaType(columnType);
                                    break;
                                }
                            }
//                            FlinkColumn flinkColumn = FlinkColumn.build(i, column.getName(), column.getDataType().getConversionClass().getName(), isPrimaryKey.get(), column.getDataType().getLogicalType().isNullable(), column.explainExtras().orElse(""), "", column.getComment().orElse(""));


                            columns.add(column);
                        }
                    });
        }
        return columns;
    }

    private JobManager getJobManager(StudioMetaStoreDTO studioMetaStoreDTO, String envSql) {
        JobManager jobManager = jobManagerCache.get(envSql, () -> {
            JobConfig config = studioMetaStoreDTO.getJobConfig();
            JobManager jobManagerTmp = JobManager.build(config);
            jobManagerTmp.executeDDL(envSql);
            return jobManagerTmp;
        });
        return jobManager;
    }

    private List<String> showInfo(StudioMetaStoreDTO studioMetaStoreDTO, String baseStatement, String statement) {
        List<String> infos = new ArrayList<>();
        studioMetaStoreDTO.setStatement(baseStatement + statement);
        IResult result = executeMSFlinkSql(studioMetaStoreDTO);
        if (result instanceof DDLResult) {
            DDLResult ddlResult = (DDLResult) result;
            ddlResult.getColumns().stream().findFirst().ifPresent(key -> {
                for (Map<String, Object> item : ddlResult.getRowData()) {
                    infos.add(item.get(key).toString());
                }
            });
        }
        return infos;
    }
}
