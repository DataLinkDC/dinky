package com.dlink.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
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
@TableName(value = "dlink_task_version")
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

    /**
     * CheckPoint
     */
    @TableField(value = "check_point")
    private Integer checkPoint;

    /**
     * SavePoint策略
     */
    @TableField(value = "save_point_strategy")
    private Integer savePointStrategy;

    /**
     * SavePointPath
     */
    @TableField(value = "save_point_path")
    private String savePointPath;

    /**
     * parallelism
     */
    @TableField(value = "parallelism")
    private Integer parallelism;

    /**
     * fragment
     */
    @TableField(value = "fragment")
    private Boolean fragment;

    /**
     * 启用语句集
     */
    @TableField(value = "statement_set")
    private Boolean statementSet;

    /**
     * 使用批模式
     */
    @TableField(value = "batch_model")
    private Boolean batchModel;

    /**
     * Flink集群ID
     */
    @TableField(value = "cluster_id")
    private Integer clusterId;

    /**
     * 集群配置ID
     */
    @TableField(value = "cluster_configuration_id")
    private Integer clusterConfigurationId;

    /**
     * 数据源ID
     */
    @TableField(value = "database_id")
    private Integer databaseId;

    /**
     * jarID
     */
    @TableField(value = "jar_id")
    private Integer jarId;

    /**
     * 环境ID
     */
    @TableField(value = "env_id")
    private Integer envId;

    /**
     * 报警组ID
     */
    @TableField(value = "alert_group_id")
    private Integer alertGroupId;

    /**
     * 配置JSON
     */
    @TableField(value = "config_json")
    private String configJson;

    /**
     * 注释
     */
    @TableField(value = "note")
    private String note;

    /**
     * 作业生命周期
     */
    @TableField(value = "step")
    private Integer step;

    /**
     * 作业实例ID
     */
    @TableField(value = "job_instance_id")
    private Integer jobInstanceId;

    /**
     * 是否启用
     */
    @TableField(value = "enabled")
    private Boolean enabled;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
