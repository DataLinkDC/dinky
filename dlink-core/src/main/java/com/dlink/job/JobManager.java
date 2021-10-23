package com.dlink.job;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.explainer.Explainer;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.parser.SqlType;
import com.dlink.result.*;
import com.dlink.session.ExecutorEntity;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.dlink.trans.Operations;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.JobID;
import org.apache.flink.table.api.TableResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * JobManager
 *
 * @author wenmo
 * @since 2021/5/25 15:27
 **/
public class JobManager extends RunTime {

    private JobHandler handler;
    private String sessionId;
    private Integer maxRowNum = 100;
    private EnvironmentSetting environmentSetting;
    private ExecutorSetting executorSetting;
    private JobConfig config;
    private Executor executor;

    public JobManager() {
    }

    public JobManager(String address, ExecutorSetting executorSetting) {
        if (address != null) {
            this.environmentSetting = EnvironmentSetting.build(address);
            this.executorSetting = executorSetting;
            this.executor = createExecutor();
        }
    }

    public JobManager(String address, String sessionId, Integer maxRowNum, ExecutorSetting executorSetting) {
        this.environmentSetting = EnvironmentSetting.build(address);
        this.sessionId = sessionId;
        this.maxRowNum = maxRowNum;
        this.executorSetting = executorSetting;
        this.executor = createExecutorWithSession();
    }

    public JobManager(JobConfig config) {
        this.config = config;
    }

    public static JobManager build() {
        JobManager manager = new JobManager();
        manager.init();
        return manager;
    }

    public static JobManager build(JobConfig config) {
        JobManager manager = new JobManager(config);
        manager.init();
        return manager;
    }

    private Executor createExecutor() {
        initEnvironmentSetting();
        if (config.isUseRemote()&&config.getClusterId()!=0) {
            executor = Executor.buildRemoteExecutor(environmentSetting, config.getExecutorSetting());
            return executor;
        } else {
            executor = Executor.buildLocalExecutor(config.getExecutorSetting());
            return executor;
        }
    }

    private Executor createExecutorWithSession() {
        if(config.isUseSession()) {
            ExecutorEntity executorEntity = SessionPool.get(config.getSession());
            if (Asserts.isNotNull(executorEntity)) {
                executor = executorEntity.getExecutor();
                config.setSessionConfig(executorEntity.getSessionConfig());
                initEnvironmentSetting();
                executor.update(executorSetting);
            } else {
                createExecutor();
                SessionPool.push(new ExecutorEntity(config.getSession(), executor));
            }
        }else {
            createExecutor();
        }
        return executor;
    }

    private void initEnvironmentSetting(){
        environmentSetting = EnvironmentSetting.build(config.getAddress());
    }

    private void initExecutorSetting(){
        executorSetting = config.getExecutorSetting();
    }

    @Override
    public boolean init() {
        handler = JobHandler.build();
        initExecutorSetting();
        createExecutorWithSession();
        return false;
    }

    @Override
    public boolean ready() {
        return handler.init();
    }

    @Override
    public boolean success() {
        return handler.success();
    }

    @Override
    public boolean failed() {
        return handler.failed();
    }

    @Override
    public boolean close() {
        JobContextHolder.clear();
        return false;
    }

    public SubmitResult submit(String statement) {
        if (statement == null || "".equals(statement)) {
            return SubmitResult.error("FlinkSql语句不存在");
        }
        String[] statements = statement.split(FlinkSQLConstant.SEPARATOR);
        return submit(Arrays.asList(statements));
    }

