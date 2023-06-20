package org.dinky.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricsLayoutDTO {

    /** */
    private Integer id;
    private Integer taskId;

    /** */
    private String vertices;

    /** */
    private String metrics;

    /** */
    private Integer position;

    /** */
    private String showType;

    /** */
    private String showSize;

    /** */
    private String title;

    /** */
    private String layoutName;
}
