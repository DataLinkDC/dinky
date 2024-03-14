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

package org.dinky.executor;

import org.dinky.classloader.DinkyClassLoader;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.SqlExplainResult;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dinky.job.JobParam;

public interface Executor {
    // return dinkyClassLoader
    DinkyClassLoader getDinkyClassLoader();

    VariableManager getVariableManager();

    boolean isUseSqlFragment();

    ExecutionConfig getExecutionConfig();

    StreamExecutionEnvironment getStreamExecutionEnvironment();

    void setStreamExecutionEnvironment(StreamExecutionEnvironment environment);

    CustomTableEnvironment getCustomTableEnvironment();

    ExecutorConfig getExecutorConfig();

    Map<String, String> getSetConfig();

    TableConfig getTableConfig();

    String getTimeZone();

    String pretreatStatement(String statement);

    JobExecutionResult execute(String jobName) throws Exception;

    JobClient executeAsync(String jobName) throws Exception;

    TableResult executeSql(String statement);

    void initUDF(String... udfFilePath);

    void initPyUDF(String executable, String... udfPyFilePath);

    void addJar(File... jarPath);

    SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails);

    String getJarStreamingPlanStringJson(String parameter);

    ObjectNode getStreamGraph(List<String> statements);

    List<URL> getAllFileSet();

    FlinkUdfPathContextHolder getUdfPathContextHolder();

    StreamGraph getStreamGraph();

    ObjectNode getStreamGraphFromDataStream(List<String> statements);

    JobPlanInfo getJobPlanInfo(List<String> statements);

    JobPlanInfo getJobPlanInfoFromDataStream(List<String> statements);

    JobGraph getJobGraphFromInserts(List<String> statements);

    TableResult executeStatementSet(List<String> statements);

    String explainStatementSet(List<String> statements);

    JobPlanInfo getJobPlanInfo(JobParam jobParam);

    String getJobPlanJson(JobParam jobParam);

    void initializeFileSystem();

    List<LineageRel> getLineage(String statement);
}
