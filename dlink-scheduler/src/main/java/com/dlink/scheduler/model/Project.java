package com.dlink.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * project
 */
@Data
public class Project {

    /**
     * id
     */
    private int id;

    /**
     * user id
     */
    private int userId;

    /**
     * user name
     */
    private String userName;

    /**
     * project code
     */
    private long code;

    /**
     * project name
     */
    private String name;

    /**
     * project description
     */
    private String description;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * permission
     */
    private int perm;

    /**
     * process define count
     */
    private int defCount;

    /**
     * process instance running count
     */
    private int instRunningCount;

}
