package com.dlink.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Resource;

/**
 * resource mapper interface
 */
@Mapper
public interface ResourceMapper extends SuperMapper<Resource> {

}
