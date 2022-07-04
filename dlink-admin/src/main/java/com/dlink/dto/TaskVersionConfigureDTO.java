package com.dlink.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huang
 * @description: 版本信息配置
 * @date 2022/6/23 10:25
 */
@Data
public class TaskVersionConfigureDTO implements Serializable {

    /**
     * CheckPoint
     */
    private Integer checkPoint;

    /**
     * SavePoint策略
     */
    private Integer savePointStrategy;

    /**
     * SavePointPath
     */
    private String savePointPath;

    /**
     * parallelism
     */
    private Integer parallelism;

    /**
     * fragment
     */
    private Boolean fragment;

    /**
     * 启用语句集
     */
    private Boolean statementSet;

    /**
     * 使用批模式
     */
    private Boolean batchModel;

    /**
     * Flink集群ID
     */
    private Integer clusterId;

    /**
     * 集群配置ID
     */
    private Integer clusterConfigurationId;

    /**
     * 数据源ID
     */
    private Integer databaseId;

    /**
     * jarID
     */
    private Integer jarId;

    /**
     * 环境ID
     */
    private Integer envId;

    /**
     * 报警组ID
     */
    private Integer alertGroupId;

    /**
     * 配置JSON
     */
    private String configJson;

    /**
     * 注释
     */
    private String note;

    /**
     * 作业生命周期
     */
    private Integer step;

    /**
     * 作业实例ID
     */
    private Integer jobInstanceId;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
