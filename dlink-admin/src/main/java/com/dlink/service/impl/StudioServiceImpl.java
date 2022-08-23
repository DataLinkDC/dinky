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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.config.Dialect;
import com.dlink.dto.AbstractStatementDTO;
import com.dlink.dto.SessionDTO;
import com.dlink.dto.SqlDTO;
import com.dlink.dto.StudioCADTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.dto.StudioMetaStoreDTO;
import com.dlink.explainer.lineage.LineageBuilder;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Catalog;
import com.dlink.model.Cluster;
import com.dlink.model.DataBase;
import com.dlink.model.FlinkColumn;
import com.dlink.model.Savepoints;
import com.dlink.model.Schema;
import com.dlink.model.SystemConfiguration;
import com.dlink.model.Table;
import com.dlink.model.Task;
import com.dlink.result.DDLResult;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.service.ClusterConfigurationService;
import com.dlink.service.ClusterService;
import com.dlink.service.DataBaseService;
import com.dlink.service.FragmentVariableService;
import com.dlink.service.SavepointsService;
import com.dlink.service.StudioService;
import com.dlink.service.TaskService;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.dlink.sql.FlinkQuery;
import com.dlink.utils.RunTimeUtil;

/**
 * StudioServiceImpl
 *
 * @author wenmo
 * @since 2021/5/30 11:08
 */
