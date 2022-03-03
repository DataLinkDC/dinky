package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.constant.CommonConstant;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Table;
import com.dlink.metadata.convert.PhoenixTypeConvert;
import com.dlink.metadata.query.PhoenixQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * PhoenixDriver
 *
 * @author gy
 * @since 2022/3/2 11:34
 **/

public class PhoenixDriver extends AbstractJdbcDriver {
    private static Logger logger = LoggerFactory.getLogger(AbstractJdbcDriver.class);

    @Override
    public IDBQuery getDBQuery() {
        return new PhoenixQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new PhoenixTypeConvert();
    }

    @Override
    public String getType() {
        return "Phoenix";
    }

    @Override
    public String getName() {
        return "Phoenix数据库";
    }

    @Override
    String getDriverClass() {
        return "org.apache.phoenix.jdbc.PhoenixDriver";
    }

    @Override
    public String getCreateTableSql(Table table) {
        return null;
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap();
    }

    @Override
    public String test() {
        Asserts.checkNotNull(config, "无效的数据源配置");
        try {
            Class.forName(getDriverClass());
            Properties properties = new Properties();
            properties.put("phoenix.schema.isNamespaceMappingEnabled", "true");
            properties.put("phoenix.schema.mapSystemTablesToNamespac", "true");
            DriverManager.getConnection(config.getUrl(), properties).close();
        } catch (Exception e) {
            logger.error("Phoenix JDBC链接测试失败！错误信息为：" + e.getMessage(), e);
            return e.getMessage();
        }
        return CommonConstant.HEALTHY;
    }

    @Override
    public Driver connect() {
        try {
            System.out.println(getDriverClass());
            Class.forName(getDriverClass());
            Properties properties = new Properties();
            properties.put("phoenix.schema.isNamespaceMappingEnabled", "true");
            properties.put("phoenix.schema.mapSystemTablesToNamespac", "true");
            conn = DriverManager.getConnection(config.getUrl(), properties);
            conn.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    @Override
    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String sql = getDBQuery().tablesSql(schemaName);
        try {
            preparedStatement = conn.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String tableName = results.getString(getDBQuery().tableName());
                if (Asserts.isNotNullString(tableName)) {
                    Table tableInfo = new Table();
                    tableInfo.setName(tableName);
                    tableInfo.setSchema(schemaName);
                    tableList.add(tableInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return tableList;
    }


    @Override
    public List<Column> listColumns(String schemaName, String tableName) {
        List<Column> columns = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String tableFieldsSql = dbQuery.columnsSql(schemaName, tableName);
        tableFieldsSql = String.format(tableFieldsSql, tableName);
        try {
            preparedStatement = conn.prepareStatement(tableFieldsSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                Column field = new Column();
                String columnName = results.getString(dbQuery.columnName());
                if (Asserts.isNotNullString(columnName)) {
                    String key = results.getString(dbQuery.columnKey());
                    field.setKeyFlag(Asserts.isNotNullString(key) && Asserts.isNullString(results.getString("COLUMN_FAMILY")));
                    field.setName(columnName);
                    field.setType(results.getString(dbQuery.columnType()));
                    columns.add(field);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return columns;
    }
}
