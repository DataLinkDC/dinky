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

package org.apache.flink.connector.jdbc.table;

import org.apache.flink.annotation.Internal;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.connector.jdbc.dialect.JdbcDialect;
import org.apache.flink.connector.jdbc.dialect.JdbcDialects;
import org.apache.flink.connector.jdbc.internal.connection.JdbcConnectionProvider;
import org.apache.flink.connector.jdbc.internal.connection.SimpleJdbcConnectionProvider;
import org.apache.flink.connector.jdbc.internal.converter.JdbcRowConverter;
import org.apache.flink.connector.jdbc.internal.options.JdbcLookupOptions;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.connector.jdbc.statement.FieldNamedPreparedStatement;
import org.apache.flink.shaded.guava18.com.google.common.cache.Cache;
import org.apache.flink.shaded.guava18.com.google.common.cache.CacheBuilder;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.util.Preconditions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class JdbcRowDataLookupFunction extends TableFunction<RowData> {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcRowDataLookupFunction.class);
    private static final long serialVersionUID = 2L;
    private final String query;
    private final JdbcConnectionProvider connectionProvider;
    private final DataType[] keyTypes;
    private final String[] keyNames;
    private final long cacheMaxSize;
    private final long cacheExpireMs;
    private final int maxRetryTimes;
    private final JdbcDialect jdbcDialect;
    private final JdbcRowConverter jdbcRowConverter;
    private final JdbcRowConverter lookupKeyRowConverter;
    private transient FieldNamedPreparedStatement statement;
    private transient Cache<RowData, List<RowData>> cache;

    public JdbcRowDataLookupFunction(JdbcOptions options, JdbcLookupOptions lookupOptions, String[] fieldNames,
                                     DataType[] fieldTypes, String[] keyNames, RowType rowType) {
        Preconditions.checkNotNull(options, "No JdbcOptions supplied.");
        Preconditions.checkNotNull(fieldNames, "No fieldNames supplied.");
        Preconditions.checkNotNull(fieldTypes, "No fieldTypes supplied.");
        Preconditions.checkNotNull(keyNames, "No keyNames supplied.");
        this.connectionProvider = new SimpleJdbcConnectionProvider(options);
        this.keyNames = keyNames;
        List<String> nameList = Arrays.asList(fieldNames);
        this.keyTypes = (DataType[]) Arrays.stream(keyNames).map((s) -> {
            Preconditions.checkArgument(nameList.contains(s), "keyName %s can't find in fieldNames %s.",
                    new Object[]{s, nameList});
            return fieldTypes[nameList.indexOf(s)];
        }).toArray((x$0) -> {
            return new DataType[x$0];
        });
        this.cacheMaxSize = lookupOptions.getCacheMaxSize();
        this.cacheExpireMs = lookupOptions.getCacheExpireMs();
        this.maxRetryTimes = lookupOptions.getMaxRetryTimes();
        String[] preFilterCondition = lookupOptions.getPreFilterCondition();
        String[] finalKeyNames = new String[keyNames.length + preFilterCondition.length];
        System.arraycopy(keyNames, 0, finalKeyNames, 0, keyNames.length);
        System.arraycopy(preFilterCondition, 0, finalKeyNames, keyNames.length, preFilterCondition.length);
        this.query = options.getDialect().getSelectFromStatement(options.getTableName(), fieldNames, finalKeyNames);
        String dbURL = options.getDbURL();
        this.jdbcDialect = (JdbcDialect) JdbcDialects.get(dbURL).orElseThrow(() -> {
            return new UnsupportedOperationException(String.format("Unknown dbUrl:%s", dbURL));
        });
        this.jdbcRowConverter = this.jdbcDialect.getRowConverter(rowType);
        this.lookupKeyRowConverter = this.jdbcDialect.getRowConverter(
                RowType.of((LogicalType[]) Arrays.stream(this.keyTypes).map(DataType::getLogicalType).toArray((x$0) -> {
                    return new LogicalType[x$0];
                })));
    }

    public void open(FunctionContext context) throws Exception {
        try {
            this.establishConnectionAndStatement();
            this.cache =
                    this.cacheMaxSize != -1L && this.cacheExpireMs != -1L
                            ? CacheBuilder.newBuilder().expireAfterWrite(this.cacheExpireMs, TimeUnit.MILLISECONDS)
                                    .maximumSize(this.cacheMaxSize).build()
                            : null;
        } catch (SQLException var3) {
            throw new IllegalArgumentException("open() failed.", var3);
        } catch (ClassNotFoundException var4) {
            throw new IllegalArgumentException("JDBC driver class not found.", var4);
        }
    }

    public void eval(Object... keys) {
        RowData keyRow = GenericRowData.of(keys);
        if (this.cache != null) {
            List<RowData> cachedRows = (List) this.cache.getIfPresent(keyRow);
            if (cachedRows != null) {
                Iterator var24 = cachedRows.iterator();

                while (var24.hasNext()) {
                    RowData cachedRow = (RowData) var24.next();
                    this.collect(cachedRow);
                }

                return;
            }
        }

        int retry = 0;

        while (retry <= this.maxRetryTimes) {
            try {
                this.statement.clearParameters();
                this.statement = this.lookupKeyRowConverter.toExternal(keyRow, this.statement);
                ResultSet resultSet = this.statement.executeQuery();
                Throwable var5 = null;

                try {
                    if (this.cache == null) {
                        while (resultSet.next()) {
                            this.collect(this.jdbcRowConverter.toInternal(resultSet));
                        }

                        return;
                    }

                    ArrayList rows = new ArrayList();

                    while (resultSet.next()) {
                        RowData row = this.jdbcRowConverter.toInternal(resultSet);
                        rows.add(row);
                        this.collect(row);
                    }

                    rows.trimToSize();
                    this.cache.put(keyRow, rows);
                    break;
                } catch (Throwable var20) {
                    var5 = var20;
                    throw var20;
                } finally {
                    if (resultSet != null) {
                        if (var5 != null) {
                            try {
                                resultSet.close();
                            } catch (Throwable var18) {
                                var5.addSuppressed(var18);
                            }
                        } else {
                            resultSet.close();
                        }
                    }

                }
            } catch (SQLException var22) {
                LOG.error(String.format("JDBC executeBatch error, retry times = %d", retry), var22);
                if (retry >= this.maxRetryTimes) {
                    throw new RuntimeException("Execution of JDBC statement failed.", var22);
                }

                try {
                    if (!this.connectionProvider.isConnectionValid()) {
                        this.statement.close();
                        this.connectionProvider.closeConnection();
                        this.establishConnectionAndStatement();
                    }
                } catch (ClassNotFoundException | SQLException var19) {
                    LOG.error("JDBC connection is not valid, and reestablish connection failed", var19);
                    throw new RuntimeException("Reestablish JDBC connection failed", var19);
                }

                try {
                    Thread.sleep((long) (1000 * retry));
                } catch (InterruptedException var17) {
                    throw new RuntimeException(var17);
                }

                ++retry;
            }
        }

    }

    private void establishConnectionAndStatement() throws SQLException, ClassNotFoundException {
        Connection dbConn = this.connectionProvider.getOrEstablishConnection();
        this.statement = FieldNamedPreparedStatement.prepareStatement(dbConn, this.query, this.keyNames);
    }

    public void close() throws IOException {
        if (this.cache != null) {
            this.cache.cleanUp();
            this.cache = null;
        }

        if (this.statement != null) {
            try {
                this.statement.close();
            } catch (SQLException var5) {
                LOG.info("JDBC statement could not be closed: " + var5.getMessage());
            } finally {
                this.statement = null;
            }
        }

        this.connectionProvider.closeConnection();
    }

    @VisibleForTesting
    public Connection getDbConnection() {
        return this.connectionProvider.getConnection();
    }
}
