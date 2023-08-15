package org.dinky.data.model;

import org.dinky.mybatis.model.SuperEntity;

import java.time.LocalDateTime;

public class TaskOpertionLog  extends SuperEntity<TaskOpertionLog> {
    private int id;
    private int taskId;
    private int opType;
    private int status;
    private String detail;
    private LocalDateTime op_time;
    private int userId;
}
