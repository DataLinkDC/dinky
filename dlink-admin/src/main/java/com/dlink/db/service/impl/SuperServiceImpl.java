package com.dlink.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.db.mapper.SuperMapper;
import com.dlink.db.service.ISuperService;
import com.dlink.db.util.ProTableUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SuperServiceImpl
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements ISuperService<T> {
    
    @Override
    public ProTableResult<T> selectForProTable(JsonNode para) {
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        Page<T> page = new Page<>(current, pageSize);
        List<T> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<T>builder().success(true).data(list).total(page.getTotal()).current(current).pageSize(pageSize).build();
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode para,boolean isDelete) {
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper,isDelete);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        Page<T> page = new Page<>(current, pageSize);
        List<T> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<T>builder().success(true).data(list).total(page.getTotal()).current(current).pageSize(pageSize).build();
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode para,Map<String, Object> paraMap) {
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        if(Asserts.isNotNull(paraMap)){
            for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
                param.put(entry.getKey(),entry.getValue());
            }
        }
        Page<T> page = new Page<>(current, pageSize);
        List<T> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<T>builder().success(true).data(list).total(page.getTotal()).current(current).pageSize(pageSize).build();
    }
    
}
