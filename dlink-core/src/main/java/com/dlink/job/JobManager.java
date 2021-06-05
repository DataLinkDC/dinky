package com.dlink.job;

import com.dlink.constant.FlinkConstant;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.result.*;
import com.dlink.session.ExecutorEntity;
import com.dlink.session.SessionPool;
import com.dlink.trans.Operations;
import org.apache.flink.api.common.JobID;
import org.apache.flink.table.api.TableResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JobManager
 *
 * @author wenmo
 * @since 2021/5/25 15:27
 **/
public class JobManager {

    private String flinkHost;
    private Integer port;
    private String sessionId;
    private Integer maxRowNum = 100;

    public JobManager(String host) {
        if(host!=null) {
            String[] strs = host.split(":");
            if(strs.length>=2) {
                this.flinkHost = strs[0];
                this.port = Integer.parseInt(strs[1]);
            }else{
                this.flinkHost = strs[0];
                this.port = 8081;
            }
        }
    }

    public JobManager(String host,String sessionId, Integer maxRowNum) {
        if(host!=null) {
            String[] strs = host.split(":");
            if(strs.length>=2) {
                this.flinkHost = strs[0];
                this.port = Integer.parseInt(strs[1]);
            }else{
                this.flinkHost = strs[0];
                this.port = 8081;
            }
        }
        this.sessionId = sessionId;
        this.maxRowNum = maxRowNum;
    }

    public JobManager(String flinkHost, Integer port) {
        this.flinkHost = flinkHost;
        this.port = port;
    }

    public JobManager(String flinkHost, Integer port, String sessionId, Integer maxRowNum) {
        this.flinkHost = flinkHost;
        this.sessionId = sessionId;
        this.maxRowNum = maxRowNum;
        this.port = port;
    }

    public RunResult execute(String statement,ExecutorSetting executorSetting) {
        RunResult runResult = new RunResult(sessionId, statement, flinkHost,port,executorSetting);
        Executor executor = null;
        ExecutorEntity executorEntity = SessionPool.get(sessionId);
        if (executorEntity != null) {
            executor = executorEntity.getExecutor();
        } else {
            if(executorSetting.isRemote()) {
                executor = Executor.build(new EnvironmentSetting(flinkHost, FlinkConstant.PORT), executorSetting);
            }else{
                executor = Executor.build(null, executorSetting);
            }
            SessionPool.push(new ExecutorEntity(sessionId, executor));
        }
        String[] Statements = statement.split(";");
        int currentIndex = 0;
        //当前只支持对 show select的操作的结果的数据查询  后期需要可添加
        try {
            for (String item : Statements) {
                currentIndex++;
                if (item.trim().isEmpty()) {
                    continue;
                }
                String operationType = Operations.getOperationType(item);
                long start = System.currentTimeMillis();
                TableResult tableResult = executor.executeSql(item);
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                IResult result = ResultBuilder.build(operationType, maxRowNum, "", false).getResult(tableResult);
                runResult.setResult(result);
                runResult.setTime(timeElapsed);
                runResult.setFinishDate(LocalDateTime.now());
                if(tableResult.getJobClient().isPresent()) {
                    runResult.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                }
                runResult.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getCause().getStackTrace();
            /*StringBuffer resMsg = new StringBuffer("");
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }*/
            runResult.setFinishDate(LocalDateTime.now());
            runResult.setSuccess(false);
//            runResult.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage());
//            runResult.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + " \n >>>堆栈信息<<<" + resMsg.toString());
            runResult.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + "\n >>>异常原因<<< \n" + e.getCause().toString());
            return runResult;
        }
        return runResult;
    }

    public SubmitResult submit(String statement, ExecutorSetting executerSetting) {
        if(statement==null||"".equals(statement)){
            return SubmitResult.error("FlinkSql语句不存在");
        }
        String [] statements = statement.split(FlinkSQLConstant.SEPARATOR);
        return submit(Arrays.asList(statements),executerSetting);
    }

    public SubmitResult submit(List<String> sqlList, ExecutorSetting executerSetting) {
        SubmitResult result = new SubmitResult(sessionId,sqlList,flinkHost);
        Map<String, String> map = new HashMap<>();
        int currentIndex = 0;
        try {
            if (sqlList != null && sqlList.size() > 0) {
                Executor executor = Executor.build(new EnvironmentSetting(flinkHost, port), executerSetting);
                for (String sqlText : sqlList) {
                    currentIndex++;
                    String operationType = Operations.getOperationType(sqlText);
                    if (operationType.equalsIgnoreCase(FlinkSQLConstant.INSERT)) {
                        long start = System.currentTimeMillis();
                        TableResult tableResult = executor.executeSql(sqlText);
                        long finish = System.currentTimeMillis();
                        long timeElapsed = finish - start;
                        JobID jobID = tableResult.getJobClient().get().getJobID();
                        result.setSuccess(true);
                        result.setTime(timeElapsed);
                        result.setFinishDate(LocalDateTime.now());
                        InsertResult insertResult = new InsertResult(sqlText,(jobID == null ? "" : jobID.toHexString()),true,timeElapsed,LocalDateTime.now());
                        result.setResult(insertResult);
                    } else {
                        executor.executeSql(sqlText);
                    }
                }
            } else {
                result.setSuccess(false);
                result.setMsg(LocalDateTime.now().toString()+":执行sql语句为空。");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getStackTrace();
            /*StringBuilder resMsg = new StringBuilder();
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }*/
            result.setSuccess(false);
//            result.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage());
//            result.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + "\n >>>堆栈信息<<<" + resMsg.toString());
            result.setError(LocalDateTime.now().toString() + ":" + "运行第" + currentIndex + "行sql时出现异常:" + e.getMessage() + "\n >>>异常原因<<< \n" + e.getCause().toString());
            return result;

        }
        result.setSuccess(true);
        result.setMsg(LocalDateTime.now().toString() + ":任务提交成功！");
        return result;
    }
}
