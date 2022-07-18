package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Role;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * role mapper interface
 */
@Mapper
public interface RoleMapper extends SuperMapper<Role> {
    List<Role> getRoleByIds(@Param("roleIds") List<Integer> roleIds);
}