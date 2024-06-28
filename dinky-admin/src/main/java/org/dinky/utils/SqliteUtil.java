package org.dinky.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
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

    public void createTable(String tableName, String columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            log.error("Failed to create table: " + e.getMessage());
        }
    }

    public void write(String tableName, String columns, String values) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columns, values);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
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
    }

    private static @NotNull String createInsertSql(String tableName, List<String> columns) {
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

    public ResultSet read(String tableName, String condition) throws SQLException {
        String sql = String.format("SELECT * FROM %s where %s;", tableName, condition);

        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}