    public SubmitResult submit(List<String> sqlList) {
        SubmitResult result = new SubmitResult(sessionId, sqlList, environmentSetting.getHost(), executorSetting.getJobName());
        int currentIndex = 0;
        try {
            if (Asserts.isNullCollection(sqlList)) {
                result.setSuccess(false);
                result.setMsg(LocalDateTime.now().toString() + ":执行sql语句为空。");
                return result;
            }
            Executor executor = createExecutor();
            for (String sqlText : sqlList) {
                currentIndex++;
                SqlType operationType = Operations.getOperationType(sqlText);
                CustomTableEnvironmentImpl stEnvironment = executor.getCustomTableEnvironmentImpl();
                if (operationType.equals(SqlType.INSERT)) {
                    long start = System.currentTimeMillis();
                    if (!FlinkInterceptor.build(stEnvironment, sqlText)) {
                        TableResult tableResult = executor.executeSql(sqlText);
                        JobID jobID = tableResult.getJobClient().get().getJobID();
                        long finish = System.currentTimeMillis();
                        long timeElapsed = finish - start;
                        InsertResult insertResult = new InsertResult((jobID == null ? "" : jobID.toHexString()), true);
                        result.setResult(insertResult);
                        result.setJobId((jobID == null ? "" : jobID.toHexString()));
                        result.setTime(timeElapsed);
                    }
                    result.setSuccess(true);
                    result.setFinishDate(LocalDateTime.now());
                } else if(operationType.equals(SqlType.SET)){

                } else {
                    if (!FlinkInterceptor.build(stEnvironment, sqlText)) {
                        executor.executeSql(sqlText);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getStackTrace();
            StringBuilder resMsg = new StringBuilder();
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }
            result.setSuccess(false);
//            result.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage());
            result.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + "\n >>>堆栈信息<<<" + resMsg.toString());
//            result.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + "\n >>>异常原因<<< \n" + e.toString());
            return result;

        }
        result.setSuccess(true);
        result.setMsg(LocalDateTime.now().toString() + ":任务提交成功！");
        return result;
    }

    public JobResult executeSql(String statement) {
        Job job = new Job(config,environmentSetting.getAddress(),
                Job.JobStatus.INITIALIZE,statement,executorSetting, LocalDateTime.now(),executor);
        JobContextHolder.setJob(job);
        job.setType(Operations.getSqlTypeFromStatements(statement));
        ready();
        String[] statements = statement.split(";");
        int currentIndex = 0;
        try {
            for (String item : statements) {
                if (item.trim().isEmpty()) {
                    continue;
                }
                currentIndex++;
                SqlType operationType = Operations.getOperationType(item);
                if (!FlinkInterceptor.build(executor.getCustomTableEnvironmentImpl(), item)) {
                    TableResult tableResult = executor.executeSql(item);
                    if (tableResult.getJobClient().isPresent()) {
                        job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                    }
                    if(config.isUseResult()) {
                        IResult result = ResultBuilder.build(operationType, maxRowNum, "", true).getResult(tableResult);
                        job.setResult(result);
                    }
                }
                if(operationType==SqlType.INSERT||operationType==SqlType.SELECT){
                    break;
                }
            }
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.SUCCESS);
            success();
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer resMsg = new StringBuffer("");
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }
            LocalDateTime now = LocalDateTime.now();
            job.setEndTime(now);
            job.setStatus(Job.JobStatus.FAILED);
            String error = now.toString() + ":" + "运行第" + currentIndex + "个sql时出现异常:" + e.getMessage() + " \n >>>堆栈信息<<<" + resMsg.toString();
            job.setError(error);
            failed();
            close();
        }
        close();
        return job.getJobResult();
    }

    public IResult executeDDL(String statement) {
        String[] statements = statement.split(";");
        try {
            for (String item : statements) {
                if (item.trim().isEmpty()) {
                    continue;
                }
                SqlType operationType = Operations.getOperationType(item);
                if(SqlType.INSERT==operationType||SqlType.SELECT==operationType){
                    continue;
                }
                LocalDateTime startTime = LocalDateTime.now();
                TableResult tableResult = executor.executeSql(item);
                IResult result = ResultBuilder.build(operationType, maxRowNum, "", false).getResult(tableResult);
                result.setStartTime(startTime);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ErrorResult();
    }

    public static SelectResult getJobData(String jobId){
        return ResultPool.get(jobId);
    }

    public static SessionInfo createSession(String session, SessionConfig sessionConfig,String createUser){
        if(SessionPool.exist(session)){
            return SessionPool.getInfo(session);
        }
        Executor sessionExecutor = null;
        if (sessionConfig.isUseRemote()) {
            sessionExecutor = Executor.buildRemoteExecutor(EnvironmentSetting.build(sessionConfig.getAddress()), ExecutorSetting.DEFAULT);
        } else {
            sessionExecutor = Executor.buildLocalExecutor(sessionConfig.getExecutorSetting());
        }
        ExecutorEntity executorEntity = new ExecutorEntity(session, sessionConfig, createUser, LocalDateTime.now(), sessionExecutor);
        SessionPool.push(executorEntity);
        return SessionInfo.build(executorEntity);
    }

    public static List<SessionInfo> listSession(String createUser){
        return SessionPool.filter(createUser);
    }

    public List<SqlExplainResult> explainSql(String statement){
        Explainer explainer = Explainer.build(executor);
        return explainer.explainSqlResult(statement);
    }

    public ObjectNode getStreamGraph(String statement){
        Explainer explainer = Explainer.build(executor);
        return explainer.getStreamGraph(statement);
    }
}
