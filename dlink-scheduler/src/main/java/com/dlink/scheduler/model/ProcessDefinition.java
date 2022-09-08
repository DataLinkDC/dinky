package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.Flag;
import com.dlink.scheduler.enums.ProcessExecutionTypeEnum;
import com.dlink.scheduler.enums.ReleaseState;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * process definition
 */
@Data
public class ProcessDefinition {

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
     * release state : online/offline
     */
    private ReleaseState releaseState;

    /**
     * project code
     */
    private long projectCode;

    /**
     * description
     */
    private String description;

    /**
     * user defined parameters
     */
    private String globalParams;

    /**
     * user defined parameter list
     */
    private List<Property> globalParamList;

    /**
     * user define parameter map
     */
    private Map<String, String> globalParamMap;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * process is valid: yes/no
     */
    private Flag flag;

    /**
     * process user id
     */
    private int userId;

    /**
     * user name
     */
    private String userName;

    /**
     * project name
     */
    private String projectName;

    /**
     * locations array for web
     */
    private String locations;

    /**
     * schedule release state : online/offline
     */
    private ReleaseState scheduleReleaseState;

    /**
     * process warning time out. unit: minute
     */
    private int timeout;

    /**
     * tenant id
     */
    private int tenantId;

    /**
     * tenant code
     */
    private String tenantCode;

    /**
     * modify user name
     */
    private String modifyBy;

    /**
     * warningGroupId
     */
    private int warningGroupId;

    /**
     * execution type
     */
    private ProcessExecutionTypeEnum executionType;

}