@Service
public class StudioServiceImpl implements StudioService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudioServiceImpl.class);
    
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private SavepointsService savepointsService;
    @Autowired
    private DataBaseService dataBaseService;
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private FragmentVariableService fragmentVariableService;
    
    private void addFlinkSQLEnv(AbstractStatementDTO statementDTO) {
        statementDTO.setVariables(fragmentVariableService.listEnabledVariables());
        String flinkWithSql = dataBaseService.getEnabledFlinkWithSql();
        if (statementDTO.isFragment() && Asserts.isNotNullString(flinkWithSql)) {
            statementDTO.setStatement(flinkWithSql + "\r\n" + statementDTO.getStatement());
        }
        if (Asserts.isNotNull(statementDTO.getEnvId()) && !statementDTO.getEnvId().equals(0)) {
            Task task = taskService.getTaskInfoById(statementDTO.getEnvId());
            if (Asserts.isNotNull(task) && Asserts.isNotNullString(task.getStatement())) {
                statementDTO.setStatement(task.getStatement() + "\r\n" + statementDTO.getStatement());
            }
        }
    }
    
    private void buildSession(JobConfig config) {
        // If you are using a shared session, configure the current jobmanager address
        if (!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), config.getClusterId()));
        }
    }
    
    @Override
    public JobResult executeSql(StudioExecuteDTO studioExecuteDTO) {
        if (Dialect.isSql(studioExecuteDTO.getDialect())) {
            return executeCommonSql(SqlDTO.build(studioExecuteDTO.getStatement(),
                studioExecuteDTO.getDatabaseId(), studioExecuteDTO.getMaxRowNum()));
        } else {
            return executeFlinkSql(studioExecuteDTO);
        }
    }
    
    private JobResult executeFlinkSql(StudioExecuteDTO studioExecuteDTO) {
        addFlinkSQLEnv(studioExecuteDTO);
        JobConfig config = studioExecuteDTO.getJobConfig();
        buildSession(config);
        // To initialize java udf, but it has a bug in the product environment now.
        // initUDF(config,studioExecuteDTO.getStatement());
        JobManager jobManager = JobManager.build(config);
        JobResult jobResult = jobManager.executeSql(studioExecuteDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return jobResult;
    }
    
    private IResult executeMSFlinkSql(StudioMetaStoreDTO studioMetaStoreDTO) {
        addFlinkSQLEnv(studioMetaStoreDTO);
        JobConfig config = studioMetaStoreDTO.getJobConfig();
        JobManager jobManager = JobManager.build(config);
        IResult jobResult = jobManager.executeDDL(studioMetaStoreDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return jobResult;
    }
    
    public JobResult executeCommonSql(SqlDTO sqlDTO) {
        JobResult result = new JobResult();
        result.setStatement(sqlDTO.getStatement());
        result.setStartTime(LocalDateTime.now());
        if (Asserts.isNull(sqlDTO.getDatabaseId())) {
            result.setSuccess(false);
            result.setError("请指定数据源");
            result.setEndTime(LocalDateTime.now());
            return result;
        } else {
            DataBase dataBase = dataBaseService.getById(sqlDTO.getDatabaseId());
            if (Asserts.isNull(dataBase)) {
                result.setSuccess(false);
                result.setError("数据源不存在");
                result.setEndTime(LocalDateTime.now());
                return result;
            }
            Driver driver = Driver.build(dataBase.getDriverConfig());
            JdbcSelectResult selectResult = driver.executeSql(sqlDTO.getStatement(), sqlDTO.getMaxRowNum());
            driver.close();
            result.setResult(selectResult);
            if (selectResult.isSuccess()) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
                result.setError(selectResult.getError());
            }
            result.setEndTime(LocalDateTime.now());
            return result;
        }
    }
    
    @Override
    public IResult executeDDL(StudioDDLDTO studioDDLDTO) {
        JobConfig config = studioDDLDTO.getJobConfig();
        if (!config.isUseSession()) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), studioDDLDTO.getClusterId()));
        }
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeDDL(studioDDLDTO.getStatement());
    }
    
    @Override
    public List<SqlExplainResult> explainSql(StudioExecuteDTO studioExecuteDTO) {
        if (Dialect.isSql(studioExecuteDTO.getDialect())) {
            return explainCommonSql(studioExecuteDTO);
        } else {
            return explainFlinkSql(studioExecuteDTO);
        }
    }
    
    private List<SqlExplainResult> explainFlinkSql(StudioExecuteDTO studioExecuteDTO) {
        addFlinkSQLEnv(studioExecuteDTO);
        JobConfig config = studioExecuteDTO.getJobConfig();
        // If you are using explainSql | getStreamGraph | getJobPlan, make the dialect change to local.
        config.buildLocal();
        buildSession(config);
        // To initialize java udf, but it has a bug in the product environment now.
        // initUDF(config,studioExecuteDTO.getStatement());
        JobManager jobManager = JobManager.buildPlanMode(config);
        return jobManager.explainSql(studioExecuteDTO.getStatement()).getSqlExplainResults();
    }
    
    private List<SqlExplainResult> explainCommonSql(StudioExecuteDTO studioExecuteDTO) {
        if (Asserts.isNull(studioExecuteDTO.getDatabaseId())) {
            return new ArrayList<SqlExplainResult>() {{
                add(SqlExplainResult.fail(studioExecuteDTO.getStatement(), "请指定数据源"));
            }};
        } else {
            DataBase dataBase = dataBaseService.getById(studioExecuteDTO.getDatabaseId());
            if (Asserts.isNull(dataBase)) {
                return new ArrayList<SqlExplainResult>() {{
                    add(SqlExplainResult.fail(studioExecuteDTO.getStatement(), "数据源不存在"));
                }};
            }
            Driver driver = Driver.build(dataBase.getDriverConfig());
            List<SqlExplainResult> sqlExplainResults = driver.explain(studioExecuteDTO.getStatement());
            driver.close();
            return sqlExplainResults;
        }
    }
    
    @Override
    public ObjectNode getStreamGraph(StudioExecuteDTO studioExecuteDTO) {
        addFlinkSQLEnv(studioExecuteDTO);
        JobConfig config = studioExecuteDTO.getJobConfig();
        // If you are using explainSql | getStreamGraph | getJobPlan, make the dialect change to local.
        config.buildLocal();
        buildSession(config);
        JobManager jobManager = JobManager.buildPlanMode(config);
        return jobManager.getStreamGraph(studioExecuteDTO.getStatement());
    }
    
    @Override
    public ObjectNode getJobPlan(StudioExecuteDTO studioExecuteDTO) {
        addFlinkSQLEnv(studioExecuteDTO);
        JobConfig config = studioExecuteDTO.getJobConfig();
        // If you are using explainSql | getStreamGraph | getJobPlan, make the dialect change to local.
        config.buildLocal();
        buildSession(config);
        JobManager jobManager = JobManager.buildPlanMode(config);
        String planJson = jobManager.getJobPlanJson(studioExecuteDTO.getStatement());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(planJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            return objectNode;
        }
    }
    
    @Override
    public SelectResult getJobData(String jobId) {
        return JobManager.getJobData(jobId);
    }
    
    @Override
    public SessionInfo createSession(SessionDTO sessionDTO, String createUser) {
        if (sessionDTO.isUseRemote()) {
            Cluster cluster = clusterService.getById(sessionDTO.getClusterId());
            SessionConfig sessionConfig = SessionConfig.build(
                sessionDTO.getType(), true,
                cluster.getId(), cluster.getAlias(),
                clusterService.buildEnvironmentAddress(true, sessionDTO.getClusterId()));
            return JobManager.createSession(sessionDTO.getSession(), sessionConfig, createUser);
        } else {
            SessionConfig sessionConfig = SessionConfig.build(
                sessionDTO.getType(), false,
                null, null,
                clusterService.buildEnvironmentAddress(false, null));
            return JobManager.createSession(sessionDTO.getSession(), sessionConfig, createUser);
        }
    }
    
    @Override
    public boolean clearSession(String session) {
        return SessionPool.remove(session) > 0;
    }
    
    @Override
    public List<SessionInfo> listSession(String createUser) {
        return JobManager.listSession(createUser);
    }
    
    @Override
    public LineageResult getLineage(StudioCADTO studioCADTO) {
        if (Asserts.isNotNullString(studioCADTO.getDialect()) && !studioCADTO.getDialect().equalsIgnoreCase("flinksql")) {
            if (Asserts.isNull(studioCADTO.getDatabaseId())) {
                return null;
            }
            DataBase dataBase = dataBaseService.getById(studioCADTO.getDatabaseId());
            if (Asserts.isNull(dataBase)) {
                return null;
            }
            if (studioCADTO.getDialect().equalsIgnoreCase("doris")) {
                return com.dlink.explainer.sqlLineage.LineageBuilder.getSqlLineage(studioCADTO.getStatement(), "mysql", dataBase.getDriverConfig());
            } else {
                return com.dlink.explainer.sqlLineage.LineageBuilder.getSqlLineage(studioCADTO.getStatement(), studioCADTO.getDialect().toLowerCase(), dataBase.getDriverConfig());
            }
        } else {
            addFlinkSQLEnv(studioCADTO);
            if (SystemConfiguration.getInstances().isUseLogicalPlan()) {
                return LineageBuilder.getColumnLineageByLogicalPlan(studioCADTO.getStatement());
            } else {
                return LineageBuilder.getLineage(studioCADTO.getStatement());
            }
        }
    }
    
    @Override
    public List<JsonNode> listJobs(Integer clusterId) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster, "该集群不存在");
        try {
            return FlinkAPI.build(cluster.getJobManagerHost()).listJobs();
        } catch (Exception e) {
            logger.info("查询作业时集群不存在");
        }
        return new ArrayList<>();
    }
    
    @Override
    public boolean cancel(Integer clusterId, String jobId) {
        Cluster cluster = clusterService.getById(clusterId);
        Asserts.checkNotNull(cluster, "该集群不存在");
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress(cluster.getJobManagerHost());
        if (Asserts.isNotNull(cluster.getClusterConfigurationId())) {
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
        }
        JobManager jobManager = JobManager.build(jobConfig);
        return jobManager.cancel(jobId);
    }
    
    @Override
    public boolean savepoint(Integer taskId, Integer clusterId, String jobId, String savePointType, String name) {
        Cluster cluster = clusterService.getById(clusterId);
        
        Asserts.checkNotNull(cluster, "该集群不存在");
        boolean useGateway = false;
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress(cluster.getJobManagerHost());
        jobConfig.setType(cluster.getType());
        //如果用户选择用dlink平台来托管集群信息 说明任务一定是从dlink发起提交的
        if (Asserts.isNotNull(cluster.getClusterConfigurationId())) {
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
            jobConfig.getGatewayConfig().getClusterConfig().setAppId(cluster.getName());
            jobConfig.setTaskId(cluster.getTaskId());
            useGateway = true;
        }
        //用户选择外部的平台来托管集群信息，但是集群上的任务不一定是通过dlink提交的
        else {
            jobConfig.setTaskId(taskId);
        }
        JobManager jobManager = JobManager.build(jobConfig);
        jobManager.setUseGateway(useGateway);
        
        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType, null);
        if (Asserts.isNotNull(savePointResult)) {
            if (jobConfig.getTaskId().equals(0)) {
                return true;
            }
            for (JobInfo item : savePointResult.getJobInfos()) {
                if (Asserts.isEqualsIgnoreCase(jobId, item.getJobId()) && Asserts.isNotNull(jobConfig.getTaskId())) {
                    Savepoints savepoints = new Savepoints();
                    savepoints.setName(name);
                    savepoints.setType(savePointType);
                    savepoints.setPath(item.getSavePoint());
                    savepoints.setTaskId(jobConfig.getTaskId());
                    savepointsService.save(savepoints);
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public List<Catalog> getMSCatalogs(StudioMetaStoreDTO studioMetaStoreDTO) {
        List<Catalog> catalogs = new ArrayList<>();
        if (Dialect.isSql(studioMetaStoreDTO.getDialect())) {
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
                Iterator<String> iterator = ddlResult.getColumns().iterator();
                if (iterator.hasNext()) {
                    String key = iterator.next();
                    List<Map<String, Object>> rowData = ddlResult.getRowData();
                    for (Map<String, Object> item : rowData) {
                        catalogs.add(Catalog.build(item.get(key).toString()));
                    }
                }
                for (Catalog catalog : catalogs) {
                    String statement = FlinkQuery.useCatalog(catalog.getName()) + FlinkQuery.separator() + FlinkQuery.showDatabases();
                    studioMetaStoreDTO.setStatement(statement);
                    IResult tableResult = executeMSFlinkSql(studioMetaStoreDTO);
                    if (result instanceof DDLResult) {
                        DDLResult tableDDLResult = (DDLResult) tableResult;
                        Iterator<String> tableIterator = tableDDLResult.getColumns().iterator();
                        if (tableIterator.hasNext()) {
                            String key = tableIterator.next();
                            List<Map<String, Object>> rowData = tableDDLResult.getRowData();
                            List<Schema> schemas = new ArrayList<>();
                            for (Map<String, Object> item : rowData) {
                                schemas.add(Schema.build(item.get(key).toString()));
                            }
                            catalog.setSchemas(schemas);
                        }
                    }
                }
            }
        }
        return catalogs;
    }
    
    @Override
    public Schema getMSSchemaInfo(StudioMetaStoreDTO studioMetaStoreDTO) {
        Schema schema = Schema.build(studioMetaStoreDTO.getDatabase());
        List<Table> tables = new ArrayList<>();
        if (Dialect.isSql(studioMetaStoreDTO.getDialect())) {
            DataBase dataBase = dataBaseService.getById(studioMetaStoreDTO.getDatabaseId());
            if (Asserts.isNotNull(dataBase)) {
                Driver driver = Driver.build(dataBase.getDriverConfig());
                tables.addAll(driver.listTables(studioMetaStoreDTO.getDatabase()));
            }
        } else {
            String baseStatement = FlinkQuery.useCatalog(studioMetaStoreDTO.getCatalog()) + FlinkQuery.separator()
                + FlinkQuery.useDatabase(studioMetaStoreDTO.getDatabase()) + FlinkQuery.separator();
            // show tables
            String tableStatement = baseStatement + FlinkQuery.showTables();
            studioMetaStoreDTO.setStatement(tableStatement);
            IResult result = executeMSFlinkSql(studioMetaStoreDTO);
            if (result instanceof DDLResult) {
                DDLResult ddlResult = (DDLResult) result;
                Iterator<String> iterator = ddlResult.getColumns().iterator();
                if (iterator.hasNext()) {
                    String key = iterator.next();
                    List<Map<String, Object>> rowData = ddlResult.getRowData();
                    for (Map<String, Object> item : rowData) {
                        Table table = Table.build(item.get(key).toString(), studioMetaStoreDTO.getDatabase());
                        table.setCatalog(studioMetaStoreDTO.getCatalog());
                        tables.add(table);
                    }
                }
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
        if (Dialect.isSql(studioMetaStoreDTO.getDialect())) {
            // nothing to do
        } else {
            String baseStatement = FlinkQuery.useCatalog(studioMetaStoreDTO.getCatalog()) + FlinkQuery.separator()
                + FlinkQuery.useDatabase(studioMetaStoreDTO.getDatabase()) + FlinkQuery.separator();
            // desc tables
            String tableStatement = baseStatement + FlinkQuery.descTable(studioMetaStoreDTO.getTable());
            studioMetaStoreDTO.setStatement(tableStatement);
            IResult result = executeMSFlinkSql(studioMetaStoreDTO);
            if (result instanceof DDLResult) {
                DDLResult ddlResult = (DDLResult) result;
                List<Map<String, Object>> rowData = ddlResult.getRowData();
                int i = 1;
                for (Map<String, Object> item : rowData) {
                    FlinkColumn column = FlinkColumn.build(i,
                        item.get(FlinkQuery.columnName()).toString(),
                        item.get(FlinkQuery.columnType()).toString(),
                        item.get(FlinkQuery.columnKey()).toString(),
                        item.get(FlinkQuery.columnNull()).toString(),
                        item.get(FlinkQuery.columnExtras()).toString(),
                        item.get(FlinkQuery.columnWatermark()).toString()
                    );
                    columns.add(column);
                    i++;
                }
            }
        }
        return columns;
    }
    
    private List<String> showInfo(StudioMetaStoreDTO studioMetaStoreDTO, String baseStatement, String statement) {
        List<String> infos = new ArrayList<>();
        String tableStatement = baseStatement + statement;
        studioMetaStoreDTO.setStatement(tableStatement);
        IResult result = executeMSFlinkSql(studioMetaStoreDTO);
        if (result instanceof DDLResult) {
            DDLResult ddlResult = (DDLResult) result;
            Iterator<String> iterator = ddlResult.getColumns().iterator();
            if (iterator.hasNext()) {
                String key = iterator.next();
                List<Map<String, Object>> rowData = ddlResult.getRowData();
                for (Map<String, Object> item : rowData) {
                    infos.add(item.get(key).toString());
                }
            }
        }
        return infos;
    }
    
    private void initUDF(JobConfig config, String statement) {
        if (!GatewayType.LOCAL.equalsValue(config.getType())) {
            return;
        }
        List<String> udfClassNameList = JobManager.getUDFClassName(statement);
        for (String item : udfClassNameList) {
            Task task = taskService.getUDFByClassName(item);
            JobManager.initUDF(item, task.getStatement());
        }
    }
}
