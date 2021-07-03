package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.History;

/**
 * HistoryService
 *
 * @author wenmo
 * @since 2021/6/26 23:07
 */
public interface HistoryService extends ISuperService<History> {
    public boolean removeHistoryById(Integer id);
}
