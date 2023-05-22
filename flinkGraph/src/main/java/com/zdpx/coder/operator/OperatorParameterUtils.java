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

package com.zdpx.coder.operator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.squareup.javapoet.ClassName;
import com.zdpx.coder.Specifications;
import com.zdpx.coder.utils.Preconditions;

/** */
public final class OperatorParameterUtils {
    private OperatorParameterUtils() {}

    public static ClassName getClassNameByName(String name) {
        if ("STRING".equals(name)) {
            return Specifications.STRING;
        }
        return null;
    }

    public static Map<String, String> getColumns(String name, Map<String, Object> parameter) {
        Preconditions.checkArgument(name != null, "getColumns function parameter name null");

        Map<String, String> params = new LinkedHashMap<>();

        if (parameter == null) {
            return params;
        }

        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, String>>> columns =
                (Map<String, List<Map<String, String>>>) parameter.get(name);
        if (columns != null) {
            for (Map<String, String> column : columns.get("columns")) {
                params.put(column.get("name"), column.get("type"));
            }
        }

        return params;
    }

    public static String generateColumnNames(Map<String, String> columns) {
        return columns.keySet().stream()
                .map(t -> String.format("\"%s\"", t))
                .collect(Collectors.joining(","));
    }
}
