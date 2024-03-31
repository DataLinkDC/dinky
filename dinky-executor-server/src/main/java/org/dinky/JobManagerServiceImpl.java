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

package org.dinky;

import org.dinky.cluster.FlinkCluster;
import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.CheckPointReadTable;
import org.dinky.data.model.Column;
import org.dinky.data.model.ResourcesVO;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.explainer.lineage.LineageBuilder;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.explainer.print_table.PrintStatementExplainer;
import org.dinky.explainer.sqllineage.SQLLineageBuilder;
import org.dinky.flink.checkpoint.CheckpointRead;
import org.dinky.function.FunctionFactory;
import org.dinky.function.data.model.UDF;
import org.dinky.function.data.model.UDFPath;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.function.util.UDFUtil;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.Job;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManagerHandler;
import org.dinky.job.JobResult;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.parser.SqlType;
import org.dinky.remote.ServerExecutorService;
import org.dinky.resource.BaseResourceManager;
import org.dinky.trans.Operations;
import org.dinky.utils.FlinkTableMetadataUtil;
import org.dinky.utils.SqlUtil;

@Slf4j
public class JobManagerServiceImpl extends UnicastRemoteObject implements ServerExecutorService {

    JobManagerHandler jobManagerHandler;
    protected static final CheckpointRead INSTANCE = new CheckpointRead();


    public JobManagerServiceImpl() throws RemoteException {
    }

    @Override
    public void init(JobConfig config, boolean isPlanMode) throws RemoteException {
        jobManagerHandler = JobManagerHandler.build(config, isPlanMode);

    }

    @Override
    public boolean close() {
        return jobManagerHandler.close();
    }

    @Override
    public ObjectNode getJarStreamGraphJson(String statement) throws RemoteException {
        return jobManagerHandler.getJarStreamGraphJson(statement);
    }

