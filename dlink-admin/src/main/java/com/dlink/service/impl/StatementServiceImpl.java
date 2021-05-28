package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.StatementMapper;
import com.dlink.model.Statement;
import com.dlink.service.StatementService;
import org.springframework.stereotype.Service;

/**
 * StatementServiceImpl
 *
 * @author wenmo
 * @since 2021/5/28 13:45
 **/
@Service
public class StatementServiceImpl extends SuperServiceImpl<StatementMapper, Statement> implements StatementService {
}
