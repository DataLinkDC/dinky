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

package org.dinky.alert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AlertPool
 *
 * @since 2022/2/23 19:16
 */
public class AlertPool {

    private static volatile Map<String, Alert> alertMap = new ConcurrentHashMap<>();

    public static boolean exist(String key) {
        if (alertMap.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static Integer push(String key, Alert alert) {
        alertMap.put(key, alert);
        return alertMap.size();
    }

    public static Integer remove(String key) {
        alertMap.remove(key);
        return alertMap.size();
    }

    public static Alert get(String key) {
        return alertMap.get(key);
    }
}
