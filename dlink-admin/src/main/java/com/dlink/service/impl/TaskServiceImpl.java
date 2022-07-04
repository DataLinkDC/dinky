package com.dlink.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.alert.Alert;
import com.dlink.alert.AlertConfig;
import com.dlink.alert.AlertMsg;
import com.dlink.alert.AlertResult;
import com.dlink.alert.ShowType;
import com.dlink.assertion.Assert;
import com.dlink.assertion.Asserts;
import com.dlink.assertion.Tips;
import com.dlink.common.result.Result;
import com.dlink.config.Dialect;
import com.dlink.constant.FlinkRestResultConstant;
import com.dlink.constant.NetConstant;
import com.dlink.daemon.task.DaemonFactory;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.dto.SqlDTO;
import com.dlink.dto.TaskRollbackVersionDTO;
import com.dlink.dto.TaskVersionConfigureDTO;
import com.dlink.exception.BusException;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.FlinkJobTask;
import com.dlink.job.FlinkJobTaskPool;
import com.dlink.job.Job;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.mapper.TaskMapper;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.AlertGroup;
import com.dlink.model.AlertHistory;
import com.dlink.model.AlertInstance;
import com.dlink.model.Cluster;
import com.dlink.model.DataBase;
import com.dlink.model.History;
import com.dlink.model.Jar;
import com.dlink.model.JobHistory;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobLifeCycle;
import com.dlink.model.JobStatus;
import com.dlink.model.Savepoints;
import com.dlink.model.Statement;
import com.dlink.model.SystemConfiguration;
import com.dlink.model.Task;
import com.dlink.model.TaskVersion;
import com.dlink.result.SqlExplainResult;
import com.dlink.service.AlertGroupService;
import com.dlink.service.AlertHistoryService;
import com.dlink.service.ClusterConfigurationService;
import com.dlink.service.ClusterService;
import com.dlink.service.DataBaseService;
import com.dlink.service.HistoryService;
import com.dlink.service.JarService;
import com.dlink.service.JobHistoryService;
import com.dlink.service.JobInstanceService;
import com.dlink.service.SavepointsService;
import com.dlink.service.StatementService;
import com.dlink.service.TaskService;
import com.dlink.service.TaskVersionService;
import com.dlink.utils.CustomStringJavaCompiler;
import com.dlink.utils.JSONUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.bean.BeanUtil;

/**
 * 任务 服务实现类
 *
 * @author wenmo
 * @since 2021-05-24
 */
