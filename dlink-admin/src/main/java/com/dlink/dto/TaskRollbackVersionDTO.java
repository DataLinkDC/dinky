package com.dlink.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huang
 * @description: TODO
 * @date 2022/6/23 10:25
 */
@Data
public class TaskRollbackVersionDTO implements Serializable {
    private Integer id;
    private Integer versionId;
}
