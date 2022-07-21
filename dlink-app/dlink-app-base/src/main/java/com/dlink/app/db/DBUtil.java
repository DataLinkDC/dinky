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


package com.dlink.app.db;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBUtil
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class DBUtil {

    private static Connection getConnection(DBConfig config) throws IOException {
        Connection conn = null;
        try {
            Class.forName(config.getDriver());
            conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            close(conn);
        }
        return conn;
    }

    private static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getOneByID(String sql, DBConfig config) throws SQLException, IOException {
        Connection conn = getConnection(config);
        String result = null;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                result = rs.getString(1);
            }
        }
        close(conn);
        /*catch (SQLException e1) {
            e1.printStackTrace();
            String message = e1.getMessage();
            System.err.println(LocalDateTime.now().toString() + " --> 获取 FlinkSQL 异常，ID 为");
        }*/
        return result;
    }

    public static Map<String, String> getMapByID(String sql, DBConfig config) throws SQLException, IOException {
        Connection conn = getConnection(config);
        HashMap<String, String> map = new HashMap();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<String> columnList = new ArrayList<>();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                columnList.add(rs.getMetaData().getColumnLabel(i + 1));
            }
            if (rs.next()) {
                for (int i = 0; i < columnList.size(); i++) {
                    map.put(columnList.get(i), rs.getString(i + 1));
                }
            }
        }
        close(conn);
        return map;
    }

    public static List<Map<String, String>> getListByID(String sql, DBConfig config) throws SQLException, IOException {
        Connection conn = getConnection(config);
        List<Map<String, String>> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<String> columnList = new ArrayList<>();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                columnList.add(rs.getMetaData().getColumnName(i));
            }
            while (rs.next()) {
                HashMap<String, String> map = new HashMap();
                for (int i = 0; i < columnList.size(); i++) {
                    map.put(columnList.get(i), rs.getString(i));
                }
                list.add(map);
            }
        }
        close(conn);
        return list;
    }
}
