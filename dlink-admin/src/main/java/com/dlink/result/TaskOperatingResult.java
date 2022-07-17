package com.dlink.result;

import com.dlink.common.result.Result;
import com.dlink.model.CodeEnum;
import com.dlink.model.Task;
import com.dlink.model.TaskOperatingStatus;
import lombok.Data;

/**
 * @author mydq
 * @version 1.0
 * @date 2022/7/16 21:09
 **/
@Data
public class TaskOperatingResult {

    private Task task;

    private TaskOperatingStatus status;

    private Integer code;

    private String message;

    private boolean selectLatestSavepoint;

    public TaskOperatingResult(Task task) {
        this.task = task;
        this.status = TaskOperatingStatus.INIT;
        this.selectLatestSavepoint = false;
    }

    public TaskOperatingResult(Task task, boolean selectLatestSavepoint) {
        this.task = task;
        this.status = TaskOperatingStatus.INIT;
        this.selectLatestSavepoint = selectLatestSavepoint;
    }

    public void parseResult(Result result) {
        if (result == null) {
            return;
        }
        if (CodeEnum.SUCCESS.getCode().equals(result.getCode())) {
            this.status = TaskOperatingStatus.SUCCESS;
        } else if (CodeEnum.ERROR.getCode().equals(result.getCode())) {
            this.status = TaskOperatingStatus.FAIL;
        }
        this.code = result.getCode();
        this.message = result.getMsg();
    }
}
