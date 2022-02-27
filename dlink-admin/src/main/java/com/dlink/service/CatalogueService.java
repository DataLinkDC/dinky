package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.dto.CatalogueTaskDTO;
import com.dlink.model.Catalogue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CatalogueService
 *
 * @author wenmo
 * @since 2021/5/28 14:01
 **/
public interface CatalogueService extends ISuperService<Catalogue> {

    List<Catalogue> getAllData();

    Catalogue findByParentIdAndName(Integer parent_id, String name);

    Catalogue createCatalogueAndTask(CatalogueTaskDTO catalogueTaskDTO);

    Catalogue createCatalogAndFileTask(CatalogueTaskDTO catalogueTaskDTO, String ment);

    boolean toRename(Catalogue catalogue);

    boolean removeCatalogueAndTaskById(Integer id);
}
