package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dlink.mapper.TaskVersionMapper;
import com.dlink.model.TaskVersion;
import com.dlink.service.TaskVersionService;

import java.util.List;

/**
 * @author huang
 */
@Service
public class TaskVersionServiceImpl extends SuperServiceImpl<TaskVersionMapper, TaskVersion> implements TaskVersionService {

    @Override
    public List<TaskVersion> getTaskVersionByTaskId(Integer taskId) {

        return baseMapper.selectList(new LambdaQueryWrapper<TaskVersion>().eq(TaskVersion::getTaskId, taskId));
    }

    @Override
    public TaskVersion getTaskMaxVersionByTaskId(Integer taskId) {
        return baseMapper.getTaskMaxVersionByTaskId(taskId);
    }
}
