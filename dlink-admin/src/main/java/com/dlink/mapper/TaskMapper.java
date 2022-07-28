package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Task;
import com.dlink.model.UserRole;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业 Mapper 接口
 *
 * @author wenmo
 * @since 2021-05-28
 */
@Mapper
public interface TaskMapper extends SuperMapper<Task> {

    Integer queryAllSizeByName(String name);
}
