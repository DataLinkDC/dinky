package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Cluster;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClusterMapper
 *
 * @author wenmo
 * @since 2021/5/28 13:56
 **/
@Mapper
public interface ClusterMapper extends SuperMapper<Cluster> {

    List<Cluster> listSessionEnable();
}
