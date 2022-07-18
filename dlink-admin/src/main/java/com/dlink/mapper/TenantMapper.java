package com.dlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Role;
import com.dlink.model.Tenant;

/**
 * tenant mapper interface
 */
@Mapper
public interface TenantMapper extends SuperMapper<Tenant> {
    List<Tenant> getTenantByIds(@Param("tenantIds") List<Integer> tenantIds);
}