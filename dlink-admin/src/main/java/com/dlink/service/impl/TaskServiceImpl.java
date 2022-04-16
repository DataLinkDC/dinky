package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.alert.Alert;
import com.dlink.alert.AlertConfig;
import com.dlink.alert.AlertMsg;
import com.dlink.alert.AlertResult;
import com.dlink.assertion.Assert;
import com.dlink.assertion.Asserts;
import com.dlink.assertion.Tips;
import com.dlink.common.result.Result;
import com.dlink.config.Dialect;
import com.dlink.constant.FlinkRestResultConstant;
import com.dlink.daemon.task.DaemonFactory;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.dto.SqlDTO;
import com.dlink.exception.BusException;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.job.*;
import com.dlink.mapper.TaskMapper;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.*;
import com.dlink.result.SqlExplainResult;
import com.dlink.service.*;
import com.dlink.utils.CustomStringJavaCompiler;
import com.dlink.utils.JSONUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

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
            if (Asserts.isNull(task.getJobInstanceId()) || task.getJobInstanceId() == 0) {
                JobInstance jobInstance = jobInstanceService.getJobInstanceByTaskId(id);
                if (Asserts.isNotNull(jobInstance) && !JobStatus.isDone(jobInstance.getStatus())) {
                    task.setJobInstanceId(jobInstance.getId());
                }
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
            if (updateById(task)) {
                return Result.succeed("发布成功");
            } else {
                return Result.failed("由于未知原因，发布失败");
            }
        }
        return Result.succeed("发布成功");
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
        }
        return Result.failed("上线失败，作业不存在。");
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
        if (savepointTask(id, type)) {
            if (!JobLifeCycle.ONLINE.equalsValue(task.getStep())) {
                return Result.succeed("停止成功");
            }
            task.setStep(JobLifeCycle.RELEASE.getValue());
            if (updateById(task)) {
                return Result.succeed("下线成功");
            } else {
                return Result.failed("由于未知原因，下线失败");
            }
        } else {
            return Result.failed("SavePoint失败，下线失败");
        }
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
        if (!isJarTask && task.getFragment()) {
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
        JobInstance jobInstance = jobInstanceService.getById(id);
        Asserts.checkNull(jobInstance, "该任务实例不存在");
        if (!isCoercive && !inRefreshPlan(jobInstance)) {
            return jobInstance;
        }
        String status = jobInstance.getStatus();
        Cluster cluster = clusterService.getById(jobInstance.getClusterId());
        JobHistory jobHistoryJson = jobHistoryService.refreshJobHistory(id, cluster.getJobManagerHost(), jobInstance.getJid());
        JobHistory jobHistory = jobHistoryService.getJobHistoryInfo(jobHistoryJson);
        if (Asserts.isNull(jobHistory.getJob()) || jobHistory.getJob().has(FlinkRestResultConstant.ERRORS)) {
            jobInstance.setStatus(JobStatus.UNKNOWN.getValue());
        } else {
            jobInstance.setDuration(jobHistory.getJob().get(FlinkRestResultConstant.JOB_DURATION).asLong() / 1000);
            jobInstance.setStatus(jobHistory.getJob().get(FlinkRestResultConstant.JOB_STATE).asText());
        }
        if (JobStatus.isDone(jobInstance.getStatus()) && !status.equals(jobInstance.getStatus())) {
            jobInstance.setFinishTime(LocalDateTime.now());
            handleJobDone(jobInstance);
        }
        if (isCoercive) {
            DaemonFactory.addTask(DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInstance.getId()));
        }
        jobInstanceService.updateById(jobInstance);
        return jobInstance;
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
        return jobInstanceService.getJobInfoDetailInfo(refreshJobInstance(id, true));
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
        if (Asserts.isNotNull(task.getAlertGroupId())) {
            AlertGroup alertGroup = alertGroupService.getAlertGroupInfo(task.getAlertGroupId());
            if (Asserts.isNotNull(alertGroup)) {
                List<AlertMsg> alertMsgList = new ArrayList<>();
                AlertMsg alertMsg = new AlertMsg();
                alertMsg.setType("Flink 实时监控");
                alertMsg.setTime(LocalDateTime.now().toString());
                alertMsg.setId(jobInstance.getId().toString());
                alertMsg.setName(task.getAlias());
                alertMsg.setStatus(jobInstance.getStatus());
                alertMsg.setContent(jobInstance.getJid());
                alertMsgList.add(alertMsg);
                for (AlertInstance alertInstance : alertGroup.getInstances()) {
                    sendAlert(alertInstance, jobInstance, task, alertMsgList);
                }
            }
        }
        updateTask.setStep(JobLifeCycle.RELEASE.getValue());
        updateById(updateTask);
    }

    private void sendAlert(AlertInstance alertInstance, JobInstance jobInstance, Task task, List<AlertMsg> alertMsgList) {
        AlertConfig alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.build(alertConfig);
        String title = "任务【" + task.getAlias() + "】：" + jobInstance.getStatus();
        String content = JSONUtil.toJsonString(alertMsgList);
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
