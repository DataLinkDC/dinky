package org.dinky.scheduler.model;

import lombok.Data;

import java.util.Date;
@Data
public class TaskGroup {
    private Integer id;
    private String name;
    private Long projectCode;
    private String description;
    private Integer groupSize;
    private Integer useSize;
    private Integer userId;
    private String status;
    private Date createTime;
    private Date updateTime;
}
