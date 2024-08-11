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

package org.dinky.data.result;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * ResultPool
 *
 * @since 2021/7/1 22:20
 */
@Slf4j
public final class ResultPool {

    private ResultPool() {}

    private static final Map<String, SelectResult> RESULTS = Maps.newConcurrentMap();

    public static boolean containsKey(String key) {
        return RESULTS.containsKey(key);
    }

    public static void put(SelectResult result) {
        RESULTS.put(result.getJobId(), result);
        log.info("Put job result into cache. Job id: {}", result.getJobId());
        log.info("Number of results in the running: {}", RESULTS.size());
    }

    public static SelectResult get(String key) {
        SelectResult selectResult = RESULTS.get(key);
        if (Objects.nonNull(selectResult)) {
            return selectResult;
        }
        return SelectResult.buildDestruction(key);
    }

    public static boolean remove(String key) {
        log.info("Remove job result from cache. Job id: {}", key);
        if (RESULTS.containsKey(key)) {
            RESULTS.remove(key);
            return true;
        }
        return false;
    }

    public static void clear() {
        RESULTS.clear();
    }

    public static List<String> getJobIds() {
        return Lists.newArrayList(RESULTS.keySet());
    }
}
