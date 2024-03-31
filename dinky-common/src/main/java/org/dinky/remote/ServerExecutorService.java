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

package org.dinky.remote;

import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.CheckPointReadTable;
import org.dinky.data.model.Column;
import org.dinky.data.model.ResourcesVO;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.function.data.model.UDF;
import org.dinky.function.data.model.UDFPath;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.job.Job;
import org.dinky.job.JobConfig;
import org.dinky.job.JobResult;
import org.dinky.metadata.config.DriverConfig;


public interface ServerExecutorService extends Remote {
    void init(JobConfig config, boolean isPlanMode) throws RemoteException;

    boolean close() throws RemoteException;

    ObjectNode getJarStreamGraphJson(String statement) throws RemoteException;

    JobResult executeJarSql(String statement) throws RemoteException;

    JobResult executeSql(String statement) throws RemoteException;

    IResult executeDDL(String statement) throws RemoteException;

    ExplainResult explainSql(String statement) throws RemoteException;

    ObjectNode getStreamGraph(String statement) throws RemoteException;

    String getJobPlanJson(String statement) throws RemoteException;

    boolean cancelNormal(String jobId) throws RemoteException;

    SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) throws RemoteException;

    String exportSql(String sql) throws RemoteException;

    Job getJob() throws RemoteException;

    void prepare(String statement) throws RemoteException;

    List<String> getPythonUdfList(String udfFile)throws RemoteException;

    JobStatus getJobStatus(GatewayConfig gatewayConfig, String appId)throws RemoteException;

    void onJobGatewayFinishCallback(JobConfig jobConfig, String status)throws RemoteException;

    List<String> getUdfClassNameByJarPath(String path) throws RemoteException;

    void putFile(String fullName, byte[] context) throws RemoteException;

    List<ResourcesVO> getFullDirectoryStructure(int rootId) throws RemoteException;

    void rename(String path, String newPath) throws RemoteException;

    String getFileContent(String path) throws RemoteException;

    void updateGitPool(Map<String, String> newPool) throws RemoteException;

    UDFPath initUDF(List<UDF> udfClassList, Integer missionId) throws RemoteException;

    LineageResult getColumnLineageByLogicalPlan(String statement) throws RemoteException;

    LineageResult getSqlLineageByOne(String statement, String type) throws RemoteException;

    LineageResult getSqlLineage(String statement, String mysql, DriverConfig<Map<String, Object>> driverConfig) throws RemoteException;

    List<Catalog> getCatalog() throws RemoteException;

    void setSchemaInfo(
            String catalogName,
            String database,
            Schema schema,
            List<Table> tables) throws RemoteException;

    List<Column> getColumnList(String catalogName, String database, String tableName) throws RemoteException;

    Map<String, Map<String, CheckPointReadTable>> readCheckpoint(String path, String operatorId) throws RemoteException;

    byte[] readFile(String path) throws RemoteException;

    Map<String, List<String>> buildJar(List<UDF> udfCodes) throws RemoteException;

    void buildRowPermission(ConcurrentHashMap<String, String> permission) throws RemoteException;

    List<String> getPrintTables(String statement) throws RemoteException;

    FlinkClusterInfo testFlinkJobManagerIP(String hosts, String host) throws RemoteException;

    void killCluster(GatewayConfig gatewayConfig)throws RemoteException;

    GatewayResult deployCluster(GatewayConfig gatewayConfig) throws RemoteException;

    void addOrUpdate(UDF udf)throws RemoteException;

    void removeUdfCodePool(String className) throws RemoteException;

    String templateParse(String dialect, String templateCode, String className) throws RemoteException;

    void registerPool(List<UDF> collect) throws RemoteException;

    void initResourceManager() throws RemoteException;

    String getPyUDFAttr(String statement) throws RemoteException;

    String getScalaFullClassName(String statement) throws RemoteException;
}
