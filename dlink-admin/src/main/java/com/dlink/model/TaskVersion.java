package com.dlink.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dlink.dto.TaskVersionConfigureDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
    * 作业
    */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "dlink_task_version", autoResultMap = true)
public class TaskVersion implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 作业ID
     */
    @TableField(value = "task_id")
    private Integer taskId;

    /**
     * 版本ID
     */
    @TableField(value = "version_id")
    private Integer versionId;

    /**
     * flink sql 内容
     */
    @TableField(value = "`statement`")
    private String statement;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 别名
     */
    @TableField(value = "`alias`")
    private String alias;

    /**
     * 方言
     */
    @TableField(value = "dialect")
    private String dialect;

    /**
     * 类型
     */
    @TableField(value = "`type`")
    private String type;

    @TableField(value = "task_configure",typeHandler = JacksonTypeHandler.class)
    private TaskVersionConfigureDTO taskConfigure;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;






}
