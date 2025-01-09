package org.dinky.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dinky.data.typehandler.JSONObjectHandler;

import java.util.List;
import java.util.Map;

/**
 * Test case for a table in a task
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@TableName("dinky_task_test_case")
@ApiModel(value = "Table Test Case", description = "Test cases for a table in a task")
public class TableTestCase {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for a test case")
    private Integer id;

    @ApiModelProperty(value = "Task Id", dataType = "Integer", example = "1", notes = "test case related task id")
    private Integer taskId;

    @ApiModelProperty(value = "Task Name", dataType = "String", example = "inputTableName", notes = "test case related table name in a task")
    private String tableName;

    @ApiModelProperty(value = "columns", dataType = "String", example = "[\"columnA\", \"columnB\"]", notes = "table columns")
    @TableField(typeHandler = JSONObjectHandler.class)
    private List<String> columns;

    @ApiModelProperty(value = "row data", dataType = "String", example = "[{\"columnA\":\"valueA\", \"columnB\":\"valueB\"}]")
    @TableField(typeHandler = JSONObjectHandler.class)
    private List<Map<String, String>> rowData;


}
