package org.dinky.data.bo.catalogue.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dinky.data.model.ext.TaskExtConfig;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportTaskBO {

    private String name;

    private String dialect;

    private String type;

    private Integer checkPoint;

    private Integer savePointStrategy;

    // private String savePointPath;

    private Integer parallelism;

    private Boolean fragment;

    private Boolean statementSet;

    private Boolean batchModel;

    private Integer clusterId;

    private Integer clusterConfigurationId;

    private Integer databaseId;

    private Integer envId;

    private Integer alertGroupId;

    private TaskExtConfig configJson;

    private String note;

    private Integer step;

    private Boolean enabled;

    private String statement;

}
