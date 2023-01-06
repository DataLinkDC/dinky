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

package com.dlink.result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ResultPool
 *
 * @author wenmo
 * @since 2021/7/1 22:20
 */
public final class ResultPool {

    private ResultPool() {
    }

    private static final Map<String, SelectResult> results = new ConcurrentHashMap<>();

    public static boolean containsKey(String key) {
        return results.containsKey(key);
    }

    public static void put(SelectResult result) {
        results.put(result.getJobId(), result);
    }

    public static SelectResult get(String key) {
        return results.getOrDefault(key, SelectResult.buildDestruction(key));
    }

    public static boolean remove(String key) {
        if (results.containsKey(key)) {
            results.remove(key);
            return true;
        }
        return false;
    }

    public static void clear() {
        results.clear();
    }

}
