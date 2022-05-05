package com.dlink.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Tenant;

/**
 * tenant mapper interface
 */
@Mapper
public interface TenantMapper extends SuperMapper<Tenant> {
}
