package com.dlink.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Namespace;

/**
 * namespace mapper interface
 */
@Mapper
public interface NamespaceMapper extends SuperMapper<Namespace> {

}