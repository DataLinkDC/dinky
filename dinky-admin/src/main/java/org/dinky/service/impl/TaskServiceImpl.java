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

import org.dinky.alert.Alert;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertMsg;
import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;
import org.dinky.assertion.Assert;
import org.dinky.assertion.Asserts;
import org.dinky.config.Dialect;
import org.dinky.config.Docker;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.context.TenantContextHolder;
import org.dinky.daemon.task.DaemonFactory;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.constant.FlinkRestResultConstant;
import org.dinky.data.constant.NetConstant;
import org.dinky.data.dto.SqlDTO;
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.dto.TaskVersionConfigureDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.TaskOperatingSavepointSelect;
import org.dinky.data.enums.TaskOperatingStatus;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.AlertGroup;
import org.dinky.data.model.AlertHistory;
import org.dinky.data.model.AlertInstance;
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.Cluster;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.History;
import org.dinky.data.model.Jar;
import org.dinky.data.model.JobHistory;
import org.dinky.data.model.JobInfoDetail;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.RoleSelectPermissions;
import org.dinky.data.model.Savepoints;
import org.dinky.data.model.Statement;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.Task;
import org.dinky.data.model.TaskVersion;
import org.dinky.data.model.UDFTemplate;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.data.result.TaskOperatingResult;
import org.dinky.function.compiler.CustomStringJavaCompiler;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.function.util.UDFUtil;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.model.JobInfo;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.FlinkJobTask;
import org.dinky.job.FlinkJobTaskPool;
import org.dinky.job.Job;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;
import org.dinky.mapper.TaskMapper;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.process.context.ProcessContextHolder;
import org.dinky.process.enums.ProcessType;
import org.dinky.process.exception.DinkyException;
import org.dinky.process.model.ProcessEntity;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertHistoryService;
import org.dinky.service.CatalogueService;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.DataBaseService;
import org.dinky.service.FragmentVariableService;
import org.dinky.service.HistoryService;
import org.dinky.service.JarService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.SavepointsService;
import org.dinky.service.StatementService;
import org.dinky.service.TaskService;
import org.dinky.service.TaskVersionService;
import org.dinky.service.UDFTemplateService;
import org.dinky.service.UserService;
import org.dinky.utils.DockerClientUtils;
import org.dinky.utils.JSONUtil;
import org.dinky.utils.UDFUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/** TaskServiceImpl */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends SuperServiceImpl<TaskMapper, Task> implements TaskService {

    private final StatementService statementService;
    private final ClusterInstanceService clusterInstanceService;
    private final ClusterConfigurationService clusterConfigurationService;
    private final SavepointsService savepointsService;
    private final JarService jarService;
    private final DataBaseService dataBaseService;
    private final JobInstanceService jobInstanceService;
    private final JobHistoryService jobHistoryService;
    private final AlertGroupService alertGroupService;
    private final AlertHistoryService alertHistoryService;
    private final HistoryService historyService;
    private final TaskVersionService taskVersionService;
    private final FragmentVariableService fragmentVariableService;
    private final UDFTemplateService udfTemplateService;
    private final DataSourceProperties dataSourceProperties;
    private final UserService userService;

    @Resource @Lazy private CatalogueService catalogueService;

    private static final ObjectMapper mapper = new ObjectMapper();

    private String driver() {
        return dataSourceProperties.getDriverClassName();
    }

    private String url() {
        return dataSourceProperties.getUrl();
    }

    private String username() {
        return dataSourceProperties.getUsername();
    }

    private String password() {
        return dataSourceProperties.getPassword();
    }

    @Value("server.port")
    private String serverPort;

    private String buildParas(Integer id) {
        return buildParas(id, StrUtil.NULL);
    }

    private String buildParas(Integer id, String dinkyAddr) {
        return String.format(
                "--id %d --driver %s --url %s --username %s --password %s --dinkyAddr %s",
                id, driver(), url(), username(), password(), dinkyAddr);
    }

    @Override
    public JobResult submitTask(Integer id) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMsg());

        if (Dialect.notFlinkSql(task.getDialect())) {
            return executeCommonSql(SqlDTO.build(task.getStatement(), task.getDatabaseId(), null));
        }

        ProcessEntity process =
                StpUtil.isLogin()
                        ? ProcessContextHolder.registerProcess(
                                ProcessEntity.init(
                                        ProcessType.FLINK_SUBMIT, StpUtil.getLoginIdAsInt()))
                        : ProcessEntity.NULL_PROCESS;

        process.info("Initializing Flink job config...");
        JobConfig config = buildJobConfig(task);

        if (GatewayType.KUBERNETES_APPLICATION.equalsValue(config.getType())) {
            loadDocker(id, config.getClusterConfigurationId(), config.getGatewayConfig());
        }

        JobManager jobManager = JobManager.build(config);
        process.start();
        JobResult jobResult;
        if (config.isJarTask()) {
            jobResult = jobManager.executeJar();
            process.finish("Submit Flink Jar finished.");
        } else {
            jobResult = jobManager.executeSql(task.getStatement());
            process.finish("Submit Flink SQL finished.");
        }
        return jobResult;
    }

    private void loadDocker(
            Integer taskId, Integer clusterConfigurationId, GatewayConfig gatewayConfig) {
        Map<String, Object> dockerConfig =
                (Map<String, Object>)
                        clusterConfigurationService
                                .getClusterConfigById(clusterConfigurationId)
                                .getConfig()
                                .get("dockerConfig");

        if (dockerConfig == null) {
            return;
        }

        String params =
                buildParas(taskId, dockerConfig.getOrDefault("dinky.remote.addr", "").toString());

        gatewayConfig.getAppConfig().setUserJarParas(params.split(" "));

        Docker docker = Docker.build(dockerConfig);
        if (docker == null || StringUtils.isBlank(docker.getInstance())) {
            return;
        }

        DockerClientUtils dockerClientUtils = new DockerClientUtils(docker);
        String tag = dockerClientUtils.getDocker().getTag();
        if (StrUtil.isNotBlank(tag)) {
            gatewayConfig
                    .getFlinkConfig()
                    .getConfiguration()
                    .put("kubernetes.container.image", tag);
        }
    }

    @Override
    public JobResult submitTaskToOnline(Task dtoTask, Integer id) {
        final Task task = dtoTask == null ? this.getTaskInfoById(id) : dtoTask;
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMsg());
        task.setStep(JobLifeCycle.ONLINE.getValue());

        if (Dialect.notFlinkSql(task.getDialect())) {
            return executeCommonSql(SqlDTO.build(task.getStatement(), task.getDatabaseId(), null));
        }

        JobConfig config = buildJobConfig(task);
        JobManager jobManager = JobManager.build(config);
        if (config.isJarTask()) {
            return jobManager.executeJar();
        }
        return jobManager.executeSql(task.getStatement());
    }

    @Override
    public JobResult restartTask(Integer id, String savePointPath) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMsg());
        if (checkJobInstanceId(task)) {
            savepointJobInstance(task.getJobInstanceId(), SavePointType.CANCEL.getValue());
        }

        if (Dialect.notFlinkSql(task.getDialect())) {
            return executeCommonSql(SqlDTO.build(task.getStatement(), task.getDatabaseId(), null));
        }

        if (StringUtils.isBlank(savePointPath)) {
            task.setSavePointStrategy(SavePointStrategy.LATEST.getValue());
        } else {
            task.setSavePointStrategy(SavePointStrategy.CUSTOM.getValue());
            task.setSavePointPath(savePointPath);
            updateById(task);
        }

        JobConfig config = buildJobConfig(task);
        JobManager jobManager = JobManager.build(config);
        if (!config.isJarTask()) {
            return jobManager.executeSql(task.getStatement());
        } else {
            return jobManager.executeJar();
        }
    }

    private JobResult executeCommonSql(SqlDTO sqlDTO) {
        JobResult result = new JobResult();
        result.setStatement(sqlDTO.getStatement());
        result.setStartTime(LocalDateTime.now());

        if (Asserts.isNull(sqlDTO.getDatabaseId())) {
            result.setSuccess(false);
            result.setError("please assign data source");
            result.setEndTime(LocalDateTime.now());
            return result;
        }

        DataBase dataBase = dataBaseService.getById(sqlDTO.getDatabaseId());
        if (Asserts.isNull(dataBase)) {
            result.setSuccess(false);
            result.setError("data source not exist.");
            result.setEndTime(LocalDateTime.now());
            return result;
        }

        JdbcSelectResult selectResult;
        try (Driver driver = Driver.build(dataBase.getDriverConfig())) {
            selectResult = driver.executeSql(sqlDTO.getStatement(), sqlDTO.getMaxRowNum());
        }

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

    @Override
    public List<SqlExplainResult> explainTask(Integer id) {
        Task task = getTaskInfoById(id);
        if (Dialect.notFlinkSql(task.getDialect())) {
            return explainCommonSqlTask(task);
        }

        return explainFlinkSqlTask(task);
    }

    private List<SqlExplainResult> explainFlinkSqlTask(Task task) {
        JobConfig config = buildJobConfig(task);
        config.buildLocal();
        JobManager jobManager = JobManager.buildPlanMode(config);
        return jobManager.explainSql(task.getStatement()).getSqlExplainResults();
    }

    private List<SqlExplainResult> explainCommonSqlTask(Task task) {
        if (Asserts.isNull(task.getDatabaseId())) {
            return Collections.singletonList(
                    SqlExplainResult.fail(task.getStatement(), "please assign data source."));
        }

        DataBase dataBase = dataBaseService.getById(task.getDatabaseId());
        if (Asserts.isNull(dataBase)) {
            return Collections.singletonList(
                    SqlExplainResult.fail(task.getStatement(), "data source not exist."));
        }

        List<SqlExplainResult> sqlExplainResults;
        try (Driver driver = Driver.build(dataBase.getDriverConfig())) {
            sqlExplainResults = driver.explain(task.getStatement());
        }
        return sqlExplainResults;
    }

    @Override
    public Task getTaskInfoById(Integer id) {
        Task task = this.getById(id);
        if (task == null) {
            return null;
        }

        task.parseConfig();
        if (task.getClusterId() != null) {
            Cluster cluster = clusterInstanceService.getById(task.getClusterId());
            if (cluster != null) {
                task.setClusterName(cluster.getAlias());
            }
        }

        Statement statement = statementService.getById(id);
        if (statement != null) {
            task.setStatement(statement.getStatement());
        }

        JobInstance jobInstance = jobInstanceService.getJobInstanceByTaskId(id);
        if (Asserts.isNotNull(jobInstance) && !JobStatus.isDone(jobInstance.getStatus())) {
            task.setJobInstanceId(jobInstance.getId());
        } else {
            task.setJobInstanceId(0);
        }
        return task;
    }

    @Override
    public void initTenantByTaskId(Integer id) {
        Integer tenantId = baseMapper.getTenantByTaskId(id);
        Asserts.checkNull(tenantId, Status.TASK_NOT_EXIST.getMsg());
        TenantContextHolder.set(tenantId);
    }

    @Override
    public boolean saveOrUpdateTask(Task task) {
        if (Dialect.isUDF(task.getDialect())) {
            if (CollUtil.isNotEmpty(task.getConfig())
                    && Asserts.isNullString(task.getStatement())
                    && Convert.toInt(task.getConfig().get(0).get("templateId"), 0) != 0) {
                Map<String, String> config = task.getConfig().get(0);
                UDFTemplate template = udfTemplateService.getById(config.get("templateId"));
                if (template != null) {
                    String code =
                            UDFUtil.templateParse(
                                    task.getDialect(),
                                    template.getTemplateCode(),
                                    config.get("className"));
                    task.setStatement(code);
                }
            }
            // to compiler udf
            if (Asserts.isNotNullString(task.getDialect())
                    && Dialect.JAVA.equalsVal(task.getDialect())
                    && Asserts.isNotNullString(task.getStatement())) {
                CustomStringJavaCompiler compiler =
                        new CustomStringJavaCompiler(task.getStatement());
                task.setSavePointPath(compiler.getFullClassName());
            } else if (Dialect.PYTHON.equalsVal(task.getDialect())) {
                task.setSavePointPath(
                        task.getName() + "." + UDFUtil.getPyUDFAttr(task.getStatement()));
            } else if (Dialect.SCALA.equalsVal(task.getDialect())) {
                task.setSavePointPath(UDFUtil.getScalaFullClassName(task.getStatement()));
            }
            UdfCodePool.addOrUpdate(UDFUtils.taskToUDF(task));
        }

        // if modify task else create task
        if (task.getId() != null) {
            Task taskInfo = getById(task.getId());
            Assert.check(taskInfo);
            if (JobLifeCycle.RELEASE.equalsValue(taskInfo.getStep())
                    || JobLifeCycle.ONLINE.equalsValue(taskInfo.getStep())
                    || JobLifeCycle.CANCEL.equalsValue(taskInfo.getStep())) {
                throw new BusException(
                        "该作业已" + JobLifeCycle.get(taskInfo.getStep()).getLabel() + "，禁止修改！");
            }
            task.setStep(JobLifeCycle.DEVELOP.getValue());
            this.updateById(task);
            if (task.getStatement() != null) {
                Statement statement = new Statement();
                statement.setId(task.getId());
                statement.setStatement(task.getStatement());
                statementService.updateById(statement);
            }
        } else {
            task.setStep(JobLifeCycle.CREATE.getValue());
            if (task.getCheckPoint() == null) {
                task.setCheckPoint(0);
            }
            if (task.getParallelism() == null) {
                task.setParallelism(1);
            }
            if (task.getClusterId() == null) {
                task.setClusterId(0);
            }
            this.save(task);
            Statement statement = new Statement();
            statement.setId(task.getId());
            if (task.getStatement() == null) {
                task.setStatement("");
            }
            statement.setStatement(task.getStatement());
            statementService.insert(statement);
        }
        return true;
    }

    @Override
    public List<Task> listFlinkSQLEnv() {
        return this.list(
                new QueryWrapper<Task>()
                        .eq("dialect", Dialect.FLINK_SQL_ENV.getValue())
                        .eq("enabled", 1));
    }

    @Override
    public Task initDefaultFlinkSQLEnv(Integer tenantId) {
        String separator = SystemConfiguration.getInstances().getSqlSeparator();
        separator = separator.replace("\\r", "\r").replace("\\n", "\n");
        String name = "DefaultCatalog";

        Task defaultFlinkSQLEnvTask = getTaskByNameAndTenantId(name, tenantId);

        String sql =
                String.format(
                        "create catalog my_catalog with(\n    "
                                + "'type' = 'dinky_mysql',\n"
                                + "    'username' = "
                                + "'%s',\n    "
                                + "'password' = '%s',\n"
                                + "    'url' = '%s'\n"
                                + ")%suse catalog my_catalog%s",
                        username(), password(), url(), separator, separator);

        if (null != defaultFlinkSQLEnvTask) {
            statementEquals(tenantId, defaultFlinkSQLEnvTask, sql);
            return defaultFlinkSQLEnvTask;
        }

        defaultFlinkSQLEnvTask = new Task();
        defaultFlinkSQLEnvTask.setName("DefaultCatalog");
        defaultFlinkSQLEnvTask.setDialect(Dialect.FLINK_SQL_ENV.getValue());
        defaultFlinkSQLEnvTask.setStatement(sql);
        defaultFlinkSQLEnvTask.setFragment(true);
        defaultFlinkSQLEnvTask.setTenantId(tenantId);
        defaultFlinkSQLEnvTask.setEnabled(true);
        saveOrUpdate(defaultFlinkSQLEnvTask);

        Statement statement = new Statement();
        statement.setId(defaultFlinkSQLEnvTask.getId());
        statement.setTenantId(tenantId);
        statement.setStatement(sql);
        statementService.saveOrUpdate(statement);

        return defaultFlinkSQLEnvTask;
    }

    /**
     * 数据库信息发生修改后，catalog ddl也随之改变
     *
     * @param tenantId
     * @param defaultFlinkSQLEnvTask
     * @param sql
     */
    private void statementEquals(Integer tenantId, Task defaultFlinkSQLEnvTask, String sql) {
        TenantContextHolder.set(tenantId);

        // 对比catalog ddl,不相同则更新dinky_task_statement表
        boolean equals =
                StringUtils.equals(
                        sql,
                        statementService.getById(defaultFlinkSQLEnvTask.getId()).getStatement());
        if (!equals) {
            Statement statement = new Statement();
            statement.setId(defaultFlinkSQLEnvTask.getId());
            statement.setTenantId(tenantId);
            statement.setStatement(sql);
            statementService.saveOrUpdate(statement);
        }
    }

    @Override
    public Task getTaskByNameAndTenantId(String name, Integer tenantId) {
        Task task = baseMapper.getTaskByNameAndTenantId(name, tenantId);
        return task;
    }

    @Override
    public JobStatus checkJobStatus(JobInfoDetail jobInfoDetail) {
        if (Asserts.isNull(jobInfoDetail.getClusterConfiguration())) {
            return JobStatus.UNKNOWN;
        }

        Map<String, Object> gatewayConfigMap =
                clusterConfigurationService.getGatewayConfig(
                        jobInfoDetail.getClusterConfiguration().getId());

        JobConfig jobConfig = new JobConfig();
        jobConfig.buildGatewayConfig(gatewayConfigMap);
        GatewayConfig gatewayConfig = jobConfig.getGatewayConfig();
        gatewayConfig.setType(GatewayType.get(jobInfoDetail.getCluster().getType()));
        gatewayConfig.getClusterConfig().setAppId(jobInfoDetail.getCluster().getName());

        Gateway gateway = Gateway.build(gatewayConfig);
        return gateway.getJobStatusById(jobInfoDetail.getCluster().getName());
    }

    @Override
    public String exportSql(Integer id) {
        Task task = getTaskInfoById(id);
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMsg());
        if (Dialect.notFlinkSql(task.getDialect())) {
            return task.getStatement();
        }

        JobConfig config = buildJobConfig(task);
        JobManager jobManager = JobManager.build(config);
        if (config.isJarTask()) {
            return "";
        }

        return jobManager.exportSql(task.getStatement());
    }

    @Override
    public Task getUDFByClassName(String className) {
        Task task =
                getOne(
                        new QueryWrapper<Task>()
                                .in("dialect", Dialect.JAVA, Dialect.SCALA, Dialect.PYTHON)
                                .eq("enabled", 1)
                                .eq("save_point_path", className));
        Asserts.checkNull(task, StrUtil.format("class: {} ,not exists!", className));
        task.setStatement(statementService.getById(task.getId()).getStatement());
        return task;
    }

    @Override
    public List<Task> getAllUDF() {
        List<Task> tasks =
                list(
                        new QueryWrapper<Task>()
                                .in("dialect", Dialect.JAVA, Dialect.SCALA, Dialect.PYTHON)
                                .eq("enabled", 1)
                                .isNotNull("save_point_path"));
        return tasks.stream()
                .peek(
                        task -> {
                            Assert.check(task);
                            task.setStatement(
                                    statementService.getById(task.getId()).getStatement());
                        })
                .collect(Collectors.toList());
    }

    @Override
    public Result<Void> releaseTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (!JobLifeCycle.DEVELOP.equalsValue(task.getStep())) {
            return Result.succeed("publish success!");
        }

        // KubernetesApplication is not sql, skip sqlExplain verify
        if (!Dialect.KUBERNETES_APPLICATION.equalsVal(task.getDialect())) {
            List<SqlExplainResult> sqlExplainResults = explainTask(id);
            for (SqlExplainResult sqlExplainResult : sqlExplainResults) {
                if (!sqlExplainResult.isParseTrue() || !sqlExplainResult.isExplainTrue()) {
                    return Result.failed("syntax or logic check error, publish failed");
                }
            }
        }

        task.setStep(JobLifeCycle.RELEASE.getValue());
        Task newTask = createTaskVersionSnapshot(task);
        if (updateById(newTask)) {
            return Result.succeed("publish success!");
        } else {
            return Result.failed("publish failed, due to unknown reason");
        }
    }

    public Task createTaskVersionSnapshot(Task task) {
        List<TaskVersion> taskVersions = taskVersionService.getTaskVersionByTaskId(task.getId());
        List<Integer> versionIds =
                taskVersions.stream().map(TaskVersion::getVersionId).collect(Collectors.toList());
        Map<Integer, TaskVersion> versionMap =
                taskVersions.stream().collect(Collectors.toMap(TaskVersion::getVersionId, t -> t));

        TaskVersion taskVersion = new TaskVersion();
        BeanUtil.copyProperties(task, taskVersion);
        TaskVersionConfigureDTO taskVersionConfigureDTO = new TaskVersionConfigureDTO();
        BeanUtil.copyProperties(task, taskVersionConfigureDTO);
        taskVersion.setTaskConfigure(taskVersionConfigureDTO);
        taskVersion.setTaskId(taskVersion.getId());
        taskVersion.setId(null);
        if (Asserts.isNull(task.getVersionId())) {
            // 首次发布，新增版本
            taskVersion.setVersionId(1);
            task.setVersionId(1);
            taskVersionService.save(taskVersion);
        } else {
            // 说明存在版本，需要判断是否 是回退后的老版本
            // 1、版本号存在
            // 2、md5值与上一个版本一致
            TaskVersion version = versionMap.get(task.getVersionId());
            version.setId(null);

            if (versionIds.contains(task.getVersionId()) && !taskVersion.equals(version)) {
                // || !versionIds.contains(task.getVersionId()) && !taskVersion.equals(version)
                taskVersion.setVersionId(Collections.max(versionIds) + 1);
                task.setVersionId(Collections.max(versionIds) + 1);
                taskVersionService.save(taskVersion);
            }
        }
        return task;
    }

    @Override
    public Result<Void> rollbackTask(TaskRollbackVersionDTO dto) {
        if (Asserts.isNull(dto.getVersionId()) || Asserts.isNull(dto.getId())) {
            return Result.failed("the version is error");
        }

        Task taskInfo = getTaskInfoById(dto.getId());
        if (JobLifeCycle.RELEASE.equalsValue(taskInfo.getStep())
                || JobLifeCycle.ONLINE.equalsValue(taskInfo.getStep())
                || JobLifeCycle.CANCEL.equalsValue(taskInfo.getStep())) {
            return Result.failed(
                    "this job had"
                            + JobLifeCycle.get(taskInfo.getStep()).getLabel()
                            + ", refuse to rollback！");
        }

        LambdaQueryWrapper<TaskVersion> queryWrapper =
                new LambdaQueryWrapper<TaskVersion>()
                        .eq(TaskVersion::getTaskId, dto.getId())
                        .eq(TaskVersion::getVersionId, dto.getVersionId());

        TaskVersion taskVersion = taskVersionService.getOne(queryWrapper);

        Task updateTask = new Task();
        BeanUtil.copyProperties(taskVersion, updateTask);
        BeanUtil.copyProperties(taskVersion.getTaskConfigure(), updateTask);
        updateTask.setId(taskVersion.getTaskId());
        updateTask.setStep(JobLifeCycle.DEVELOP.getValue());
        baseMapper.updateById(updateTask);

        Statement statement = new Statement();
        statement.setStatement(taskVersion.getStatement());
        statement.setId(taskVersion.getTaskId());
        statementService.updateById(statement);
        return Result.succeed("version rollback success！");
    }

    @Override
    public boolean developTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.RELEASE.equalsValue(task.getStep())) {
            task.setStep(JobLifeCycle.DEVELOP.getValue());
            return updateById(task);
        }
        return false;
    }

    @Override
    public Result<JobResult> onLineTask(Integer id) {
        final Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.RELEASE.equalsValue(task.getStep())) {
            if (checkJobInstanceId(task)) {
                return Result.failed("当前发布状态下有作业正在运行，上线失败，请停止后上线");
            }

            final JobResult jobResult = submitTaskToOnline(task, id);
            if (Job.JobStatus.SUCCESS == jobResult.getStatus()) {
                task.setStep(JobLifeCycle.ONLINE.getValue());
                task.setJobInstanceId(jobResult.getJobInstanceId());
                if (updateById(task)) {
                    return Result.succeed(jobResult, "上线成功");
                }

                return Result.failed("由于未知原因，上线失败");
            }

            return Result.failed("上线失败，原因：" + jobResult.getError());
        }

        if (JobLifeCycle.ONLINE.equalsValue(task.getStep())) {
            return Result.failed("上线失败，当前作业已上线。");
        }
        return Result.failed("上线失败，当前作业未发布。");
    }

    private static boolean checkJobInstanceId(Task task) {
        return Asserts.isNotNull(task.getJobInstanceId()) && task.getJobInstanceId() != 0;
    }

    @Override
    public Result<JobResult> reOnLineTask(Integer id, String savePointPath) {
        final Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMsg());
        if (checkJobInstanceId(task)) {
            savepointJobInstance(task.getJobInstanceId(), SavePointType.CANCEL.getValue());
        }

        if (StringUtils.isNotBlank(savePointPath)) {
            task.setSavePointStrategy(SavePointStrategy.CUSTOM.getValue());
            task.setSavePointPath(savePointPath);
        }

        final JobResult jobResult = submitTaskToOnline(task, id);
        if (Job.JobStatus.SUCCESS == jobResult.getStatus()) {
            task.setStep(JobLifeCycle.ONLINE.getValue());
            task.setJobInstanceId(jobResult.getJobInstanceId());
            if (updateById(task)) {
                return Result.succeed(jobResult, "重新上线成功");
            }
            return Result.failed("由于未知原因，重新上线失败");
        }
        return Result.failed("重新上线失败，原因：" + jobResult.getError());
    }

    @Override
    public Result<Void> offLineTask(Integer id, String type) {
        Task task = getTaskInfoById(id);
        Assert.check(task);

        if (Asserts.isNullString(type)) {
            type = SavePointType.CANCEL.getValue();
        }

        savepointTask(id, type);
        if (!JobLifeCycle.ONLINE.equalsValue(task.getStep())) {
            return Result.succeed("停止成功");
        }

        task.setStep(JobLifeCycle.RELEASE.getValue());
        updateById(task);
        return Result.succeed("下线成功");
    }

    @Override
    public Result<Void> cancelTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.ONLINE != JobLifeCycle.get(task.getStep())) {
            if (checkJobInstanceId(task)) {
                return Result.failed("当前有作业正在运行，注销失败，请停止后注销");
            }

            task.setStep(JobLifeCycle.CANCEL.getValue());
            if (updateById(task)) {
                return Result.succeed("注销成功");
            }
            return Result.failed("由于未知原因，注销失败");
        }
        return Result.failed("当前有作业已上线，无法注销，请下线后注销");
    }

    @Override
    public boolean recoveryTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.CANCEL == JobLifeCycle.get(task.getStep())) {
            task.setStep(JobLifeCycle.DEVELOP.getValue());
            return updateById(task);
        }
        return false;
    }

    private boolean savepointJobInstance(Integer jobInstanceId, String savePointType) {
        JobInstance jobInstance = jobInstanceService.getById(jobInstanceId);
        if (Asserts.isNull(jobInstance)) {
            return true;
        }

        Cluster cluster = clusterInstanceService.getById(jobInstance.getClusterId());
        Asserts.checkNotNull(cluster, "该集群不存在");

        Task task = this.getTaskInfoById(jobInstance.getTaskId());
        JobConfig jobConfig = task.buildSubmitConfig();
        jobConfig.setType(cluster.getType());

        boolean useGateway = false;
        if (Asserts.isNotNull(cluster.getClusterConfigurationId())) {
            Map<String, Object> gatewayConfig =
                    clusterConfigurationService.getGatewayConfig(
                            cluster.getClusterConfigurationId());
            // 如果是k8s application 模式,且不是sql任务，则需要补齐statement 内的自定义配置
            if (Dialect.KUBERNETES_APPLICATION.equalsVal(task.getDialect())) {
                Statement statement = statementService.getById(cluster.getTaskId());
                Map<String, Object> statementConfig =
                        JSONUtil.toMap(statement.getStatement(), String.class, Object.class);
                gatewayConfig.putAll(statementConfig);
            }
            jobConfig.buildGatewayConfig(gatewayConfig);
            jobConfig.getGatewayConfig().getClusterConfig().setAppId(cluster.getName());
            useGateway = true;
        }
        jobConfig.setTaskId(jobInstance.getTaskId());
        jobConfig.setAddress(cluster.getJobManagerHost());

        JobManager jobManager = JobManager.build(jobConfig);
        jobManager.setUseGateway(useGateway);

        String jobId = jobInstance.getJid();
        if ("canceljob".equals(savePointType)) {
            return jobManager.cancel(jobId);
        }

        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType, null);
        if (Asserts.isNotNull(savePointResult.getJobInfos())) {
            for (JobInfo item : savePointResult.getJobInfos()) {
                if (Asserts.isEqualsIgnoreCase(jobId, item.getJobId())
                        && Asserts.isNotNull(jobConfig.getTaskId())) {
                    Savepoints savepoints = new Savepoints();
                    savepoints.setName(savePointType);
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
    public boolean savepointTask(Integer taskId, String savePointType) {
        Task task = getTaskInfoById(taskId);
        return savepointJobInstance(task.getJobInstanceId(), savePointType);
    }

    private JobConfig buildJobConfig(Task task) {
        boolean isJarTask =
                Dialect.FLINK_JAR.equalsVal(task.getDialect())
                        || Dialect.KUBERNETES_APPLICATION.equalsVal(task.getDialect());

        boolean fragment = Asserts.isNotNull(task.getFragment()) ? task.getFragment() : false;
        if (!isJarTask && fragment) {
            String flinkWithSql = dataBaseService.getEnabledFlinkWithSql();
            if (Asserts.isNotNullString(flinkWithSql)) {
                task.setStatement(flinkWithSql + "\r\n" + task.getStatement());
            }
        }

        boolean isEnvIdValid = Asserts.isNotNull(task.getEnvId()) && task.getEnvId() != 0;
        if (!isJarTask && isEnvIdValid) {
            Task envTask = getTaskInfoById(task.getEnvId());
            if (Asserts.isNotNull(envTask) && Asserts.isNotNullString(envTask.getStatement())) {
                task.setStatement(envTask.getStatement() + "\r\n" + task.getStatement());
            }
        }

        JobConfig config = task.buildSubmitConfig();
        config.setJarTask(isJarTask);
        if (!JobManager.useGateway(config.getType())) {
            config.setAddress(
                    clusterInstanceService.buildEnvironmentAddress(
                            config.isUseRemote(), task.getClusterId()));
        } else if (Dialect.KUBERNETES_APPLICATION.equalsVal(task.getDialect())
                // support custom K8s app submit, rather than clusterConfiguration
                && (GatewayType.KUBERNETES_APPLICATION.equalsValue(config.getType())
                        || GatewayType.KUBERNETES_APPLICATION_OPERATOR.equalsValue(
                                config.getType()))) {
            Map<String, Object> taskConfig =
                    JSONUtil.toMap(task.getStatement(), String.class, Object.class);
            Map<String, Object> clusterConfiguration =
                    clusterConfigurationService.getGatewayConfig(task.getClusterConfigurationId());
            clusterConfiguration.putAll((Map<String, Object>) taskConfig.get("appConfig"));
            clusterConfiguration.put("taskCustomConfig", taskConfig);
            config.buildGatewayConfig(clusterConfiguration);
        } else {
            Map<String, Object> gatewayConfig =
                    clusterConfigurationService.getGatewayConfig(task.getClusterConfigurationId());
            // submit application type with clusterConfiguration
            if (GatewayType.YARN_APPLICATION.equalsValue(config.getType())
                    || GatewayType.KUBERNETES_APPLICATION.equalsValue(config.getType())
                    || GatewayType.KUBERNETES_APPLICATION_OPERATOR.equalsValue(config.getType())) {
                if (isJarTask) {
                    Jar jar = jarService.getById(task.getJarId());
                    Assert.check(jar);
                    gatewayConfig.put("userJarPath", jar.getPath());
                    gatewayConfig.put("userJarParas", jar.getParas());
                    gatewayConfig.put("userJarMainAppClass", jar.getMainClass());
                } else {
                    Opt.ofBlankAble(gatewayConfig.get("userJarPath"))
                            .orElseThrow(
                                    () ->
                                            new DinkyException(
                                                    "application 模式支持需要在 注册中心->集群管理->集群配置管理 填写jar路径。"));
                    gatewayConfig.put("userJarParas", buildParas(config.getTaskId()));
                    gatewayConfig.put("userJarMainAppClass", "org.dinky.app.MainApp");
                }
            }
            config.buildGatewayConfig(gatewayConfig);
            config.addGatewayConfig(task.parseConfig());
        }

        switch (config.getSavePointStrategy()) {
            case LATEST:
                Savepoints latestSavepoints =
                        savepointsService.getLatestSavepointByTaskId(task.getId());
                if (Asserts.isNotNull(latestSavepoints)) {
                    config.setSavePointPath(latestSavepoints.getPath());
                    config.getConfig().put("execution.savepoint.path", latestSavepoints.getPath());
                }
                break;
            case EARLIEST:
                Savepoints earliestSavepoints =
                        savepointsService.getEarliestSavepointByTaskId(task.getId());
                if (Asserts.isNotNull(earliestSavepoints)) {
                    config.setSavePointPath(earliestSavepoints.getPath());
                    config.getConfig()
                            .put("execution.savepoint.path", earliestSavepoints.getPath());
                }
                break;
            case CUSTOM:
                config.setSavePointPath(config.getSavePointPath());
                config.getConfig().put("execution.savepoint.path", config.getSavePointPath());
                break;
            default:
                config.setSavePointPath(null);
        }
        config.setVariables(fragmentVariableService.listEnabledVariables());
        List<RoleSelectPermissions> currentRoleSelectPermissions =
                userService.getCurrentRoleSelectPermissions();
        if (Asserts.isNotNullCollection(currentRoleSelectPermissions)) {
            ConcurrentHashMap<String, String> permission = new ConcurrentHashMap<>();
            for (RoleSelectPermissions roleSelectPermissions : currentRoleSelectPermissions) {
                if (Asserts.isAllNotNullString(
                        roleSelectPermissions.getTableName(),
                        roleSelectPermissions.getExpression())) {
                    permission.put(
                            roleSelectPermissions.getTableName(),
                            roleSelectPermissions.getExpression());
                }
            }
            RowLevelPermissionsContext.set(permission);
        }
        return config;
    }

    @Override
    public JobInstance refreshJobInstance(Integer id, boolean isCoercive) {
        JobInfoDetail jobInfoDetail;
        FlinkJobTaskPool pool = FlinkJobTaskPool.INSTANCE;
        String key = id.toString();

        if (pool.containsKey(key)) {
            jobInfoDetail = pool.get(key);
        } else {
            jobInfoDetail = new JobInfoDetail(id);
            JobInstance jobInstance = jobInstanceService.getByIdWithoutTenant(id);
            Asserts.checkNull(jobInstance, "the task instance not exist.");
            TenantContextHolder.set(jobInstance.getTenantId());
            jobInfoDetail.setInstance(jobInstance);
            Cluster cluster = clusterInstanceService.getById(jobInstance.getClusterId());
            jobInfoDetail.setCluster(cluster);
            History history = historyService.getById(jobInstance.getHistoryId());
            history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
            if (Asserts.isNotNull(history.getClusterConfigurationId())) {
                ClusterConfiguration clusterConfigById =
                        clusterConfigurationService.getClusterConfigById(
                                history.getClusterConfigurationId());
                jobInfoDetail.setClusterConfiguration(clusterConfigById);
                jobInfoDetail.getInstance().setType(history.getType());
            }
            jobInfoDetail.setHistory(history);
            jobInfoDetail.setJobHistory(jobHistoryService.getJobHistory(id));
            pool.put(key, jobInfoDetail);
        }

        if (!isCoercive && !inRefreshPlan(jobInfoDetail.getInstance())) {
            return jobInfoDetail.getInstance();
        }

        JobHistory jobHistoryJson =
                jobHistoryService.refreshJobHistory(
                        id,
                        jobInfoDetail.getCluster().getJobManagerHost(),
                        jobInfoDetail.getInstance().getJid(),
                        jobInfoDetail.isNeedSave());
        JobHistory jobHistory = jobHistoryService.getJobHistoryInfo(jobHistoryJson);
        jobInfoDetail.setJobHistory(jobHistory);
        JobStatus checkStatus = null;
        if (JobStatus.isDone(jobInfoDetail.getInstance().getStatus())
                && (Asserts.isNull(jobHistory.getJob()) || jobHistory.isError())) {
            checkStatus = checkJobStatus(jobInfoDetail);
            if (checkStatus.isDone()) {
                jobInfoDetail.getInstance().setStatus(checkStatus.getValue());
                jobInstanceService.updateById(jobInfoDetail.getInstance());
                return jobInfoDetail.getInstance();
            }
        }

        String status = jobInfoDetail.getInstance().getStatus();
        boolean jobStatusChanged = false;
        if (Asserts.isNull(jobInfoDetail.getJobHistory().getJob())
                || jobInfoDetail.getJobHistory().isError()) {
            if (Asserts.isNotNull(checkStatus)) {
                jobInfoDetail.getInstance().setStatus(checkStatus.getValue());
            } else {
                jobInfoDetail.getInstance().setStatus(JobStatus.UNKNOWN.getValue());
            }
        } else {
            jobInfoDetail
                    .getInstance()
                    .setDuration(
                            jobInfoDetail
                                            .getJobHistory()
                                            .getJob()
                                            .get(FlinkRestResultConstant.JOB_DURATION)
                                            .asLong()
                                    / 1000);
            jobInfoDetail
                    .getInstance()
                    .setStatus(
                            jobInfoDetail
                                    .getJobHistory()
                                    .getJob()
                                    .get(FlinkRestResultConstant.JOB_STATE)
                                    .asText());
        }
        if (JobStatus.isDone(jobInfoDetail.getInstance().getStatus())
                && !status.equals(jobInfoDetail.getInstance().getStatus())) {
            jobStatusChanged = true;
            jobInfoDetail.getInstance().setFinishTime(LocalDateTime.now());
        }
        if (isCoercive) {
            DaemonFactory.addTask(
                    DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInfoDetail.getInstance().getId()));
        }
        if (jobStatusChanged || jobInfoDetail.isNeedSave()) {
            jobInstanceService.updateById(jobInfoDetail.getInstance());
        }
        pool.refresh(jobInfoDetail);
        return jobInfoDetail.getInstance();
    }

    private boolean inRefreshPlan(JobInstance jobInstance) {
        return !JobStatus.isDone(jobInstance.getStatus())
                || (Asserts.isNotNull(jobInstance.getFinishTime())
                        && Duration.between(jobInstance.getFinishTime(), LocalDateTime.now())
                                        .toMinutes()
                                < 1);
    }

    @Override
    public JobInfoDetail refreshJobInfoDetail(Integer id) {
        return jobInstanceService.refreshJobInfoDetailInfo(refreshJobInstance(id, true));
    }

    @Override
    public String getTaskAPIAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            if (inetAddress != null) {
                return inetAddress.getHostAddress() + NetConstant.COLON + serverPort;
            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        return "127.0.0.1:" + serverPort;
    }

    @Override
    public Integer queryAllSizeByName(String name) {
        return baseMapper.queryAllSizeByName(name);
    }

    @Override
    public String exportJsonByTaskId(Integer taskId) {
        Task task = getTaskInfoById(taskId);
        if (Asserts.isNotNull(task.getClusterId())) {
            Cluster cluster = clusterInstanceService.getById(task.getClusterId());
            if (Asserts.isNotNull(cluster)) {
                task.setClusterName(cluster.getName());
            }
        }

        // path
        ObjectNode jsonNode = (ObjectNode) task.parseJsonNode(mapper);
        jsonNode.put("path", getTaskPathByTaskId(taskId));

        // clusterConfigurationName
        if (Asserts.isNotNull(task.getClusterConfigurationId())) {
            ClusterConfiguration clusterConfiguration =
                    clusterConfigurationService.getById(task.getClusterConfigurationId());
            jsonNode.put(
                    "clusterConfigurationName",
                    Asserts.isNotNull(clusterConfiguration)
                            ? clusterConfiguration.getName()
                            : null);
        }

        // databaseName
        if (Asserts.isNotNull(task.getDatabaseId())) {
            DataBase dataBase = dataBaseService.getById(task.getDatabaseId());
            jsonNode.put("databaseName", Asserts.isNotNull(dataBase) ? dataBase.getName() : null);
        }

        // jarName
        if (Asserts.isNotNull(task.getJarId())) {
            Jar jar = jarService.getById(task.getJarId());
            jsonNode.put("jarName", Asserts.isNotNull(jar) ? jar.getName() : null);
        }

        // envName
        if (Asserts.isNotNull(task.getEnvId())) {
            Task envTask = getById(task.getEnvId());
            jsonNode.put("envName", Asserts.isNotNull(envTask) ? envTask.getName() : null);
        }

        // alertGroupName
        if (Asserts.isNotNull(task.getAlertGroupId())) {
            AlertGroup alertGroup = alertGroupService.getById(task.getAlertGroupId());
            jsonNode.put(
                    "alertGroupName", Asserts.isNotNull(alertGroup) ? alertGroup.getName() : null);
        }
        return jsonNode.toString();
    }

    @Override
    public String exportJsonByTaskIds(JsonNode para) {
        StringBuilder tasksJson = new StringBuilder();
        tasksJson.append("[");
        for (final JsonNode item : para.get("taskIds")) {
            Integer id = item.asInt();
            tasksJson.append(exportJsonByTaskId(id) + ",");
        }
        tasksJson.deleteCharAt(tasksJson.length() - 1);
        tasksJson.append("]");
        return tasksJson.toString();
    }

    @Override
    public Result<Void> uploadTaskJson(MultipartFile file) throws Exception {
        if (file == null || file.getSize() == 0) {
            return Result.failed("上传失败，找不到文件");
        }

        String fileName = file.getOriginalFilename().split("\\.")[0];
        if (file.isEmpty() || file.getSize() <= 0 || fileName == null || "".equals(fileName)) {
            return Result.failed("传入的文件数据为空");
        }

        JsonNode jsonNode = mapper.readTree(getStrByJsonFile(file));
        return buildTaskByJsonNode(jsonNode, mapper);
    }

    public Result<Void> buildTaskByJsonNode(JsonNode jsonNode, ObjectMapper mapper)
            throws JsonProcessingException {
        List<JsonNode> jsonNodes = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode a : jsonNode) {
                jsonNodes.add(a);
            }
        } else {
            jsonNodes.add(jsonNode);
        }

        int errorNumber = 0;
        List<Task> tasks = new ArrayList<>();
        for (JsonNode json : jsonNodes) {
            Task task = mapper.treeToValue(json, Task.class);
            if (Asserts.isNotNull(task.getClusterName())) {
                Cluster cluster =
                        clusterInstanceService.getOne(
                                new QueryWrapper<Cluster>().eq("name", task.getClusterName()));
                if (Asserts.isNotNull(cluster)) {
                    task.setClusterId(cluster.getId());
                }
            }

            if (Asserts.isNotNull(task.getClusterConfigurationName())) {
                ClusterConfiguration clusterConfiguration =
                        clusterConfigurationService.getOne(
                                new QueryWrapper<ClusterConfiguration>()
                                        .eq("name", task.getClusterConfigurationName()));
                if (Asserts.isNotNull(clusterConfiguration)) {
                    task.setClusterConfigurationId(clusterConfiguration.getId());
                }
            }

            if (Asserts.isNotNull(task.getDatabaseName())) {
                DataBase dataBase =
                        dataBaseService.getOne(
                                new QueryWrapper<DataBase>().eq("name", task.getDatabaseName()));
                if (Asserts.isNotNull(dataBase)) {
                    task.setDatabaseId(dataBase.getId());
                }
            }

            if (Asserts.isNotNull(task.getJarName())) {
                Jar jar = jarService.getOne(new QueryWrapper<Jar>().eq("name", task.getJarName()));
                if (Asserts.isNotNull(jar)) {
                    task.setJarId(jar.getId());
                }
            }

            if (Asserts.isNotNull(task.getAlertGroupName())) {
                AlertGroup alertGroup =
                        alertGroupService.getOne(
                                new QueryWrapper<AlertGroup>()
                                        .eq("name", task.getAlertGroupName()));
                if (Asserts.isNotNull(alertGroup)) {
                    task.setAlertGroupId(alertGroup.getId());
                }
            }

            // 路径生成
            String[] paths = task.getPath().split("/");
            Integer parentId = catalogueService.addDependCatalogue(paths);
            Task task1 = getOne(new QueryWrapper<Task>().eq("name", task.getName()));
            if (Asserts.isNotNull(task1)) {
                errorNumber++;
                continue;
            }

            Integer step = task.getStep();
            this.saveOrUpdateTask(task);
            if (!JobLifeCycle.CREATE.getValue().equals(step)) {
                task.setStep(step);
                updateById(task);
            }
            if (Asserts.isNotNull(task.getEnvName())) {
                tasks.add(task);
            }
            Catalogue catalogue =
                    new Catalogue(task.getName(), task.getId(), task.getDialect(), parentId, true);
            catalogueService.saveOrUpdate(catalogue);
        }

        for (Task task : tasks) {
            Task task1 = getOne(new QueryWrapper<Task>().eq("name", task.getEnvName()));
            if (Asserts.isNotNull(task1)) {
                task.setEnvId(task1.getId());
                this.saveOrUpdateTask(task);
            }
        }

        if (errorNumber > 0 && errorNumber == jsonNodes.size()) {
            return Result.failed("一共" + jsonNodes.size() + "个作业,全部导入失败");
        }

        if (errorNumber > 0) {
            return Result.failed(
                    String.format(
                            "一共%d个作业,其中成功导入%d个,失败%d个",
                            jsonNodes.size(), jsonNode.size() - errorNumber, errorNumber));
        }
        return Result.succeed("成功导入" + jsonNodes.size() + "个作业");
    }

    public String getStrByJsonFile(MultipartFile jsonFile) {
        String jsonStr = "";
        try {
            Reader reader =
                    new InputStreamReader(jsonFile.getInputStream(), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTaskPathByTaskId(Integer taskId) {
        StringBuilder path = new StringBuilder();
        path.append(getById(taskId).getName());
        Catalogue catalogue =
                catalogueService.getOne(new QueryWrapper<Catalogue>().eq("task_id", taskId));
        if (Asserts.isNull(catalogue)) {
            return path.toString();
        }
        int catalogueId = catalogue.getParentId();
        do {
            catalogue = catalogueService.getById(catalogueId);
            if (Asserts.isNull(catalogue)) {
                return path.toString();
            }
            path.insert(0, catalogue.getName() + "/");
            catalogueId = catalogue.getParentId();
        } while (catalogueId != 0);
        return path.toString();
    }

    private String getDuration(long jobStartTimeMills, long jobEndTimeMills) {
        Instant startTime = Instant.ofEpochMilli(jobStartTimeMills);
        Instant endTime = Instant.ofEpochMilli(jobEndTimeMills);

        long days = ChronoUnit.DAYS.between(startTime, endTime);
        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
        String duration =
                String.format(
                        "%d天 %d小时 %d分 %d秒",
                        days,
                        hours - (days * 24),
                        minutes - (hours * 60),
                        seconds - (minutes * 60));
        return duration;
    }

    @Override
    public void handleJobDone(JobInstance jobInstance) {
        if (Asserts.isNull(jobInstance.getTaskId()) || Asserts.isNull(jobInstance.getType())) {
            return;
        }

        Task updateTask = new Task();
        updateTask.setId(jobInstance.getTaskId());
        updateTask.setJobInstanceId(0);

        Integer jobInstanceId = jobInstance.getId();
        // 获取任务历史信息
        JobHistory jobHistory = jobHistoryService.getJobHistory(jobInstanceId);
        // some job need do something on Done, example flink-kubernets-operator
        if (GatewayType.isDeployCluster(jobInstance.getType())) {
            JobConfig jobConfig = new JobConfig();
            Map<String, Object> clusterConfig =
                    JSONUtil.toMap(
                            jobHistory.getClusterConfiguration().get("configJson").asText(),
                            String.class,
                            Object.class);
            jobConfig.buildGatewayConfig(clusterConfig);
            jobConfig.getGatewayConfig().setType(GatewayType.get(jobInstance.getType()));
            jobConfig.getGatewayConfig().getFlinkConfig().setJobName(jobInstance.getName());
            Gateway.build(jobConfig.getGatewayConfig())
                    .onJobFinishCallback(jobInstance.getStatus());
        }

        if (!JobLifeCycle.ONLINE.equalsValue(jobInstance.getStep())) {
            updateById(updateTask);
            return;
        }

        ObjectNode jsonNodes = jobHistory.getJob();
        if (jsonNodes.has("errors")) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取任务历史信息的start-time
        long asLongStartTime = jsonNodes.get("start-time").asLong();
        // 获取任务历史信息的end-time
        long asLongEndTime = jsonNodes.get("end-time").asLong();

        if (asLongEndTime < asLongStartTime) {
            asLongEndTime = System.currentTimeMillis();
        }

        String startTime = dateFormat.format(asLongStartTime);
        String endTime = dateFormat.format(asLongEndTime);
        String duration = getDuration(asLongStartTime, asLongEndTime);

        // 获取任务的 duration 使用的是 start-time 和 end-time 计算
        // 不采用 duration 字段
        // 获取任务历史信息的clusterJson 主要获取 jobManagerHost
        ObjectNode clusterJsonNodes = jobHistory.getCluster();
        String jobManagerHost = clusterJsonNodes.get("jobManagerHost").asText();

        Task task = getTaskInfoById(jobInstance.getTaskId());
        if (Asserts.isNotNull(task.getAlertGroupId())) {
            AlertGroup alertGroup = alertGroupService.getAlertGroupInfo(task.getAlertGroupId());
            if (Asserts.isNotNull(alertGroup)) {

                // build alert msg of flink job link url
                String linkUrl =
                        String.format(
                                "http://%s/#/job/%s/overview",
                                jobManagerHost, jobInstance.getJid());

                // build alert msg of flink job exception url
                String exceptionUrl =
                        String.format(
                                "http://%s/#/job/%s/exceptions",
                                jobManagerHost, jobInstance.getJid());

                AlertMsg.AlertMsgBuilder alertMsgBuilder =
                        AlertMsg.builder()
                                .alertType("Flink 实时监控")
                                .alertTime(dateFormat.format(new Date()))
                                .jobID(jobInstance.getJid())
                                .jobName(task.getName())
                                .jobType(task.getDialect())
                                .jobStatus(jobInstance.getStatus())
                                .jobStartTime(startTime)
                                .jobEndTime(endTime)
                                .jobDuration(duration);

                for (AlertInstance alertInstance : alertGroup.getInstances()) {
                    if (alertInstance == null
                            || (Asserts.isNotNull(alertInstance.getEnabled())
                                    && !alertInstance.getEnabled())) {
                        continue;
                    }
                    Map<String, String> map = JSONUtil.toMap(alertInstance.getParams());
                    if (map.get("msgtype").equals(ShowType.MARKDOWN.getValue())) {
                        alertMsgBuilder
                                .linkUrl("[跳转至该任务的 FlinkWeb](" + linkUrl + ")")
                                .exceptionUrl("[点击查看该任务的异常日志](" + exceptionUrl + ")");
                    } else {
                        alertMsgBuilder.linkUrl(linkUrl).exceptionUrl(exceptionUrl);
                    }
                    AlertMsg alertMsg = alertMsgBuilder.build();

                    sendAlert(alertInstance, jobInstance, task, alertMsg);
                }
            }
        }
        updateTask.setStep(JobLifeCycle.RELEASE.getValue());
        updateById(updateTask);
    }

    private void sendAlert(
            AlertInstance alertInstance, JobInstance jobInstance, Task task, AlertMsg alertMsg) {
        AlertConfig alertConfig =
                AlertConfig.build(
                        alertInstance.getName(),
                        alertInstance.getType(),
                        JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.build(alertConfig);
        String title = "Task[" + task.getName() + "]: " + jobInstance.getStatus();
        String content = alertMsg.toString();
        AlertResult alertResult = alert.send(title, content);

        AlertHistory alertHistory = new AlertHistory();
        alertHistory.setAlertGroupId(task.getAlertGroupId());
        alertHistory.setJobInstanceId(jobInstance.getId());
        alertHistory.setTitle(title);
        alertHistory.setContent(content);
        alertHistory.setStatus(alertResult.getSuccessCode());
        alertHistory.setLog(alertResult.getMessage());
        alertHistoryService.save(alertHistory);
    }

    @Override
    public Result<Tree<Integer>> queryAllCatalogue() {
        final LambdaQueryWrapper<Catalogue> queryWrapper =
                new LambdaQueryWrapper<Catalogue>()
                        .select(Catalogue::getId, Catalogue::getName, Catalogue::getParentId)
                        .eq(Catalogue::getIsLeaf, 0)
                        .eq(Catalogue::getEnabled, 1)
                        .isNull(Catalogue::getTaskId);
        final List<Catalogue> catalogueList = catalogueService.list(queryWrapper);
        return Result.succeed(TreeUtil.build(dealWithCatalogue(catalogueList), -1).get(0));
    }

    private List<TreeNode<Integer>> dealWithCatalogue(List<Catalogue> catalogueList) {
        final List<TreeNode<Integer>> treeNodes = new ArrayList<>(8);
        treeNodes.add(new TreeNode<>(-1, null, "全部", -1));
        treeNodes.add(new TreeNode<>(0, -1, "全部", 0));
        if (CollectionUtils.isEmpty(catalogueList)) {
            return treeNodes;
        }
        for (int i = 0; i < catalogueList.size(); i++) {
            final Catalogue catalogue = catalogueList.get(i);
            if (Objects.isNull(catalogue)) {
                continue;
            }
            treeNodes.add(
                    new TreeNode<>(
                            catalogue.getId(),
                            catalogue.getParentId(),
                            catalogue.getName(),
                            i + 1));
        }
        return treeNodes;
    }

    @Override
    public Result<List<Task>> queryOnLineTaskByDoneStatus(
            List<JobLifeCycle> jobLifeCycle,
            List<JobStatus> jobStatuses,
            boolean includeNull,
            Integer catalogueId) {
        final Tree<Integer> node =
                queryAllCatalogue()
                        .getDatas()
                        .getNode(Objects.isNull(catalogueId) ? 0 : catalogueId);
        final List<Integer> parentIds = new ArrayList<>(0);
        parentIds.add(node.getId());
        childrenNodeParse(node, parentIds);
        final List<Task> taskList = getTasks(jobLifeCycle, jobStatuses, includeNull, parentIds);
        return Result.succeed(taskList);
    }

    private List<Task> getTasks(
            List<JobLifeCycle> jobLifeCycle,
            List<JobStatus> jobStatuses,
            boolean includeNull,
            List<Integer> parentIds) {
        return this.baseMapper.queryOnLineTaskByDoneStatus(
                parentIds,
                jobLifeCycle.stream()
                        .filter(Objects::nonNull)
                        .map(JobLifeCycle::getValue)
                        .collect(Collectors.toList()),
                includeNull,
                jobStatuses.stream().map(JobStatus::name).collect(Collectors.toList()));
    }

    private void childrenNodeParse(Tree<Integer> node, List<Integer> parentIds) {
        final List<Tree<Integer>> children = node.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        for (Tree<Integer> child : children) {
            parentIds.add(child.getId());
            if (!child.hasChild()) {
                continue;
            }
            childrenNodeParse(child, parentIds);
        }
    }

    @Override
    public void selectSavepointOnLineTask(TaskOperatingResult taskOperatingResult) {
        final JobInstance jobInstanceByTaskId =
                jobInstanceService.getJobInstanceByTaskId(taskOperatingResult.getTask().getId());
        if (jobInstanceByTaskId == null) {
            startGoingLiveTask(taskOperatingResult, null);
            return;
        }

        if (!JobStatus.isDone(jobInstanceByTaskId.getStatus())) {
            taskOperatingResult.setStatus(TaskOperatingStatus.TASK_STATUS_NO_DONE);
            return;
        }

        if (taskOperatingResult
                .getTaskOperatingSavepointSelect()
                .equals(TaskOperatingSavepointSelect.DEFAULT_CONFIG)) {
            startGoingLiveTask(taskOperatingResult, null);
            return;
        }
        findTheConditionSavePointToOnline(taskOperatingResult, jobInstanceByTaskId);
    }

    private void findTheConditionSavePointToOnline(
            TaskOperatingResult taskOperatingResult, JobInstance jobInstanceByTaskId) {
        final JobHistory jobHistory = jobHistoryService.getJobHistory(jobInstanceByTaskId.getId());
        if (jobHistory != null) {
            final ObjectNode jsonNodes = jobHistory.getCheckpoints();
            final ArrayNode history = jsonNodes.withArray("history");
            if (!history.isEmpty()) {
                startGoingLiveTask(taskOperatingResult, findTheConditionSavePoint(history));
                return;
            }
        }
        startGoingLiveTask(taskOperatingResult, null);
    }

    private void startGoingLiveTask(TaskOperatingResult taskOperatingResult, String savepointPath) {
        taskOperatingResult.setStatus(TaskOperatingStatus.OPERATING);
        final Result result = reOnLineTask(taskOperatingResult.getTask().getId(), savepointPath);
        taskOperatingResult.parseResult(result);
    }

    private String findTheConditionSavePoint(ArrayNode history) {
        JsonNode latestCompletedJsonNode = null;
        for (JsonNode item : history) {
            if (!"COMPLETED".equals(item.get("status").asText())) {
                continue;
            }

            if (latestCompletedJsonNode == null) {
                latestCompletedJsonNode = item;
                continue;
            }

            if (latestCompletedJsonNode.get("id").asInt() < item.get("id").asInt(-1)) {
                latestCompletedJsonNode = item;
            }
        }

        return latestCompletedJsonNode == null
                ? null
                : latestCompletedJsonNode.get("external_path").asText();
    }

    @Override
    public void selectSavepointOffLineTask(TaskOperatingResult taskOperatingResult) {
        taskOperatingResult.setStatus(TaskOperatingStatus.OPERATING);
        final Result result =
                offLineTask(taskOperatingResult.getTask().getId(), SavePointType.CANCEL.getValue());
        taskOperatingResult.parseResult(result);
    }
}
