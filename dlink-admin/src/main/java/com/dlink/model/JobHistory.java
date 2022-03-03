package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JobHistory
 *
 * @author wenmo
 * @since 2022/3/2 19:48
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_job_history")
public class JobHistory implements Serializable {

    private static final long serialVersionUID = 4984787372340047250L;

    private Integer id;

    @TableField(exist = false)
    private ObjectNode job;

    private String jobJson;

    @TableField(exist = false)
    private ObjectNode exceptions;

    private String exceptionsJson;

    @TableField(exist = false)
    private ObjectNode checkpoints;

    private String checkpointsJson;

    @TableField(exist = false)
    private ObjectNode checkpointsConfig;

    private String checkpointsConfigJson;

    @TableField(exist = false)
    private ObjectNode config;

    private String configJson;

    @TableField(exist = false)
    private ObjectNode jar;

    private String jarJson;

    @TableField(exist = false)
    private ObjectNode cluster;

    private String clusterJson;

    @TableField(exist = false)
    private ObjectNode clusterConfiguration;

    private String clusterConfigurationJson;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
