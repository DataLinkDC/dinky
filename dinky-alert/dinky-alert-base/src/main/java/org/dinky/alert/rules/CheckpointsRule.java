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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckpointsRule {

    private static final Logger logger = LoggerFactory.getLogger(CheckpointsRule.class);

    private static final LoadingCache<String, Object> checkpointsCache =
            CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(CacheLoader.from(key -> null));

    /**
     * Checks if a checkpoint has expired.
     *
     * @param latest The latest checkpoint node.
     * @param jobInstanceID    The key used to identify the checkpoint.
     * @param ckKey  The checkpoint key to check for expiration.
     * @return True if the checkpoint has expired, false otherwise.
     */
    private static boolean isExpire(CheckPointOverView latest, String jobInstanceID, String ckKey) {
        logger.debug("checkpointTime key: {} ,checkpoints: {}, key: {}", jobInstanceID, latest, ckKey);

        CheckPointOverView his = (CheckPointOverView) checkpointsCache.getIfPresent(jobInstanceID);

        switch (ckKey) {
            case "completed":
                if (his != null) {
                    CheckPointOverView.CompletedCheckpointStatistics completedCheckpointStatistics =
                            his.getLatestCheckpoints().getCompletedCheckpointStatistics();
                    if (completedCheckpointStatistics != null) {
                        return Objects.equals(completedCheckpointStatistics.getStatus(), "completed");
                    }
                }
                return false;
            case "failed":
                CheckPointOverView.FailedCheckpointStatistics failedCheckpointStatistics = null;
                if (his != null) {
                    failedCheckpointStatistics = his.getLatestCheckpoints().getFailedCheckpointStatistics();
                }
                long failureTimestamp = 0;
                CheckPointOverView.LatestCheckpoints latestLatestCheckpoints = latest.getLatestCheckpoints();
                if (latestLatestCheckpoints != null
                        && latestLatestCheckpoints.getFailedCheckpointStatistics() != null) {
                    failureTimestamp = latestLatestCheckpoints
                            .getFailedCheckpointStatistics()
                            .getTriggerTimestamp();
                }
                if (null == latestLatestCheckpoints || 0 == failureTimestamp) {
                    return true;
                }
                long latestTime =
                        latestLatestCheckpoints.getFailedCheckpointStatistics().getTriggerTimestamp();
                checkpointsCache.put(jobInstanceID, latest);
                if (his != null) {
                    long hisTime = 0;
                    if (failedCheckpointStatistics != null) {
                        hisTime = failedCheckpointStatistics.getTriggerTimestamp();
                    }
                    return hisTime == latestTime || System.currentTimeMillis() - latestTime > 60000;
                }
                return false;

            default:
                return false;
        }
    }

    /**
     * Retrieves the checkpoint time for a specific key.
     *
     * @param key         The key used to identify the checkpoint.
     * @param checkpoints The checkpoints object containing relevant data.
     * @return The checkpoint time, or null if the checkpoint has expired.
     */
    public static Long checkpointTime(String key, CheckPointOverView checkpoints) {
        if (isExpire(checkpoints, key, "completed")) {
            return -1L;
        }
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
     * @param key         The key used to identify the checkpoint.
     * @param checkpoints The checkpoints object containing relevant data.
     * @return True if the checkpoint has failed, null if it has expired.
     */
    public static Boolean checkFailed(String key, CheckPointOverView checkpoints) {
        return !isExpire(checkpoints, key, "failed");
    }
}
