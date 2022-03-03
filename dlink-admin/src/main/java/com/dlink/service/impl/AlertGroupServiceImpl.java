package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertGroupMapper;
import com.dlink.model.AlertGroup;
import com.dlink.service.AlertGroupService;
import org.springframework.stereotype.Service;

/**
 * AlertGroupServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 20:01
 **/
@Service
public class AlertGroupServiceImpl extends SuperServiceImpl<AlertGroupMapper, AlertGroup> implements AlertGroupService {
}
