/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.connector.phoenix.internal;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.phoenix.internal.connection.PhoneixJdbcConnectionProvider;
import org.apache.flink.connector.phoenix.internal.options.JdbcDmlOptions;
import org.apache.flink.connector.phoenix.internal.options.JdbcOptions;
import org.apache.flink.connector.phoenix.statement.FieldNamedPreparedStatementImpl;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.flink.types.Row;

import static org.apache.flink.connector.phoenix.utils.JdbcUtils.setRecordToStatement;
import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * A JDBC outputFormat that supports batching records before writing records to database.
 */
@Internal
public class PhoenixSinkFunction extends RichSinkFunction<Tuple2<Boolean, Row>>
        implements CheckpointedFunction {

    private static final Logger logger = LoggerFactory.getLogger(PhoenixSinkFunction.class);
    private static Connection connection = null;
    private PreparedStatement psUp = null;
    private static int batchcount = 0;
    private static int totalcount = 0;
    private final JdbcOptions jdbcOptions;
    private final PhoneixJdbcConnectionProvider connectionProvider;
    private String[] fieldNames;
    private String[] keyFields;
    private int[] fieldTypes;


    public PhoenixSinkFunction(JdbcOptions jdbcOptions, PhoneixJdbcConnectionProvider connectionProvider,String[] fieldNames,String[] keyFields,int[] fieldTypes) {
        super();
        this.jdbcOptions = jdbcOptions;
        this.connectionProvider = connectionProvider;
        this.fieldNames = fieldNames;
        this.keyFields = keyFields;
        this.fieldTypes = fieldTypes;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        logger.info("打开连接！！！");

        try {
            connection = connectionProvider.getOrEstablishConnection();
        } catch (Exception e) {
            throw new IOException("unable to open JDBC writer", e);
        }
        checkNotNull(jdbcOptions, "No options supplied.");
        checkNotNull(fieldNames, "No fieldNames supplied.");
        JdbcDmlOptions dml =
                JdbcDmlOptions.builder()
                        .withTableName(jdbcOptions.getTableName())
                        .withDialect(jdbcOptions.getDialect())
                        .withFieldNames(fieldNames)
                        .withKeyFields(keyFields)
                        .withFieldTypes(fieldTypes)
                        .build();

        String sql =
                FieldNamedPreparedStatementImpl.parseNamedStatement(
                        jdbcOptions.getDialect()
                                .getInsertIntoStatement(
                                        dml.getTableName(), dml.getFieldNames()),
                        new HashMap<>());
        psUp = connection.prepareStatement(sql);
        logger.info("创建prepareStatement！！！ sql: "+sql);
    }

    @Override
    public void invoke(Tuple2<Boolean, Row> value, Context context) throws Exception {

        setRecordToStatement(psUp, fieldTypes, value.f1);
        psUp.executeUpdate();

        batchcount++;

        if (batchcount == 1000) {
            connection.commit();
            batchcount = 0;
        }
    }

    @Override
    public void close() throws Exception {
        logger.info("关闭连接！！！");
        connection.commit();
        if (psUp != null ) {
            try {
                psUp.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {

    }

    @Override
    public void initializeState(FunctionInitializationContext functionInitializationContext) throws Exception {

    }


}
