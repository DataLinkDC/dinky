package com.dlink.metadata.driver;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.dlink.assertion.Asserts;
import com.dlink.constant.CommonConstant;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;
import com.dlink.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
        IDBQuery dbQuery = getDBQuery();
        String sql = dbQuery.tablesSql(schemaName);
        try {
            preparedStatement = conn.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String tableName = results.getString(dbQuery.tableName());
                if (Asserts.isNotNullString(tableName)) {
                    Table tableInfo = new Table();
                    tableInfo.setName(tableName);
                    tableInfo.setComment(results.getString(dbQuery.tableComment()));
                    tableInfo.setSchema(schemaName);
                    tableInfo.setType(results.getString(dbQuery.tableType()));
                    tableInfo.setCatalog(results.getString(dbQuery.catalogName()));
                    tableInfo.setEngine(results.getString(dbQuery.engine()));
                    tableInfo.setOptions(results.getString(dbQuery.options()));
                    tableInfo.setRows(results.getLong(dbQuery.rows()));
                    tableInfo.setCreateTime(results.getTimestamp(dbQuery.createTime()));
                    tableInfo.setUpdateTime(results.getTimestamp(dbQuery.updateTime()));
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
                String key = results.getString(dbQuery.columnKey());
                field.setKeyFlag(Asserts.isNotNullString(key) && Asserts.isEqualsIgnoreCase("PRI",key));
                field.setName(columnName);
                field.setType(results.getString(dbQuery.columnType()));
                field.setJavaType(getTypeConvert().convert(field.getType()).getType());
                field.setComment(results.getString(dbQuery.columnComment()));
                field.setNullable(Asserts.isEqualsIgnoreCase(results.getString(dbQuery.isNullable()),"YES"));
                field.setCharacterSet(results.getString(dbQuery.characterSet()));
                field.setCollation(results.getString(dbQuery.collation()));
                field.setPosition(results.getInt(dbQuery.columnPosition()));
                field.setPrecision(results.getInt(dbQuery.precision()));
                field.setScale(results.getInt(dbQuery.scale()));
                field.setAutoIncrement(Asserts.isEqualsIgnoreCase(results.getString(dbQuery.autoIncrement()),"auto_increment"));
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
    public boolean createTable(Table table) throws Exception {
        String sql = getCreateTableSql(table).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public boolean dropTable(Table table) throws Exception {
        String sql = getDropTableSql(table).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public boolean truncateTable(Table table) throws Exception {
        String sql = getTruncateTableSql(table).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public String getCreateTableSql(Table table) {
        String createTable = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String createTableSql = getDBQuery().createTableSql(table.getSchema(),table.getName());
        try {
            preparedStatement = conn.prepareStatement(createTableSql);
            results = preparedStatement.executeQuery();
            if (results.next()) {
                createTable = results.getString(getDBQuery().createTableName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return createTable;
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
    public boolean execute(String sql) throws Exception {
        Asserts.checkNullString(sql, "Sql 语句为空");
        boolean res = false;
        try (Statement statement = conn.createStatement()) {
            res = statement.execute(sql);
        }
        return res;
    }

    @Override
    public int executeUpdate(String sql) throws Exception {
        Asserts.checkNullString(sql, "Sql 语句为空");
        int res = 0;
        try (Statement statement = conn.createStatement()) {
            res = statement.executeUpdate(sql);
        }
        return res;
    }

    @Override
    public JdbcSelectResult query(String sql, Integer limit) {
        if(Asserts.isNull(limit)){
            limit = 100;
        }
        JdbcSelectResult result = new JdbcSelectResult();
        List<LinkedHashMap<String, Object>> datas = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<String> columnNameList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        int count = 0;
        try {
            preparedStatement = conn.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            if(Asserts.isNull(results)){
                result.setSuccess(true);
                close(preparedStatement, results);
                return result;
            }
            ResultSetMetaData metaData = results.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNameList.add(metaData.getColumnLabel(i));
                Column column = new Column();
                column.setName(metaData.getColumnLabel(i));
                column.setType(metaData.getColumnTypeName(i));
                column.setJavaType(getTypeConvert().convert(metaData.getColumnTypeName(i)).getType());
                columns.add(column);
            }
            result.setColumns(columnNameList);
            while (results.next()) {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                for (int i = 0; i < columns.size(); i++) {
                    data.put(columns.get(i).getName(), getTypeConvert().convertValue(results, columns.get(i).getName(), columns.get(i).getType()));
                }
                datas.add(data);
                count ++;
                if(count >= limit){
                    break;
                }
            }
            result.setSuccess(true);
        } catch (Exception e) {
            result.setError(LogUtil.getError(e));
            result.setSuccess(false);
        } finally {
            close(preparedStatement, results);
            result.setRowData(datas);
            return result;
        }
    }

    @Override
    public JdbcSelectResult executeSql(String sql, Integer limit) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql,config.getType().toLowerCase());
        List<Object> resList = new ArrayList<>();
        JdbcSelectResult result = JdbcSelectResult.buildResult();
        for(SQLStatement item : stmtList){
            String type = item.getClass().getSimpleName();
            if(type.toUpperCase().contains("SELECT")||type.toUpperCase().contains("SHOW")||type.toUpperCase().contains("DESC")){
                return query(item.toString(),limit);
            }else if(type.toUpperCase().contains("INSERT")||type.toUpperCase().contains("UPDATE")||type.toUpperCase().contains("DELETE")){
                try {
                    resList.add(executeUpdate(item.toString()));
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    return result;
                }
            }else {
                try {
                    resList.add(execute(item.toString()));
                } catch (Exception e) {
                    resList.add(false);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    return result;
                }
            }
        }
        result.setStatusList(resList);
        result.success();
        return result;
    }

    @Override
    public List<SqlExplainResult> explain(String sql){
        List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
        String current = null;
        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql,config.getType().toLowerCase());
            for(SQLStatement item : stmtList){
                current = item.toString();
                String type = item.getClass().getSimpleName();
                sqlExplainResults.add(SqlExplainResult.success(type, current, null));
            }
        } catch (Exception e) {
            sqlExplainResults.add(SqlExplainResult.fail(current,LogUtil.getError(e)));
        } finally {
            return sqlExplainResults;
        }
    }

    @Override
    public Map<String,String> getFlinkColumnTypeConversion(){
        return new HashMap<>();
    }
}
