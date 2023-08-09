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

package org.dinky.data.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;

import cn.hutool.json.JSONArray;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class ListTypeHandler extends AbstractJsonTypeHandler<List<Map>> {
    private static final Logger log = LoggerFactory.getLogger(FastjsonTypeHandler.class);

    protected List<Map> parse(String json) {
        return new JSONArray(json).toList(Map.class);
    }

    @Override
    protected String toJson(List<Map> obj) {
        return new JSONArray(obj).toString();
    }
}
