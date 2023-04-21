package com.zdpx.coder.operator;

import com.squareup.javapoet.ClassName;
import com.zdpx.coder.Specifications;
import com.zdpx.coder.utils.Preconditions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public final class OperatorParameterUtils {
    private OperatorParameterUtils() {
    }

    public static ClassName getClassNameByName(String name) {
        if ("STRING".equals(name)) {
            return Specifications.STRING;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static Map<String, String> getColumns(String name, Map<String, Object> parameter) {
        Preconditions.checkArgument(name != null, "getColumns function parameter name null");

        Map<String, String> params = new LinkedHashMap<>();

        if (parameter == null) {
            return params;
        }

        Map<String, List<Map<String, String>>> columns = (Map<String, List<Map<String, String>>>) parameter.get(name);
        if (columns != null) {
            for (Map<String, String> column : columns.get("columns")) {
                params.put(column.get("name"), column.get("type"));
            }
        }

        return params;
    }

    static String generateColumnNames(Map<String, String> columns) {
        return columns.keySet().stream()
            .map(t -> String.format("\"%s\"", t))
            .collect(Collectors.joining(","));
    }
}
