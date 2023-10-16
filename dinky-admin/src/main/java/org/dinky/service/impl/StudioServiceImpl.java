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

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.config.Dialect;
import org.dinky.data.dto.StudioCADTO;
import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.FlinkColumn;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.DDLResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
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
import java.util.List;
import java.util.Map;

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
        ClusterInstance clusterInstance = clusterInstanceService.getById(clusterId);
        Asserts.checkNotNull(clusterInstance, "该集群不存在");
        try {
            return FlinkAPI.build(clusterInstance.getJobManagerHost()).listJobs();
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
            studioMetaStoreDTO.setStatement(FlinkQuery.showCatalogs());
            IResult result = executeMSFlinkSql(studioMetaStoreDTO);

            if (result instanceof DDLResult) {
                DDLResult ddlResult = (DDLResult) result;
                ddlResult.getColumns().stream().findFirst().ifPresent(key -> {
                    for (Map<String, Object> item : ddlResult.getRowData()) {
                        catalogs.add(Catalog.build(item.get(key).toString()));
                    }
                });

                for (Catalog catalog : catalogs) {
                    String statement = FlinkQuery.useCatalog(catalog.getName())
                            + FlinkQuery.separator()
                            + FlinkQuery.showDatabases();
                    studioMetaStoreDTO.setStatement(statement);
                    IResult tableResult = executeMSFlinkSql(studioMetaStoreDTO);
                    DDLResult tableDDLResult = (DDLResult) tableResult;
                    tableDDLResult.getColumns().stream().findFirst().ifPresent(key -> {
                        List<Map<String, Object>> rowData = tableDDLResult.getRowData();
                        List<Schema> schemas = new ArrayList<>();
                        for (Map<String, Object> item : rowData) {
                            schemas.add(Schema.build(item.get(key).toString()));
                        }
                        catalog.setSchemas(schemas);
                    });
                }
            }
        }
        return catalogs;
    }

    @Override
    public Schema getMSSchemaInfo(StudioMetaStoreDTO studioMetaStoreDTO) {
        Schema schema = Schema.build(studioMetaStoreDTO.getDatabase());
        List<Table> tables = new ArrayList<>();
        if (Dialect.isCommonSql(studioMetaStoreDTO.getDialect())) {
            DataBase dataBase = dataBaseService.getById(studioMetaStoreDTO.getDatabaseId());
            if (Asserts.isNotNull(dataBase)) {
                Driver driver = Driver.build(dataBase.getDriverConfig());
                tables.addAll(driver.listTables(studioMetaStoreDTO.getDatabase()));
            }
        } else {
            String baseStatement = FlinkQuery.useCatalog(studioMetaStoreDTO.getCatalog())
                    + FlinkQuery.separator()
                    + FlinkQuery.useDatabase(studioMetaStoreDTO.getDatabase())
                    + FlinkQuery.separator();

            // show tables
            String tableStatement = baseStatement + FlinkQuery.showTables();
            studioMetaStoreDTO.setStatement(tableStatement);
            IResult result = executeMSFlinkSql(studioMetaStoreDTO);
            if (result instanceof DDLResult) {
                DDLResult ddlResult = (DDLResult) result;
                ddlResult.getColumns().stream().findFirst().ifPresent(key -> {
                    List<Map<String, Object>> rowData = ddlResult.getRowData();
                    for (Map<String, Object> item : rowData) {
                        Table table = Table.build(item.get(key).toString(), studioMetaStoreDTO.getDatabase());
                        table.setCatalog(studioMetaStoreDTO.getCatalog());
                        tables.add(table);
                    }
                });
            }
            // show views
            schema.setViews(showInfo(studioMetaStoreDTO, baseStatement, FlinkQuery.showViews()));
            // show functions
            schema.setFunctions(showInfo(studioMetaStoreDTO, baseStatement, FlinkQuery.showFunctions()));
            // show user functions
            schema.setUserFunctions(showInfo(studioMetaStoreDTO, baseStatement, FlinkQuery.showUserFunctions()));
            // show modules
            schema.setModules(showInfo(studioMetaStoreDTO, baseStatement, FlinkQuery.showModules()));
        }
        schema.setTables(tables);
        return schema;
    }

    @Override
    public List<FlinkColumn> getMSFlinkColumns(StudioMetaStoreDTO studioMetaStoreDTO) {
        List<FlinkColumn> columns = new ArrayList<>();
        if (!Dialect.isCommonSql(studioMetaStoreDTO.getDialect())) {
            String baseStatement = FlinkQuery.useCatalog(studioMetaStoreDTO.getCatalog())
                    + FlinkQuery.separator()
                    + FlinkQuery.useDatabase(studioMetaStoreDTO.getDatabase())
                    + FlinkQuery.separator();

            // desc tables
            String tableStatement = baseStatement + FlinkQuery.descTable(studioMetaStoreDTO.getTable());
            studioMetaStoreDTO.setStatement(tableStatement);
            IResult result = executeMSFlinkSql(studioMetaStoreDTO);
            if (result instanceof DDLResult) {
                DDLResult ddlResult = (DDLResult) result;
                List<Map<String, Object>> rowData = ddlResult.getRowData();
                int i = 1;
                for (Map<String, Object> item : rowData) {
                    FlinkColumn column = FlinkColumn.build(
                            i,
                            item.get(FlinkQuery.columnName()).toString(),
                            item.get(FlinkQuery.columnType()).toString(),
                            item.get(FlinkQuery.columnKey()).toString(),
                            item.get(FlinkQuery.columnNull()).toString(),
                            item.get(FlinkQuery.columnExtras()).toString(),
                            item.get(FlinkQuery.columnWatermark()).toString());
                    columns.add(column);
                    i++;
                }
            }
        }
        return columns;
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
