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

package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.UDFTemplate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


/**
 * @author ZackYoung
 * @since 0.6.8
 * udf 模板mapper
 */
@Mapper
public interface UDFTemplateMapper extends SuperMapper<UDFTemplate> {
    @Override
    @Select("select * from dlink_udf_template")
    List<UDFTemplate> selectForProTable(Page<UDFTemplate> page, Wrapper<UDFTemplate> queryWrapper, Map<String, Object> param);
}