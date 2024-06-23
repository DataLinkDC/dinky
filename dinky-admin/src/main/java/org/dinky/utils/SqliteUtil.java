package org.dinky.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public enum SqliteUtil {
    INSTANCE;

    private Connection connection;

    static {
        try {
            SqliteUtil.INSTANCE.connect("dinky.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public void createTable(String tableName, String columns) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void write(String tableName, String columns, String values) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
    }

    public void write(String tableName, List<String> columns, List<String> values) throws SQLException {
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

        String sql = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + placeholders + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }
            pstmt.executeUpdate();
        }
    }

    public ResultSet read(String tableName, String condition) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " where " + condition + ";";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            return pstmt.executeQuery();
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}