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

import org.dinky.data.constant.DirConstant;
import org.dinky.data.constant.MonitorTableConstant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SqliteUtil {
    INSTANCE;

    private Connection connection;
    private final AtomicLong lastRecycle = new AtomicLong(0);

    static {
        try {
            SqliteUtil.INSTANCE.connect(
                    DirConstant.getTempRootDir() + DirConstant.FILE_SEPARATOR + MonitorTableConstant.DINKY_DB);
            SqliteUtil.INSTANCE.recyleData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public void createTable(String tableName, String columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            log.error("Failed to create table: " + e.getMessage());
        }
    }

    public void executeSql(String sql) throws SQLException {
        Statement pstmt = connection.createStatement();
        pstmt.executeUpdate(sql);
        connection.commit();
    }

    public void recyleData() {
        long now = System.currentTimeMillis();
        if (now - lastRecycle.get() < 1000 * 60 * 60) {
            return;
        }
        lastRecycle.set(now);
        try {
            String sql = "DELETE FROM dinky_metrics WHERE heart_time <= datetime('now', '-7 days')";
            executeSql(sql);
            executeSql("VACUUM");
        } catch (SQLException e) {
            log.error("Failed to recyle database: " + e.getMessage());
        }
    }

    public void write(String tableName, List<String> columns, List<List<String>> values) throws SQLException {
        String sql = createInsertSql(tableName, columns);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (List<String> value : values) {
                for (int i = 0; i < value.size(); i++) {
                    pstmt.setString(i + 1, value.get(i));
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            log.error("Failed to write to SQLite: " + e.getMessage());
        }
        recyleData();
    }

    private static String createInsertSql(String tableName, List<String> columns) {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            columnNames.append(columns.get(i));
            placeholders.append("?");

            if (i < columns.size() - 1) {
                columnNames.append(", ");
                placeholders.append(", ");
            }
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columnNames, placeholders);
    }

    public PreparedResultSet read(String tableName, String condition) throws SQLException {
        String sql = String.format("SELECT * FROM %s where %s;", tableName, condition);

        PreparedStatement pstmt = connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        return new PreparedResultSet(pstmt, rs);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static class PreparedResultSet implements AutoCloseable {
        private final PreparedStatement pstmt;
        private final ResultSet rs;

        public PreparedResultSet(PreparedStatement pstmt, ResultSet rs) {
            this.pstmt = pstmt;
            this.rs = rs;
        }

        public PreparedStatement getPstmt() {
            return pstmt;
        }

        public ResultSet getRs() {
            return rs;
        }

        @Override
        public void close() throws Exception {
            pstmt.close();
        }
    }
}
