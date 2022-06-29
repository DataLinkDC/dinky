package com.dlink.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huang
 * @description: 任务版本记录
 * @date 2022/6/27 18:17
 */
@Data
public class TaskVersionHistoryDTO implements Serializable {
    private Integer id;
    private Integer versionId;
    private Date createTime;
}
