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

package org.dinky.mybatis.service.impl;

import org.dinky.data.result.ProTableResult;
import org.dinky.mybatis.mapper.SuperMapper;
import org.dinky.mybatis.service.ISuperService;
import org.dinky.mybatis.util.ProTableUtil;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.lang.Opt;

/**
 * SuperServiceImpl
 *
 * @since 2021/5/25
 */
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T>
        implements ISuperService<T> {

    private ProTableResult<T> selectForProTable(
            JsonNode params, boolean isDelete, Map<String, Object> paramsMap) {
        Integer current = params.has("current") ? params.get("current").asInt() : 1;
        Integer pageSize = params.has("pageSize") ? params.get("pageSize").asInt() : 10;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(params, queryWrapper, isDelete);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(params, Map.class);

        Opt.ofNullable(paramsMap).ifPresent(x -> param.putAll(paramsMap));

        Page<T> page = new Page<>(current, pageSize);
        List<T> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<T>builder()
                .success(true)
                .data(list)
                .total(page.getTotal())
                .current(current)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode params) {
        return selectForProTable(params, false, null);
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode params, boolean isDelete) {
        return selectForProTable(params, isDelete, null);
    }

    @Override
    public ProTableResult<T> selectForProTable(JsonNode params, Map<String, Object> paramMap) {
        return selectForProTable(params, false, paramMap);
    }
}
