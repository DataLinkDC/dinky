package com.dlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.TaskVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author huang
 */
@Mapper
public interface TaskVersionMapper extends SuperMapper<TaskVersion> {

    TaskVersion getTaskMaxVersionByTaskId(@Param("taskId") Integer taskId);
}
