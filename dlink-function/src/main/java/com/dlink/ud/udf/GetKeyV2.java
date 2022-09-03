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

package com.dlink.ud.udf;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.types.inference.InputTypeStrategies;
import org.apache.flink.table.types.inference.TypeInference;

import java.util.Objects;
import java.util.Optional;

public class GetKeyV2<T> extends ScalarFunction {

    public String eval(String map, String key, T defaultValue) {
        if (map == null || !map.contains(key)) {
            return defaultValue.toString();
        }

        String[] maps = extractProperties(map);

        for (String s : maps) {
            String[] items = s.split("=");
            if (items.length == 2 && Objects.equals(key, items[0])) {
                return items[1];
            }
        }

        return defaultValue.toString();
    }

    private String[] extractProperties(String map) {
        map = map.replace("{", "").replace("}", "");
        return map.split(", ");
    }
}
