package org.dinky.data.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dinky.data.annotations.TaskId;
import org.dinky.data.model.TableTestCase;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "TaskTestCaseDTo", description = "Task Test Case DTo")
public class TaskTestCaseListDTO extends AbstractStatementDTO {

    @ApiModelProperty(value = "taskId", required = true, dataType = "Integer")
    @TaskId
    private Integer taskId;
    @ApiModelProperty(value = "testCaseList", required = true, dataType = "List", allowEmptyValue = true)
    private List<TableTestCase> taskTableInputList;
}
