package com.dlink.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * task definition log
 */
@Data
public class TaskDefinitionLog extends TaskDefinition {

    /**
     * operator user id
     */
    private int operator;

    /**
     * operate time
     */
    private Date operateTime;

}
