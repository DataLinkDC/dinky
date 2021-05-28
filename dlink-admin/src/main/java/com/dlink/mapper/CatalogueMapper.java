package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Catalogue;
import org.apache.ibatis.annotations.Mapper;

/**
 * CatalogueMapper
 *
 * @author wenmo
 * @since 2021/5/28 13:53
 **/
@Mapper
public interface CatalogueMapper  extends SuperMapper<Catalogue> {
}
