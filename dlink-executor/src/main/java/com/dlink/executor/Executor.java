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

package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.context.DinkyClassLoaderContextHolder;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.interceptor.FlinkInterceptorResult;
import com.dlink.model.LineageRel;
import com.dlink.result.SqlExplainResult;

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
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

/**
 * Executor
 *
 * @author wenmo
 * @since 2021/11/17
 **/
@Slf4j
public abstract class Executor {

    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    protected StreamExecutionEnvironment environment;
    protected CustomTableEnvironment stEnvironment;
    protected EnvironmentSetting environmentSetting;
    protected ExecutorSetting executorSetting;
    protected Map<String, Object> setConfig = new HashMap<>();

    protected SqlManager sqlManager = new SqlManager();
    protected boolean useSqlFragment = true;

    public static Executor build() {
        return new LocalStreamExecutor(ExecutorSetting.DEFAULT);
    }

    public static Executor build(EnvironmentSetting environmentSetting, ExecutorSetting executorSetting) {
        if (environmentSetting.isUseRemote()) {
            return buildRemoteExecutor(environmentSetting, executorSetting);
        } else {
            return buildLocalExecutor(executorSetting);
        }
    }

    public static Executor buildLocalExecutor(ExecutorSetting executorSetting) {
        if (executorSetting.isUseBatchModel()) {
            return new LocalBatchExecutor(executorSetting);
        } else {
            return new LocalStreamExecutor(executorSetting);
        }
    }

    public static Executor buildAppStreamExecutor(ExecutorSetting executorSetting) {
        if (executorSetting.isUseBatchModel()) {
            return new AppBatchExecutor(executorSetting);
        } else {
            return new AppStreamExecutor(executorSetting);
        }
    }

    public static Executor buildRemoteExecutor(EnvironmentSetting environmentSetting, ExecutorSetting executorSetting) {
        environmentSetting.setUseRemote(true);
        if (executorSetting.isUseBatchModel()) {
            return new RemoteBatchExecutor(environmentSetting, executorSetting);
        } else {
            return new RemoteStreamExecutor(environmentSetting, executorSetting);
        }
    }

    public SqlManager getSqlManager() {
        return sqlManager;
    }

    public boolean isUseSqlFragment() {
        return useSqlFragment;
    }

    public ExecutionConfig getExecutionConfig() {
        return environment.getConfig();
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return environment;
    }

    public CustomTableEnvironment getCustomTableEnvironment() {
        return stEnvironment;
    }

    public ExecutorSetting getExecutorSetting() {
        return executorSetting;
    }

    public EnvironmentSetting getEnvironmentSetting() {
        return environmentSetting;
    }

    public Map<String, Object> getSetConfig() {
        return setConfig;
    }

    public void setSetConfig(Map<String, Object> setConfig) {
        this.setConfig = setConfig;
    }

    public TableConfig getTableConfig() {
        return stEnvironment.getConfig();
    }

    public String getTimeZone() {
        return getTableConfig().getLocalTimeZone().getId();
    }

    protected void init() {
        initEnvironment();
        initStreamExecutionEnvironment();
    }

    public void update(ExecutorSetting executorSetting) {
        updateEnvironment(executorSetting);
        updateStreamExecutionEnvironment(executorSetting);
    }

    public void initEnvironment() {
        updateEnvironment(executorSetting);
    }

    public void updateEnvironment(ExecutorSetting executorSetting) {
        if (executorSetting.isValidParallelism()) {
            environment.setParallelism(executorSetting.getParallelism());
        }

        if (executorSetting.getConfig() != null) {
            Configuration configuration = Configuration.fromMap(executorSetting.getConfig());
            environment.getConfig().configure(configuration, null);
        }
    }

    abstract CustomTableEnvironment createCustomTableEnvironment();

    private void initStreamExecutionEnvironment() {
        updateStreamExecutionEnvironment(executorSetting);
    }

