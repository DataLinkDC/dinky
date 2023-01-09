/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.db.service.impl;

import org.dinky.assertion.Asserts;
import org.dinky.common.result.ProTableResult;
import org.dinky.db.mapper.SuperMapper;
import org.dinky.db.service.ISuperService;
import org.dinky.db.util.ProTableUtil;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        return ProTableResult.<T>builder().success(true).data(list).total(page.getTotal()).current(current)
                .pageSize(pageSize).build();
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode para, boolean isDelete) {
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper, isDelete);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        Page<T> page = new Page<>(current, pageSize);
        List<T> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<T>builder().success(true).data(list).total(page.getTotal()).current(current)
                .pageSize(pageSize).build();
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode para, Map<String, Object> paraMap) {
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        if (Asserts.isNotNull(paraMap)) {
            for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
                param.put(entry.getKey(), entry.getValue());
            }
        }
        Page<T> page = new Page<>(current, pageSize);
        List<T> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<T>builder().success(true).data(list).total(page.getTotal()).current(current)
                .pageSize(pageSize).build();
    }

}
