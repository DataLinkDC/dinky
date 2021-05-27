package com.dlink.result;

import java.time.LocalDate;

/**
 * RunResult
 *
 * @author wenmo
 * @since 2021/5/25 16:46
 **/
public class RunResult {
    private String sessionId;
    private String statement;
    private String flinkHost;
    private boolean success;
    private long time;
    private LocalDate finishDate;
    private String msg;
    private String error;
    private IResult result;

    public RunResult() {
    }

    public RunResult(String sessionId, String statement, String flinkHost) {
        this.sessionId = sessionId;
        this.statement = statement;
        this.flinkHost = flinkHost;
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

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
