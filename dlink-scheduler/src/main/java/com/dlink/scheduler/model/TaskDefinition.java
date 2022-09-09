package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.Flag;
import com.dlink.scheduler.enums.Priority;
import com.dlink.scheduler.enums.TaskExecuteType;
import com.dlink.scheduler.enums.TaskTimeoutStrategy;
import com.dlink.scheduler.enums.TimeoutFlag;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * task definition
 */
@Data
public class TaskDefinition {

    /**
     * id
     */
    private int id;

    /**
     * code
     */
    private long code;

    /**
     * name
     */
    private String name;

    /**
     * version
     */
    private int version;

    /**
     * description
     */
    private String description;

    /**
     * project code
     */
    private long projectCode;

    /**
     * task user id
     */
    private int userId;

    /**
     * task type
     */
    private String taskType;

    /**
     * user defined parameters
     */
    private String taskParams;

    /**
     * user defined parameter list
     */
    private List<Property> taskParamList;

    /**
     * user defined parameter map
     */
    private Map<String, String> taskParamMap;

    /**
     * task is valid: yes/no
     */
    private Flag flag;

    /**
     * task priority
     */
    private Priority taskPriority;

    /**
     * user name
     */
    private String userName;

    /**
     * project name
     */
    private String projectName;

    /**
     * worker group
     */
    private String workerGroup;

    /**
     * environment code
     */
    private long environmentCode;

    /**
     * fail retry times
     */
    private int failRetryTimes;

    /**
     * fail retry interval
     */
    private int failRetryInterval;

    /**
     * timeout flag
     */
    private TimeoutFlag timeoutFlag;

    /**
     * timeout notify strategy
     */
    private TaskTimeoutStrategy timeoutNotifyStrategy;

    /**
     * task warning time out. unit: minute
     */
    private int timeout;

    /**
     * delay execution time.
     */
    private int delayTime;

    /**
     * resource ids
     */
    private String resourceIds;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * modify user name
     */
    private String modifyBy;

    /**
     * task group id
     */
    private int taskGroupId;
    /**
     * task group id
     */
    private int taskGroupPriority;

    /**
     * cpu quota
     */
    private Integer cpuQuota;

    /**
     * max memory
     */
    private Integer memoryMax;

    /**
     * task execute type
     */
    private TaskExecuteType taskExecuteType;
}
