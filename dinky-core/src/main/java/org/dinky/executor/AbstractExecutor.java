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

import org.dinky.assertion.Asserts;
import org.dinky.classloader.DinkyClassLoader;
import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.interceptor.FlinkInterceptorResult;
import org.dinky.utils.KerberosUtil;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.python.PythonOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Executor
 *
 * @since 2021/11/17
 */
@Slf4j
public abstract class AbstractExecutor implements Executor{

    private static final Logger logger = LoggerFactory.getLogger(AbstractExecutor.class);

    // Flink stream execution environment, batch model also use it.
    protected StreamExecutionEnvironment environment;

    // Dinky table environment.
    protected CustomTableEnvironment tableEnvironment;

    // The config of Dinky executor.
    protected ExecutorConfig executorConfig;

    protected WeakReference<DinkyClassLoader> dinkyClassLoader = new WeakReference<>(DinkyClassLoader.build());

    // Flink configuration, such as set rest.port = 8086
    protected Map<String, String> setConfig = new HashMap<>();

    // Dinky variable manager
    protected VariableManager variableManager = new VariableManager();

    // return dinkyClassLoader
    @Override
    public DinkyClassLoader getDinkyClassLoader() {
        return dinkyClassLoader.get();
    }

    @Override
    public VariableManager getVariableManager() {
        return variableManager;
    }

    @Override
    public boolean isUseSqlFragment() {
        return executorConfig.isUseSqlFragment();
    }

    @Override
    public ExecutionConfig getExecutionConfig() {
        return environment.getConfig();
    }

