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

package org.dinky.mybatis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.CaseFormat;

/**
 * ProTableUtil
 *
 * @since 2021/5/25
 */
public class ProTableUtil {

    private static final Logger log = LoggerFactory.getLogger(ProTableUtil.class);

    /**
     * Accept only simple identifiers: letters, digits and underscores, optionally one dotted
     * property. This covers current ProTable column names such as createTime or enabled.
     */
    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?$");

    /**
     * @Author wenmo @Description 自动装载表格分页排序参数 @Date 2021/5/18 @Param [para, wrapper,
     * camelToUnderscore, isDelete]
     */
    public static void autoQuery(JsonNode para, QueryWrapper<?> wrapper, boolean camelToUnderscore, boolean isDelete) {
        buildDelete(wrapper, camelToUnderscore, isDelete);
        JsonNode sortField = para.get("sorter");
        if (sortField != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = sortField.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                buildSort(entry.getKey(), entry.getValue().asText(), wrapper, camelToUnderscore);
            }
        }
        JsonNode filter = para.get("filter");
        if (filter != null) {
            Iterator<Map.Entry<String, JsonNode>> fields2 = filter.fields();
            while (fields2.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields2.next();
                buildFilter(entry.getKey(), entry.getValue(), wrapper, camelToUnderscore);
            }
        }
    }

    private static void buildDelete(QueryWrapper<?> wrapper, boolean camelToUnderscore, boolean isDelete) {
        if (isDelete) {
            if (camelToUnderscore) {
                wrapper.eq(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "is_delete"), false);
            } else {
                wrapper.eq("is_delete", false);
            }
        }
    }

    private static void buildSort(
            String sortField, String sortValue, QueryWrapper<?> wrapper, boolean camelToUnderscore) {
        if (sortField != null) {
            sortField = sortField.trim();
        }
        if (isPlaceholderField(sortField)) {
            return;
        }
        if (!isSafeIdentifier(sortField) || !isSortOrder(sortValue)) {
            log.warn("Ignore unexpected ProTable sort field or direction: {}, {}", sortField, sortValue);
            return;
        }
        if (camelToUnderscore) {
            sortField = toColumnName(sortField, true);
        }
        if (!isSafeIdentifier(sortField)) {
            log.warn("Ignore unexpected ProTable sort field after conversion: {}", sortField);
            return;
        }
        if ("descend".equals(sortValue)) {
            wrapper.orderByDesc(qualifyColumn(sortField));
        } else {
            wrapper.orderByAsc(qualifyColumn(sortField));
        }
    }

    /**
     * ProTable 在翻页时可能提交字段名为 undefined/null 的排序。这类字段不能拼进 ORDER BY，否则查询失败，列表无法翻页。
     */
    private static boolean isPlaceholderField(String field) {
        return field == null
                || field.isEmpty()
                || "undefined".equalsIgnoreCase(field)
                || "null".equalsIgnoreCase(field);
    }

    private static boolean isSafeIdentifier(String field) {
        return field != null && IDENTIFIER_PATTERN.matcher(field).matches();
    }

    private static boolean isSortOrder(String sortValue) {
        return "ascend".equals(sortValue) || "descend".equals(sortValue);
    }

    private static String toColumnName(String field, boolean camelToUnderscore) {
        if (!camelToUnderscore) {
            return field;
        }
        int dot = field.indexOf('.');
        if (dot < 0) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field);
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.substring(0, dot))
                + "."
                + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.substring(dot + 1));
    }

    /**
     * Default ProTable queries alias the main table as {@code a}. An already-qualified name is kept
     * as-is.
     */
    private static String qualifyColumn(String field) {
        if (field.contains(".")) {
            return field;
        }
        return "a." + field;
    }

    private static void buildFilter(
            String searchField, JsonNode searchValue, QueryWrapper<?> wrapper, boolean camelToUnderscore) {
        if (searchField != null) {
            searchField = searchField.trim();
        }
        if (isPlaceholderField(searchField) || searchValue == null) {
            return;
        }
        if (!isSafeIdentifier(searchField)) {
            log.warn("Ignore unexpected ProTable filter field: {}", searchField);
            return;
        }
        searchField = toColumnName(searchField, camelToUnderscore);
        if (!isSafeIdentifier(searchField)) {
            log.warn("Ignore unexpected ProTable filter field after conversion: {}", searchField);
            return;
        }
        final String field = qualifyColumn(searchField);
        List<String> searchValues = new ArrayList<>();
        String type = "String";
        if (searchValue.isArray()) {
            for (final JsonNode objNode : searchValue) {
                if (objNode.getNodeType() == JsonNodeType.NUMBER) {
                    type = "Number";
                }
                searchValues.add(objNode.asText());
            }
        }
        if (searchValues.size() > 0) {
            if ("Number".equals(type)) {
                wrapper.and(qw -> {
                    for (int i = 0; i < searchValues.size(); i++) {
                        Double itemField = Double.parseDouble(searchValues.get(i));
                        if (i > 0) {
                            qw.or();
                        }
                        qw.eq(field, itemField);
                    }
                });
            } else {
                wrapper.and(qw -> {
                    for (int i = 0; i < searchValues.size(); i++) {
                        String itemField = searchValues.get(i);
                        if (i > 0) {
                            qw.or();
                        }
                        qw.eq(field, itemField);
                    }
                });
            }
        }
    }

    /**
     * @return void @Author wenmo @Description 自动装载表单查询参数 @Date 2021/5/18 @Param [wrapper, para,
     *     blackarr, writearr, camelToUnderscore]
     */
    public static void autoSetFromPara(
            QueryWrapper<?> wrapper, JsonNode para, String[] blackarr, String[] writearr, boolean camelToUnderscore) {
        List<String> blacklist = Arrays.asList(blackarr);
        List<String> writelist = Arrays.asList(writearr);
        if (para.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = para.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String mapKey = entry.getKey();
                if (blacklist.indexOf(mapKey) == -1 || writelist.indexOf(mapKey) > -1) {
                    if (!isSafeIdentifier(mapKey)) {
                        log.warn("Ignore unexpected ProTable query field: {}", mapKey);
                        continue;
                    }
                    String column = toColumnName(mapKey, camelToUnderscore);
                    if (!isSafeIdentifier(column)) {
                        log.warn("Ignore unexpected ProTable query field after conversion: {}", mapKey);
                        continue;
                    }
                    if (entry.getValue().getNodeType() == JsonNodeType.NUMBER) {
                        Double mapValue = entry.getValue().asDouble();
                        if (mapValue != null) {
                            wrapper.eq(column, mapValue);
                        }
                    } else {
                        String mapValue = entry.getValue().asText();
                        if (mapValue != null && !"".equals(mapValue)) {
                            wrapper.eq(column, mapValue);
                        }
                    }
                }
            }
        }
    }

    /** @return void @Author wenmo @Description 默认表单黑白名单 @Date 2021/5/18 @Param [wrapper, para] */
    public static void autoSetFromParaDefault(QueryWrapper<?> wrapper, JsonNode para) {
        final String[] blackarr = {"current", "pageSize", "sorter", "filter"};
        final String[] writearr = {};
        autoSetFromPara(wrapper, para, blackarr, writearr, true);
    }

    /** @return void @Author wenmo @Description 默认表格参数 @Date 2021/5/18 @Param [para, wrapper] */
    public static void autoQueryDefalut(JsonNode para, QueryWrapper<?> wrapper) {
        autoQuery(para, wrapper, true, false);
    }

    public static void autoQueryDefalut(JsonNode para, QueryWrapper<?> wrapper, boolean isDelete) {
        autoQuery(para, wrapper, true, isDelete);
    }

    /**
     * @return void @Author wenmo @Description protable默认调用方法 @Date 2021/5/18 @Param [para, wrapper]
     */
    public static void autoQueryAndSetFormParaDefalut(JsonNode para, QueryWrapper<?> wrapper) {
        autoSetFromParaDefault(wrapper, para);
        autoQueryDefalut(para, wrapper);
    }

    public static void autoQueryAndSetFormParaCustom(JsonNode para, QueryWrapper<?> wrapper) {
        autoSetFromParaDefault(wrapper, para);
        autoQuery(para, wrapper, true, false);
    }
}
