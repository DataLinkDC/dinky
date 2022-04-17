package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;

import com.dlink.model.Tenant;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

/**
 * tenant mapper interface
 */
@Mapper
public interface TenantMapper extends SuperMapper<Tenant> {

}
