package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.JarMapper;
import com.dlink.model.Jar;
import com.dlink.service.JarService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JarServiceImpl
 *
 * @author wenmo
 * @since 2021/11/13
 **/
@Service
public class JarServiceImpl extends SuperServiceImpl<JarMapper, Jar> implements JarService {
    @Override
    public List<Jar> listEnabledAll() {
        return list(new QueryWrapper<Jar>().eq("enabled",1));
    }
}
