package org.dinky.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.dinky.data.model.Metrics;

import java.util.List;

@Data
@Builder
public class MetricsLayoutVo {
    @ApiModelProperty(value = "Layout Name", dataType = "String", notes = "Name of the layout")
    private String layoutName;

    @ApiModelProperty(value = "Job ID", dataType = "String", notes = "ID of the associated job")
    private String flinkJobId;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "1001", notes = "ID of the associated task")
    private int taskId;

    /**
     * The feature is not complete and may be implemented in the future
     */
    @ApiModelProperty(value = "Show In Dashboard", dataType = "Boolean", notes = "Whether to show in dashboard")
    private boolean showInDashboard;

    @ApiModelProperty(value = "Metrics", dataType = "List<Metrics>", notes = "Metrics information")
    private List<Metrics> metrics;
}
