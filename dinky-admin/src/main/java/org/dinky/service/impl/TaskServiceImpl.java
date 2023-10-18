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

import org.dinky.assertion.Asserts;
import org.dinky.config.Dialect;
import org.dinky.context.TenantContextHolder;
import org.dinky.data.constant.CommonConstant;
import org.dinky.data.dto.AbstractStatementDTO;
import org.dinky.data.dto.SqlDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.exception.SqlExplainExcepition;
import org.dinky.data.exception.TaskNotDoneException;
import org.dinky.data.model.AlertGroup;
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.Jar;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.JobModelOverview;
import org.dinky.data.model.JobTypeOverView;
import org.dinky.data.model.Savepoints;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.Task;
import org.dinky.data.model.TaskExtConfig;
import org.dinky.data.model.TaskVersion;
import org.dinky.data.model.UDFTemplate;
import org.dinky.data.result.Result;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.explainer.printTable.PrintStatementExplainer;
import org.dinky.function.compiler.CustomStringJavaCompiler;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.function.util.UDFUtil;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.gateway.model.JobInfo;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.Job;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;
import org.dinky.mapper.TaskMapper;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.parser.SqlType;
import org.dinky.process.annotations.ProcessStep;
import org.dinky.process.enums.ProcessStepType;
import org.dinky.service.AlertGroupService;
import org.dinky.service.CatalogueService;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.DataBaseService;
import org.dinky.service.FragmentVariableService;
import org.dinky.service.JarService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.SavepointsService;
import org.dinky.service.TaskService;
import org.dinky.service.TaskVersionService;
import org.dinky.service.UDFTemplateService;
import org.dinky.service.UserService;
import org.dinky.trans.Operations;
import org.dinky.utils.FragmentVariableUtils;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.RunTimeUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.UDFUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.text.StrFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TaskServiceImpl
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl extends SuperServiceImpl<TaskMapper, Task> implements TaskService {

    private final SavepointsService savepointsService;
    private final ClusterInstanceService clusterInstanceService;
    private final ClusterConfigurationService clusterCfgService;
    private final JarService jarService;
    private final DataBaseService dataBaseService;
    private final JobInstanceService jobInstanceService;
    private final AlertGroupService alertGroupService;
    private final TaskVersionService taskVersionService;
    private final FragmentVariableService fragmentVariableService;
    private final UDFTemplateService udfTemplateService;
    private final DataSourceProperties dsProperties;
    private final UserService userService;
    private final ApplicationContext applicationContext;

    @Resource
    @Lazy
    private CatalogueService catalogueService;

    private String[] buildParams(int id) {
        return String.format(
                        "--id %d --driver %s --url %s --username %s --password %s --dinkyAddr %s",
                        id,
                        dsProperties.getDriverClassName(),
                        dsProperties.getUrl(),
                        dsProperties.getUsername(),
                        dsProperties.getPassword(),
                        SystemConfiguration.getInstances().getDinkyAddr())
                .split(" ");
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_PRECHECK)
    public void preCheckTask(TaskDTO task) throws TaskNotDoneException, SqlExplainExcepition {
        log.info("Start check and config task, task:{}", task.getName());

        Assert.notNull(task, Status.TASK_NOT_EXIST.getMessage());

        if (!Dialect.isCommonSql(task.getDialect())
                && Asserts.isNotNull(task.getJobInstanceId())
                && task.getJobInstanceId() > 0) {
            JobInstance jobInstance = jobInstanceService.getById(task.getJobInstanceId());
            if (jobInstance != null && !JobStatus.isDone(jobInstance.getStatus())) {
                throw new TaskNotDoneException(Status.TASK_STATUS_IS_NOT_DONE.getMessage());
            }
        }

        log.info("Start explain Sql,task: {},Dialect:{}", task.getName(), task.getDialect());

        List<SqlExplainResult> sqlExplainResults = explainTask(task);
        for (SqlExplainResult sqlExplainResult : sqlExplainResults) {
            if (!sqlExplainResult.isParseTrue() || !sqlExplainResult.isExplainTrue()) {
                throw new SqlExplainExcepition(StrFormatter.format(
                        "task [{}] sql explain failed, sql [{}], error: [{}]",
                        task.getName(),
                        sqlExplainResult.getSql(),
                        sqlExplainResult.getError()));
            }
        }

        log.info("Explain Sql finish");
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeJob(TaskDTO task) throws Exception {
        JobResult jobResult;
        if (Dialect.isCommonSql(task.getDialect())) {
            log.info("Preparing to execute common sql...");
            SqlDTO sqlDTO = SqlDTO.build(task.getStatement(), task.getDatabaseId(), null);
            jobResult = dataBaseService.executeCommonSql(sqlDTO);
            ResultPool.putCommonSqlCache(task.getId(), (JdbcSelectResult) jobResult.getResult());
        } else {
            log.info("Initializing Flink job config...");
            JobManager jobManager = JobManager.build(
                    applicationContext.getBean(TaskServiceImpl.class).buildJobConfig(task));
            jobResult = jobManager.executeSql(task.getStatement());
        }
        log.info("execute job finished,status is {}", jobResult.getStatus());
        return jobResult;
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_BUILD_CONFIG)
    public JobConfig buildJobConfig(TaskDTO task) {
        task.setStatement(buildEnvSql(task) + task.getStatement());
        JobConfig config = task.getJobConfig();
        Savepoints savepoints = savepointsService.getSavePointWithStrategy(task);
        if (Asserts.isNotNull(savepoints)) {
            log.info("Init savePoint");
            config.setSavePointPath(savepoints.getPath());
            config.getConfigJson().put("execution.savepoint.path", savepoints.getPath()); // todo: 写工具类处理相关配置
        }
        if (GatewayType.get(task.getType()).isDeployCluster()) {
            log.info("Init gateway config, type:{}", task.getType());
            FlinkClusterConfig flinkClusterCfg =
                    clusterCfgService.getFlinkClusterCfg(config.getClusterConfigurationId());
            flinkClusterCfg.getAppConfig().setUserJarParas(buildParams(config.getTaskId()));
            config.buildGatewayConfig(flinkClusterCfg);
        } else {
            log.info("Init remote cluster");
            String address = clusterInstanceService.buildEnvironmentAddress(config.isUseRemote(), task.getClusterId());
            config.setAddress(address);
        }
        return config;
    }

    @Override
    public String buildEnvSql(AbstractStatementDTO task) {
        log.info("Start initialize FlinkSQLEnv:");
        String sql = CommonConstant.LineSep;
        if (task.isFragment()) {
            String flinkWithSql = dataBaseService.getEnabledFlinkWithSql();
            if (Asserts.isNotNullString(flinkWithSql)) {
                sql += flinkWithSql + CommonConstant.LineSep;
            }
            task.setVariables(fragmentVariableService.listEnabledVariables());
        }
        int envId = Optional.ofNullable(task.getEnvId()).orElse(-1);
        if (envId != -1) {
            TaskDTO envTask = this.getTaskInfoById(task.getEnvId());
            if (Asserts.isNotNull(envTask) && Asserts.isNotNullString(envTask.getStatement())) {
                sql += envTask.getStatement() + CommonConstant.LineSep;
            }
        }
        log.info("Initializing data permissions...");
        userService.buildRowPermission();
        log.info("Finish initialize FlinkSQLEnv.");
        return sql;
    }

    @Override
    @ProcessStep(type = ProcessStepType.SUBMIT_TASK)
    public JobResult submitTask(Integer id, String savePointPath) throws Exception {
        initTenantByTaskId(id);

        TaskDTO taskDTO = this.getTaskInfoById(id);

        if (StringUtils.isNotBlank(savePointPath)) {
            taskDTO.setSavePointStrategy(SavePointStrategy.CUSTOM.getValue());
            taskDTO.setSavePointPath(savePointPath);
        }
        // 注解自调用会失效，这里通过获取对象方法绕过此限制
        TaskServiceImpl taskServiceBean = applicationContext.getBean(TaskServiceImpl.class);
        taskServiceBean.preCheckTask(taskDTO);

        JobResult jobResult = taskServiceBean.executeJob(taskDTO);

        if (Job.JobStatus.SUCCESS == jobResult.getStatus()) {
            log.info("Job Submit success");
            Task task = new Task(id, jobResult.getJobInstanceId());
            if (!this.updateById(task)) {
                throw new BusException(Status.TASK_UPDATE_FAILED.getMessage());
            }
        } else {
            log.error("Job Submit failed, error: " + jobResult.getError());
        }
        return jobResult;
    }

    @Override
    public JobResult restartTask(Integer id, String savePointPath) throws Exception {
        TaskDTO task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMessage());
        if (!Dialect.isCommonSql(task.getDialect()) && Asserts.isNotNull(task.getJobInstanceId())) {
            String status = jobInstanceService.getById(task.getJobInstanceId()).getStatus();
            if (!JobStatus.isDone(status)) {
                cancelTaskJob(task);
            }
        }
        return submitTask(id, savePointPath);
    }

    @Override
    public boolean cancelTaskJob(TaskDTO task) {
        JobInstance jobInstance = jobInstanceService.getById(task.getJobInstanceId());
        Assert.notNull(jobInstance, Status.JOB_INSTANCE_NOT_EXIST.getMessage());
        ClusterInstance clusterInstance = clusterInstanceService.getById(jobInstance.getClusterId());
        Assert.notNull(clusterInstance, Status.CLUSTER_NOT_EXIST.getMessage());

        JobManager jobManager = JobManager.build(buildJobConfig(task));
        return jobManager.cancel(jobInstance.getJid());
    }

    @Override
    public SavePointResult savepointTaskJob(TaskDTO task, SavePointType savePointType) {
        JobInstance jobInstance = jobInstanceService.getById(task.getJobInstanceId());
        Assert.notNull(jobInstance, Status.JOB_INSTANCE_NOT_EXIST.getMessage());

        JobManager jobManager = JobManager.build(buildJobConfig(task));
        String jobId = jobInstance.getJid();

        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType, null);
        Assert.notNull(savePointResult.getJobInfos());
        for (JobInfo item : savePointResult.getJobInfos()) {
            if (Asserts.isEqualsIgnoreCase(jobId, item.getJobId()) && Asserts.isNotNull(jobInstance.getTaskId())) {
                Savepoints savepoints = new Savepoints();
                savepoints.setName(savePointType.getValue());
                savepoints.setType(savePointType.getValue());
                savepoints.setPath(item.getSavePoint());
                savepoints.setTaskId(task.getId());
                savepointsService.save(savepoints);
            }
        }
        return savePointResult;
    }

    @Override
    public List<SqlExplainResult> explainTask(TaskDTO task) throws NotSupportExplainExcepition {

        if (Dialect.isCommonSql(task.getDialect())) {
            return dataBaseService.explainCommonSql(task);
        } else if (task.getDialect().equals(Dialect.FLINK_SQL.getValue())) {
            JobConfig config = buildJobConfig(task);
            config.buildLocal();
            JobManager jobManager = JobManager.buildPlanMode(config);
            return jobManager.explainSql(task.getStatement()).getSqlExplainResults();
        }
        throw new NotSupportExplainExcepition(StrFormatter.format(
                "task [{}] dialect [{}] is can not explain, skip sqlExplain verify",
                task.getName(),
                task.getDialect()));
    }

    @Override
    public ObjectNode getJobPlan(TaskDTO task) {
        JobManager jobManager = JobManager.buildPlanMode(buildJobConfig(task));
        String planJson = jobManager.getJobPlanJson(task.getStatement());
        return JsonUtils.parseObject(planJson);
    }

    @Override
    public ObjectNode getStreamGraph(TaskDTO taskDTO) {
        JobConfig config = taskDTO.getJobConfig();
        JobManager jobManager = JobManager.buildPlanMode(config);
        ObjectNode streamGraph = jobManager.getStreamGraph(taskDTO.getStatement());
        RunTimeUtil.recovery(jobManager);
        return streamGraph;
    }

    @Override
    public String exportSql(Integer id) {
        TaskDTO task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Status.TASK_NOT_EXIST.getMessage());
        if (Dialect.isCommonSql(task.getDialect())) {
            return task.getStatement();
        }

        JobConfig config = buildJobConfig(task);

        // 加密敏感信息
        if (config.getVariables() != null) {
            for (Map.Entry<String, String> entry : config.getVariables().entrySet()) {
                if (FragmentVariableUtils.isSensitive(entry.getKey())) {
                    entry.setValue(FragmentVariableUtils.HIDDEN_CONTENT);
                }
            }
        }

        JobManager jobManager = JobManager.build(config);
        if (config.isJarTask()) {
            return "";
        }

        return jobManager.exportSql(task.getStatement());
    }

    @Override
    public TaskDTO getTaskInfoById(Integer id) {
        Task mTask = this.getById(id);
        Assert.notNull(mTask, Status.TASK_NOT_EXIST.getMessage());
        TaskDTO taskDTO = new TaskDTO();
        BeanUtil.copyProperties(mTask, taskDTO);

        if (taskDTO.getClusterId() != null) {
            ClusterInstance clusterInstance = clusterInstanceService.getById(taskDTO.getClusterId());
            if (clusterInstance != null) {
                taskDTO.setClusterName(clusterInstance.getAlias());
            }
        }
        if (taskDTO.getJobInstanceId() != null) {
            JobInstance jobInstance = jobInstanceService.getById(taskDTO.getJobInstanceId());
            if (jobInstance != null) {
                taskDTO.setStatus(jobInstance.getStatus());
            }
        }
        return taskDTO;
    }

    @Override
    public void initTenantByTaskId(Integer id) {
        Integer tenantId = baseMapper.getTenantByTaskId(id);
        Asserts.checkNull(tenantId, Status.TASK_NOT_EXIST.getMessage());
        TenantContextHolder.set(tenantId);
    }

    @Override
    public boolean changeTaskLifeRecyle(Integer taskId, JobLifeCycle lifeCycle) {
        TaskDTO taskInfoById = getTaskInfoById(taskId);
        taskInfoById.setStep(lifeCycle.getValue());
        if (lifeCycle == JobLifeCycle.ONLINE) {
            taskVersionService.createTaskVersionSnapshot(taskInfoById);
        }
        return saveOrUpdate(taskInfoById.buildTask());
    }

    @Override
    public boolean saveOrUpdateTask(Task task) {

        if (JobLifeCycle.ONLINE.equalsValue(task.getStep())) {
            throw new BusException(Status.TASK_IS_ONLINE.getMessage());
        }

        if (Dialect.isUDF(task.getDialect())) {

            TaskExtConfig taskConfigJson = task.getConfigJson();

            if (BeanUtil.isNotEmpty(task.getConfigJson())
                    && Asserts.isNullString(task.getStatement())
                    && BeanUtil.isNotEmpty(taskConfigJson.getUdfConfig())) {

                UDFTemplate template =
                        udfTemplateService.getById(taskConfigJson.getUdfConfig().getTemplateId());
                if (template != null) {
                    String code = UDFUtil.templateParse(
                            task.getDialect(),
                            template.getTemplateCode(),
                            taskConfigJson.getUdfConfig().getClassName());
                    task.setStatement(code);
                }
            }
            // to compiler udf
            if (Asserts.isNotNullString(task.getDialect())
                    && Dialect.JAVA.equalsVal(task.getDialect())
                    && Asserts.isNotNullString(task.getStatement())) {
                CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(task.getStatement());
                task.setSavePointPath(compiler.getFullClassName());
            } else if (Dialect.PYTHON.equalsVal(task.getDialect())) {
                task.setSavePointPath(task.getName() + "." + UDFUtil.getPyUDFAttr(task.getStatement()));
            } else if (Dialect.SCALA.equalsVal(task.getDialect())) {
                task.setSavePointPath(UDFUtil.getScalaFullClassName(task.getStatement()));
            }
            UdfCodePool.addOrUpdate(UDFUtils.taskToUDF(task));
        }

        return this.saveOrUpdate(task);
    }

    @Override
    public List<Task> listFlinkSQLEnv() {
        return this.list(new QueryWrapper<Task>()
                .lambda()
                .eq(Task::getDialect, Dialect.FLINK_SQL_ENV.getValue())
                .eq(Task::getEnabled, 1));
    }

    @Override
    public Task initDefaultFlinkSQLEnv(Integer tenantId) {
        TenantContextHolder.set(tenantId);
        String separator = SystemConfiguration.getInstances().getSqlSeparator();
        separator = separator.replace("\\r", "\r").replace("\\n", "\n");
        String name = "DefaultCatalog";

        Task defaultFlinkSQLEnvTask = getTaskByNameAndTenantId(name, tenantId);

        String sql = String.format(
                "create catalog my_catalog with(\n    "
                        + "'type' = 'dinky_mysql',\n"
                        + "    'username' = "
                        + "'%s',\n    "
                        + "'password' = '%s',\n"
                        + "    'url' = '%s'\n"
                        + ")%suse catalog my_catalog%s",
                dsProperties.getUsername(), dsProperties.getPassword(), dsProperties.getUrl(), separator, separator);

        if (null != defaultFlinkSQLEnvTask) {
            defaultFlinkSQLEnvTask.setStatement(sql);
            saveOrUpdateTask(defaultFlinkSQLEnvTask);
            return defaultFlinkSQLEnvTask;
        }

        defaultFlinkSQLEnvTask = new Task();
        defaultFlinkSQLEnvTask.setName(name);
        defaultFlinkSQLEnvTask.setDialect(Dialect.FLINK_SQL_ENV.getValue());
        defaultFlinkSQLEnvTask.setStatement(sql);
        defaultFlinkSQLEnvTask.setFragment(true);
        defaultFlinkSQLEnvTask.setTenantId(tenantId);
        defaultFlinkSQLEnvTask.setEnabled(true);
        saveOrUpdate(defaultFlinkSQLEnvTask);

        return defaultFlinkSQLEnvTask;
    }

    @Override
    public Task getTaskByNameAndTenantId(String name, Integer tenantId) {
        return baseMapper.getTaskByNameAndTenantId(name, tenantId);
    }

    @Override
    public List<JobTypeOverView> getTaskOnlineRate() {
        return baseMapper.getTaskOnlineRate();
    }

    @Override
    public JobModelOverview getJobStreamingOrBatchModelOverview() {
        return baseMapper.getJobStreamingOrBatchModelOverview();
    }

    @Override
    public List<Task> getAllUDF() {
        List<Task> tasks = list(new QueryWrapper<Task>()
                .in("dialect", Dialect.JAVA, Dialect.SCALA, Dialect.PYTHON)
                .eq("enabled", 1)
                .isNotNull("save_point_path"));
        return tasks.stream()
                .peek(task -> {
                    Assert.notNull(task, Status.TASK_NOT_EXIST.getMessage());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Result<Void> rollbackTask(TaskRollbackVersionDTO dto) {
        if (Asserts.isNull(dto.getVersionId()) || Asserts.isNull(dto.getId())) {
            return Result.failed("the version is error");
        }

        LambdaQueryWrapper<TaskVersion> queryWrapper = new LambdaQueryWrapper<TaskVersion>()
                .eq(TaskVersion::getTaskId, dto.getId())
                .eq(TaskVersion::getVersionId, dto.getVersionId());

        TaskVersion taskVersion = taskVersionService.getOne(queryWrapper);

        Task updateTask = new Task();
        BeanUtil.copyProperties(taskVersion, updateTask);
        BeanUtil.copyProperties(taskVersion.getTaskConfigure(), updateTask);
        updateTask.setId(taskVersion.getTaskId());
        updateTask.setStep(JobLifeCycle.DEVELOP.getValue());
        baseMapper.updateById(updateTask);
        return Result.succeed("version rollback success！");
    }

    @Override
    public String getTaskAPIAddress() {
        return SystemConfiguration.getInstances().getDinkyAddr().getValue();
    }

    @Override
    public Integer queryAllSizeByName(String name) {
        return baseMapper.queryAllSizeByName(name);
    }

    @Override
    public String exportJsonByTaskId(Integer taskId) {
        TaskDTO task = getTaskInfoById(taskId);
        if (Asserts.isNotNull(task.getClusterId())) {
            ClusterInstance clusterInstance = clusterInstanceService.getById(task.getClusterId());
            if (Asserts.isNotNull(clusterInstance)) {
                task.setClusterName(clusterInstance.getName());
            }
        }

        // path
        ObjectNode jsonNode = (ObjectNode) JsonUtils.toJsonNode(task);
        jsonNode.put("path", getTaskPathByTaskId(taskId));

        // clusterConfigurationName
        if (Asserts.isNotNull(task.getClusterConfigurationId())) {
            ClusterConfiguration clusterConfiguration = clusterCfgService.getById(task.getClusterConfigurationId());
            jsonNode.put(
                    "clusterConfigurationName",
                    Asserts.isNotNull(clusterConfiguration) ? clusterConfiguration.getName() : null);
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
            jsonNode.put("alertGroupName", Asserts.isNotNull(alertGroup) ? alertGroup.getName() : null);
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(getStrByJsonFile(file));
        return buildTaskByJsonNode(jsonNode, mapper);
    }

    public Result<Void> buildTaskByJsonNode(JsonNode jsonNode, ObjectMapper mapper) throws JsonProcessingException {
        List<JsonNode> jsonNodes = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode a : jsonNode) {
                jsonNodes.add(a);
            }
        } else {
            jsonNodes.add(jsonNode);
        }

        int errorNumber = 0;
        List<TaskDTO> tasks = new ArrayList<>();
        for (JsonNode json : jsonNodes) {
            TaskDTO task = mapper.treeToValue(json, TaskDTO.class);
            if (Asserts.isNotNull(task.getClusterName())) {
                ClusterInstance clusterInstance = clusterInstanceService.getOne(
                        new QueryWrapper<ClusterInstance>().eq("name", task.getClusterName()));
                if (Asserts.isNotNull(clusterInstance)) {
                    task.setClusterId(clusterInstance.getId());
                }
            }

            if (Asserts.isNotNull(task.getClusterConfigurationName())) {
                ClusterConfiguration clusterConfiguration = clusterCfgService.getOne(
                        new QueryWrapper<ClusterConfiguration>().eq("name", task.getClusterConfigurationName()));
                if (Asserts.isNotNull(clusterConfiguration)) {
                    task.setClusterConfigurationId(clusterConfiguration.getId());
                }
            }

            if (Asserts.isNotNull(task.getDatabaseName())) {
                DataBase dataBase =
                        dataBaseService.getOne(new QueryWrapper<DataBase>().eq("name", task.getDatabaseName()));
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
                        alertGroupService.getOne(new QueryWrapper<AlertGroup>().eq("name", task.getAlertGroupName()));
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

            this.saveOrUpdateTask(task.buildTask());
            if (Asserts.isNotNull(task.getEnvName())) {
                tasks.add(task);
            }
            Catalogue catalogue = new Catalogue(task.getName(), task.getId(), task.getDialect(), parentId, true);
            catalogueService.saveOrUpdate(catalogue);
        }

        for (TaskDTO task : tasks) {
            Task task1 = getOne(new QueryWrapper<Task>().eq("name", task.getEnvName()));
            if (Asserts.isNotNull(task1)) {
                task.setEnvId(task1.getId());
                this.saveOrUpdateTask(task.buildTask());
            }
        }

        if (errorNumber > 0 && errorNumber == jsonNodes.size()) {
            return Result.failed("一共" + jsonNodes.size() + "个作业,全部导入失败");
        }

        if (errorNumber > 0) {
            return Result.failed(String.format(
                    "一共%d个作业,其中成功导入%d个,失败%d个", jsonNodes.size(), jsonNode.size() - errorNumber, errorNumber));
        }
        return Result.succeed("成功导入" + jsonNodes.size() + "个作业");
    }

    public String getStrByJsonFile(MultipartFile jsonFile) {
        String jsonStr = "";
        try {
            Reader reader = new InputStreamReader(jsonFile.getInputStream(), StandardCharsets.UTF_8);
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
        Catalogue catalogue = catalogueService.getOne(new QueryWrapper<Catalogue>().eq("task_id", taskId));
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

    @Override
    public Result<Tree<Integer>> queryAllCatalogue() {
        final LambdaQueryWrapper<Catalogue> queryWrapper = new LambdaQueryWrapper<Catalogue>()
                .select(Catalogue::getId, Catalogue::getName, Catalogue::getParentId)
                .eq(Catalogue::getIsLeaf, 0)
                .eq(Catalogue::getEnabled, 1)
                .isNull(Catalogue::getTaskId);
        final List<Catalogue> catalogueList = catalogueService.list(queryWrapper);
        return Result.succeed(
                TreeUtil.build(dealWithCatalogue(catalogueList), -1).get(0));
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
            treeNodes.add(new TreeNode<>(catalogue.getId(), catalogue.getParentId(), catalogue.getName(), i + 1));
        }
        return treeNodes;
    }

    @Override
    public List<String> getPrintTables(String statement) {
        // TODO: 2023/4/7 this function not support variable sql, because, JobManager and executor
        // couple function
        //  and status and task execute.
        final String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement));
        return Arrays.stream(statements)
                .filter(t -> SqlType.PRINT.equals(Operations.getOperationType(t)))
                .flatMap(t -> Arrays.stream(PrintStatementExplainer.splitTableNames(t)))
                .collect(Collectors.toList());
    }
}
