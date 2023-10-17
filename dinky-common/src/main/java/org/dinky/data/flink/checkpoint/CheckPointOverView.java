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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckPointOverView {
    public static final String FIELD_NAME_COUNTS = "counts";

    public static final String FIELD_NAME_SUMMARY = "summary";

    public static final String FIELD_NAME_LATEST_CHECKPOINTS = "latest";

    public static final String FIELD_NAME_HISTORY = "history";

    @JsonProperty(FIELD_NAME_COUNTS)
    private Counts counts;

    @JsonProperty(FIELD_NAME_SUMMARY)
    private Summary summary;

    @JsonProperty(FIELD_NAME_LATEST_CHECKPOINTS)
    private LatestCheckpoints latestCheckpoints;

    @JsonProperty(FIELD_NAME_HISTORY)
    private List<CheckpointStatistics> history;

    @JsonCreator
    public CheckPointOverView(
            @JsonProperty(FIELD_NAME_COUNTS) Counts counts,
            @JsonProperty(FIELD_NAME_SUMMARY) Summary summary,
            @JsonProperty(FIELD_NAME_LATEST_CHECKPOINTS) LatestCheckpoints latestCheckpoints,
            @JsonProperty(FIELD_NAME_HISTORY) List<CheckpointStatistics> history) {
        this.counts = Preconditions.checkNotNull(counts);
        this.summary = Preconditions.checkNotNull(summary);
        this.latestCheckpoints = Preconditions.checkNotNull(latestCheckpoints);
        this.history = Preconditions.checkNotNull(history);
    }

    @Data
    @NoArgsConstructor
    public static final class Counts {

        public static final String FIELD_NAME_RESTORED_CHECKPOINTS = "restored";

        public static final String FIELD_NAME_TOTAL_CHECKPOINTS = "total";

        public static final String FIELD_NAME_IN_PROGRESS_CHECKPOINTS = "in_progress";

        public static final String FIELD_NAME_COMPLETED_CHECKPOINTS = "completed";

        public static final String FIELD_NAME_FAILED_CHECKPOINTS = "failed";

        @JsonProperty(FIELD_NAME_RESTORED_CHECKPOINTS)
        private long numberRestoredCheckpoints;

        @JsonProperty(FIELD_NAME_TOTAL_CHECKPOINTS)
        private long totalNumberCheckpoints;

        @JsonProperty(FIELD_NAME_IN_PROGRESS_CHECKPOINTS)
        private int numberInProgressCheckpoints;

        @JsonProperty(FIELD_NAME_COMPLETED_CHECKPOINTS)
        private long numberCompletedCheckpoints;

        @JsonProperty(FIELD_NAME_FAILED_CHECKPOINTS)
        private long numberFailedCheckpoints;

        @JsonCreator
        public Counts(
                @JsonProperty(FIELD_NAME_RESTORED_CHECKPOINTS) long numberRestoredCheckpoints,
                @JsonProperty(FIELD_NAME_TOTAL_CHECKPOINTS) long totalNumberCheckpoints,
                @JsonProperty(FIELD_NAME_IN_PROGRESS_CHECKPOINTS) int numberInProgressCheckpoints,
                @JsonProperty(FIELD_NAME_COMPLETED_CHECKPOINTS) long numberCompletedCheckpoints,
                @JsonProperty(FIELD_NAME_FAILED_CHECKPOINTS) long numberFailedCheckpoints) {
            this.numberRestoredCheckpoints = numberRestoredCheckpoints;
            this.totalNumberCheckpoints = totalNumberCheckpoints;
            this.numberInProgressCheckpoints = numberInProgressCheckpoints;
            this.numberCompletedCheckpoints = numberCompletedCheckpoints;
            this.numberFailedCheckpoints = numberFailedCheckpoints;
        }
    }

    @Data
    @NoArgsConstructor
    public final class StatsSummaryDto {

        public static final String FIELD_NAME_MINIMUM = "min";

        public static final String FIELD_NAME_MAXIMUM = "max";

        public static final String FIELD_NAME_AVERAGE = "avg";

        public static final String FIELD_NAME_P50 = "p50";

        public static final String FIELD_NAME_P90 = "p90";

        public static final String FIELD_NAME_P95 = "p95";

        public static final String FIELD_NAME_P99 = "p99";

        public static final String FIELD_NAME_P999 = "p999";

        @JsonProperty(FIELD_NAME_MINIMUM)
        private long minimum;

        @JsonProperty(FIELD_NAME_MAXIMUM)
        private long maximum;

        @JsonProperty(FIELD_NAME_AVERAGE)
        private long average;

        @JsonProperty(FIELD_NAME_P50)
        private String p50;

        @JsonProperty(FIELD_NAME_P90)
        private String p90;

        @JsonProperty(FIELD_NAME_P95)
        private String p95;

        @JsonProperty(FIELD_NAME_P99)
        private String p99;

        @JsonProperty(FIELD_NAME_P999)
        private String p999;

        @JsonCreator
        public StatsSummaryDto(
                @JsonProperty(FIELD_NAME_MINIMUM) long minimum,
                @JsonProperty(FIELD_NAME_MAXIMUM) long maximum,
                @JsonProperty(FIELD_NAME_AVERAGE) long average,
                @JsonProperty(FIELD_NAME_P50) String p50,
                @JsonProperty(FIELD_NAME_P90) String p90,
                @JsonProperty(FIELD_NAME_P95) String p95,
                @JsonProperty(FIELD_NAME_P99) String p99,
                @JsonProperty(FIELD_NAME_P999) String p999) {
            this.minimum = minimum;
            this.maximum = maximum;
            this.average = average;
            this.p50 = p50;
            this.p90 = p90;
            this.p95 = p95;
            this.p99 = p99;
            this.p999 = p999;
        }
    }

    /**
     * Checkpoint summary.
     */
    @Data
    @NoArgsConstructor
    public static final class Summary {

        public static final String FIELD_NAME_CHECKPOINTED_SIZE = "checkpointed_size";

        /**
         * The accurate name of this field should be 'checkpointed_data_size', keep it as before to
         * not break backwards compatibility for old web UI.
         *
         * @see <a href="https://issues.apache.org/jira/browse/FLINK-13390">FLINK-13390</a>
         */
        public static final String FIELD_NAME_STATE_SIZE = "state_size";

        public static final String FIELD_NAME_DURATION = "end_to_end_duration";

        public static final String FIELD_NAME_ALIGNMENT_BUFFERED = "alignment_buffered";

        public static final String FIELD_NAME_PROCESSED_DATA = "processed_data";

        public static final String FIELD_NAME_PERSISTED_DATA = "persisted_data";

        @JsonProperty(FIELD_NAME_CHECKPOINTED_SIZE)
        private StatsSummaryDto checkpointedSize;

        @JsonProperty(FIELD_NAME_STATE_SIZE)
        private StatsSummaryDto stateSize;

        @JsonProperty(FIELD_NAME_DURATION)
        private StatsSummaryDto duration;

        @JsonProperty(FIELD_NAME_ALIGNMENT_BUFFERED)
        private StatsSummaryDto alignmentBuffered;

        @JsonProperty(FIELD_NAME_PROCESSED_DATA)
        private StatsSummaryDto processedData;

        @JsonProperty(FIELD_NAME_PERSISTED_DATA)
        private StatsSummaryDto persistedData;

        @JsonCreator
        public Summary(
                @JsonProperty(FIELD_NAME_CHECKPOINTED_SIZE) StatsSummaryDto checkpointedSize,
                @JsonProperty(FIELD_NAME_STATE_SIZE) StatsSummaryDto stateSize,
                @JsonProperty(FIELD_NAME_DURATION) StatsSummaryDto duration,
                @JsonProperty(FIELD_NAME_ALIGNMENT_BUFFERED) StatsSummaryDto alignmentBuffered,
                @JsonProperty(FIELD_NAME_PROCESSED_DATA) StatsSummaryDto processedData,
                @JsonProperty(FIELD_NAME_PERSISTED_DATA) StatsSummaryDto persistedData) {
            this.checkpointedSize = checkpointedSize;
            this.stateSize = stateSize;
            this.duration = duration;
            this.alignmentBuffered = alignmentBuffered;
            this.processedData = processedData;
            this.persistedData = persistedData;
        }
    }

    /**
     * Statistics for a completed checkpoint.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static final class CompletedCheckpointStatistics extends CheckpointStatistics {

        public static final String FIELD_NAME_EXTERNAL_PATH = "external_path";

        public static final String FIELD_NAME_DISCARDED = "discarded";

        @JsonProperty(FIELD_NAME_EXTERNAL_PATH)
        private String externalPath;

        @JsonProperty(FIELD_NAME_DISCARDED)
        private boolean discarded;

        @JsonCreator
        public CompletedCheckpointStatistics(
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
                @JsonProperty(FIELD_NAME_TASKS) Map<String, TaskCheckpointStatistics> checkpointingStatisticsPerTask,
                @JsonProperty(FIELD_NAME_EXTERNAL_PATH) String externalPath,
                @JsonProperty(FIELD_NAME_DISCARDED) boolean discarded) {
            super(
                    id,
                    status,
                    savepoint,
                    triggerTimestamp,
                    latestAckTimestamp,
                    checkpointedSize,
                    stateSize,
                    duration,
                    alignmentBuffered,
                    processedData,
                    persistedData,
                    numSubtasks,
                    numAckSubtasks,
                    checkpointType,
                    checkpointingStatisticsPerTask);
            this.externalPath = externalPath;
            this.discarded = discarded;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static final class FailedCheckpointStatistics extends CheckpointStatistics {

        public static final String FIELD_NAME_FAILURE_TIMESTAMP = "failure_timestamp";

        public static final String FIELD_NAME_FAILURE_MESSAGE = "failure_message";

        @JsonProperty(FIELD_NAME_FAILURE_TIMESTAMP)
        private long failureTimestamp;

        @JsonProperty(FIELD_NAME_FAILURE_MESSAGE)
        private String failureMessage;

        @JsonCreator
        public FailedCheckpointStatistics(
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
                @JsonProperty(FIELD_NAME_TASKS) Map<String, TaskCheckpointStatistics> checkpointingStatisticsPerTask,
                @JsonProperty(FIELD_NAME_FAILURE_TIMESTAMP) long failureTimestamp,
                @JsonProperty(FIELD_NAME_FAILURE_MESSAGE) String failureMessage) {
            super(
                    id,
                    status,
                    savepoint,
                    triggerTimestamp,
                    latestAckTimestamp,
                    checkpointedSize,
                    stateSize,
                    duration,
                    alignmentBuffered,
                    processedData,
                    persistedData,
                    numSubtasks,
                    numAckSubtasks,
                    checkpointType,
                    checkpointingStatisticsPerTask);

            this.failureTimestamp = failureTimestamp;
            this.failureMessage = failureMessage;
        }
    }

    /**
     * Statistics about the latest checkpoints.
     */
    @Data
    @NoArgsConstructor
    public static final class LatestCheckpoints {

        public static final String FIELD_NAME_COMPLETED = "completed";

        public static final String FIELD_NAME_SAVEPOINT = "savepoint";

        public static final String FIELD_NAME_FAILED = "failed";

        public static final String FIELD_NAME_RESTORED = "restored";

        @JsonProperty(FIELD_NAME_COMPLETED)
        private CheckPointOverView.CompletedCheckpointStatistics completedCheckpointStatistics;

        @JsonProperty(FIELD_NAME_SAVEPOINT)
        private CheckPointOverView.CompletedCheckpointStatistics savepointStatistics;

        @JsonProperty(FIELD_NAME_FAILED)
        private CheckPointOverView.FailedCheckpointStatistics failedCheckpointStatistics;

        @JsonProperty(FIELD_NAME_RESTORED)
        private RestoredCheckpointStatistics restoredCheckpointStatistics;

        @JsonCreator
        public LatestCheckpoints(
                @JsonProperty(FIELD_NAME_COMPLETED)
                        CheckPointOverView.CompletedCheckpointStatistics completedCheckpointStatistics,
                @JsonProperty(FIELD_NAME_SAVEPOINT)
                        CheckPointOverView.CompletedCheckpointStatistics savepointStatistics,
                @JsonProperty(FIELD_NAME_FAILED)
                        CheckPointOverView.FailedCheckpointStatistics failedCheckpointStatistics,
                @JsonProperty(FIELD_NAME_RESTORED) RestoredCheckpointStatistics restoredCheckpointStatistics) {
            this.completedCheckpointStatistics = completedCheckpointStatistics;
            this.savepointStatistics = savepointStatistics;
            this.failedCheckpointStatistics = failedCheckpointStatistics;
            this.restoredCheckpointStatistics = restoredCheckpointStatistics;
        }
    }

    /**
     * Statistics for a restored checkpoint.
     */
    @Data
    @NoArgsConstructor
    public static final class RestoredCheckpointStatistics {

        public static final String FIELD_NAME_ID = "id";

        public static final String FIELD_NAME_RESTORE_TIMESTAMP = "restore_timestamp";

        public static final String FIELD_NAME_IS_SAVEPOINT = "is_savepoint";

        public static final String FIELD_NAME_EXTERNAL_PATH = "external_path";

        @JsonProperty(FIELD_NAME_ID)
        private long id;

        @JsonProperty(FIELD_NAME_RESTORE_TIMESTAMP)
        private long restoreTimestamp;

        @JsonProperty(FIELD_NAME_IS_SAVEPOINT)
        private boolean savepoint;

        @JsonProperty(FIELD_NAME_EXTERNAL_PATH)
        private String externalPath;

        @JsonCreator
        public RestoredCheckpointStatistics(
                @JsonProperty(FIELD_NAME_ID) long id,
                @JsonProperty(FIELD_NAME_RESTORE_TIMESTAMP) long restoreTimestamp,
                @JsonProperty(FIELD_NAME_IS_SAVEPOINT) boolean savepoint,
                @JsonProperty(FIELD_NAME_EXTERNAL_PATH) String externalPath) {
            this.id = id;
            this.restoreTimestamp = restoreTimestamp;
            this.savepoint = savepoint;
            this.externalPath = externalPath;
        }
    }
}
