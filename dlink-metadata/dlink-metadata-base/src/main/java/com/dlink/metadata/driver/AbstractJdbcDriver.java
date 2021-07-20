package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;

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

    protected Connection conn;

    abstract String getDriverClass();

    @Override
    public boolean test() {
        Asserts.checkNotNull(config,"无效的数据源配置");
        try {
            Class.forName(getDriverClass());
            DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword()).close();
        } catch (SQLException e) {
//            logger.error("Jdbc链接测试失败！错误信息为：" + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
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
            if(Asserts.isNotNull(conn)) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Schema> listSchemas(){
        List<Schema> schemas = new ArrayList<>();
        String schemasSql = getDBQuery().schemaAllSql();
        try {
            try (PreparedStatement preparedStatement = conn.prepareStatement(schemasSql);
                 ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    String schemaName = results.getString(getDBQuery().schemaName());
                    if (Asserts.isNotNullString(schemaName)) {
                        Schema schema = new Schema(schemaName);
                        schemas.add(schema);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schemas;
    }

    @Override
    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        ResultSet results = null;
        try {
            String tablesSql = getDBQuery().tablesSql();
            StringBuilder sql = new StringBuilder(tablesSql);
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
                preparedStatement.setString(1,schemaName);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(Asserts.isNotNull(results)){
                try {
                    results.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableList;
    }

    @Override
    public List<Column> listColumns(String schema, String tableName) {
        List<Column> columns = new ArrayList<>();
        try {
            String tableFieldsSql = getDBQuery().columnsSql();
            tableFieldsSql = String.format(tableFieldsSql, tableName);
            try (
                    PreparedStatement preparedStatement = conn.prepareStatement(tableFieldsSql);
                    ResultSet results = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    @Override
    public boolean execute(String sql){
        Asserts.checkNullString(sql,"Sql 语句为空");
        String[] sqls = sql.split(";");
        try(Statement statement = conn.createStatement()){
            for (int i = 0; i < sqls.length; i++) {
                if(Asserts.isNullString(sqls[i])){
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
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet results = preparedStatement.executeQuery()) {
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
                    data.put(columns.get(i).getName(), getTypeConvert().convertValue(results,columns.get(i).getName(),columns.get(i).getType()));
                }
                datas.add(data);
            }
            results.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }
}
