package com.dlink.scheduler.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DagData
 */
@Data
public class DagData {

    @ApiModelProperty(value = "工作流程定义")
    private ProcessDefinition processDefinition;

    @ApiModelProperty(value = "工作流程定义,任务定义关联 关联关系集合")
    private List<ProcessTaskRelation> processTaskRelationList;

    @ApiModelProperty(value = "任务定义关联集合")
    private List<TaskDefinition> taskDefinitionList;

}
