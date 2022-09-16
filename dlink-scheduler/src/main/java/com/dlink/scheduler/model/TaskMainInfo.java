package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.ReleaseState;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * task main info
 */
@Data
public class TaskMainInfo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "任务名")
    private String taskName;

    @ApiModelProperty(value = "任务编号")
    private Long taskCode;

    @ApiModelProperty(value = "任务版本")
    private Integer taskVersion;

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date taskCreateTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date taskUpdateTime;

    @ApiModelProperty(value = "工作流编号")
    private Long processDefinitionCode;

    @ApiModelProperty(value = "工作流编号")
    private Integer processDefinitionVersion;

    @ApiModelProperty(value = "工作流名")
    private String processDefinitionName;

    @ApiModelProperty(value = "状态")
    private ReleaseState processReleaseState;

    @ApiModelProperty(value = "前置任务集合")
    private Map<Long, String> upstreamTaskMap;

    @ApiModelProperty(value = "前置任务编号")
    private Long upstreamTaskCode;

    @ApiModelProperty(value = "前置名")
    private String upstreamTaskName;
}
