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

package org.dinky.service.impl;

import org.dinky.crypto.CryptoComponent;
import org.dinky.data.model.FragmentVariable;
import org.dinky.data.result.ProTableResult;
import org.dinky.mapper.FragmentVariableMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.FragmentVariableService;
import org.dinky.utils.FragmentVariableUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

/** FragmentVariableServiceImpl */
@Service
public class FragmentVariableServiceImpl
        extends SuperServiceImpl<FragmentVariableMapper, FragmentVariable>
        implements FragmentVariableService {

    @Resource private CryptoComponent cryptoComponent;

    @Override
    public boolean saveOrUpdate(FragmentVariable entity) {
        if (FragmentVariableUtils.isSensitive(entity.getName())
                && entity.getFragmentValue() != null) {
            entity.setFragmentValue(cryptoComponent.encryptText(entity.getFragmentValue()));
        }
        return super.saveOrUpdate(entity);
    }

    @Override
    public List<FragmentVariable> list(Wrapper<FragmentVariable> queryWrapper) {
        final List<FragmentVariable> list = super.list(queryWrapper);
        if (list != null) {
            for (FragmentVariable variable : list) {
                if (FragmentVariableUtils.isSensitive(variable.getName())
                        && variable.getFragmentValue() != null) {
                    variable.setFragmentValue(
                            cryptoComponent.decryptText(variable.getFragmentValue()));
                }
            }
        }
        return list;
    }

    @Override
    public ProTableResult<FragmentVariable> selectForProTable(JsonNode para) {
        final ProTableResult<FragmentVariable> result = super.selectForProTable(para);
        if (result != null && result.getData() != null) {
            for (FragmentVariable variable : result.getData()) {
                if (FragmentVariableUtils.isSensitive(variable.getName())
                        && variable.getFragmentValue() != null) {
                    variable.setFragmentValue(
                            cryptoComponent.decryptText(variable.getFragmentValue()));
                }
            }
        }
        return super.selectForProTable(para);
    }

    @Override
    public List<FragmentVariable> listEnabledAll() {
        return list(new LambdaQueryWrapper<FragmentVariable>().eq(FragmentVariable::getEnabled, 1));
    }

    @Override
    public Map<String, String> listEnabledVariables() {
        Map<String, String> variables = new LinkedHashMap<>();
        for (FragmentVariable fragmentVariable : listEnabledAll()) {
            variables.put(fragmentVariable.getName(), fragmentVariable.getFragmentValue());
        }
        return variables;
    }

    @Override
    public Boolean enable(Integer id) {
        FragmentVariable fragmentVariable = getById(id);
        fragmentVariable.setEnabled(!fragmentVariable.getEnabled());
        return updateById(fragmentVariable);
    }
}
