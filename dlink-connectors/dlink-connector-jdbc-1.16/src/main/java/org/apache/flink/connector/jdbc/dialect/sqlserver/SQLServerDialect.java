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

package org.apache.flink.connector.jdbc.dialect.sqlserver;

import org.apache.flink.connector.jdbc.converter.JdbcRowConverter;
import org.apache.flink.connector.jdbc.dialect.AbstractDialect;
import org.apache.flink.connector.jdbc.internal.converter.SQLServerRowConverter;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.table.types.logical.RowType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** JDBC dialect for SQLServer. */
class SQLServerDialect extends AbstractDialect {

    private static final long serialVersionUID = 1L;

    // jdbc:sqlserver://127.0.0.1:1433;DatabaseName=test
    @Override
    public JdbcRowConverter getRowConverter(RowType rowType) {
        return new SQLServerRowConverter(rowType);
    }

    @Override
    public String dialectName() {
        return "SQLServer";
    }

    @Override
    public Optional<String> defaultDriverName() {
        return Optional.of("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public String getLimitClause(long limit) {
        return "";
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return identifier;
    }

    /*
     * IF EXISTS(SELECT * FROM source WHERE tid = 3) BEGIN UPDATE source SET tname = 'd' WHERE tid = 3 END ELSE BEGIN
     * INSERT INTO source (tid, tname) VALUES(3, 'd') END
     */
    @Override
    public Optional<String> getUpsertStatement(
            String tableName,
            String[] fieldNames,
            String[] uniqueKeyFields) {
        /* get update field */
        ArrayList<String> updateFieldNamesList = new ArrayList<String>(fieldNames.length);
        Collections.addAll(updateFieldNamesList, fieldNames);
        ArrayList<String> uniqueKeyFieldsList = new ArrayList<String>(uniqueKeyFields.length);
        Collections.addAll(uniqueKeyFieldsList, uniqueKeyFields);
        updateFieldNamesList.removeAll(uniqueKeyFieldsList);

        String updateClause = Arrays.stream(updateFieldNamesList.toArray(new String[0]))
                .map(f -> quoteIdentifier(f) + " = :" + quoteIdentifier(f))
                .collect(Collectors.joining(", "));
        String onClause = Arrays.stream(uniqueKeyFields)
                .map(f -> quoteIdentifier(f) + " = :" + quoteIdentifier(f))
                .collect(Collectors.joining(" AND "));
        String sql = "IF EXISTS ( SELECT * FROM " + tableName + " WHERE " + onClause + " ) "
                + " BEGIN "
                + " UPDATE " + tableName + " SET " + updateClause + " WHERE " + onClause
                + " END "
                + " ELSE "
                + " BEGIN "
                + getInsertStatement(tableName, fieldNames)
                + " END";
        return Optional.of(sql);
    }

    private String getInsertStatement(String tableName, String[] fieldNames) {
        String columns = Arrays.stream(fieldNames)
                .map(this::quoteIdentifier)
                .collect(Collectors.joining(", "));
        String placeholders = Arrays.stream(fieldNames).map(f -> ":" + f).collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + "(" + columns + ") VALUES (" + placeholders + ")";
    }

    @Override
    public Set<LogicalTypeRoot> supportedTypes() {
        // The data types used in SQLServer are list at:

        return EnumSet.of(
                LogicalTypeRoot.CHAR,
                LogicalTypeRoot.VARCHAR,
                LogicalTypeRoot.BOOLEAN,
                LogicalTypeRoot.VARBINARY,
                LogicalTypeRoot.DECIMAL,
                LogicalTypeRoot.TINYINT,
                LogicalTypeRoot.SMALLINT,
                LogicalTypeRoot.INTEGER,
                LogicalTypeRoot.BIGINT,
                LogicalTypeRoot.FLOAT,
                LogicalTypeRoot.DOUBLE,
                LogicalTypeRoot.DATE,
                LogicalTypeRoot.TIME_WITHOUT_TIME_ZONE,
                LogicalTypeRoot.TIMESTAMP_WITHOUT_TIME_ZONE,
                LogicalTypeRoot.TIMESTAMP_WITH_LOCAL_TIME_ZONE,
                LogicalTypeRoot.ARRAY);
    }
}
