package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertInstanceMapper;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertInstanceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AlertInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 19:53
 **/
@Service
public class AlertInstanceServiceImpl extends SuperServiceImpl<AlertInstanceMapper, AlertInstance> implements AlertInstanceService {
    @Override
    public List<AlertInstance> listEnabledAll() {
        return list(new QueryWrapper<AlertInstance>().eq("enabled", 1));
    }
}
