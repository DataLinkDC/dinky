package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Catalogue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * CatalogueMapper
 *
 * @author wenmo
 * @since 2021/5/28 13:53
 **/
@Mapper
public interface CatalogueMapper  extends SuperMapper<Catalogue> {
    Catalogue findByParentIdAndName(@Param("parent_id")Integer parent_id, @Param("name")String name);
}
