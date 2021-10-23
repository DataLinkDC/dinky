package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.Document;

import java.util.List;

/**
 * DocumentService
 *
 * @author wenmo
 * @since 2021/6/3 14:35
 **/
public interface DocumentService extends ISuperService<Document> {
    List<Document> getFillAllByVersion(String version);
}
