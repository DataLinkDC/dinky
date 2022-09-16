package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.ConditionType;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessTaskRelation {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "工作流定义")
    private int processDefinitionVersion;

    @ApiModelProperty(value = "项目编号")
    private long projectCode;

    @ApiModelProperty(value = "工作流定义编号")
    private long processDefinitionCode;

    @ApiModelProperty(value = "前置任务编号")
    private long preTaskCode;

    @ApiModelProperty(value = "前置任务版本")
    private int preTaskVersion;

    @ApiModelProperty(value = "发布任务编号")
    private long postTaskCode;

    @ApiModelProperty(value = "发布任务版本")
    private int postTaskVersion;

    @ApiModelProperty(value = "条件类型")
    private ConditionType conditionType;

    @ApiModelProperty(value = "条件参数")
    private String conditionParams;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
