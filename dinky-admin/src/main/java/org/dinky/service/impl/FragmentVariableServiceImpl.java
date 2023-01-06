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

package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.FragmentVariableMapper;
import com.dlink.model.FragmentVariable;
import com.dlink.service.FragmentVariableService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


/**
 * FragmentVariableServiceImpl
 *
 * @author zhumingye
 * @since 2022/8/18
 */
@Service
public class FragmentVariableServiceImpl extends SuperServiceImpl<FragmentVariableMapper, FragmentVariable> implements FragmentVariableService {
    @Override
    public List<FragmentVariable> listEnabledAll() {
        return list(new QueryWrapper<FragmentVariable>().eq("enabled", 1));
    }

    @Override
    public Map<String, String> listEnabledVariables() {
        Map<String, String> variables = new LinkedHashMap<>();
        for (FragmentVariable fragmentVariable : listEnabledAll()) {
            variables.put(fragmentVariable.getName(), fragmentVariable.getFragmentValue());
        }
        return variables;
    }
}
