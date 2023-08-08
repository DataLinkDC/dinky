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

package org.dinky.mybatis.crypto;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.autoconfigure.SqlSessionFactoryBeanCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

@Component
public class MybatisPlusCustomizer implements SqlSessionFactoryBeanCustomizer {

    @Override
    public void customize(MybatisSqlSessionFactoryBean factoryBean) {
        factoryBean.setConfiguration(
                new MybatisConfiguration() {
                    @Override
                    public void addMappedStatement(MappedStatement ms) {
                        final String ns = ms.getId().replaceAll("(.*Mapper)\\.\\w+$", "$1");
                        final MapperBuilderAssistant builderAssistant =
                                new MapperBuilderAssistant(ms.getConfiguration(), "");
                        builderAssistant.setCurrentNamespace(ns);
                        boolean[] changes = new boolean[] {false};
                        final List<ResultMap> collect =
                                ms.getResultMaps().stream()
                                        .map(
                                                m -> {
                                                    // 仅ResultType为Table且包含TypeHandler时，替换ResultMap为MybatisPlus生成的
                                                    TableName table =
                                                            m.getType()
                                                                    .getAnnotation(TableName.class);
                                                    if (table != null) {
                                                        TableInfo tableInfo =
                                                                TableInfoHelper.getTableInfo(
                                                                        m.getType());
                                                        if (tableInfo == null) {
                                                            tableInfo =
                                                                    TableInfoHelper.initTableInfo(
                                                                            builderAssistant,
                                                                            m.getType());
                                                        }
                                                        if (tableInfo != null
                                                                && tableInfo.getResultMap() != null
                                                                && tableInfo.getFieldList().stream()
                                                                        .anyMatch(
                                                                                f ->
                                                                                        f
                                                                                                        .getTypeHandler()
                                                                                                != null)) {
                                                            ResultMap resultMap =
                                                                    ms.getConfiguration()
                                                                            .getResultMap(
                                                                                    tableInfo
                                                                                            .getResultMap());
                                                            if (resultMap != null
                                                                    && resultMap != m) {
                                                                changes[0] = true;
                                                                return resultMap;
                                                            }
                                                        }
                                                    }
                                                    return m;
                                                })
                                        .collect(Collectors.toList());
                        if (changes[0]) {
                            final Field field =
                                    ReflectionUtils.findField(ms.getClass(), "resultMaps");
                            if (field != null) {
                                ReflectionUtils.makeAccessible(field);
                                ReflectionUtils.setField(
                                        field, ms, Collections.unmodifiableList(collect));
                            }
                        }
                        super.addMappedStatement(ms);
                    }
                });
    }
}
