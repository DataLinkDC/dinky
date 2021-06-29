package com.dlink.job;

import com.dlink.constant.FlinkConstant;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.constant.NetConstant;
import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.result.*;
import com.dlink.session.ExecutorEntity;
import com.dlink.session.SessionPool;
import com.dlink.trans.Operations;
import org.apache.flink.api.common.JobID;
import org.apache.flink.table.api.TableResult;
import org.junit.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * JobManager
 *
 * @author wenmo
 * @since 2021/5/25 15:27
 **/
public class JobManager extends RunTime {

    private JobHandler handler;
    private String flinkHost;
    private String jobManagerHost;
    private Integer jobManagerPort;
    private Integer port;
    private String sessionId;
    private Integer maxRowNum = 100;
    private ExecutorSetting executorSetting;
    private JobConfig config;
    private Executor executor;

    public JobManager() {
    }

    public JobManager(String host, ExecutorSetting executorSetting) {
        if (host != null) {
            String[] strs = host.split(NetConstant.COLON);
            if (strs.length >= 2) {
                this.flinkHost = strs[0];
                this.port = Integer.parseInt(strs[1]);
            } else {
                this.flinkHost = strs[0];
                this.port = FlinkConstant.PORT;
            }
            this.executorSetting = executorSetting;
            this.executor = createExecutor();
        }
    }

    public JobManager(String host, String sessionId, Integer maxRowNum, ExecutorSetting executorSetting) {
        if (host != null) {
            String[] strs = host.split(NetConstant.COLON);
            if (strs.length >= 2) {
                this.flinkHost = strs[0];
                this.port = Integer.parseInt(strs[1]);
            } else {
                this.flinkHost = strs[0];
                this.port = FlinkConstant.PORT;
            }
        }
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
        if (config.isRemote()) {
            executor = Executor.build(new EnvironmentSetting(jobManagerHost, jobManagerPort), config.getExecutorSetting());
            return executor;
        } else {
            executor = Executor.build(null, config.getExecutorSetting());
            return executor;
        }
    }

    /*private boolean checkSession() {
        if (config != null) {
            String session = config.getSession();
            if (session != null && !"".equals(session)) {
                String[] keys = session.split("_");
                if (keys.length > 1 && !"".equals(keys[1])) {
                    isSession = true;
                    return true;
                }
            }
        }
        isSession = false;
        return false;
    }*/

    private Executor createExecutorWithSession() {
        if(config.isSession()) {
            ExecutorEntity executorEntity = SessionPool.get(config.getSessionKey());
            if (executorEntity != null) {
                executor = executorEntity.getExecutor();
            } else {
                createExecutor();
                SessionPool.push(new ExecutorEntity(config.getSessionKey(), executor));
            }
        }else {
            createExecutor();
        }
        return executor;
    }

    @Override
    public boolean init() {
        handler = JobHandler.build();
        String host = config.getHost();
        if (config.isRemote() && host != null && !("").equals(host)) {
            String[] strs = host.split(NetConstant.COLON);
            if (strs.length >= 2) {
                jobManagerHost = strs[0];
                jobManagerPort = Integer.parseInt(strs[1]);
            } else {
                jobManagerHost = strs[0];
                jobManagerPort = FlinkConstant.PORT;
            }
        }
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

    public RunResult execute(String statement) {
        RunResult runResult = new RunResult(sessionId, statement, flinkHost, port, executorSetting, executorSetting.getJobName());
        Executor executor = createExecutorWithSession();
        String[] Statements = statement.split(";");
        int currentIndex = 0;
        try {
            for (String item : Statements) {
                currentIndex++;
                if (item.trim().isEmpty()) {
                    continue;
                }
                String operationType = Operations.getOperationType(item);
                long start = System.currentTimeMillis();
                CustomTableEnvironmentImpl stEnvironment = executor.getCustomTableEnvironmentImpl();
                if (!FlinkInterceptor.build(stEnvironment, item)) {
                    TableResult tableResult = executor.executeSql(item);
                    if (tableResult.getJobClient().isPresent()) {
                        runResult.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                    }
                    IResult result = ResultBuilder.build(operationType, maxRowNum, "", false).getResult(tableResult);
                    runResult.setResult(result);
                }
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                runResult.setTime(timeElapsed);
                runResult.setFinishDate(LocalDateTime.now());
                runResult.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer resMsg = new StringBuffer("");
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }
            runResult.setFinishDate(LocalDateTime.now());
            runResult.setSuccess(false);
//            runResult.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage());
            runResult.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + " \n >>>堆栈信息<<<" + resMsg.toString());
//            runResult.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + "\n >>>异常原因<<< \n" + e.getCause().toString());
            return runResult;
        }
        return runResult;
    }

    public SubmitResult submit(String statement) {
        if (statement == null || "".equals(statement)) {
            return SubmitResult.error("FlinkSql语句不存在");
        }
        String[] statements = statement.split(FlinkSQLConstant.SEPARATOR);
        return submit(Arrays.asList(statements));
    }

    public SubmitResult submit(List<String> sqlList) {
        SubmitResult result = new SubmitResult(sessionId, sqlList, flinkHost, executorSetting.getJobName());
        int currentIndex = 0;
        try {
            if (sqlList != null && sqlList.size() > 0) {
                Executor executor = createExecutor();
                for (String sqlText : sqlList) {
                    currentIndex++;
                    String operationType = Operations.getOperationType(sqlText);
                    CustomTableEnvironmentImpl stEnvironment = executor.getCustomTableEnvironmentImpl();
                    if (operationType.equalsIgnoreCase(FlinkSQLConstant.INSERT)) {
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
                    } else {
                        if (!FlinkInterceptor.build(stEnvironment, sqlText)) {
                            executor.executeSql(sqlText);
                        }
                    }
                }
            } else {
                result.setSuccess(false);
                result.setMsg(LocalDateTime.now().toString() + ":执行sql语句为空。");
                return result;
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
        Job job = new Job(config,jobManagerHost+NetConstant.COLON+jobManagerPort,
                Job.JobStatus.INITIALIZE,statement,executorSetting, LocalDate.now(),executor);
        JobContextHolder.setJob(job);
        ready();
        String[] statements = statement.split(";");
        int currentIndex = 0;
        try {
            for (String item : statements) {
                if (item.trim().isEmpty()) {
                    continue;
                }
                currentIndex++;
                String operationType = Operations.getOperationType(item);
                if (!FlinkInterceptor.build(executor.getCustomTableEnvironmentImpl(), item)) {
                    TableResult tableResult = executor.executeSql(item);
                    if (tableResult.getJobClient().isPresent()) {
                        job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                    }
                    if(config.isResult()) {
                        IResult result = ResultBuilder.build(operationType, maxRowNum, "", false).getResult(tableResult);
                        job.setResult(result);
                    }
                }
                if(FlinkSQLConstant.INSERT.equals(operationType)||FlinkSQLConstant.SELECT.equals(operationType)){
                    break;
                }
            }
            job.setEndTime(LocalDate.now());
            job.setStatus(Job.JobStatus.SUCCESS);
            success();
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer resMsg = new StringBuffer("");
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }
            LocalDate now = LocalDate.now();
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
                String operationType = Operations.getOperationType(item);
                if(FlinkSQLConstant.INSERT.equals(operationType)||FlinkSQLConstant.SELECT.equals(operationType)){
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
}