    @Override
    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return environment;
    }

    @Override
    public void setStreamExecutionEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public CustomTableEnvironment getCustomTableEnvironment() {
        return tableEnvironment;
    }

    @Override
    public ExecutorConfig getExecutorConfig() {
        return executorConfig;
    }

    @Override
    public Map<String, String> getSetConfig() {
        return setConfig;
    }

    @Override
    public TableConfig getTableConfig() {
        return tableEnvironment.getConfig();
    }

    @Override
    public String getTimeZone() {
        return getTableConfig().getLocalTimeZone().getId();
    }

    private void initClassloader(DinkyClassLoader classLoader) {
        if (classLoader != null) {
            try {
                StreamExecutionEnvironment env = this.environment;
                // Fix the Classloader in the env above  to appClassLoader, causing ckp to fail to compile
                ReflectUtil.setFieldValue(env, "userClassloader", classLoader);
                env.configure(env.getConfiguration(), classLoader);
            } catch (Throwable e) {
                log.warn(
                        "The version of flink does not have a Classloader field and the classloader cannot be set.", e);
            }
        }
    }

    protected void init() {
        initClassloader(getDinkyClassLoader());
        if (executorConfig.isValidParallelism()) {
            environment.setParallelism(executorConfig.getParallelism());
        }

        tableEnvironment = createCustomTableEnvironment(getDinkyClassLoader());
        CustomTableEnvironmentContext.set(tableEnvironment);

        Configuration configuration = tableEnvironment.getConfig().getConfiguration();
        if (executorConfig.isValidJobName()) {
            configuration.setString(PipelineOptions.NAME.key(), executorConfig.getJobName());
            setConfig.put(PipelineOptions.NAME.key(), executorConfig.getJobName());
        }
        if (executorConfig.isValidConfig()) {
            for (Map.Entry<String, String> entry : executorConfig.getConfig().entrySet()) {
                configuration.setString(entry.getKey(), entry.getValue());
            }
        }
        if (executorConfig.isValidVariables()) {
            variableManager.registerVariable(executorConfig.getVariables());
        }
    }

    abstract CustomTableEnvironment createCustomTableEnvironment(ClassLoader classLoader);

    @Override
    public String pretreatStatement(String statement) {
        return FlinkInterceptor.pretreatStatement(this, statement);
    }

    private FlinkInterceptorResult pretreatExecute(String statement) {
        return FlinkInterceptor.build(this, statement);
    }

    @Override
    public JobExecutionResult execute(String jobName) throws Exception {
        return environment.execute(jobName);
    }

    @Override
    public JobClient executeAsync(String jobName) throws Exception {
        return environment.executeAsync(jobName);
    }

    @Override
    public TableResult executeSql(String statement) {
        statement = pretreatStatement(statement);
        FlinkInterceptorResult flinkInterceptorResult = pretreatExecute(statement);
        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
            return flinkInterceptorResult.getTableResult();
        }

        if (flinkInterceptorResult.isNoExecute()) {
            return CustomTableResultImpl.TABLE_RESULT_OK;
        }

        KerberosUtil.authenticate(setConfig);
        return tableEnvironment.executeSql(statement);
    }

    @Override
    public void initUDF(String... udfFilePath) {
        List<File> jarFiles = DinkyClassLoader.getJarFiles(udfFilePath, null);
        getDinkyClassLoader().addURLs(jarFiles);
    }

    @Override
    public void initPyUDF(String executable, String... udfPyFilePath) {
        if (udfPyFilePath == null || udfPyFilePath.length == 0) {
            return;
        }

        Configuration configuration = tableEnvironment.getConfig().getConfiguration();
        configuration.setString(PythonOptions.PYTHON_FILES, String.join(",", udfPyFilePath));
        configuration.setString(PythonOptions.PYTHON_CLIENT_EXECUTABLE, executable);
    }

    private void addJar(String... jarPath) {
        Configuration configuration = tableEnvironment.getRootConfiguration();
        List<String> jars = configuration.get(PipelineOptions.JARS);
        if (jars == null) {
            tableEnvironment.addConfiguration(PipelineOptions.JARS, CollUtil.newArrayList(jarPath));
        } else {
            CollUtil.addAll(jars, jarPath);
        }
    }

    @Override
    public void addJar(File... jarPath) {
        addJar(Arrays.stream(jarPath).map(URLUtil::getURL).map(URL::toString).toArray(String[]::new));
    }

    @Override
    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        statement = pretreatStatement(statement);
        if (Asserts.isNotNullString(statement) && !pretreatExecute(statement).isNoExecute()) {
            return tableEnvironment.explainSqlRecord(statement, extraDetails);
        }
        return null;
    }

    @Override
    public ObjectNode getStreamGraph(List<String> statements) {
        StreamGraph streamGraph = tableEnvironment.getStreamGraphFromInserts(statements);
        return getStreamGraphJsonNode(streamGraph);
    }

    private ObjectNode getStreamGraphJsonNode(StreamGraph streamGraph) {
        JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
        String json = jsonGenerator.getJSON();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        try {
            objectNode = (ObjectNode) mapper.readTree(json);
        } catch (JsonProcessingException e) {
            logger.error("Get stream graph json node error.", e);
        }

        return objectNode;
    }

    @Override
    public StreamGraph getStreamGraph() {
        return environment.getStreamGraph();
    }

    @Override
    public ObjectNode getStreamGraphFromDataStream(List<String> statements) {
        statements.forEach(this::executeSql);
        return getStreamGraphJsonNode(getStreamGraph());
    }

    @Override
    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return tableEnvironment.getJobPlanInfo(statements);
    }

    @Override
    public JobPlanInfo getJobPlanInfoFromDataStream(List<String> statements) {
        statements.forEach(this::executeSql);
        StreamGraph streamGraph = getStreamGraph();
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(streamGraph.getJobGraph()));
    }

    @Override
    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return tableEnvironment.getJobGraphFromInserts(statements);
    }

    @Override
    public TableResult executeStatementSet(List<String> statements) {
        StatementSet statementSet = tableEnvironment.createStatementSet();
        statements.forEach(statementSet::addInsertSql);
        return statementSet.execute();
    }

    @Override
    public String explainStatementSet(List<String> statements) {
        StatementSet statementSet = tableEnvironment.createStatementSet();
        statements.forEach(statementSet::addInsertSql);
        return statementSet.explain();
    }

    @Override
    public List<LineageRel> getLineage(String statement) {
        return tableEnvironment.getLineage(statement);
    }
}
