package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.CatalogueMapper;
import com.dlink.model.Catalogue;
import com.dlink.service.CatalogueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CatalogueServiceImpl
 *
 * @author wenmo
 * @since 2021/5/28 14:02
 **/
@Service
public class CatalogueServiceImpl extends SuperServiceImpl<CatalogueMapper, Catalogue> implements CatalogueService {
    @Override
    public List<Catalogue> getAllData() {
        return this.list();
    }
}
