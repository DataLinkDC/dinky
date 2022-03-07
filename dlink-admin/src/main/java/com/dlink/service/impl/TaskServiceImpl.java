package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.alert.Alert;
import com.dlink.alert.AlertConfig;
import com.dlink.alert.AlertResult;
import com.dlink.assertion.Assert;
import com.dlink.assertion.Asserts;
import com.dlink.assertion.Tips;
import com.dlink.config.Dialect;
import com.dlink.constant.FlinkRestResultConstant;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.dto.SqlDTO;
import com.dlink.exception.BusException;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.mapper.JobInstanceMapper;
import com.dlink.mapper.TaskMapper;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.*;
import com.dlink.service.*;
import com.dlink.utils.CustomStringJavaCompiler;
import com.dlink.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public JobResult submitByTaskId(Integer id) {
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
    public JobResult restartByTaskId(Integer id) {
        Task task = this.getTaskInfoById(id);
        Asserts.checkNull(task, Tips.TASK_NOT_EXIST);
        if(Asserts.isNotNull(task.getJobInstanceId())&&task.getJobInstanceId()!=0){
            savepointTask(task, SavePointType.CANCEL.getValue());
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
    public boolean releaseTask(Integer id) {
        Task task = getById(id);
        Assert.check(task);
        if (JobLifeCycle.DEVELOP.equalsValue(task.getStep())) {
            task.setStep(JobLifeCycle.RELEASE.getValue());
            return updateById(task);
        }
        return false;
    }

    @Override
    public boolean developTask(Integer id) {
        Task task = getById(id);
        Assert.check(task);
        if (JobLifeCycle.RELEASE.equalsValue(task.getStep())) {
            task.setStep(JobLifeCycle.DEVELOP.getValue());
            return updateById(task);
        }
        return false;
    }

    @Override
    public boolean onLineTask(Integer id) {
        Task task = getById(id);
        Assert.check(task);
        if (JobLifeCycle.RELEASE.equalsValue(task.getStep())) {
            task.setStep(JobLifeCycle.ONLINE.getValue());
            return updateById(task);
        }
        return false;
    }

    @Override
    public boolean offLineTask(Integer id) {
        Task task = getById(id);
        Assert.check(task);
        if (JobLifeCycle.ONLINE.equalsValue(task.getStep())) {
            task.setStep(JobLifeCycle.RELEASE.getValue());
            return updateById(task);
        }
        return false;
    }

    @Override
    public boolean cancelTask(Integer id) {
        Task task = getById(id);
        Assert.check(task);
        if (JobLifeCycle.ONLINE != JobLifeCycle.get(task.getStep())) {
            task.setStep(JobLifeCycle.CANCEL.getValue());
            return updateById(task);
        }
        return false;
    }

    @Override
    public boolean recoveryTask(Integer id) {
        Task task = getById(id);
        Assert.check(task);
        if (JobLifeCycle.CANCEL == JobLifeCycle.get(task.getStep())) {
            task.setStep(JobLifeCycle.DEVELOP.getValue());
            return updateById(task);
        }
        return false;
    }

    private boolean savepointTask(Task task, String savePointType) {
        Asserts.checkNotNull(task, "该任务不存在");
        Cluster cluster = clusterService.getById(task.getClusterId());
        Asserts.checkNotNull(cluster, "该集群不存在");
        Asserts.checkNotNull(task.getJobInstanceId(), "无任务需要SavePoint");
        JobInstance jobInstance = jobInstanceService.getById(task.getJobInstanceId());
        Asserts.checkNotNull(jobInstance, "任务实例不存在");
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
        jobConfig.setTaskId(task.getId());
        JobManager jobManager = JobManager.build(jobConfig);
        jobManager.setUseGateway(useGateway);
        SavePointResult savePointResult = jobManager.savepoint(jobId, savePointType, null);
        if (Asserts.isNotNull(savePointResult)) {
            for (JobInfo item : savePointResult.getJobInfos()) {
                if (Asserts.isEqualsIgnoreCase(jobId, item.getJobId())&&Asserts.isNotNull(jobConfig.getTaskId())) {
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
        return savepointTask(getById(taskId),savePointType);
    }

    private JobConfig buildJobConfig(Task task) {
        boolean isJarTask = Dialect.FLINKJAR.equalsVal(task.getDialect());
        if (!isJarTask && task.isFragment()) {
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
                }
                break;
            case EARLIEST:
                Savepoints earliestSavepoints = savepointsService.getEarliestSavepointByTaskId(task.getId());
                if (Asserts.isNotNull(earliestSavepoints)) {
                    config.setSavePointPath(earliestSavepoints.getPath());
                }
                break;
            case CUSTOM:
                break;
            default:
                config.setSavePointPath(null);
        }
        return config;
    }

    @Override
    public JobInstance refreshJobInstance(Integer id) {
        JobInstance jobInstance = jobInstanceService.getById(id);
        Asserts.checkNull(jobInstance, "该任务实例不存在");
        if(JobStatus.isDone(jobInstance.getStatus())){
            return jobInstance;
        }
        Cluster cluster = clusterService.getById(jobInstance.getClusterId());
        JobHistory jobHistoryJson = jobHistoryService.refreshJobHistory(id, cluster.getJobManagerHost(), jobInstance.getJid());
        JobHistory jobHistory = jobHistoryService.getJobHistoryInfo(jobHistoryJson);
        if(Asserts.isNull(jobHistory.getJob())||jobHistory.getJob().has(FlinkRestResultConstant.ERRORS)){
            jobInstance.setStatus(JobStatus.UNKNOWN.getValue());
        }else{
            jobInstance.setDuration(jobHistory.getJob().get(FlinkRestResultConstant.JOB_DURATION).asLong()/1000);
            jobInstance.setStatus(jobHistory.getJob().get(FlinkRestResultConstant.JOB_STATE).asText());
        }
        jobInstanceService.updateById(jobInstance);
        if(JobStatus.isDone(jobInstance.getStatus())){
            handleJobDone(jobInstance);
        }
        return jobInstance;
    }

    @Override
    public JobInfoDetail refreshJobInfoDetail(Integer id) {
        return jobInstanceService.getJobInfoDetailInfo(refreshJobInstance(id));
    }

    private void handleJobDone(JobInstance jobInstance){
        if(Asserts.isNull(jobInstance.getTaskId())){
            return;
        }
        Task task = new Task();
        task.setId(jobInstance.getTaskId());
        task.setJobInstanceId(0);
        updateById(task);
        task = getTaskInfoById(jobInstance.getTaskId());
        if(Asserts.isNotNull(task.getAlertGroupId())){
            AlertGroup alertGroup = alertGroupService.getAlertGroupInfo(task.getAlertGroupId());
            if(Asserts.isNotNull(alertGroup)){
                for(AlertInstance alertInstance: alertGroup.getInstances()){
                    sendAlert(alertInstance,jobInstance,task);
                }
            }
        }
    }

    private void sendAlert(AlertInstance alertInstance,JobInstance jobInstance,Task task){
        AlertConfig alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.build(alertConfig);
        String title = "任务【"+task.getAlias()+"】："+jobInstance.getStatus();
        String content = "jid【"+jobInstance.getJid()+"】于【"+jobInstance.getUpdateTime().toString()+"】时【"+jobInstance.getStatus()+"】！";
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
