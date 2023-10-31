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

package org.dinky.tools;

import java.math.BigDecimal;

import com.alibaba.fastjson2.JSON;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class JsonToSqlConvent {
    /**
     * Convert ordinary json data to sql
     *
     * @param jsonStr json str
     */
    public static String commonDataJson(String jsonStr) {
        if (!JSON.isValid(jsonStr)) {
            return "";
        }
        JSONObject data = JSONUtil.parseObj(jsonStr);
        StringBuilder sb = new StringBuilder();
        commonDataConvert(data, sb, 1);
        return sb.toString();
    }

    /**
     * Convert ordinary json data to sql
     *
     * @param data     json object
     * @param sb       string builder
     * @param layerNum layer number
     */
    private static void commonDataConvert(JSONObject data, StringBuilder sb, int layerNum) {
        for (int i = 0; i < data.keySet().size(); i++) {
            String key = CollUtil.get(data.keySet(), i);
            String endFlag = i == data.keySet().size() - 1 ? "\n" : ",\n";
            Object o = data.get(key);
            if (o instanceof JSONObject) {
                sb.append(StrUtil.repeat("\t", layerNum))
                        .append("`")
                        .append(key)
                        .append("` ROW(\n");
                commonDataConvert((JSONObject) o, sb, layerNum + 1);
                sb.append(StrUtil.repeat("\t", layerNum)).append(")").append(endFlag);
            } else if (o instanceof JSONArray) {
                sb.append(StrUtil.repeat("\t", layerNum))
                        .append("`")
                        .append(key)
                        .append("` ARRAY<");
                if (((JSONArray) o).isEmpty()) {
                    sb.append("STRING>").append(endFlag);
                    continue;
                }
                Object o1 = ((JSONArray) o).get(0);
                if (o1 instanceof JSONObject) {
                    sb.append("ROW(\n");
                    commonDataConvert((JSONObject) o1, sb, layerNum + 1);
                    sb.append(StrUtil.repeat("\t", layerNum)).append(")>").append(endFlag);
                } else {
                    String type = getBaseType(o1);
                    sb.append(type).append(">").append(endFlag);
                }
            } else {
                String type = getBaseType(o);
                sb.append(StrUtil.repeat("\t", layerNum))
                        .append("`")
                        .append(key)
                        .append("` ")
                        .append(type)
                        .append(endFlag);
            }
        }
    }

    /**
     * Get the base type of the object
     *
     * @param o object
     * @return base type
     */
    private static String getBaseType(Object o) {
        String type = "null";
        if (o instanceof Integer) {
            type = "BIGINT";
        } else if (o instanceof Boolean) {
            type = "BOOLEAN";
        } else if (o instanceof String) {
            type = "STRING";
        } else if (o instanceof BigDecimal) {
            type = "DOUBLE";
        }
        return type;
    }
}
