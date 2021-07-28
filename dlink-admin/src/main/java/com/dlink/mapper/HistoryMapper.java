package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.History;
import org.apache.ibatis.annotations.Mapper;

/**
 * HistoryMapper
 *
 * @author wenmo
 * @since 2021/6/26 23:00
 */
@Mapper
public interface HistoryMapper extends SuperMapper<History> {
}
