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

import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.StateObjectCollection;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.OperatorBackendSerializationProxy;
import org.apache.flink.runtime.state.OperatorStateHandle;
import org.apache.flink.runtime.state.PartitionableListState;
import org.apache.flink.runtime.state.RegisteredOperatorStateBackendMetaInfo;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.metainfo.StateMetaInfoSnapshot;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.runtime.metadata.SavepointMetadata;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;

public class CheckpointRead implements CheckpointReadInterface {
    @Override
    public Map<String, Map<String, CheckPointReadTable>> readCheckpoint(String path, String operatorId) {
        ClassLoader restoreClassLoader = Thread.currentThread().getContextClassLoader();
        Map<String, Map<String, CheckPointReadTable>> result = new LinkedHashMap<>();
        try {
            ExistingSavepoint savepoint =
                    Savepoint.load(ExecutionEnvironment.getExecutionEnvironment(), path, new HashMapStateBackend());
            List<OperatorState> operatorStateList =
                    ((SavepointMetadata) ReflectUtil.getFieldValue(savepoint, "metadata")).getExistingOperators();
            OperatorState existingOperator = operatorStateList.stream()
                    .filter(operatorState -> operatorState
                            .getOperatorID()
                            .equals(OperatorID.fromJobVertexID(JobVertexID.fromHexString(operatorId))))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("The corresponding operator ID was not found"));
            Map<Integer, OperatorSubtaskState> subtaskStates = existingOperator.getSubtaskStates();
            if (CollUtil.isNotEmpty(subtaskStates)) {
                subtaskStates.forEach((k, v) -> {
                    StateObjectCollection<OperatorStateHandle> managedOperatorState = v.getManagedOperatorState();
                    Map<String, CheckPointReadTable> read = readState(restoreClassLoader, managedOperatorState);
                    result.put("managedOperatorState", read);
                });
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, CheckPointReadTable> readState(
            ClassLoader restoreClassLoader, StateObjectCollection<OperatorStateHandle> managedOperatorState) {
        Map<String, CheckPointReadTable> map = new LinkedHashMap<>();
        OperatorBackendSerializationProxy backendSerializationProxy =
                new OperatorBackendSerializationProxy(restoreClassLoader);
        boolean isRead = false;
        for (OperatorStateHandle operatorStateHandle : managedOperatorState) {
            try (FSDataInputStream in =
                    operatorStateHandle.getDelegateStateHandle().openInputStream()) {
                if (!isRead) {
                    backendSerializationProxy.read(new DataInputViewStreamWrapper(in));
                    isRead = true;
                }

                operatorStateHandle.getStateNameToPartitionOffsets().forEach((key, value) -> {
                    try {
                        List<StateMetaInfoSnapshot> restoredOperatorMetaInfoSnapshots =
                                backendSerializationProxy.getOperatorStateMetaInfoSnapshots();
                        for (StateMetaInfoSnapshot stateMetaInfoSnapshot : restoredOperatorMetaInfoSnapshots) {
                            String name = stateMetaInfoSnapshot.getName();
                            if (!name.equals(key)) {
                                continue;
                            }
                            TypeSerializerSnapshot<?> valueSerializer = stateMetaInfoSnapshot
                                    .getSerializerSnapshotsImmutable()
                                    .get("VALUE_SERIALIZER");

                            PartitionableListState<?> partitionableListState = ReflectUtil.newInstance(
                                    PartitionableListState.class,
                                    new RegisteredOperatorStateBackendMetaInfo<>(stateMetaInfoSnapshot));
                            ;
                            deserializeOperatorStateValues(partitionableListState, in, value);
                            // get checkpoint data
                            CheckpointReadFactory.getTable(partitionableListState)
                                    .ifPresent(tableVO -> map.put(key, tableVO));
                            break;
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    protected static <S> void deserializeOperatorStateValues(
            PartitionableListState<S> stateListForName,
            FSDataInputStream in,
            OperatorStateHandle.StateMetaInfo metaInfo)
            throws IOException {

        if (null != metaInfo) {
            long[] offsets = metaInfo.getOffsets();
            if (null != offsets) {
                DataInputView div = new DataInputViewStreamWrapper(in);
                TypeSerializer<S> serializer =
                        stateListForName.getStateMetaInfo().getPartitionStateSerializer();
                for (long offset : offsets) {
                    in.seek(offset);
                    stateListForName.add(serializer.deserialize(div));
                }
            }
        }
    }
}
