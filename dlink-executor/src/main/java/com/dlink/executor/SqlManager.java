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

package com.dlink.executor;

import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;

import static java.lang.String.format;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.model.SystemConfiguration;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.ExpressionParserException;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Flink Sql Fragment Manager
 *
 * @author wenmo
 * @since 2021/6/7 22:06
 **/
public final class SqlManager {

    public static final String FRAGMENT = "fragment";
    static final String SHOW_FRAGMENTS = "SHOW FRAGMENTS";
    private final Map<String, String> sqlFragments;

    public SqlManager() {
        sqlFragments = new HashMap<>();
    }

    /**
     * Get names of sql fragments loaded.
     *
     * @return a list of names of sql fragments loaded
     */
    public List<String> listSqlFragments() {
        return new ArrayList<>(sqlFragments.keySet());
    }

    /**
     * Registers a fragment of sql under the given name. The sql fragment name must be unique.
     *
     * @param sqlFragmentName name under which to register the given sql fragment
     * @param sqlFragment     a fragment of sql to register
     * @throws CatalogException if the registration of the sql fragment under the given name failed.
     *                          But at the moment, with CatalogException, not SqlException
     */
    public void registerSqlFragment(String sqlFragmentName, String sqlFragment) {
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(sqlFragmentName),
                "sql fragment name cannot be null or empty.");
        checkNotNull(sqlFragment, "sql fragment cannot be null");

        if (sqlFragments.containsKey(sqlFragmentName)) {
            throw new CatalogException(format("The fragment of sql %s already exists.", sqlFragmentName));
        }

        sqlFragments.put(sqlFragmentName, sqlFragment);
    }

    /**
     * Registers a fragment map of sql under the given name. The sql fragment name must be unique.
     *
     * @param sqlFragmentMap a fragment map of sql to register
     * @throws CatalogException if the registration of the sql fragment under the given name failed.
     *                          But at the moment, with CatalogException, not SqlException
     */
    public void registerSqlFragment(Map<String, String> sqlFragmentMap) {
        if (Asserts.isNotNull(sqlFragmentMap)) {
            sqlFragments.putAll(sqlFragmentMap);
        }
    }

    /**
     * Unregisters a fragment of sql under the given name. The sql fragment name must be existed.
     *
     * @param sqlFragmentName   name under which to unregister the given sql fragment.
     * @param ignoreIfNotExists If false exception will be thrown if the fragment of sql to be
     *                          altered does not exist.
     * @throws CatalogException if the unregistration of the sql fragment under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public void unregisterSqlFragment(String sqlFragmentName, boolean ignoreIfNotExists) {
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(sqlFragmentName),
                "sql fragmentName name cannot be null or empty.");

        if (sqlFragments.containsKey(sqlFragmentName)) {
            sqlFragments.remove(sqlFragmentName);
        } else if (!ignoreIfNotExists) {
            throw new CatalogException(format("The fragment of sql %s does not exist.", sqlFragmentName));
        }
    }

    /**
     * Get a fragment of sql under the given name. The sql fragment name must be existed.
     *
     * @param sqlFragmentName name under which to unregister the given sql fragment.
     * @throws CatalogException if the unregistration of the sql fragment under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public String getSqlFragment(String sqlFragmentName) {
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(sqlFragmentName),
                "sql fragmentName name cannot be null or empty.");

        if (sqlFragments.containsKey(sqlFragmentName)) {
            return sqlFragments.get(sqlFragmentName);
        }

        if (isInnerDateVar(sqlFragmentName)) {
            return parseDateVar(sqlFragmentName);
        }

        throw new CatalogException(format("The fragment of sql %s does not exist.", sqlFragmentName));
    }

    public TableResult getSqlFragmentResult(String sqlFragmentName) {
        if (Asserts.isNullString(sqlFragmentName)) {
            return CustomTableResultImpl.buildTableResult(
                    Collections.singletonList(new TableSchemaField(FRAGMENT, DataTypes.STRING())), new ArrayList<>());
        }

        String sqlFragment = getSqlFragment(sqlFragmentName);
        return CustomTableResultImpl.buildTableResult(
                Collections.singletonList(new TableSchemaField(FRAGMENT, DataTypes.STRING())),
                Collections.singletonList(Row.of(sqlFragment)));
    }

    /**
     * Get a fragment of sql under the given name. The sql fragment name must be existed.
     *
     * @throws CatalogException if the unregistration of the sql fragment under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public Map<String, String> getSqlFragment() {
        return sqlFragments;
    }

    public TableResult getSqlFragments() {
        List<Row> rows = new ArrayList<>();
        for (String key : sqlFragments.keySet()) {
            rows.add(Row.of(key));
        }
        return CustomTableResultImpl.buildTableResult(
                Collections.singletonList(new TableSchemaField("fragmentName", DataTypes.STRING())), rows);
    }

    public Iterator getSqlFragmentsIterator() {
        return sqlFragments.entrySet().iterator();
    }

    public Table getSqlFragmentsTable(CustomTableEnvironmentImpl environment) {
        List<String> keys = new ArrayList<>(sqlFragments.keySet());
        return environment.fromValues(keys);
    }

    public boolean checkShowFragments(String sql) {
        return SHOW_FRAGMENTS.equals(sql.trim().toUpperCase());
    }

    /**
     * Parse some variables under the given sql.
     *
     * @param statement A sql will be parsed.
     * @throws ExpressionParserException if the name of the variable under the given sql failed.
     */
    public String parseVariable(String statement) {
        if (Asserts.isNullString(statement)) {
            return statement;
        }

        String[] values = statement.split(SystemConfiguration.getInstances().getSqlSeparator());
        StringBuilder sb = new StringBuilder();
        for (String assignment : values) {
            String[] splits = assignment.split(FlinkSQLConstant.FRAGMENTS, 2);
            if (splits.length == 2) {
                if (splits[0].trim().isEmpty()) {
                    throw new ExpressionParserException("Illegal variable name.");
                }
                this.registerSqlFragment(splits[0], replaceVariable(splits[1]));
            } else if (splits.length == 1) {
                // string not contains FlinkSQLConstant.FRAGMENTS
                sb.append(replaceVariable(assignment));
            } else {
                throw new ExpressionParserException("Illegal variable definition.");
            }
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
            String value = this.getSqlFragment(key);
            m.appendReplacement(sb, "");

            // 内置时间变量的情况
            if (value == null && isInnerDateVar(key)) {
                value = parseDateVar(key);
            }

            sb.append(value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * verify if key is inner variable
     * @param key
     * @return
     */
    private boolean isInnerDateVar(String key) {
        if (key.startsWith(FlinkSQLConstant.INNER_DATETIME_KEY)) {
            return true;
        }

        return false;
    }

    /**
     * parse datetime var
     * @param key
     * @return
     */
    private String parseDateVar(String key) {
        int days = 0;
        try {
            if (key.contains("+")) {
                int s = key.indexOf("+") + 1;
                String num = key.substring(s).trim();
                days = Integer.parseInt(num);
            } else if (key.contains("-")) {
                int s = key.indexOf("-") + 1;
                String num = key.substring(s).trim();
                days = Integer.parseInt(num) * -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        SimpleDateFormat dtf = new SimpleDateFormat(FlinkSQLConstant.INNER_DATETIME_FORMAT);
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR,days);
        Date startDate = calendar.getTime();

        return dtf.format(startDate);
    }
}
