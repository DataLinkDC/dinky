package com.dlink.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.UserRole;

/**
 * user role mapper interface
 */
@Mapper
public interface UserRoleMapper extends SuperMapper<UserRole> {
}
