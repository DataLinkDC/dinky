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

import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.data.annotations.ProcessStep;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.enums.ProcessStepType;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.CheckPointReadTable;
import org.dinky.data.model.Column;
import org.dinky.data.model.ResourcesVO;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.function.data.model.UDF;
import org.dinky.function.data.model.UDFPath;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.remote.ServerExecutorService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobManager {
    private static ServerExecutorService serverExecutorService;

    static {
        registerRemote();
    }

    private JobManager(JobConfig config, boolean isPlanMode) {
        try {
            serverExecutorService.init(config, isPlanMode);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerRemote() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");

            // 从Registry中检索远程对象的存根/代理
            serverExecutorService = (ServerExecutorService) registry.lookup("Compute");
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static JobManager build(JobConfig config) {
        return build(config, false);
    }

    public static JobManager build(JobConfig config, boolean isPlanMode) {
        return new JobManager(config, isPlanMode);
    }

    public boolean close() {
        try {
            return serverExecutorService.close();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode getJarStreamGraphJson(String statement) {
        try {
            return serverExecutorService.getJarStreamGraphJson(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepare(String statement) {
        try {
            serverExecutorService.prepare(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeJarSql(String statement) throws Exception {
        return serverExecutorService.executeJarSql(statement);
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeSql(String statement) throws Exception {
        return serverExecutorService.executeSql(statement);
    }

    public IResult executeDDL(String statement) {
        try {
            return serverExecutorService.executeDDL(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static SelectResult getJobData(String jobId) {
        return ResultPool.get(jobId);
    }

    public ExplainResult explainSql(String statement) {
        try {
            return serverExecutorService.explainSql(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode getStreamGraph(String statement) {
        try {
            return serverExecutorService.getStreamGraph(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJobPlanJson(String statement) {
        try {
            return serverExecutorService.getJobPlanJson(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cancelNormal(String jobId) {
        try {
            return serverExecutorService.cancelNormal(jobId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) {
        try {
            return serverExecutorService.savepoint(jobId, savePointType, savePoint);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String exportSql(String sql) {
        try {
            return serverExecutorService.exportSql(sql);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Job getJob() {
        try {
            return serverExecutorService.getJob();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getPythonUdfList(String udfFile) {
        try {
            return serverExecutorService.getPythonUdfList(udfFile);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public JobStatus getJobStatus(GatewayConfig gatewayConfig, String appId) {
        try {
            return serverExecutorService.getJobStatus(gatewayConfig, appId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void onJobGatewayFinishCallback(JobConfig jobConfig, String status) {
        try {
            serverExecutorService.onJobGatewayFinishCallback(jobConfig, status);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getUdfClassNameByJarPath(String path) {
        try {
            return serverExecutorService.getUdfClassNameByJarPath(path);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void putFile(String fullName, byte[] context) {
        try {
            serverExecutorService.putFile(fullName, context);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ResourcesVO> getFullDirectoryStructure(int rootId) {
        try {
            return serverExecutorService.getFullDirectoryStructure(rootId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void rename(String path, String newPath) {
        try {
            serverExecutorService.rename(path, newPath);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileContent(String path) {
        try {
            return serverExecutorService.getFileContent(path);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGitPool(Map<String, String> newPool) {
        try {
            serverExecutorService.updateGitPool(newPool);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public UDFPath initUDF(List<UDF> udfClassList, Integer missionId) {
        try {
            return serverExecutorService.initUDF(udfClassList, missionId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LineageResult getColumnLineageByLogicalPlan(String statement) {
        try {
            return serverExecutorService.getColumnLineageByLogicalPlan(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LineageResult getSqlLineage(String statement, String mysql, DriverConfig<Map<String, Object>> driverConfig) {
        try {
            return serverExecutorService.getSqlLineage(statement, mysql, driverConfig);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Catalog> getCatalog() {
        try {
            return serverExecutorService.getCatalog();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSchemaInfo(String catalogName, String databaseName, Schema schema, List<Table> tables) {
        try {
            serverExecutorService.setSchemaInfo(catalogName, databaseName, schema, tables);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Column> getColumnList(String catalogName, String databaseName, String tableName) {
        try {
            return serverExecutorService.getColumnList(catalogName, databaseName, tableName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Map<String, CheckPointReadTable>> readCheckpoint(String path, String operatorId) {
        try {
            return serverExecutorService.readCheckpoint(path, operatorId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] readFIle(String path) {
        try {
            return serverExecutorService.readFile(path);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<String>> buildJar(List<UDF> udfCodes) {
        try {
            return serverExecutorService.buildJar(udfCodes);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildRowPermission(ConcurrentHashMap<String, String> permission) {
        try {
            serverExecutorService.buildRowPermission(permission);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getPrintTable(String statement) {
        try {
            return serverExecutorService.getPrintTables(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public FlinkClusterInfo testFlinkJobManagerIP(String hosts, String host) {
        try {
            return serverExecutorService.testFlinkJobManagerIP(hosts, host);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void killCluster(GatewayConfig gatewayConfig) {
        try {
            serverExecutorService.killCluster(gatewayConfig);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public GatewayResult deployCluster(GatewayConfig gatewayConfig) {
        try {
            return serverExecutorService.deployCluster(gatewayConfig);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void addOrUpdateUdfCodePool(UDF udf) {
        try {
            serverExecutorService.addOrUpdate(udf);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUdfCodePool(String className) {
        try {
            serverExecutorService.removeUdfCodePool(className);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String templateParse(String dialect, String templateCode, String className) {
        try {
            return serverExecutorService.templateParse(dialect, templateCode, className);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPool(List<UDF> collect) {
        try {
            serverExecutorService.registerPool(collect);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void initResourceManager() {
        try {
            serverExecutorService.initResourceManager();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPyUDFAttr(String statement) {
        try {
            return serverExecutorService.getPyUDFAttr(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getScalaFullClassName(String statement) {
        try {
            return serverExecutorService.getScalaFullClassName(statement);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
