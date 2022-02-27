package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertHistoryMapper;
import com.dlink.model.AlertHistory;
import com.dlink.service.AlertHistoryService;
import org.springframework.stereotype.Service;

/**
 * AlertHistoryServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 20:42
 **/
@Service
public class AlertHistoryServiceImpl extends SuperServiceImpl<AlertHistoryMapper, AlertHistory> implements AlertHistoryService {
}
