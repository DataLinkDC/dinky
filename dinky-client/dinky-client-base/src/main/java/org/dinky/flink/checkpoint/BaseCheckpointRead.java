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

import org.apache.flink.runtime.state.ArrayListSerializer;
import org.apache.flink.runtime.state.PartitionableListState;

import java.util.Optional;

import cn.hutool.core.util.ReflectUtil;

public abstract class BaseCheckpointRead {
    protected BaseCheckpointRead() {}

    public abstract boolean isSourceCkp(PartitionableListState<?> partitionableListState);

    public abstract Optional<CheckPointReadTable> create(PartitionableListState<?> partitionableListState);

    protected static ArrayListSerializer<?> getArrayListSerializer(PartitionableListState<?> partitionableListState) {
        return (ArrayListSerializer<?>) ReflectUtil.getFieldValue(partitionableListState, "internalListCopySerializer");
    }
}
