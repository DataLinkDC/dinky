package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.TaskVersion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author huang
 */
public interface TaskVersionService extends ISuperService<TaskVersion> {


    /**
     * @description 通过作业Id查询版本数据
     * @param taskId
     * @return java.util.List<com.dlink.model.TaskVersion>
     * @author huang
     * @date 2022/6/22 17:17
     */
    List<TaskVersion> getTaskVersionByTaskId(Integer taskId);


    TaskVersion getTaskMaxVersionByTaskId(Integer taskId);
}
