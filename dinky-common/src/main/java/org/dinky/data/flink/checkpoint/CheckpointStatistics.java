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

package org.dinky.data.flink.checkpoint;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckpointStatistics {

    public static final String FIELD_NAME_ID = "id";

    public static final String FIELD_NAME_STATUS = "status";

    public static final String FIELD_NAME_IS_SAVEPOINT = "is_savepoint";

    public static final String FIELD_NAME_TRIGGER_TIMESTAMP = "trigger_timestamp";

    public static final String FIELD_NAME_LATEST_ACK_TIMESTAMP = "latest_ack_timestamp";

    public static final String FIELD_NAME_CHECKPOINTED_SIZE = "checkpointed_size";

    /**
     * The accurate name of this field should be 'checkpointed_data_size', keep it as before to not
     * break backwards compatibility for old web UI.
     *
     * @see <a href="https://issues.apache.org/jira/browse/FLINK-13390">FLINK-13390</a>
     */
    public static final String FIELD_NAME_STATE_SIZE = "state_size";

    public static final String FIELD_NAME_DURATION = "end_to_end_duration";

    public static final String FIELD_NAME_ALIGNMENT_BUFFERED = "alignment_buffered";

    public static final String FIELD_NAME_PROCESSED_DATA = "processed_data";

    public static final String FIELD_NAME_PERSISTED_DATA = "persisted_data";

    public static final String FIELD_NAME_NUM_SUBTASKS = "num_subtasks";

    public static final String FIELD_NAME_NUM_ACK_SUBTASKS = "num_acknowledged_subtasks";

    public static final String FIELD_NAME_TASKS = "tasks";

    public static final String FIELD_NAME_CHECKPOINT_TYPE = "checkpoint_type";

    @JsonProperty(FIELD_NAME_ID)
    private long id;

    @JsonProperty(FIELD_NAME_STATUS)
    private String status;

    @JsonProperty(FIELD_NAME_IS_SAVEPOINT)
    private boolean savepoint;

    @JsonProperty(FIELD_NAME_TRIGGER_TIMESTAMP)
    private long triggerTimestamp;

    @JsonProperty(FIELD_NAME_LATEST_ACK_TIMESTAMP)
    private long latestAckTimestamp;

    @JsonProperty(FIELD_NAME_CHECKPOINTED_SIZE)
    private long checkpointedSize;

    @JsonProperty(FIELD_NAME_STATE_SIZE)
    private long stateSize;

    @JsonProperty(FIELD_NAME_DURATION)
    private long duration;

    @JsonProperty(FIELD_NAME_ALIGNMENT_BUFFERED)
    private long alignmentBuffered;

    @JsonProperty(FIELD_NAME_PROCESSED_DATA)
    private long processedData;

    @JsonProperty(FIELD_NAME_PERSISTED_DATA)
    private long persistedData;

    @JsonProperty(FIELD_NAME_NUM_SUBTASKS)
    private int numSubtasks;

    @JsonProperty(FIELD_NAME_NUM_ACK_SUBTASKS)
    private int numAckSubtasks;

    @JsonProperty(FIELD_NAME_CHECKPOINT_TYPE)
    private String checkpointType;

    @JsonProperty(FIELD_NAME_TASKS)
    private Map<String, TaskCheckpointStatistics> checkpointStatisticsPerTask;

    @JsonCreator
    CheckpointStatistics(
            @JsonProperty(FIELD_NAME_ID) long id,
            @JsonProperty(FIELD_NAME_STATUS) String status,
            @JsonProperty(FIELD_NAME_IS_SAVEPOINT) boolean savepoint,
            @JsonProperty(FIELD_NAME_TRIGGER_TIMESTAMP) long triggerTimestamp,
            @JsonProperty(FIELD_NAME_LATEST_ACK_TIMESTAMP) long latestAckTimestamp,
            @JsonProperty(FIELD_NAME_CHECKPOINTED_SIZE) long checkpointedSize,
            @JsonProperty(FIELD_NAME_STATE_SIZE) long stateSize,
            @JsonProperty(FIELD_NAME_DURATION) long duration,
            @JsonProperty(FIELD_NAME_ALIGNMENT_BUFFERED) long alignmentBuffered,
            @JsonProperty(FIELD_NAME_PROCESSED_DATA) long processedData,
            @JsonProperty(FIELD_NAME_PERSISTED_DATA) long persistedData,
            @JsonProperty(FIELD_NAME_NUM_SUBTASKS) int numSubtasks,
            @JsonProperty(FIELD_NAME_NUM_ACK_SUBTASKS) int numAckSubtasks,
            @JsonProperty(FIELD_NAME_CHECKPOINT_TYPE) String checkpointType,
            @JsonProperty(FIELD_NAME_TASKS) Map<String, TaskCheckpointStatistics> checkpointStatisticsPerTask) {
        this.id = id;
        this.status = Preconditions.checkNotNull(status);
        this.savepoint = savepoint;
        this.triggerTimestamp = triggerTimestamp;
        this.latestAckTimestamp = latestAckTimestamp;
        this.checkpointedSize = checkpointedSize;
        this.stateSize = stateSize;
        this.duration = duration;
        this.alignmentBuffered = alignmentBuffered;
        this.processedData = processedData;
        this.persistedData = persistedData;
        this.numSubtasks = numSubtasks;
        this.numAckSubtasks = numAckSubtasks;
        this.checkpointType = Preconditions.checkNotNull(checkpointType);
        this.checkpointStatisticsPerTask = Preconditions.checkNotNull(checkpointStatisticsPerTask);
    }
}
