package org.dinky.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "ApprovalDTO", description = "Approval Data Transfer Object")
public class ApprovalDTO {
    @ApiModelProperty(value = "Approval ID", dataType = "Integer", example = "123", notes = "The ID of the approval")
    private Integer id;
    
    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "123", notes = "The ID of the task")
    private Integer taskId;

    @ApiModelProperty(value = "Reviewer id required for current approval", dataType = "Integer", example = "123", notes = "The ID of the reviewer")
    private Integer reviewer;

    @ApiModelProperty(value = "Comment", dataType = "String", example = "Looks good to me/Please take a look", notes = "Comment from reviewer or submitter")
    private String comment;
}
