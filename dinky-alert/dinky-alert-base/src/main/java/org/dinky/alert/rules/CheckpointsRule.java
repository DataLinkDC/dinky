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

package org.dinky.alert.rules;

import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.utils.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckpointsRule {

    /**
     * Retrieves the checkpoint time for a specific key.
     *
     * @param checkpoints The checkpoints object containing relevant data.
     * @return The checkpoint time, or null if the checkpoint has expired.
     */
    public static Long checkpointTime(CheckPointOverView checkpoints) {

        CheckPointOverView.LatestCheckpoints checkpointsLatestCheckpoints = checkpoints.getLatestCheckpoints();
        if (null == checkpointsLatestCheckpoints
                || null == checkpointsLatestCheckpoints.getCompletedCheckpointStatistics()) {
            return -1L;
        }
        return checkpoints
                .getLatestCheckpoints()
                .getCompletedCheckpointStatistics()
                .getDuration();
    }

    /**
     * Checks if a specific checkpoint has failed.
     *
     * @param checkpoints The checkpoints object containing relevant data.
     * @return True if the checkpoint has failed, null if it has expired.
     */
    public static Boolean checkFailed(CheckPointOverView checkpoints) {
        CheckPointOverView.LatestCheckpoints latestLatestCheckpoints = checkpoints.getLatestCheckpoints();
        if (latestLatestCheckpoints != null && latestLatestCheckpoints.getFailedCheckpointStatistics() != null) {
            long failureTimestamp =
                    latestLatestCheckpoints.getFailedCheckpointStatistics().getTriggerTimestamp();
            LocalDateTime localDateTime = TimeUtil.toLocalDateTime(failureTimestamp);
            LocalDateTime now = LocalDateTime.now();
            long diff = Duration.between(localDateTime, now).toMinutes();
            return diff <= 2;
        }
        return false;
    }
}
