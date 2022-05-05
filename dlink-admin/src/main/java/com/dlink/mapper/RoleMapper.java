package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Role;

import org.apache.ibatis.annotations.Mapper;

/**
 * role mapper interface
 */
@Mapper
public interface RoleMapper extends SuperMapper<Role> {
}
