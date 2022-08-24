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

import org.apache.flink.table.functions.ScalarFunction;

/**
 * GetKey
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
public class GetKey extends ScalarFunction {

    public String eval(String map, String key, String defaultValue) {
        if (map == null || !map.contains(key)) {
            return defaultValue;
        }
        String[] maps = map.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
        for (int i = 0; i < maps.length; i++) {
            String[] items = maps[i].split("=");
            if (items.length >= 2) {
                if (key.equals(items[0].trim())) {
                    return items[1];
                }
            }
        }
        return defaultValue;
    }
}
