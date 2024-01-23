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

package org.dinky.utils;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.SystemConfiguration;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * SqlUtil
 *
 * @since 2021/7/14 21:57
 */
public class SqlUtil {

    private static final String SEMICOLON = ";";

    private SqlUtil() {}

    public static String[] getStatements(String sql) {
        return getStatements(sql, SystemConfiguration.getInstances().getSqlSeparator());
    }

    public static String[] getStatements(String sql, String sqlSeparator) {
        if (Asserts.isNullString(sql)) {
            return new String[0];
        }

        String[] splits = sql.replace("\r\n", "\n").split(sqlSeparator);
        String lastStatement = splits[splits.length - 1].trim();
        if (lastStatement.endsWith(SEMICOLON)) {
            splits[splits.length - 1] = lastStatement.substring(0, lastStatement.length() - 1);
        }

        return splits;
    }

    public static String removeNote(String sql) {

        if (Asserts.isNotNullString(sql)) {
            // Remove the special-space characters
            sql = sql.replaceAll("\u00A0", " ").replaceAll("[\r\n]+", "\n");
            // Remove annotations Support '--aa' , '/**aaa*/' , '//aa' , '#aaa'
            Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|//.*?$|/\\*[^+].*?\\*/|#.*?$|");
            String presult = p.matcher(sql).replaceAll("$1");
            return presult.trim();
        }
        return sql;
    }

    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("#\\{" + name + "\\}", value);
    }

    /**
     * replace sql context with values params, map's key is origin variable express by `${key}`,
     * value is replacement. for example, if key="name", value="replacement", and sql is "${name}",
     * the result will be "replacement".
     *
     * @param sql sql context
     * @param values replacement
     * @return replace variable result
     */
    public static String replaceAllParam(String sql, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            sql = replaceAllParam(sql, entry.getKey(), entry.getValue());
        }
        return sql;
    }

    public static String addLineNumber(String input) {
        String[] lines = input.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(String.format("%-4d", i + 1));
            sb.append("  ");
            sb.append(lines[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