@Service
public class TaskServiceImpl extends SuperServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private StatementService statementService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private SavepointsService savepointsService;
    @Autowired
    private JarService jarService;
    @Autowired
    private DataBaseService dataBaseService;
    @Autowired
    private JobInstanceService jobInstanceService;
    @Autowired
    private JobHistoryService jobHistoryService;
    @Autowired
    private AlertGroupService alertGroupService;
    @Autowired
    private AlertHistoryService alertHistoryService;
    @Autowired
    private HistoryService historyService;
    @Resource
    private TaskVersionService taskVersionService;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${server.port}")
    private String serverPort;

    private String buildParas(Integer id) {
        return "--id " + id + " --driver " + driver + " --url " + url + " --username " + username + " --password " + password;
    }

    @Override
    public JobResult submitTask(Integer id) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Tips.TASK_NOT_EXIST);
        if (Dialect.isSql(task.getDialect())) {
            return executeCommonSql(SqlDTO.build(task.getStatement(),
                task.getDatabaseId(), null));
        }
        JobConfig config = buildJobConfig(task);
        JobManager jobManager = JobManager.build(config);
        if (!config.isJarTask()) {
            return jobManager.executeSql(task.getStatement());
        } else {
            return jobManager.executeJar();
        }
    }

    @Override
    public JobResult submitTaskToOnline(Integer id) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Tips.TASK_NOT_EXIST);
        task.setStep(JobLifeCycle.ONLINE.getValue());
        if (Dialect.isSql(task.getDialect())) {
            return executeCommonSql(SqlDTO.build(task.getStatement(),
                task.getDatabaseId(), null));
        }
        JobConfig config = buildJobConfig(task);
        JobManager jobManager = JobManager.build(config);
        if (!config.isJarTask()) {
            return jobManager.executeSql(task.getStatement());
        } else {
            return jobManager.executeJar();
        }
    }

    @Override
    public JobResult restartTask(Integer id) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Tips.TASK_NOT_EXIST);
        if (Asserts.isNotNull(task.getJobInstanceId()) && task.getJobInstanceId() != 0) {
            savepointJobInstance(task.getJobInstanceId(), SavePointType.CANCEL.getValue());
        }
        if (Dialect.isSql(task.getDialect())) {
            return executeCommonSql(SqlDTO.build(task.getStatement(),
                task.getDatabaseId(), null));
        }
        task.setSavePointStrategy(SavePointStrategy.LATEST.getValue());
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
    public List<SqlExplainResult> explainTask(Integer id) {
        Task task = getTaskInfoById(id);
        if (Dialect.isSql(task.getDialect())) {
            return explainCommonSqlTask(task);
        } else {
            return explainFlinkSqlTask(task);
        }
    }

    private List<SqlExplainResult> explainFlinkSqlTask(Task task) {
        JobConfig config = buildJobConfig(task);
        config.buildLocal();
        JobManager jobManager = JobManager.buildPlanMode(config);
        return jobManager.explainSql(task.getStatement()).getSqlExplainResults();
    }

    private List<SqlExplainResult> explainCommonSqlTask(Task task) {
        if (Asserts.isNull(task.getDatabaseId())) {
            return new ArrayList<SqlExplainResult>() {{
                add(SqlExplainResult.fail(task.getStatement(), "请指定数据源"));
            }};
        } else {
            DataBase dataBase = dataBaseService.getById(task.getDatabaseId());
            if (Asserts.isNull(dataBase)) {
                return new ArrayList<SqlExplainResult>() {{
                    add(SqlExplainResult.fail(task.getStatement(), "数据源不存在"));
                }};
            }
            Driver driver = Driver.build(dataBase.getDriverConfig());
            List<SqlExplainResult> sqlExplainResults = driver.explain(task.getStatement());
            driver.close();
            return sqlExplainResults;
        }
    }

    @Override
    public Task getTaskInfoById(Integer id) {
        Task task = this.getById(id);
        if (task != null) {
            task.parseConfig();
            Statement statement = statementService.getById(id);
            if (task.getClusterId() != null) {
                Cluster cluster = clusterService.getById(task.getClusterId());
                if (cluster != null) {
                    task.setClusterName(cluster.getAlias());
                }
            }
            if (statement != null) {
                task.setStatement(statement.getStatement());
            }
            JobInstance jobInstance = jobInstanceService.getJobInstanceByTaskId(id);
            if (Asserts.isNotNull(jobInstance) && !JobStatus.isDone(jobInstance.getStatus())) {
                task.setJobInstanceId(jobInstance.getId());
            } else {
                task.setJobInstanceId(0);
            }
        }
        return task;
    }

    @Override
    public boolean saveOrUpdateTask(Task task) {
        // to compiler java udf
        if (Asserts.isNotNullString(task.getDialect()) && Dialect.JAVA.equalsVal(task.getDialect())
            && Asserts.isNotNullString(task.getStatement())) {
            CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(task.getStatement());
            task.setSavePointPath(compiler.getFullClassName());
        }
        // if modify task else create task
        if (task.getId() != null) {
            Task taskInfo = getById(task.getId());
            Assert.check(taskInfo);
            if (JobLifeCycle.RELEASE.equalsValue(taskInfo.getStep()) ||
                JobLifeCycle.ONLINE.equalsValue(taskInfo.getStep()) ||
                JobLifeCycle.CANCEL.equalsValue(taskInfo.getStep())) {
                throw new BusException("该作业已" + JobLifeCycle.get(taskInfo.getStep()).getLabel() + "，禁止修改！");
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
        return this.list(new QueryWrapper<Task>().eq("dialect", Dialect.FLINKSQLENV).eq("enabled", 1));
    }

    @Override
    public String exportSql(Integer id) {
        Task task = getTaskInfoById(id);
        Asserts.checkNull(task, Tips.TASK_NOT_EXIST);
        if (Dialect.isSql(task.getDialect())) {
            return task.getStatement();
        }
        JobConfig config = buildJobConfig(task);
        JobManager jobManager = JobManager.build(config);
        if (!config.isJarTask()) {
            return jobManager.exportSql(task.getStatement());
        } else {
            return "";
        }
    }

    @Override
    public Task getUDFByClassName(String className) {
        Task task = getOne(new QueryWrapper<Task>().eq("dialect", "Java").eq("enabled", 1).eq("save_point_path", className));
        Assert.check(task);
        task.setStatement(statementService.getById(task.getId()).getStatement());
        return task;
    }

    @Override
    public Result releaseTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.DEVELOP.equalsValue(task.getStep())) {
            List<SqlExplainResult> sqlExplainResults = explainTask(id);
            for (SqlExplainResult sqlExplainResult : sqlExplainResults) {
                if (!sqlExplainResult.isParseTrue() || !sqlExplainResult.isExplainTrue()) {
                    return Result.failed("语法校验和逻辑检查有误，发布失败");
                }
            }
            task.setStep(JobLifeCycle.RELEASE.getValue());
            Task newTask = createTaskVersionSnapshot(task);
            if (updateById(newTask)) {
                return Result.succeed("发布成功");
            } else {
                return Result.failed("由于未知原因，发布失败");
            }
        }
        return Result.succeed("发布成功");
    }


    public Task createTaskVersionSnapshot(Task task) {
        List<TaskVersion> taskVersions = taskVersionService.getTaskVersionByTaskId(task.getId());
        List<Integer> versionIds = taskVersions.stream().map(TaskVersion::getVersionId).collect(Collectors.toList());
        Map<Integer, TaskVersion> versionMap = taskVersions.stream().collect(Collectors.toMap(TaskVersion::getVersionId, t -> t));

        TaskVersion taskVersion = new TaskVersion();
        BeanUtil.copyProperties(task, taskVersion);
        TaskVersionConfigureDTO taskVersionConfigureDTO = new TaskVersionConfigureDTO();
        BeanUtil.copyProperties(task, taskVersionConfigureDTO);
        taskVersion.setTaskConfigure(taskVersionConfigureDTO);
        taskVersion.setTaskId(taskVersion.getId());
        taskVersion.setId(null);
        if (Asserts.isNull(task.getVersionId())) {
            //首次发布，新增版本
            taskVersion.setVersionId(1);
            task.setVersionId(1);
            taskVersionService.save(taskVersion);
        } else {
            //说明存在版本，需要判断是否 是回退后的老版本
            //1、版本号存在
            //2、md5值与上一个版本一致
            TaskVersion version = versionMap.get(task.getVersionId());
            version.setId(null);

            if (versionIds.contains(task.getVersionId()) && !taskVersion.equals(version)
                //||  !versionIds.contains(task.getVersionId()) && !taskVersion.equals(version)
            ) {
                taskVersion.setVersionId(Collections.max(versionIds) + 1);
                task.setVersionId(Collections.max(versionIds) + 1);
                taskVersionService.save(taskVersion);
            }
        }
        return task;
    }

    @Override
    public Result rollbackTask(TaskRollbackVersionDTO dto) {
        if (Asserts.isNull(dto.getVersionId()) || Asserts.isNull(dto.getId())) {
            return Result.failed("版本指定失败");
        }
        Task taskInfo = getTaskInfoById(dto.getId());

        if (JobLifeCycle.RELEASE.equalsValue(taskInfo.getStep()) ||
            JobLifeCycle.ONLINE.equalsValue(taskInfo.getStep()) ||
            JobLifeCycle.CANCEL.equalsValue(taskInfo.getStep())) {
            //throw new BusException("该作业已" + JobLifeCycle.get(taskInfo.getStep()).getLabel() + "，禁止回滚！");
            return Result.failed("该作业已" + JobLifeCycle.get(taskInfo.getStep()).getLabel() + "，禁止回滚！");
        }

        LambdaQueryWrapper<TaskVersion> queryWrapper = new LambdaQueryWrapper<TaskVersion>().
            eq(TaskVersion::getTaskId, dto.getId()).
            eq(TaskVersion::getVersionId, dto.getVersionId());

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
        return Result.succeed("回滚版本成功！");
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
    public Result onLineTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.RELEASE.equalsValue(task.getStep())) {
            if (Asserts.isNotNull(task.getJobInstanceId()) && task.getJobInstanceId() != 0) {
                return Result.failed("当前发布状态下有作业正在运行，上线失败，请停止后上线");
            }
            JobResult jobResult = submitTaskToOnline(id);
            if (Job.JobStatus.SUCCESS == jobResult.getStatus()) {
                task.setStep(JobLifeCycle.ONLINE.getValue());
                task.setJobInstanceId(jobResult.getJobInstanceId());
                if (updateById(task)) {
                    return Result.succeed(jobResult, "上线成功");
                } else {
                    return Result.failed("由于未知原因，上线失败");
                }
            } else {
                return Result.failed("上线失败，原因：" + jobResult.getError());
            }
        } else if (JobLifeCycle.ONLINE.equalsValue(task.getStep())) {
            return Result.failed("上线失败，当前作业已上线。");
        }
        return Result.failed("上线失败，当前作业未发布。");
    }

    @Override
    public Result reOnLineTask(Integer id) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Tips.TASK_NOT_EXIST);
        if (Asserts.isNotNull(task.getJobInstanceId()) && task.getJobInstanceId() != 0) {
            savepointJobInstance(task.getJobInstanceId(), SavePointType.CANCEL.getValue());
        }
        JobResult jobResult = submitTaskToOnline(id);
        if (Job.JobStatus.SUCCESS == jobResult.getStatus()) {
            task.setStep(JobLifeCycle.ONLINE.getValue());
            task.setJobInstanceId(jobResult.getJobInstanceId());
            if (updateById(task)) {
                return Result.succeed(jobResult, "重新上线成功");
            } else {
                return Result.failed("由于未知原因，重新上线失败");
            }
        } else {
            return Result.failed("重新上线失败，原因：" + jobResult.getError());
        }
    }

    @Override
    public Result offLineTask(Integer id, String type) {
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
    public Result cancelTask(Integer id) {
        Task task = getTaskInfoById(id);
        Assert.check(task);
        if (JobLifeCycle.ONLINE != JobLifeCycle.get(task.getStep())) {
            if (Asserts.isNotNull(task.getJobInstanceId()) && task.getJobInstanceId() != 0) {
                return Result.failed("当前有作业正在运行，注销失败，请停止后注销");
            }
            task.setStep(JobLifeCycle.CANCEL.getValue());
            if (updateById(task)) {
                return Result.succeed("注销成功");
            } else {
                return Result.failed("由于未知原因，注销失败");
            }
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
        Cluster cluster = clusterService.getById(jobInstance.getClusterId());
        Asserts.checkNotNull(cluster, "该集群不存在");
        String jobId = jobInstance.getJid();
        boolean useGateway = false;
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress(cluster.getJobManagerHost());
        jobConfig.setType(cluster.getType());
        if (Asserts.isNotNull(cluster.getClusterConfigurationId())) {
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(cluster.getClusterConfigurationId());
            jobConfig.buildGatewayConfig(gatewayConfig);
            jobConfig.getGatewayConfig().getClusterConfig().setAppId(cluster.getName());
            useGateway = true;
        }
        jobConfig.setTaskId(jobInstance.getTaskId());
        JobManager jobManager = JobManager.build(jobConfig);
        jobManager.setUseGateway(useGateway);
        if ("canceljob".equals(savePointType)) {
            return jobManager.cancel(jobId);
        }
        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType, null);
        if (Asserts.isNotNull(savePointResult.getJobInfos())) {
            for (JobInfo item : savePointResult.getJobInfos()) {
                if (Asserts.isEqualsIgnoreCase(jobId, item.getJobId()) && Asserts.isNotNull(jobConfig.getTaskId())) {
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
        boolean isJarTask = Dialect.FLINKJAR.equalsVal(task.getDialect());
        if (!isJarTask && Asserts.isNotNull(task.getFragment()) ? task.getFragment() : false) {
            String flinkWithSql = dataBaseService.getEnabledFlinkWithSql();
            if (Asserts.isNotNullString(flinkWithSql)) {
                task.setStatement(flinkWithSql + "\r\n" + task.getStatement());
            }
        }
        if (!isJarTask && Asserts.isNotNull(task.getEnvId()) && task.getEnvId() != 0) {
            Task envTask = getTaskInfoById(task.getEnvId());
            if (Asserts.isNotNull(envTask) && Asserts.isNotNullString(envTask.getStatement())) {
                task.setStatement(envTask.getStatement() + "\r\n" + task.getStatement());
            }
        }
        JobConfig config = task.buildSubmitConfig();
        config.setJarTask(isJarTask);
        if (!JobManager.useGateway(config.getType())) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), task.getClusterId()));
        } else {
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(task.getClusterConfigurationId());
            if (GatewayType.YARN_APPLICATION.equalsValue(config.getType()) || GatewayType.KUBERNETES_APPLICATION.equalsValue(config.getType())) {
                if (!isJarTask) {
                    SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
                    gatewayConfig.put("userJarPath", systemConfiguration.getSqlSubmitJarPath());
                    gatewayConfig.put("userJarParas", systemConfiguration.getSqlSubmitJarParas() + buildParas(config.getTaskId()));
                    gatewayConfig.put("userJarMainAppClass", systemConfiguration.getSqlSubmitJarMainAppClass());
                } else {
                    Jar jar = jarService.getById(task.getJarId());
                    Assert.check(jar);
                    gatewayConfig.put("userJarPath", jar.getPath());
                    gatewayConfig.put("userJarParas", jar.getParas());
                    gatewayConfig.put("userJarMainAppClass", jar.getMainClass());
                }
            }
            config.buildGatewayConfig(gatewayConfig);
            config.addGatewayConfig(task.parseConfig());
        }
        switch (config.getSavePointStrategy()) {
            case LATEST:
                Savepoints latestSavepoints = savepointsService.getLatestSavepointByTaskId(task.getId());
                if (Asserts.isNotNull(latestSavepoints)) {
                    config.setSavePointPath(latestSavepoints.getPath());
                    config.getConfig().put("execution.savepoint.path", latestSavepoints.getPath());
                }
                break;
            case EARLIEST:
                Savepoints earliestSavepoints = savepointsService.getEarliestSavepointByTaskId(task.getId());
                if (Asserts.isNotNull(earliestSavepoints)) {
                    config.setSavePointPath(earliestSavepoints.getPath());
                    config.getConfig().put("execution.savepoint.path", earliestSavepoints.getPath());
                }
                break;
            case CUSTOM:
                config.getConfig().put("execution.savepoint.path", config.getSavePointPath());
                break;
            default:
                config.setSavePointPath(null);
        }
        return config;
    }

    @Override
    public JobInstance refreshJobInstance(Integer id, boolean isCoercive) {
        JobInfoDetail jobInfoDetail;
        FlinkJobTaskPool pool = FlinkJobTaskPool.getInstance();
        String key = id.toString();
        if (pool.exist(key)) {
            jobInfoDetail = pool.get(key);
        } else {
            jobInfoDetail = new JobInfoDetail(id);
            JobInstance jobInstance = jobInstanceService.getById(id);
            Asserts.checkNull(jobInstance, "该任务实例不存在");
            jobInfoDetail.setInstance(jobInstance);
            Cluster cluster = clusterService.getById(jobInstance.getClusterId());
            jobInfoDetail.setCluster(cluster);
            History history = historyService.getById(jobInstance.getHistoryId());
            history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
            if (Asserts.isNotNull(history) && Asserts.isNotNull(history.getClusterConfigurationId())) {
                jobInfoDetail.setClusterConfiguration(clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId()));
            }
            jobInfoDetail.setHistory(history);
            jobInfoDetail.setJobHistory(jobHistoryService.getJobHistory(id));
            pool.push(key, jobInfoDetail);
        }
        if (!isCoercive && !inRefreshPlan(jobInfoDetail.getInstance())) {
            return jobInfoDetail.getInstance();
        }
        JobHistory jobHistoryJson = jobHistoryService.refreshJobHistory(id, jobInfoDetail.getCluster().getJobManagerHost(), jobInfoDetail.getInstance().getJid(), jobInfoDetail.isNeedSave());
        JobHistory jobHistory = jobHistoryService.getJobHistoryInfo(jobHistoryJson);
        if (JobStatus.isDone(jobInfoDetail.getInstance().getStatus()) && Asserts.isNull(jobHistory.getJob())) {
            return jobInfoDetail.getInstance();
        }
        jobInfoDetail.setJobHistory(jobHistory);
        String status = jobInfoDetail.getInstance().getStatus();
        boolean jobStatusChanged = false;
        if (Asserts.isNull(jobInfoDetail.getJobHistory().getJob()) || Asserts.isNull(jobInfoDetail.getJobHistory().getJob())) {
            jobInfoDetail.getInstance().setStatus(JobStatus.UNKNOWN.getValue());
        } else {
            jobInfoDetail.getInstance().setDuration(jobInfoDetail.getJobHistory().getJob().get(FlinkRestResultConstant.JOB_DURATION).asLong() / 1000);
            jobInfoDetail.getInstance().setStatus(jobInfoDetail.getJobHistory().getJob().get(FlinkRestResultConstant.JOB_STATE).asText());
        }
        if (JobStatus.isDone(jobInfoDetail.getInstance().getStatus()) && !status.equals(jobInfoDetail.getInstance().getStatus())) {
            jobStatusChanged = true;
            jobInfoDetail.getInstance().setFinishTime(LocalDateTime.now());
            handleJobDone(jobInfoDetail.getInstance());
        }
        if (isCoercive) {
            DaemonFactory.addTask(DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInfoDetail.getInstance().getId()));
        }
        if (jobStatusChanged || jobInfoDetail.isNeedSave()) {
            jobInstanceService.updateById(jobInfoDetail.getInstance());
        }
        pool.refresh(jobInfoDetail);
        return jobInfoDetail.getInstance();
    }

    private boolean inRefreshPlan(JobInstance jobInstance) {
        if ((!JobStatus.isDone(jobInstance.getStatus())) || (Asserts.isNotNull(jobInstance.getFinishTime())
            && Duration.between(jobInstance.getFinishTime(), LocalDateTime.now()).toMinutes() < 1)) {
            return true;
        } else {
            return false;
        }
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
            e.printStackTrace();
        }
        return "127.0.0.1:" + serverPort;
    }

    @Override
    public Integer queryAllSizeByName(String name) {

        return baseMapper.queryAllSizeByName(name);
    }


    private String getDuration(long jobStartTimeMills, long jobEndTimeMills) {
        Instant startTime = Instant.ofEpochMilli(jobStartTimeMills);
        Instant endTime = Instant.ofEpochMilli(jobEndTimeMills);

        long days = ChronoUnit.DAYS.between(startTime, endTime);
        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
        String duration = days + "天 " + (hours - (days * 24)) + "小时 " + (minutes - (hours * 60)) + "分 " + (seconds - (minutes * 60)) + "秒";
        return duration;
    }


    private void handleJobDone(JobInstance jobInstance) {
        if (Asserts.isNull(jobInstance.getTaskId())) {
            return;
        }
        Task task = getTaskInfoById(jobInstance.getTaskId());
        Task updateTask = new Task();
        updateTask.setId(jobInstance.getTaskId());
        updateTask.setJobInstanceId(0);
        if (!JobLifeCycle.ONLINE.equalsValue(jobInstance.getStep())) {
            updateById(updateTask);
            return;
        }
        Integer jobInstanceId = jobInstance.getId();
        JobHistory jobHistory = jobHistoryService.getById(jobInstanceId); //获取任务历史信息
        String jobJson = jobHistory.getJobJson(); //获取任务历史信息的jobJson
        ObjectNode jsonNodes = JSONUtil.parseObject(jobJson);
        if (jsonNodes.has("errors")) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long asLongStartTime = jsonNodes.get("start-time").asLong(); //获取任务历史信息的start-time
        long asLongEndTime = jsonNodes.get("end-time").asLong(); //获取任务历史信息的end-time

        if (asLongEndTime < asLongStartTime) {
            asLongEndTime = System.currentTimeMillis();
        }
        String startTime = dateFormat.format(asLongStartTime);
        String endTime = dateFormat.format(asLongEndTime);
//        Long duration = jsonNodes.get("duration").asLong();
        String duration = getDuration(asLongStartTime, asLongEndTime); //获取任务的 duration 使用的是 start-time 和 end-time 计算 不采用 duration 字段

        String clusterJson = jobHistory.getClusterJson(); //获取任务历史信息的clusterJson 主要获取 jobManagerHost
        ObjectNode clusterJsonNodes = JSONUtil.parseObject(clusterJson);
        String jobManagerHost = clusterJsonNodes.get("jobManagerHost").asText();

        if (Asserts.isNotNull(task.getAlertGroupId())) {
            AlertGroup alertGroup = alertGroupService.getAlertGroupInfo(task.getAlertGroupId());
            if (Asserts.isNotNull(alertGroup)) {
                AlertMsg alertMsg = new AlertMsg();
                alertMsg.setAlertType("Flink 实时监控");
                alertMsg.setAlertTime(dateFormat.format(new Date()));
                alertMsg.setJobID(jobInstance.getJid());
                alertMsg.setJobName(task.getName());
                alertMsg.setJobType(task.getDialect());
                alertMsg.setJobStatus(jobInstance.getStatus());
                alertMsg.setJobStartTime(startTime);
                alertMsg.setJobEndTime(endTime);
                alertMsg.setJobDuration(duration);

                String linkUrl = "http://" + jobManagerHost + "/#/job/" + jobInstance.getJid() + "/overview";
                String exceptionUrl = "http://" + jobManagerHost + "/#/job/" + jobInstance.getJid() + "/exceptions";

                for (AlertInstance alertInstance : alertGroup.getInstances()) {
                    Map<String, String> map = JSONUtil.toMap(alertInstance.getParams());
                    if (map.get("msgtype").equals(ShowType.MARKDOWN.getValue())) {
                        alertMsg.setLinkUrl("[跳转至该任务的 FlinkWeb](" + linkUrl + ")");
                        alertMsg.setExceptionUrl("[点击查看该任务的异常日志](" + exceptionUrl + ")");
                    } else {
                        alertMsg.setLinkUrl(linkUrl);
                        alertMsg.setExceptionUrl(exceptionUrl);
                    }
                    sendAlert(alertInstance, jobInstance, task, alertMsg);
                }
            }
        }
        updateTask.setStep(JobLifeCycle.RELEASE.getValue());
        updateById(updateTask);
    }

    private void sendAlert(AlertInstance alertInstance, JobInstance jobInstance, Task task, AlertMsg alertMsg) {
        AlertConfig alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.build(alertConfig);
        String title = "任务【" + task.getAlias() + "】：" + jobInstance.getStatus();
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
}
