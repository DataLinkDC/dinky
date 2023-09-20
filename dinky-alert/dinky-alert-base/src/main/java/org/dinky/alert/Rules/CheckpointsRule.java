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

package org.dinky.alert.Rules;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CheckpointsRule {

    private final LoadingCache<String, JsonNode> checkpointsCache;

    /**
     * Constructor for initializing the CheckpointsRule object.
     */
    public CheckpointsRule() {
        checkpointsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(CacheLoader.from(key -> null));
    }

    /**
     * Checks if a checkpoint has expired.
     * @param latest The latest checkpoint node.
     * @param key The key used to identify the checkpoint.
     * @param ckKey The checkpoint key to check for expiration.
     * @return True if the checkpoint has expired, false otherwise.
     */
    private boolean isExpire(JsonNode latest, String key, String ckKey) {
        JsonNode his = checkpointsCache.getIfPresent(key);
        if (latest.get(ckKey) == null || !latest.get(ckKey).has("trigger_timestamp")) {
            return true;
        }
        long latestTime = latest.get(ckKey).get("trigger_timestamp").asLong(-1);
        checkpointsCache.put(key, latest);
        if (his != null) {
            long hisTime = his.get(ckKey).get("trigger_timestamp").asLong(-1);
            return hisTime == latestTime || System.currentTimeMillis() - latestTime > 60000;
        }

        return false;
    }

    /**
     * Retrieves the checkpoint time for a specific key.
     * @param key The key used to identify the checkpoint.
     * @param checkpoints The checkpoints object containing relevant data.
     * @return The checkpoint time, or null if the checkpoint has expired.
     */
    public Long checkpointTime(String key, ObjectNode checkpoints) {
        JsonNode latest = checkpoints.get("latest");
        if (isExpire(latest, key, "completed")) {
            return null;
        }
        return latest.get("completed").get("end_to_end_duration").asLong(-1);
    }

    /**
     * Checks if a specific checkpoint has failed.
     * @param key The key used to identify the checkpoint.
     * @param checkpoints The checkpoints object containing relevant data.
     * @return True if the checkpoint has failed, null if it has expired.
     */
    public Boolean checkFailed(String key, ObjectNode checkpoints) {
        JsonNode latest = checkpoints.get("latest");
        if (isExpire(latest, key, "failed")) {
            return null;
        }
        return true;
    }
}
