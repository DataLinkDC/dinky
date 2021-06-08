package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * StudioExecuteDTO
 *
 * @author wenmo
 * @since 2021/5/30 11:09
 */
@Getter
@Setter
public class StudioExecuteDTO {
    private String session;
    private String statement;
    private Integer clusterId=0;
    private Integer checkPoint=0;
    private Integer parallelism=1;
    private Integer maxRowNum=100;
    private boolean fragment=false;
    private String savePointPath;
    private String jobName;
}
