package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Role;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * role mapper interface
 */
@Mapper
public interface RoleMapper extends SuperMapper<Role> {
    List<Role> getRoleByIds(@Param("roleIds") Set<Integer> roleIds);
}