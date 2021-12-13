package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.constant.CommonConstant;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * AbstractJdbcDriver
 *
 * @author wenmo
 * @since 2021/7/20 14:09
 **/
public abstract class AbstractJdbcDriver extends AbstractDriver {

    private static Logger logger = LoggerFactory.getLogger(AbstractJdbcDriver.class);

    protected Connection conn;

    abstract String getDriverClass();

    @Override
    public String test() {
        Asserts.checkNotNull(config, "无效的数据源配置");
        try {
            Class.forName(getDriverClass());
            DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword()).close();
        } catch (Exception e) {
            logger.error("Jdbc链接测试失败！错误信息为：" + e.getMessage(), e);
            return e.getMessage();
        }
        return CommonConstant.HEALTHY;
    }

    @Override
    public Driver connect() {
        try {
            Class.forName(getDriverClass());
            conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void close() {
        try {
            if (Asserts.isNotNull(conn)) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(PreparedStatement preparedStatement, ResultSet results) {
        try {
            if (Asserts.isNotNull(results)) {
                results.close();
            }
            if (Asserts.isNotNull(preparedStatement)) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Schema> listSchemas() {
        List<Schema> schemas = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String schemasSql = getDBQuery().schemaAllSql();
        try {
            preparedStatement = conn.prepareStatement(schemasSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String schemaName = results.getString(getDBQuery().schemaName());
                if (Asserts.isNotNullString(schemaName)) {
                    Schema schema = new Schema(schemaName);
                    schemas.add(schema);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
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
                    String tableComment = results.getString(getDBQuery().tableComment());
                    tableInfo.setComment(tableComment);
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
        String tableFieldsSql = getDBQuery().columnsSql(schemaName, tableName);
        tableFieldsSql = String.format(tableFieldsSql, tableName);
        try {
            preparedStatement = conn.prepareStatement(tableFieldsSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                Column field = new Column();
                String columnName = results.getString(getDBQuery().columnName());
                boolean isId;
                String key = results.getString(getDBQuery().columnKey());
                isId = Asserts.isNotNullString(key) && "PRI".equals(key.toUpperCase());
                if (isId) {
                    field.setKeyFlag(true);
                } else {
                    field.setKeyFlag(false);
                }
                field.setName(columnName);
                field.setType(results.getString(getDBQuery().columnType()));
                field.setJavaType(getTypeConvert().convert(field.getType()).getType());
                field.setComment(results.getString(getDBQuery().columnComment()));
                field.setIsNotNull(results.getString(getDBQuery().isNotNull()));
                columns.add(field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return columns;
    }

    @Override
    public boolean createTable(Table table) {
        String sql = getCreateTableSql(table).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public boolean dropTable(Table table) {
        String sql = getDropTableSql(table).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public boolean truncateTable(Table table) {
        String sql = getTruncateTableSql(table).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public String getDropTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        if (Asserts.isNotNullString(table.getSchema())) {
            sb.append(table.getSchema() + ".");
        }
        sb.append(table.getName());
        return sb.toString();
    }

    @Override
    public String getTruncateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("TRUNCATE TABLE ");
        if (Asserts.isNotNullString(table.getSchema())) {
            sb.append(table.getSchema() + ".");
        }
        sb.append(table.getName());
        return sb.toString();
    }

    @Override
    public boolean execute(String sql) {
        Asserts.checkNullString(sql, "Sql 语句为空");
        String[] sqls = sql.split(";");
        try (Statement statement = conn.createStatement()) {
            for (int i = 0; i < sqls.length; i++) {
                if (Asserts.isNullString(sqls[i])) {
                    continue;
                }
                statement.execute(sqls[i]);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<HashMap<String, Object>> query(String sql) {
        List<HashMap<String, Object>> datas = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Column column = new Column();
                column.setName(metaData.getColumnName(i));
                column.setType(metaData.getColumnTypeName(i));
                column.setJavaType(getTypeConvert().convert(metaData.getColumnTypeName(i)).getType());
                columns.add(column);
            }
            while (results.next()) {
                HashMap<String, Object> data = new HashMap<>();
                for (int i = 0; i < columns.size(); i++) {
                    data.put(columns.get(i).getName(), getTypeConvert().convertValue(results, columns.get(i).getName(), columns.get(i).getType()));
                }
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return datas;
    }

    @Override
    public SqlExplainResult explain(String sql){
        boolean correct = true;
        String error = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        try {
            preparedStatement = conn.prepareStatement("explain "+sql);
            results = preparedStatement.executeQuery();
            if(!results.next()){
                correct = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            correct = false;
            error = e.getMessage();
        } finally {
            close(preparedStatement, results);
        }
        if(correct) {
            return SqlExplainResult.success(null, sql, null);
        }else {
            return SqlExplainResult.fail(sql,error);
        }
    }
}
