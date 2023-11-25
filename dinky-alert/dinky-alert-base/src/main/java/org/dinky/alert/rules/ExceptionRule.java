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

import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ExceptionRule {

    private static final LoadingCache<String, Long> hisTime =
            CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(CacheLoader.from(key -> null));

    /**
     * Executes a certain operation based on the provided key and exceptions object.
     * This method is stored within the database, is called through SPEL, and is not an executable method
     * @param jobinstanceId The key used to identify the operation.
     * @param exceptions The exceptions object containing relevant data.
     * @return True if the operation should be executed, false otherwise.
     */
    public static Boolean isException(String jobinstanceId, FlinkJobExceptionsDetail exceptions) {

        // If the exception is the same as the previous one, it will not be reported again
        if (exceptions.getTimestamp() == null) {
            return false;
        }
        long timestamp = exceptions.getTimestamp();
        Long hisTimeIfPresent = hisTime.getIfPresent(jobinstanceId);
        if (hisTimeIfPresent != null && hisTimeIfPresent == timestamp) {
            return false;
        }
        hisTime.put(jobinstanceId, timestamp);
        if (exceptions.getRootException() != null) {
            return !exceptions.getRootException().isEmpty();
        } else {
            return false;
        }
    }
}
