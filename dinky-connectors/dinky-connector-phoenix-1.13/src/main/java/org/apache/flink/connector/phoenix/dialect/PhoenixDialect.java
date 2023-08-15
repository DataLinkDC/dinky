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

package org.apache.flink.connector.phoenix.dialect;

import org.apache.flink.connector.phoenix.internal.converter.JdbcRowConverter;
import org.apache.flink.connector.phoenix.internal.converter.PhoenixRowConverter;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.table.types.logical.RowType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PhoenixDialect
 *
 * @since 2022/3/16 11:19
 */
public class PhoenixDialect extends AbstractDialect {
    private static final long serialVersionUID = 1L;

    private static final int MAX_TIMESTAMP_PRECISION = 6;
    private static final int MIN_TIMESTAMP_PRECISION = 1;

    private static final int MAX_DECIMAL_PRECISION = 65;
    private static final int MIN_DECIMAL_PRECISION = 1;

    @Override
    public String getSelectFromStatement(String tableName, String[] selectFields, String[] conditionFields) {

        return null;
    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:phoenix:");
    }

    @Override
    public JdbcRowConverter getRowConverter(RowType rowType) {
        return new PhoenixRowConverter(rowType);
    }

    @Override
    public String getLimitClause(long limit) {
        return "LIMIT " + limit;
    }

    @Override
    public Optional<String> defaultDriverName() {
        return Optional.of("org.apache.phoenix.jdbc.PhoenixDriver");
    }

    /**
     * phoenix不支持 ` 号 不加任何 " ` 号 在列名以及表名上，否则会导致phoenix解析错误
     *
     * @param identifier
     * @return
     */
    @Override
    public String quoteIdentifier(String identifier) {
        // return "`" + identifier + "`";
        // return super.quoteIdentifier(identifier);
        return identifier;
    }

    @Override
    public Optional<String> getUpsertStatement(String tableName, String[] fieldNames, String[] uniqueKeyFields) {
        String columns =
                (String) Arrays.stream(fieldNames).map(this::quoteIdentifier).collect(Collectors.joining(", "));
        String placeholders = (String) Arrays.stream(fieldNames)
                .map((f) -> {
                    return ":" + f;
                })
                .collect(Collectors.joining(", "));
        String sql =
                "UPSERT INTO " + this.quoteIdentifier(tableName) + "(" + columns + ") VALUES (" + placeholders + ")";
        return Optional.of(sql);
    }

    @Override
    public String getInsertIntoStatement(String tableName, String[] fieldNames) {
        return this.getUpsertStatement(tableName, fieldNames, null).get();
    }

    @Override
    public String dialectName() {
        return "Phoenix";
    }

    @Override
    public int maxDecimalPrecision() {
        return MAX_DECIMAL_PRECISION;
    }

    @Override
    public int minDecimalPrecision() {
        return MIN_DECIMAL_PRECISION;
    }

    @Override
    public int maxTimestampPrecision() {
        return MAX_TIMESTAMP_PRECISION;
    }

    @Override
    public int minTimestampPrecision() {
        return MIN_TIMESTAMP_PRECISION;
    }

    @Override
    public List<LogicalTypeRoot> unsupportedTypes() {

        return Arrays.asList(
                LogicalTypeRoot.BINARY,
                LogicalTypeRoot.TIMESTAMP_WITH_LOCAL_TIME_ZONE,
                LogicalTypeRoot.TIMESTAMP_WITH_TIME_ZONE,
                LogicalTypeRoot.INTERVAL_YEAR_MONTH,
                LogicalTypeRoot.INTERVAL_DAY_TIME,
                LogicalTypeRoot.ARRAY,
                LogicalTypeRoot.MULTISET,
                LogicalTypeRoot.MAP,
                LogicalTypeRoot.ROW,
                LogicalTypeRoot.DISTINCT_TYPE,
                LogicalTypeRoot.STRUCTURED_TYPE,
                LogicalTypeRoot.NULL,
                LogicalTypeRoot.RAW,
                LogicalTypeRoot.SYMBOL,
                LogicalTypeRoot.UNRESOLVED);
    }
}
