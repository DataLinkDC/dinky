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

package org.dinky.flink.checkpoint.pojo;

import org.dinky.data.model.CheckPointReadTable;
import org.dinky.flink.checkpoint.BaseCheckpointRead;

import org.apache.flink.api.java.typeutils.runtime.PojoSerializer;
import org.apache.flink.runtime.state.PartitionableListState;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;

public class PojoTypeCheckpointRead extends BaseCheckpointRead {
    public static final Class<?> SERIALIZER_CLASS = PojoSerializer.class;

    public Optional<CheckPointReadTable> create(PartitionableListState<?> partitionableListState) {
        PojoSerializer<?> typeSerializer = (PojoSerializer<?>)
                getArrayListSerializer(partitionableListState).getElementSerializer();
        Field[] fields = (Field[]) ReflectUtil.getFieldValue(typeSerializer, "fields");

        List<String> headers =
                Arrays.stream(fields).map(ReflectUtil::getFieldName).collect(Collectors.toList());
        List<JSONObject> data = CollUtil.newArrayList(partitionableListState.get()).stream()
                .map(JSONObject::new)
                .collect(Collectors.toList());
        CheckPointReadTable tableVO =
                CheckPointReadTable.builder().headers(headers).datas(data).build();
        return Optional.of(tableVO);
    }

    public boolean isSourceCkp(PartitionableListState<?> partitionableListState) {
        return getArrayListSerializer(partitionableListState)
                .getElementSerializer()
                .getClass()
                .equals(SERIALIZER_CLASS);
    }
}
