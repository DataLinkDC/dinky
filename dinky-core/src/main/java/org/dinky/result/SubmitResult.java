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

package com.dlink.result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SubmitResult
 *
 * @author wenmo
 * @since 2021/5/25 19:04
 **/
public class SubmitResult {
    private String sessionId;
    private List<String> statements;
    private String flinkHost;
    private String jobId;
    private String jobName;
    private boolean success;
    private long time;
    private LocalDateTime finishDate;
    private String msg;
    private String error;
    private IResult result;

    public SubmitResult() {
    }

    public static SubmitResult error(String error) {
        return new SubmitResult(false, error);
    }

    public SubmitResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public SubmitResult(String sessionId, List<String> statements, String flinkHost, String jobName) {
        this.sessionId = sessionId;
        this.statements = statements;
        this.flinkHost = flinkHost;
        this.jobName = jobName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public String getFlinkHost() {
        return flinkHost;
    }

    public void setFlinkHost(String flinkHost) {
        this.flinkHost = flinkHost;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
