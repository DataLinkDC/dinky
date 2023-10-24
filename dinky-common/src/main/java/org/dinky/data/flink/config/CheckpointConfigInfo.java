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

package org.dinky.data.flink.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckpointConfigInfo {

    public static final String FIELD_NAME_PROCESSING_MODE = "mode";

    public static final String FIELD_NAME_CHECKPOINT_INTERVAL = "interval";

    public static final String FIELD_NAME_CHECKPOINT_TIMEOUT = "timeout";

    public static final String FIELD_NAME_CHECKPOINT_MIN_PAUSE = "min_pause";

    public static final String FIELD_NAME_CHECKPOINT_MAX_CONCURRENT = "max_concurrent";

    public static final String FIELD_NAME_EXTERNALIZED_CHECKPOINT_CONFIG = "externalization";

    public static final String FIELD_NAME_STATE_BACKEND = "state_backend";

    public static final String FIELD_NAME_CHECKPOINT_STORAGE = "checkpoint_storage";

    public static final String FIELD_NAME_UNALIGNED_CHECKPOINTS = "unaligned_checkpoints";

    public static final String FIELD_NAME_TOLERABLE_FAILED_CHECKPOINTS = "tolerable_failed_checkpoints";

    public static final String FIELD_NAME_ALIGNED_CHECKPOINT_TIMEOUT = "aligned_checkpoint_timeout";

    public static final String FIELD_NAME_CHECKPOINTS_AFTER_TASKS_FINISH = "checkpoints_after_tasks_finish";

    public static final String FIELD_NAME_STATE_CHANGELOG = "state_changelog_enabled";

    public static final String FIELD_NAME_PERIODIC_MATERIALIZATION_INTERVAL =
            "changelog_periodic_materialization_interval";

    public static final String FIELD_NAME_CHANGELOG_STORAGE = "changelog_storage";

    @JsonProperty(FIELD_NAME_PROCESSING_MODE)
    private String processingMode;

    @JsonProperty(FIELD_NAME_CHECKPOINT_INTERVAL)
    private long checkpointInterval;

    @JsonProperty(FIELD_NAME_CHECKPOINT_TIMEOUT)
    private long checkpointTimeout;

    @JsonProperty(FIELD_NAME_CHECKPOINT_MIN_PAUSE)
    private long minPauseBetweenCheckpoints;

    @JsonProperty(FIELD_NAME_CHECKPOINT_MAX_CONCURRENT)
    private long maxConcurrentCheckpoints;

    @JsonProperty(FIELD_NAME_EXTERNALIZED_CHECKPOINT_CONFIG)
    private ExternalizedCheckpointInfo externalizedCheckpointInfo;

    @JsonProperty(FIELD_NAME_STATE_BACKEND)
    private String stateBackend;

    @JsonProperty(FIELD_NAME_CHECKPOINT_STORAGE)
    private String checkpointStorage;

    @JsonProperty(FIELD_NAME_UNALIGNED_CHECKPOINTS)
    private boolean unalignedCheckpoints;

    @JsonProperty(FIELD_NAME_TOLERABLE_FAILED_CHECKPOINTS)
    private int tolerableFailedCheckpoints;

    @JsonProperty(FIELD_NAME_ALIGNED_CHECKPOINT_TIMEOUT)
    private long alignedCheckpointTimeout;

    @JsonProperty(FIELD_NAME_CHECKPOINTS_AFTER_TASKS_FINISH)
    private boolean checkpointsWithFinishedTasks;

    @JsonProperty(FIELD_NAME_STATE_CHANGELOG)
    private boolean stateChangelog;

    @JsonProperty(FIELD_NAME_PERIODIC_MATERIALIZATION_INTERVAL)
    private long periodicMaterializationInterval;

    @JsonProperty(FIELD_NAME_CHANGELOG_STORAGE)
    private String changelogStorage;

    @JsonCreator
    public CheckpointConfigInfo(
            @JsonProperty(FIELD_NAME_PROCESSING_MODE) String processingMode,
            @JsonProperty(FIELD_NAME_CHECKPOINT_INTERVAL) long checkpointInterval,
            @JsonProperty(FIELD_NAME_CHECKPOINT_TIMEOUT) long checkpointTimeout,
            @JsonProperty(FIELD_NAME_CHECKPOINT_MIN_PAUSE) long minPauseBetweenCheckpoints,
            @JsonProperty(FIELD_NAME_CHECKPOINT_MAX_CONCURRENT) int maxConcurrentCheckpoints,
            @JsonProperty(FIELD_NAME_EXTERNALIZED_CHECKPOINT_CONFIG)
                    ExternalizedCheckpointInfo externalizedCheckpointInfo,
            @JsonProperty(FIELD_NAME_STATE_BACKEND) String stateBackend,
            @JsonProperty(FIELD_NAME_CHECKPOINT_STORAGE) String checkpointStorage,
            @JsonProperty(FIELD_NAME_UNALIGNED_CHECKPOINTS) boolean unalignedCheckpoints,
            @JsonProperty(FIELD_NAME_TOLERABLE_FAILED_CHECKPOINTS) int tolerableFailedCheckpoints,
            @JsonProperty(FIELD_NAME_ALIGNED_CHECKPOINT_TIMEOUT) long alignedCheckpointTimeout,
            @JsonProperty(FIELD_NAME_CHECKPOINTS_AFTER_TASKS_FINISH) boolean checkpointsWithFinishedTasks,
            @JsonProperty(FIELD_NAME_STATE_CHANGELOG) boolean stateChangelog,
            @JsonProperty(FIELD_NAME_PERIODIC_MATERIALIZATION_INTERVAL) long periodicMaterializationInterval,
            @JsonProperty(FIELD_NAME_CHANGELOG_STORAGE) String changelogStorage) {
        this.processingMode = Preconditions.checkNotNull(processingMode);
        this.checkpointInterval = checkpointInterval;
        this.checkpointTimeout = checkpointTimeout;
        this.minPauseBetweenCheckpoints = minPauseBetweenCheckpoints;
        this.maxConcurrentCheckpoints = maxConcurrentCheckpoints;
        this.externalizedCheckpointInfo = Preconditions.checkNotNull(externalizedCheckpointInfo);
        this.stateBackend = Preconditions.checkNotNull(stateBackend);
        this.checkpointStorage = Preconditions.checkNotNull(checkpointStorage);
        this.unalignedCheckpoints = unalignedCheckpoints;
        this.tolerableFailedCheckpoints = tolerableFailedCheckpoints;
        this.alignedCheckpointTimeout = alignedCheckpointTimeout;
        this.checkpointsWithFinishedTasks = checkpointsWithFinishedTasks;
        this.stateChangelog = stateChangelog;
        this.periodicMaterializationInterval = periodicMaterializationInterval;
        this.changelogStorage = changelogStorage;
    }

    /** Contains information about the externalized checkpoint configuration. */
    @Data
    @NoArgsConstructor
    public static final class ExternalizedCheckpointInfo {

        public static final String FIELD_NAME_ENABLED = "enabled";

        public static final String FIELD_NAME_DELETE_ON_CANCELLATION = "delete_on_cancellation";

        @JsonProperty(FIELD_NAME_ENABLED)
        private boolean enabled;

        @JsonProperty(FIELD_NAME_DELETE_ON_CANCELLATION)
        private boolean deleteOnCancellation;

        @JsonCreator
        public ExternalizedCheckpointInfo(
                @JsonProperty(FIELD_NAME_ENABLED) boolean enabled,
                @JsonProperty(FIELD_NAME_DELETE_ON_CANCELLATION) boolean deleteOnCancellation) {
            this.enabled = enabled;
            this.deleteOnCancellation = deleteOnCancellation;
        }
    }
}
