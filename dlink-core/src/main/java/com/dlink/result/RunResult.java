package com.dlink.result;

import com.dlink.executor.ExecutorSetting;

import java.time.LocalDateTime;

/**
 * RunResult
 *
 * @author wenmo
 * @since 2021/5/25 16:46
 **/
public class RunResult {
    private String sessionId;
    private String jobId;
    private String jobName;
    private String statement;
    private String flinkHost;
    private Integer flinkPort;
    private boolean success;
    private long time;
    private LocalDateTime finishDate;
    private String msg;
    private String error;
    private IResult result;
    private ExecutorSetting setting;

    public RunResult() {
    }

    public RunResult(String sessionId, String statement, String flinkHost, Integer flinkPort,ExecutorSetting setting,String jobName) {
        this.sessionId = sessionId;
        this.statement = statement;
        this.flinkHost = flinkHost;
        this.flinkPort = flinkPort;
        this.setting = setting;
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ExecutorSetting getSetting() {
        return setting;
    }

    public void setSetting(ExecutorSetting setting) {
        this.setting = setting;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public IResult getResult() {
        return result;
    }

    public void setResult(IResult result) {
        this.result = result;
    }

    public String getFlinkHost() {
        return flinkHost;
    }

    public void setFlinkHost(String flinkHost) {
        this.flinkHost = flinkHost;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getFlinkPort() {
        return flinkPort;
    }

    public void setFlinkPort(Integer flinkPort) {
        this.flinkPort = flinkPort;
    }
}
