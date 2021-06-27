package com.dlink.job;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Job
 *
 * @author wenmo
 * @since 2021/6/26 23:39
 */
@Getter
@Setter
public class Job {
    private Integer id;
    private Integer clusterId;
    private String session;
    private String jobId;
    private String jobName;
    private String jobManagerAddress;
    private Integer status;
    private String statement;
    private Integer type;
    private String error;
    private String result;
    private ExecutorSetting config;
    private LocalDate startTime;
    private LocalDate endTime;
    private String msg;
    private Integer taskId;
    private Executor executor;
}
