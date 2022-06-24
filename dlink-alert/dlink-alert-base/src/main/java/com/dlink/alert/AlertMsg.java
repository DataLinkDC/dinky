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

    private String AlertType; // 告警类型
    private String AlertTime; // 告警时间
    private String JobID; // 任务ID
    private String JobName; // 任务名称
    private String JobType; // 任务类型
    private String JobStatus; // 任务状态
    private String JobStartTime; // 任务开始时间
    private String JobEndTime; // 任务结束时间
    private String JobDuration; // 任务耗时
    private String LinkUrl; // Flink webUI 链接

    private String ExceptionUrl; // Flink job Root Exception 链接

    public AlertMsg() {
    }

    public AlertMsg(String alertType, String alertTime, String jobID, String jobName, String jobType, String jobStatus, String jobStartTime, String jobEndTime, String jobDuration, String linkUrl, String exceptionUrl) {
        this.AlertType = alertType;
        this.AlertTime = alertTime;
        this.JobID = jobID;
        this.JobName = jobName;
        this.JobType = jobType;
        this.JobStatus = jobStatus;
        this.JobStartTime = jobStartTime;
        this.JobEndTime = jobEndTime;
        this.JobDuration = jobDuration;
        this.LinkUrl = linkUrl;
        this.ExceptionUrl = exceptionUrl;
    }
    public String toString() {
        return "[{ \"Alert Type\":\""+AlertType+"\","
                +
                "\"Alert Time\":\""+AlertTime+"\","
                +
                "\"Job ID\":\""+JobID+"\","
                +
                "\"Job Name\":\""+ JobName +"\","
                +
                "\"Job Type\":\""+ JobType +"\","
                +
                "\"Job Status\":\""+ JobStatus +"\","
                +
                "\"Job StartTime\": \"" +JobStartTime +"\","
                +
                "\"Job EndTime\": \""+JobEndTime+"\","
                +
                "\"Job Duration\": \""+JobDuration+"\","
//                +
//                "\"LinkUrl\": \""+ LinkUrl +"\","
                +
                "\"Exception Log\" :\""+ ExceptionUrl +"\"" +
                "}]";
    }
}
