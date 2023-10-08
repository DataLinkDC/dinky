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

package org.dinky.flink.checkpoint.source;

import org.dinky.data.model.CheckPointReadTable;
import org.dinky.flink.checkpoint.BaseCheckpointRead;
import org.dinky.flink.checkpoint.SupportSplitSerializer;

import org.apache.flink.api.common.typeutils.base.array.BytePrimitiveArraySerializer;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.state.PartitionableListState;
import org.apache.flink.streaming.api.operators.util.SimpleVersionedListState;
import org.apache.flink.util.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;

public class CheckpointSourceRead extends BaseCheckpointRead {
    public static final String STATE_NAME = "SourceReaderState";
    public static final Class<?> SERIALIZER_CLASS = BytePrimitiveArraySerializer.class;
    private static final Map<
                    Supplier<Class<? extends SimpleVersionedSerializer<?>>>,
                    Function<List, ? extends BaseCheckpointSource<?>>>
            MAPPING = new LinkedHashMap<>();

    static {
        ClassUtil.scanPackageBySuper("org.dinky.flink.checkpoint.source", BaseCheckpointSource.class).stream()
                .filter(x -> x.getAnnotation(SupportSplitSerializer.class) != null)
                .sorted((x, y) -> {
                    SupportSplitSerializer x1 = x.getAnnotation(SupportSplitSerializer.class);
                    SupportSplitSerializer y1 = y.getAnnotation(SupportSplitSerializer.class);
                    return x1.order() - y1.order();
                })
                .forEach(x -> {
                    SupportSplitSerializer supportSplitSerializer = x.getAnnotation(SupportSplitSerializer.class);
                    if (supportSplitSerializer != null) {
                        MAPPING.put(supportSplitSerializer::clazz, d ->
                                (BaseCheckpointSource<?>) ReflectUtil.newInstance(x, d));
                    }
                });
    }

    public Optional<CheckPointReadTable> create(PartitionableListState<?> partitionableListState) {
        for (Map.Entry<
                        Supplier<Class<? extends SimpleVersionedSerializer<?>>>,
                        Function<List, ? extends BaseCheckpointSource<?>>>
                entry : MAPPING.entrySet()) {
            try {
                List list = CollectionUtil.iterableToList(new SimpleVersionedListState(
                                partitionableListState,
                                Singleton.get(entry.getKey().get()))
                        .get());
                BaseCheckpointSource apply = entry.getValue().apply(list);
                return Optional.of(new CheckPointReadTable(apply.headers(), apply.datas()));
            } catch (Exception e) {
                e.printStackTrace();
            } catch (NoClassDefFoundError ignored) {
            }
        }
        return Optional.empty();
    }

    public boolean isSourceCkp(PartitionableListState<?> partitionableListState) {
        return partitionableListState.getStateMetaInfo().getName().equals(STATE_NAME)
                && getArrayListSerializer(partitionableListState)
                        .getElementSerializer()
                        .getClass()
                        .equals(SERIALIZER_CLASS);
    }
}