    @Override
    public JobResult executeJarSql(String statement) throws RemoteException {
        try {
            return jobManagerHandler.executeJarSql(statement);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public JobResult executeSql(String statement) throws RemoteException {
        try {
            return jobManagerHandler.executeSql(statement);
        } catch (Exception ex) {
            log.error("executeSql error", ex);
            return null;
        }
    }

    @Override
    public IResult executeDDL(String statement) throws RemoteException {
        return jobManagerHandler.executeDDL(statement);
    }

    @Override
    public ExplainResult explainSql(String statement) throws RemoteException {
        return jobManagerHandler.explainSql(statement);
    }

    @Override
    public ObjectNode getStreamGraph(String statement) throws RemoteException {
        return jobManagerHandler.getStreamGraph(statement);
    }

    @Override
    public String getJobPlanJson(String statement) throws RemoteException {
        return jobManagerHandler.getJobPlanJson(statement);
    }

    @Override
    public boolean cancelNormal(String jobId) throws RemoteException {
        return jobManagerHandler.cancelNormal(jobId);
    }

    @Override
    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint)
            throws RemoteException {
        return jobManagerHandler.savepoint(jobId, savePointType, savePoint);
    }

    @Override
    public String exportSql(String sql) {
        return jobManagerHandler.exportSql(sql);
    }

    @Override
    public Job getJob() throws RemoteException {
        return jobManagerHandler.getJob();
    }

    @Override
    public void prepare(String statement) throws RemoteException {
        jobManagerHandler.prepare(statement);
    }


    // TODO: 2024/3/29 utils, coud individual rmeote interface
    @Override
    public List<String> getPythonUdfList(String udfFile) throws RemoteException {
        return UDFUtil.getPythonUdfList(udfFile);
    }

    @Override
    public JobStatus getJobStatus(GatewayConfig gatewayConfig, String appId) throws RemoteException {
        Gateway gateway = Gateway.build(gatewayConfig);
        return gateway.getJobStatusById(appId);
    }

    @Override
    public void onJobGatewayFinishCallback(JobConfig jobConfig, String status) throws RemoteException {
        Gateway.build(jobConfig.getGatewayConfig()).onJobFinishCallback(status);
    }

    @Override
    public List<String> getUdfClassNameByJarPath(String path) throws RemoteException {
        return UDFUtil.getUdfClassNameByJarPath(path);
    }

    @Override
    public Map<String, List<String>> buildJar(List<UDF> udfCodes) throws RemoteException {
        return UDFUtil.buildJar(udfCodes);
    }

    @Override
    public void buildRowPermission(ConcurrentHashMap<String, String> permission) throws RemoteException {
        RowLevelPermissionsContext.set(permission);
    }

    @Override
    public void putFile(String fullName, byte[] context) throws RemoteException {
        BaseResourceManager.getInstance().putFile(fullName, context);
    }

    @Override
    public List<ResourcesVO> getFullDirectoryStructure(int rootId) throws RemoteException {
        return BaseResourceManager.getInstance().getFullDirectoryStructure(rootId);
    }

    @Override
    public void rename(String path, String newPath) throws RemoteException {
        BaseResourceManager.getInstance().rename(path, newPath);
    }

    @Override
    public String getFileContent(String path) throws RemoteException {
        return BaseResourceManager.getInstance().getFileContent(path);
    }

    @Override
    public byte[] readFile(String path) throws RemoteException {
        return BaseResourceManager.getInstance().readFileContext(path);
    }

    @Override
    public void updateGitPool(Map<String, String> newPool) throws RemoteException {
        UdfCodePool.updateGitPool(newPool);
    }

    @Override
    public UDFPath initUDF(List<UDF> udfClassList, Integer missionId) throws RemoteException {
        return FunctionFactory.initUDF(udfClassList, missionId);
    }

    @Override
    public LineageResult getColumnLineageByLogicalPlan(String statement) throws RemoteException {
        return LineageBuilder.getColumnLineageByLogicalPlan(statement);
    }

    @Override
    public LineageResult getSqlLineageByOne(String statement, String type) throws RemoteException {
        return SQLLineageBuilder.getSqlLineageByOne(statement, type);
    }

    @Override
    public LineageResult getSqlLineage(String statement, String mysql, DriverConfig<Map<String, Object>> driverConfig) throws RemoteException {
        return SQLLineageBuilder.getSqlLineage(statement, mysql, driverConfig);
    }

    @Override
    public List<Catalog> getCatalog() throws RemoteException {
        return FlinkTableMetadataUtil.getCatalog(jobManagerHandler.getExecutor().getCustomTableEnvironment());
    }

    @Override
    public void setSchemaInfo(
            String catalogName,
            String database,
            Schema schema,
            List<Table> tables) throws RemoteException {
        FlinkTableMetadataUtil.setSchemaInfo(jobManagerHandler.getExecutor().getCustomTableEnvironment(), catalogName, database, schema, tables);
    }

    @Override
    public List<Column> getColumnList(String catalogName, String database, String tableName) throws RemoteException {
        return FlinkTableMetadataUtil.getColumnList(jobManagerHandler.getExecutor().getCustomTableEnvironment(), catalogName, database, tableName);
    }

    @Override
    public Map<String, Map<String, CheckPointReadTable>> readCheckpoint(String path, String operatorId) throws RemoteException {
        return INSTANCE.readCheckpoint(path, operatorId);
    }

    @Override
    public List<String> getPrintTables(String statement) throws RemoteException{
        // TODO: 2023/4/7 this function not support variable sql, because, JobManager and executor
        // couple function
        //  and status and task execute.
        final String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement));
        return Arrays.stream(statements)
                .filter(t -> SqlType.PRINT.equals(Operations.getOperationType(t)))
                .flatMap(t -> Arrays.stream(PrintStatementExplainer.splitTableNames(t)))
                .collect(Collectors.toList());
    }

    @Override
    public FlinkClusterInfo testFlinkJobManagerIP(String hosts, String host) throws RemoteException {
        return FlinkCluster.testFlinkJobManagerIP(hosts, host);
    }

    @Override
    public void killCluster(GatewayConfig gatewayConfig) throws RemoteException {
        Gateway.build(gatewayConfig).killCluster();
    }

    @Override
    public GatewayResult deployCluster(GatewayConfig gatewayConfig) throws RemoteException {
        return Gateway.build(gatewayConfig).deployCluster(UDFUtil.createFlinkUdfPathContextHolder());
    }

    @Override
    public void addOrUpdate(UDF udf) throws RemoteException {
        UdfCodePool.addOrUpdate(udf);
    }

    @Override
    public void removeUdfCodePool(String className) throws RemoteException {
        UdfCodePool.remove(className);
    }

    @Override
    public String templateParse(String dialect, String templateCode, String className) throws RemoteException {
        return UDFUtil.templateParse(dialect, templateCode, className);
    }

    @Override
    public void registerPool(List<UDF> collect) throws RemoteException {
        UdfCodePool.registerPool(collect);
    }

    @Override
    public void initResourceManager() throws RemoteException {
        BaseResourceManager.initResourceManager();
    }

    @Override
    public String getPyUDFAttr(String statement) throws RemoteException {
        return UDFUtil.getPyUDFAttr(statement);
    }

    @Override
    public String getScalaFullClassName(String statement) throws RemoteException {
        return UDFUtil.getScalaFullClassName(statement);
    }
}