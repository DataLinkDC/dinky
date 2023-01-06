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

package com.dlink.metadata.driver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DriverPool
 *
 * @author wenmo
 * @since 2022/2/17 15:29
 **/
public class DriverPool {

    private static volatile Map<String, Driver> driverMap = new ConcurrentHashMap<>();

    public static boolean exist(String key) {
        if (driverMap.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static Integer push(String key, Driver gainer) {
        driverMap.put(key, gainer);
        return driverMap.size();
    }

    public static Integer remove(String key) {
        driverMap.remove(key);
        return driverMap.size();
    }

    public static Driver get(String key) {
        return driverMap.get(key);
    }

}
