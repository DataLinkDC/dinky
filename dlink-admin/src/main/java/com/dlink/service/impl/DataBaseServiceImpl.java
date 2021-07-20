package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.DataBaseMapper;
import com.dlink.model.DataBase;
import com.dlink.service.DataBaseService;
import org.springframework.stereotype.Service;

/**
 * DataBaseServiceImpl
 *
 * @author wenmo
 * @since 2021/7/20 23:47
 */
@Service
public class DataBaseServiceImpl extends SuperServiceImpl<DataBaseMapper, DataBase> implements DataBaseService {
}
