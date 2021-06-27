package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.HistoryMapper;
import com.dlink.model.History;
import com.dlink.service.HistoryService;
import org.springframework.stereotype.Service;

/**
 * HistoryServiceImpl
 *
 * @author wenmo
 * @since 2021/6/26 23:08
 */
@Service
public class HistoryServiceImpl extends SuperServiceImpl<HistoryMapper, History> implements HistoryService {
}
