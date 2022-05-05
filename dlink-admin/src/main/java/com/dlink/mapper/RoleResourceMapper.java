package com.dlink.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.RoleNamespace;
import com.dlink.model.RoleResource;

/**
 * role resource mapper interface
 */
@Mapper
public interface RoleResourceMapper extends SuperMapper<RoleResource> {
}
