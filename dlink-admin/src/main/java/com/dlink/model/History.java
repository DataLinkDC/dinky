package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * History
 *
 * @author wenmo
 * @since 2021/6/26 22:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_history")
public class History implements Serializable {

    private static final long serialVersionUID = 4058280957630503072L;

    private Integer id;

    private Integer tenantId;

    private Integer clusterId;

    private Integer clusterConfigurationId;

    private String session;

    private String jobId;

    private String jobName;

    private String jobManagerAddress;

    private Integer status;

    private String statement;

    private String type;

    private String error;

    private String result;

    @TableField(exist = false)
    private ObjectNode config;

    private String configJson;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer taskId;

    @TableField(exist = false)
    private String statusText;

    @TableField(exist = false)
    private String clusterAlias;

    @TableField(exist = false)
    private String taskAlias;

    public JobInstance buildJobInstance() {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setHistoryId(id);
        jobInstance.setClusterId(clusterId);
        jobInstance.setTaskId(taskId);
        jobInstance.setName(jobName);
        return jobInstance;
    }
}
