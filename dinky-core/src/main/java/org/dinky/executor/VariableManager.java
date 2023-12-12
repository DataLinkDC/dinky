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

package org.dinky.executor;

import static java.lang.String.format;
import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;

import org.dinky.assertion.Asserts;
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.data.exception.DinkyException;
import org.dinky.utils.StringUtil;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.expression.engine.jexl.JexlEngine;
import lombok.extern.slf4j.Slf4j;

/**
 * Flink Sql Variable Manager
 *
 * @since 2021/6/7 22:06
 */
@Slf4j
public final class VariableManager {
    public static final String VARIABLE = "variable";
    static final String SHOW_VARIABLES = "SHOW VARIABLES";
    private final Map<String, String> variables;

    public static final JexlEngine ENGINE = new JexlEngine();

    public static final Dict ENGINE_CONTEXT = Dict.create();

    /**
     * load expression variable class
     */
    private static void loadExpressionVariableClass() {
        List<String> classLoaderVariableJexlClass = getClassLoaderVariableJexlClass();
        if (CollUtil.isEmpty(classLoaderVariableJexlClass)) {
            return;
        }
        classLoaderVariableJexlClass.forEach(fullClassName -> {
            try {
                String classSimpleName =
                        BeanUtil.getBeanDesc(Class.forName(fullClassName)).getSimpleName();
                String snakeCaseClassName = StringUtil.toSnakeCase(true, classSimpleName);
                ENGINE_CONTEXT.set(snakeCaseClassName, Class.forName(fullClassName));
                log.info("load class : {}", fullClassName);
            } catch (ClassNotFoundException e) {
                log.error(
                        "The class [{}] that needs to be loaded may not be loaded by dinky or there is no jar file of this class under dinky's lib/plugins. Please check, and try again. {}",
                        fullClassName,
                        e.getMessage(),
                        e);
            }
        });
    }

    public VariableManager() {
        variables = new HashMap<>();
    }

    public static List<String> getClassLoaderVariableJexlClass() {
        return Arrays.asList(ResourceUtil.readUtf8Str("dinky-loader/ExpressionVariableClass")
                .replace("\r", "")
                .split("\n"));
    }

    /**
     * Get names of sql variables loaded.
     *
     * @return a list of names of sql variables loaded
     */
    public List<String> listVariablesName() {
        return new ArrayList<>(variables.keySet());
    }

    /**
     * Registers a variable of sql under the given name. The sql variable name must be unique.
     *
     * @param variableName name under which to register the given sql variable
     * @param variable     a variable of sql to register
     */
    public void registerVariable(String variableName, String variable) {
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(variableName), "sql variable name cannot be null or empty.");
        checkNotNull(variable, "sql variable cannot be null");
        variables.put(variableName, variable);
    }

    /**
     * Registers a variable map of sql under the given name. The sql variable name must be unique.
     *
     * @param variableMap a variable map of sql to register
     * @throws CatalogException if the registration of the sql variable under the given name failed.
     *                          But at the moment, with CatalogException, not SqlException
     */
    public void registerVariable(Map<String, String> variableMap) {
        if (Asserts.isNotNull(variableMap)) {
            variables.putAll(variableMap);
        }
    }

    /**
     * Unregisters a variable of sql under the given name. The sql variable name must be existed.
     *
     * @param variableName      name under which to unregister the given sql variable.
     * @param ignoreIfNotExists If false exception will be thrown if the variable of sql to be
     *                          altered does not exist.
     * @throws CatalogException if the unregistration of the sql variable under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public void unregisterVariable(String variableName, boolean ignoreIfNotExists) {
        checkArgument(
                !StringUtils.isNullOrWhitespaceOnly(variableName), "sql variableName name cannot be null or empty.");

        if (variables.containsKey(variableName)) {
            variables.remove(variableName);
        } else if (!ignoreIfNotExists) {
            throw new CatalogException(format("The variable of sql %s does not exist.", variableName));
        }
    }

    /**
     * Get a variable of sql under the given name. The sql variable name must be existed.
     *
     * @param variableName name under which to unregister the given sql variable.
     * @throws CatalogException if the unregistration of the sql variable under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public Object getVariable(String variableName) {
        checkArgument(
                !StringUtils.isNullOrWhitespaceOnly(variableName),
                "sql variable name or jexl key cannot be null or empty.");
        try {
            if (variables.containsKey(variableName)) {
                return variables.get(variableName);
            }
            // load expression variable class
            loadExpressionVariableClass();
            // use jexl to parse variable value
            return ENGINE.eval(variableName, ENGINE_CONTEXT, null);
        } catch (Exception e) {
            throw new DinkyException(format("The variable name or jexl key of sql %s does not exist.", variableName));
        }
    }

    /**
     * Get a table result of sql under the given name. The sql variable name must be existed.
     *
     * @param variableName name under which to unregister the given sql variable.
     */
    public TableResult getVariableResult(String variableName) {
        if (Asserts.isNullString(variableName)) {
            return CustomTableResultImpl.buildTableResult(
                    Collections.singletonList(new TableSchemaField(VARIABLE, DataTypes.STRING())), new ArrayList<>());
        }

        return CustomTableResultImpl.buildTableResult(
                Collections.singletonList(new TableSchemaField(VARIABLE, DataTypes.STRING())),
                Collections.singletonList(Row.of(getVariable(variableName))));
    }

    /**
     * Get a variable of sql under the given name. The sql variable name must be existed.
     *
     * @throws CatalogException if the unregistration of the sql variable under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public Map<String, String> getVariable() {
        return variables;
    }

    /**
     * Get a table result of sql all variables.
     */
    public TableResult getVariables() {
        List<Row> rows = new ArrayList<>();
        for (String key : variables.keySet()) {
            rows.add(Row.of(key));
        }
        return CustomTableResultImpl.buildTableResult(
                Collections.singletonList(new TableSchemaField("variableName", DataTypes.STRING())), rows);
    }

    public Table getVariablesTable(CustomTableEnvironmentImpl environment) {
        List<String> keys = new ArrayList<>(variables.keySet());
        return environment.fromValues(keys);
    }

    public boolean checkShowVariables(String sql) {
        return SHOW_VARIABLES.equalsIgnoreCase(sql.trim());
    }

    /**
     * Parse some variables under the given sql. The parsed parameter will be replaced with its value.
     *
     * @param statement A sql will be parsed.
     * @throws CatalogException if the name of the variable under the given sql failed.
     */
    public String parseVariable(String statement) {
        if (Asserts.isNullString(statement)) {
            return statement;
        }

        StringBuilder sb = new StringBuilder();
        String[] splits = statement.split(FlinkSQLConstant.VARIABLES, 2);
        if (splits.length == 2) {
            if (splits[0].trim().isEmpty()) {
                throw new CatalogException("Illegal variable name.");
            }
            this.registerVariable(splits[0], replaceVariable(splits[1]));
        } else if (splits.length == 1) {
            // statement not contains FlinkSQLConstant.VARIABLES
            sb.append(replaceVariable(statement));
        } else {
            throw new CatalogException("Illegal variable definition.");
        }
        return sb.toString();
    }

    /**
     * Replace some variables under the given sql.
     *
     * @param statement A sql will be replaced.
     */
    private String replaceVariable(String statement) {
        Pattern p = Pattern.compile("\\$\\{(.+?)}");
        Matcher m = p.matcher(statement);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            Object value = getVariable(key);
            m.appendReplacement(sb, "");
            sb.append(value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