    private void updateStreamExecutionEnvironment(ExecutorSetting executorSetting) {
        useSqlFragment = executorSetting.isUseSqlFragment();

        CustomTableEnvironment newestEnvironment = createCustomTableEnvironment();
        if (stEnvironment != null) {
            for (String catalog : stEnvironment.listCatalogs()) {
                stEnvironment.getCatalog(catalog).ifPresent(t -> {
                    newestEnvironment.getCatalogManager().unregisterCatalog(catalog, true);
                    newestEnvironment.registerCatalog(catalog, t);
                });
            }
        }
        stEnvironment = newestEnvironment;

        final Configuration configuration = stEnvironment.getConfig().getConfiguration();
        if (executorSetting.isValidJobName()) {
            configuration.setString(PipelineOptions.NAME.key(), executorSetting.getJobName());
        }

        setConfig.put(PipelineOptions.NAME.key(), executorSetting.getJobName());
        if (executorSetting.getConfig() != null) {
            for (Map.Entry<String, String> entry : executorSetting.getConfig().entrySet()) {
                configuration.setString(entry.getKey(), entry.getValue());
            }
        }
    }

    public String pretreatStatement(String statement) {
        return FlinkInterceptor.pretreatStatement(this, statement);
    }

    private FlinkInterceptorResult pretreatExecute(String statement) {
        return FlinkInterceptor.build(this, statement);
    }

    public JobExecutionResult execute(String jobName) throws Exception {
        return environment.execute(jobName);
    }

    public JobClient executeAsync(String jobName) throws Exception {
        return environment.executeAsync(jobName);
    }

