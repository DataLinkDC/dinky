package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.Catalogue;

import java.util.List;

/**
 * CatalogueService
 *
 * @author wenmo
 * @since 2021/5/28 14:01
 **/
public interface CatalogueService extends ISuperService<Catalogue> {

    List<Catalogue> getAllData();
}
