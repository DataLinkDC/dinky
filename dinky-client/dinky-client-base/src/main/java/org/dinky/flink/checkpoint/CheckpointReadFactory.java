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

package org.dinky.flink.checkpoint;

import org.dinky.data.model.CheckPointReadTable;
import org.dinky.flink.checkpoint.base.BaseTypeCheckpointRead;
import org.dinky.flink.checkpoint.pojo.PojoTypeCheckpointRead;
import org.dinky.flink.checkpoint.source.CheckpointSourceRead;

import org.apache.flink.runtime.state.PartitionableListState;

import java.util.List;
import java.util.Optional;

import cn.hutool.core.collection.CollUtil;

public class CheckpointReadFactory {
    public static final List<? extends BaseCheckpointRead> FACTORY_LIST = CollUtil.newArrayList(
            new BaseTypeCheckpointRead(), new CheckpointSourceRead(), new PojoTypeCheckpointRead());

    public static Optional<CheckPointReadTable> getTable(PartitionableListState<?> partitionableListState) {
        Iterable<?> objects = partitionableListState.get();
        boolean empty = CollUtil.isEmpty(objects);
        if (empty) {
            return Optional.empty();
        }
        for (BaseCheckpointRead baseCheckpointRead : FACTORY_LIST) {
            if (baseCheckpointRead.isSourceCkp(partitionableListState)) {
                return baseCheckpointRead.create(partitionableListState);
            }
        }
        return Optional.empty();
    }
}