    public TableResult executeSql(String statement) {
        statement = pretreatStatement(statement);
        FlinkInterceptorResult flinkInterceptorResult = pretreatExecute(statement);
        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
            return flinkInterceptorResult.getTableResult();
        }
        if (!flinkInterceptorResult.isNoExecute()) {
            this.loginFromKeytabIfNeed();
            return stEnvironment.executeSql(statement);
        } else {
            return CustomTableResultImpl.TABLE_RESULT_OK;
        }
    }

    private void reset() {
        try {
            if (UserGroupInformation.isLoginKeytabBased()) {
                Method reset = UserGroupInformation.class.getDeclaredMethod("reset");
                reset.invoke(UserGroupInformation.class);
                log.info("Reset kerberos authentication...");
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loginFromKeytabIfNeed() {
        setConfig.forEach((k, v) -> log.debug("setConfig key: [{}], value: [{}]", k, v));
        String krb5ConfPath = (String) setConfig.getOrDefault("java.security.krb5.conf", "");
        String keytabPath = (String) setConfig.getOrDefault("security.kerberos.login.keytab", "");
        String principal = (String) setConfig.getOrDefault("security.kerberos.login.principal", "");

        if (Asserts.isAllNullString(krb5ConfPath, keytabPath, principal)) {
            log.info("Simple authentication mode");
            return;
        }
        log.info("Kerberos authentication mode");
        if (Asserts.isNullString(krb5ConfPath)) {
            log.error("Parameter [java.security.krb5.conf] is null or empty.");
            return;
        }

        if (Asserts.isNullString(keytabPath)) {
            log.error("Parameter [security.kerberos.login.keytab] is null or empty.");
            return;
        }

        if (Asserts.isNullString(principal)) {
            log.error("Parameter [security.kerberos.login.principal] is null or empty.");
            return;
        }

        this.reset();

        System.setProperty("java.security.krb5.conf", krb5ConfPath);
        org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
        config.set("hadoop.security.authentication", "Kerberos");
        config.setBoolean("hadoop.security.authorization", true);
        UserGroupInformation.setConfiguration(config);
        try {
            UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
            log.error("Kerberos [{}] authentication success.", UserGroupInformation.getLoginUser().getUserName());
        } catch (IOException e) {
            log.error("Kerberos authentication failed.");
            e.printStackTrace();
        }
    }

    /**
     * init udf
     *
     * @param udfFilePath udf文件路径
     */
    public void initUDF(String... udfFilePath) {
        DinkyClassLoaderContextHolder.get().addURL(udfFilePath);
    }

    public void initPyUDF(String executable, String... udfPyFilePath) {
        if (udfPyFilePath == null || udfPyFilePath.length == 0) {
            return;
        }
        Map<String, String> config = executorSetting.getConfig();
        if (Asserts.isNotNull(config)) {
            config.put(PythonOptions.PYTHON_FILES.key(), String.join(",", udfPyFilePath));
            config.put(PythonOptions.PYTHON_CLIENT_EXECUTABLE.key(), executable);
        }
        update(executorSetting);
    }

    private static void loadJar(final URL jarUrl) {
        // 从URLClassLoader类加载器中获取类的addURL方法
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e) {
            logger.error(e.getMessage());
        }

        // 获取方法的访问权限
        boolean accessible = method.isAccessible();
        try {
            // 修改访问权限为可写
            if (!accessible) {
                method.setAccessible(true);
            }
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            // jar路径加入到系统url路径里
            method.invoke(classLoader, jarUrl);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            method.setAccessible(accessible);
        }
    }

    public String explainSql(String statement, ExplainDetail... extraDetails) {
        statement = pretreatStatement(statement);
        if (pretreatExecute(statement).isNoExecute()) {
            return "";
        }

        return stEnvironment.explainSql(statement, extraDetails);
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        statement = pretreatStatement(statement);
        if (Asserts.isNotNullString(statement) && !pretreatExecute(statement).isNoExecute()) {
            return stEnvironment.explainSqlRecord(statement, extraDetails);
        }

        return null;
    }

    public ObjectNode getStreamGraph(String statement) {
        statement = pretreatStatement(statement);
        if (pretreatExecute(statement).isNoExecute()) {
            return null;
        }

        return stEnvironment.getStreamGraph(statement);
    }

    public ObjectNode getStreamGraph(List<String> statements) {
        StreamGraph streamGraph = stEnvironment.getStreamGraphFromInserts(statements);
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
            e.printStackTrace();
        }

        return objectNode;
    }

    public StreamGraph getStreamGraph() {
        return environment.getStreamGraph();
    }

    public ObjectNode getStreamGraphFromDataStream(List<String> statements) {
        for (String statement : statements) {
            executeSql(statement);
        }

        StreamGraph streamGraph = getStreamGraph();
        return getStreamGraphJsonNode(streamGraph);
    }

    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return stEnvironment.getJobPlanInfo(statements);
    }

    public JobPlanInfo getJobPlanInfoFromDataStream(List<String> statements) {
        for (String statement : statements) {
            executeSql(statement);
        }
        StreamGraph streamGraph = getStreamGraph();
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(streamGraph.getJobGraph()));
    }

    public CatalogManager getCatalogManager() {
        return stEnvironment.getCatalogManager();
    }

    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return stEnvironment.getJobGraphFromInserts(statements);
    }

    public StatementSet createStatementSet() {
        return stEnvironment.createStatementSet();
    }

    public TableResult executeStatementSet(List<String> statements) {
        StatementSet statementSet = stEnvironment.createStatementSet();
        for (String item : statements) {
            statementSet.addInsertSql(item);
        }
        return statementSet.execute();
    }

    public String explainStatementSet(List<String> statements) {
        StatementSet statementSet = stEnvironment.createStatementSet();
        for (String item : statements) {
            statementSet.addInsertSql(item);
        }
        return statementSet.explain();
    }

    public void submitSql(String statements) {
        executeSql(statements);
    }

    public void submitStatementSet(List<String> statements) {
        executeStatementSet(statements);
    }

    public boolean parseAndLoadConfiguration(String statement) {
        return stEnvironment.parseAndLoadConfiguration(statement, environment, setConfig);
    }

    public List<LineageRel> getLineage(String statement) {
        return stEnvironment.getLineage(statement);
    }
}
