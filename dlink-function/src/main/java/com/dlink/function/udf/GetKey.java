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

package com.dlink.function.udf;

import org.apache.flink.table.functions.ScalarFunction;

import java.util.Objects;

public class GetKey extends ScalarFunction {

    public int eval(String map, String key, int defaultValue) {
        if (map == null || !map.contains(key)) {
            return defaultValue;
        }

        String[] maps = extractProperties(map);

        for (String s : maps) {
            String[] items = s.split("=");
            if (items.length == 2 && Objects.equals(key, items[0])) {
                return Integer.parseInt(items[1]);
            }
        }
        return defaultValue;
    }

    public String eval(String map, String key, String defaultValue) {
        if (map == null || !map.contains(key)) {
            return defaultValue;
        }

        String[] maps = extractProperties(map);

        for (String s : maps) {
            String[] items = s.split("=");
            if (items.length == 2 && Objects.equals(key, items[0])) {
                return items[1];
            }
        }
        return defaultValue;
    }

    private String[] extractProperties(String map) {
        map = map.replace("{", "").replace("}", "");
        return map.split(", ");
    }
}
