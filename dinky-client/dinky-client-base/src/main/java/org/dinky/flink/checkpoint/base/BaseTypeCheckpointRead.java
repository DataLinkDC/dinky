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

package org.dinky.flink.checkpoint.base;

import org.dinky.data.model.CheckPointReadTable;
import org.dinky.flink.checkpoint.BaseCheckpointRead;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.runtime.state.PartitionableListState;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;

public class BaseTypeCheckpointRead extends BaseCheckpointRead {
    private static final ExecutionConfig EXECUTION_CONFIG = new ExecutionConfig();

    @Override
    public Optional<CheckPointReadTable> create(PartitionableListState<?> partitionableListState) {
        List<JSONObject> data = CollUtil.newArrayList(partitionableListState.get()).stream()
                .map(x -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.set("data", x);
                    return jsonObject;
                })
                .collect(Collectors.toList());
        CheckPointReadTable tableVO = CheckPointReadTable.builder()
                .datas(data)
                .headers(CollUtil.newArrayList("data"))
                .build();
        return Optional.of(tableVO);
    }

    private static TypeSerializer<?> getTypeSerializer(PartitionableListState<?> partitionableListState) {
        Map<Class<?>, BasicTypeInfo<?>> types = (Map<Class<?>, BasicTypeInfo<?>>)
                ReflectUtil.getStaticFieldValue(ReflectUtil.getField(BasicTypeInfo.class, "TYPES"));
        for (Map.Entry<Class<?>, BasicTypeInfo<?>> entry : types.entrySet()) {
            TypeSerializer<?> serializer = entry.getValue().createSerializer(EXECUTION_CONFIG);
            boolean equals = getArrayListSerializer(partitionableListState)
                    .getElementSerializer()
                    .getClass()
                    .equals(serializer.getClass());
            if (equals) {
                return serializer;
            }
        }
        return null;
    }

    @Override
    public boolean isSourceCkp(PartitionableListState<?> partitionableListState) {
        TypeSerializer<?> typeSerializer = getTypeSerializer(partitionableListState);
        return typeSerializer != null;
    }
}
