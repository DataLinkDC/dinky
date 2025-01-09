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
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * SqlUtil
 *
 * @since 2021/7/14 21:57
 */
public class SqlUtil {

    private static final String SEMICOLON = ";";
    private static final String SQL_SEPARATOR = ";\\s*(?:\\n|--.*)";

    private SqlUtil() {}

    public static String[] getStatements(String sql) {
        return getStatements(sql, SQL_SEPARATOR);
    }

    public static String[] getStatements(String sql, String sqlSeparator) {
        if (Asserts.isNullString(sql)) {
            return new String[0];
        }

        final String localSqlSeparator = ";\\s*(?:\\n|--.*)";
        String[] splits = sql.replace("\r\n", "\n").split(localSqlSeparator);
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
            Pattern p = Pattern.compile("(?ms)('(?:[^'])*')|--.*?$|/\\*[^+].*?\\*/|");
            String presult = p.matcher(sql).replaceAll("$1");
            return presult.trim();
        }
        return sql;
    }

    /*
    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("#\\{" + name + "\\}", value);
    }*/

    public static String replaceAllParam(String sql, Map<String, String> values) {
        if (Asserts.isNullString(sql)) {
            return "";
        }
        EvaluationContext context = new StandardEvaluationContext(values);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            sql = replaceAllParam(sql, entry.getKey(), entry.getValue());
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return replaceAllParamSPEL(sql, context);
    }

    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("\\$\\{" + name + "}", value);
    }

    private static final TemplateParserContext template = new TemplateParserContext();
    private static final ExpressionParser parser = new SpelExpressionParser();
    public static String replaceAllParamSPEL(String expressionString, EvaluationContext context) {
        return (String) parser.parseExpression(expressionString, template).getValue(context);
    }

    public static String replaceAllParamSPEL(String expressionString, Object properties) {
        return (String) parser.parseExpression(expressionString, template).getValue(new StandardEvaluationContext(properties));
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
