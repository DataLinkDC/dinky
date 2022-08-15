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

package com.dlink.alert;

import lombok.Data;

/**
 * AlertMsg
 *
 * @author wenmo
 * @since 2022/3/7 18:30
 **/
@Data
public class AlertMsg {

    private String alertType;
    private String alertTime;
    private String jobID;
    private String jobName;
    private String jobType;
    private String jobStatus;
    private String jobStartTime;
    private String jobEndTime;
    private String jobDuration;

    /**
     * Flink WebUI link url
     */
    private String linkUrl;

    /**
     * Flink job Root Exception url
     */
    private String exceptionUrl;

    public AlertMsg() {
    }

    public AlertMsg(String alertType,
                    String alertTime,
                    String jobID,
                    String jobName,
                    String jobType,
                    String jobStatus,
                    String jobStartTime,
                    String jobEndTime,
                    String jobDuration,
                    String linkUrl,
                    String exceptionUrl) {

        this.alertType = alertType;
        this.alertTime = alertTime;
        this.jobID = jobID;
        this.jobName = jobName;
        this.jobType = jobType;
        this.jobStatus = jobStatus;
        this.jobStartTime = jobStartTime;
        this.jobEndTime = jobEndTime;
        this.jobDuration = jobDuration;
        this.linkUrl = linkUrl;
        this.exceptionUrl = exceptionUrl;
    }

    public String toString() {
        return "[{ \"Alert Type\":\"" + alertType + "\","
                +
                "\"Alert Time\":\"" + alertTime + "\","
                +
                "\"Job ID\":\"" + jobID + "\","
                +
                "\"Job Name\":\"" + jobName + "\","
                +
                "\"Job Type\":\"" + jobType + "\","
                +
                "\"Job Status\":\"" + jobStatus + "\","
                +
                "\"Job StartTime\": \"" + jobStartTime + "\","
                +
                "\"Job EndTime\": \"" + jobEndTime + "\","
                +
                "\"Job Duration\": \"" + jobDuration + "\","
                +
                "\"Exception Log\" :\"" + exceptionUrl + "\""
                +
                "}]";
    }

}
