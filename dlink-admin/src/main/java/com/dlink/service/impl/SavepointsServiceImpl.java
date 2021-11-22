package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.SavepointsMapper;
import com.dlink.model.Savepoints;
import com.dlink.service.SavepointsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SavepointsServiceImpl
 *
 * @author wenmo
 * @since 2021/11/21
 **/
@Service
public class SavepointsServiceImpl extends SuperServiceImpl<SavepointsMapper, Savepoints> implements SavepointsService {

    @Override
    public List<Savepoints> listSavepointsByTaskId(Integer taskId) {
        return list(new QueryWrapper<Savepoints>().eq("task_id",taskId));
    }
}
