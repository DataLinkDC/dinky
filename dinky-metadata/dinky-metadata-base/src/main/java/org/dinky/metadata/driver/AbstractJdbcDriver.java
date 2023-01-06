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

package com.dlink.metadata.driver;

import static com.dlink.utils.SplitUtil.contains;
import static com.dlink.utils.SplitUtil.getReValue;
import static com.dlink.utils.SplitUtil.isSplit;

import com.dlink.assertion.Asserts;
import com.dlink.constant.CommonConstant;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.QueryData;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.model.TableType;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.result.SqlExplainResult;
import com.dlink.utils.LogUtil;
import com.dlink.utils.TextUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * AbstractJdbcDriver
 *
 * @author wenmo
 * @since 2021/7/20 14:09
 **/
public abstract class AbstractJdbcDriver extends AbstractDriver {

    protected static Logger logger = LoggerFactory.getLogger(AbstractJdbcDriver.class);

    protected ThreadLocal<Connection> conn = new ThreadLocal<>();

    private DruidDataSource dataSource;

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

    public DruidDataSource createDataSource() throws SQLException {
        if (null == dataSource) {
            synchronized (this.getClass()) {
                if (null == dataSource) {
                    DruidDataSource ds = new DruidDataSource();
                    createDataSource(ds, config);
                    ds.init();
                    this.dataSource = ds;
                }
            }
        }
        return dataSource;
    }

    @Override
    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        try {
            this.dataSource = createDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    protected void createDataSource(DruidDataSource ds, DriverConfig config) {
        ds.setName(config.getName().replaceAll(":", ""));
        ds.setUrl(config.getUrl());
        ds.setDriverClassName(getDriverClass());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setValidationQuery("select 1");
        ds.setTestWhileIdle(true);
        ds.setBreakAfterAcquireFailure(true);
        ds.setFailFast(true);
        ds.setInitialSize(1);
        ds.setMaxActive(8);
        ds.setMinIdle(5);
    }

    @Override
    public Driver connect() {
        if (Asserts.isNull(conn.get())) {
            try {
                Class.forName(getDriverClass());
                DruidPooledConnection connection = createDataSource().getConnection();
                conn.set(connection);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public boolean isHealth() {
        try {
            if (Asserts.isNotNull(conn.get())) {
                return !conn.get().isClosed();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        try {
            if (Asserts.isNotNull(conn.get())) {
                conn.get().close();
                conn.remove();
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
            preparedStatement = conn.get().prepareStatement(schemasSql);
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
    public boolean existSchema(String schemaName) {
        return listSchemas().stream().anyMatch(schemaItem -> Asserts.isEquals(schemaItem.getName(), schemaName));
    }

    @Override
    public boolean createSchema(String schemaName) throws Exception {
        String sql = generateCreateSchemaSql(schemaName).replaceAll("\r\n", " ");
        if (Asserts.isNotNull(sql)) {
            return execute(sql);
        } else {
            return false;
        }
    }

    @Override
    public String generateCreateSchemaSql(String schemaName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE DATABASE ").append(schemaName);
        return sb.toString();
    }

    @Override
    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql = dbQuery.tablesSql(schemaName);
        try {
            preparedStatement = conn.get().prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                String tableName = results.getString(dbQuery.tableName());
                if (Asserts.isNotNullString(tableName)) {
                    Table tableInfo = new Table();
                    tableInfo.setName(tableName);
                    if (columnList.contains(dbQuery.tableComment())) {
                        tableInfo.setComment(results.getString(dbQuery.tableComment()));
                    }
                    tableInfo.setSchema(schemaName);
                    if (columnList.contains(dbQuery.tableType())) {
                        tableInfo.setType(results.getString(dbQuery.tableType()));
                    }
                    if (columnList.contains(dbQuery.catalogName())) {
                        tableInfo.setCatalog(results.getString(dbQuery.catalogName()));
                    }
                    if (columnList.contains(dbQuery.engine())) {
                        tableInfo.setEngine(results.getString(dbQuery.engine()));
                    }
                    if (columnList.contains(dbQuery.options())) {
                        tableInfo.setOptions(results.getString(dbQuery.options()));
                    }
                    if (columnList.contains(dbQuery.rows())) {
                        tableInfo.setRows(results.getLong(dbQuery.rows()));
                    }
                    if (columnList.contains(dbQuery.createTime())) {
                        tableInfo.setCreateTime(results.getTimestamp(dbQuery.createTime()));
                    }
                    if (columnList.contains(dbQuery.updateTime())) {
                        tableInfo.setUpdateTime(results.getTimestamp(dbQuery.updateTime()));
                    }
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
        try {
            preparedStatement = conn.get().prepareStatement(tableFieldsSql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Column field = new Column();
                String columnName = results.getString(dbQuery.columnName());
                if (columnList.contains(dbQuery.columnKey())) {
                    String key = results.getString(dbQuery.columnKey());
                    field.setKeyFlag(Asserts.isNotNullString(key) && Asserts.isEqualsIgnoreCase(dbQuery.isPK(), key));
                }
                field.setName(columnName);
                if (columnList.contains(dbQuery.columnType())) {
                    String columnType = results.getString(dbQuery.columnType());
                    if (columnType.contains("(")) {
                        String type = columnType.replaceAll("\\(.*\\)", "");
                        if (!columnType.contains(",")) {
                            Integer length = Integer.valueOf(columnType.replaceAll("\\D", ""));
                            field.setLength(length);
                        } else {
                            // some database does not have precision
                            if (dbQuery.precision() != null) {
                                // 例如浮点类型的长度和精度是一样的，decimal(10,2)
                                field.setLength(results.getInt(dbQuery.precision()));
                            }
                        }
                        field.setType(type);
                    } else {
                        field.setType(columnType);
                    }
                }
                if (columnList.contains(dbQuery.columnComment())
                        && Asserts.isNotNull(results.getString(dbQuery.columnComment()))) {
                    String columnComment = results.getString(dbQuery.columnComment()).replaceAll("\"|'", "");
                    field.setComment(columnComment);
                }
                if (columnList.contains(dbQuery.columnLength())) {
                    int length = results.getInt(dbQuery.columnLength());
                    if (!results.wasNull()) {
                        field.setLength(length);
                    }
                }
                if (columnList.contains(dbQuery.isNullable())) {
                    field.setNullable(Asserts.isEqualsIgnoreCase(results.getString(dbQuery.isNullable()),
                            dbQuery.nullableValue()));
                }
                if (columnList.contains(dbQuery.characterSet())) {
                    field.setCharacterSet(results.getString(dbQuery.characterSet()));
                }
                if (columnList.contains(dbQuery.collation())) {
                    field.setCollation(results.getString(dbQuery.collation()));
                }
                if (columnList.contains(dbQuery.columnPosition())) {
                    field.setPosition(results.getInt(dbQuery.columnPosition()));
                }
                if (columnList.contains(dbQuery.precision())) {
                    field.setPrecision(results.getInt(dbQuery.precision()));
                }
                if (columnList.contains(dbQuery.scale())) {
                    field.setScale(results.getInt(dbQuery.scale()));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    field.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                if (columnList.contains(dbQuery.autoIncrement())) {
                    field.setAutoIncrement(
                            Asserts.isEqualsIgnoreCase(results.getString(dbQuery.autoIncrement()), "auto_increment"));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    field.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                field.setJavaType(getTypeConvert().convert(field));
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
    public List<Column> listColumnsSortByPK(String schemaName, String tableName) {
        List<Column> columnList = listColumns(schemaName, tableName);
        columnList.sort(Comparator.comparing(Column::isKeyFlag).reversed());
        return columnList;
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
    public boolean generateCreateTable(Table table) throws Exception {
        String sql = generateCreateTableSql(table).replaceAll("\r\n", " ");
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
        String createTableSql = getDBQuery().createTableSql(table.getSchema(), table.getName());
        try {
            preparedStatement = conn.get().prepareStatement(createTableSql);
            results = preparedStatement.executeQuery();
            if (results.next()) {
                ResultSetMetaData rsmd = results.getMetaData();
                int columns = rsmd.getColumnCount();
                for (int x = 1; x <= columns; x++) {
                    if (getDBQuery().createTableName().equals(rsmd.getColumnName(x))) {
                        createTable = results.getString(getDBQuery().createTableName());
                        break;
                    }
                    if (getDBQuery().createViewName().equals(rsmd.getColumnName(x))) {
                        createTable = results.getString(getDBQuery().createViewName());
                        break;
                    }
                }
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

    // todu impl by subclass
    @Override
    public String generateCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    @Override
    public boolean execute(String sql) throws Exception {
        Asserts.checkNullString(sql, "Sql 语句为空");
        try (Statement statement = conn.get().createStatement()) {
            // logger.info("执行sql的连接id：" + ((DruidPooledConnection) conn).getTransactionInfo().getId());
            statement.execute(sql);
        }
        return true;
    }

    @Override
    public int executeUpdate(String sql) throws Exception {
        Asserts.checkNullString(sql, "Sql 语句为空");
        int res = 0;
        try (Statement statement = conn.get().createStatement()) {
            res = statement.executeUpdate(sql);
        }
        return res;
    }

    /**
     * 标准sql where与order语法都是相同的
     * 不同数据库limit语句不一样，需要单独交由driver去处理，例如oracle
     * 通过{@query(String sql, Integer limit)}去截断返回数据，但是在大量数据情况下会导致数据库负载过高。
     */
    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();
        String limitStart = queryData.getOption().getLimitStart();
        String limitEnd = queryData.getOption().getLimitEnd();

        StringBuilder optionBuilder = new StringBuilder()
                .append("select * from ")
                .append(queryData.getSchemaName())
                .append(".")
                .append(queryData.getTableName());

        if (where != null && !where.equals("")) {
            optionBuilder.append(" where ").append(where);
        }
        if (order != null && !order.equals("")) {
            optionBuilder.append(" order by ").append(order);
        }

        if (TextUtil.isEmpty(limitStart)) {
            limitStart = "0";
        }
        if (TextUtil.isEmpty(limitEnd)) {
            limitEnd = "100";
        }
        optionBuilder.append(" limit ")
                .append(limitStart)
                .append(",")
                .append(limitEnd);

        return optionBuilder;
    }

    @Override
    public JdbcSelectResult query(String sql, Integer limit) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        if (Asserts.isNull(limit)) {
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
            preparedStatement = conn.get().prepareStatement(sql);
            results = preparedStatement.executeQuery();
            if (Asserts.isNull(results)) {
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
                column.setAutoIncrement(metaData.isAutoIncrement(i));
                column.setNullable(metaData.isNullable(i) == 0 ? false : true);
                column.setJavaType(getTypeConvert().convert(column));
                columns.add(column);
            }
            result.setColumns(columnNameList);
            while (results.next()) {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                for (int i = 0; i < columns.size(); i++) {
                    data.put(columns.get(i).getName(),
                            getTypeConvert().convertValue(results, columns.get(i).getName(), columns.get(i).getType()));
                }
                datas.add(data);
                count++;
                if (count >= limit) {
                    break;
                }
            }
            result.setSuccess(true);
        } catch (Exception e) {
            result.setError(LogUtil.getError(e));
            result.setSuccess(false);
            process.error(e.getMessage());
        } finally {
            close(preparedStatement, results);
            result.setRowData(datas);
            return result;
        }
    }

    /**
     * 如果执行多条语句返回最后一条语句执行结果
     *
     * @param sql
     * @param limit
     * @return
     */
    @Override
    public JdbcSelectResult executeSql(String sql, Integer limit) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Start parse sql...");
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, config.getType().toLowerCase());
        process.info(CharSequenceUtil.format("A total of {} statement have been Parsed.", stmtList.size()));
        List<Object> resList = new ArrayList<>();
        JdbcSelectResult result = JdbcSelectResult.buildResult();
        process.info("Start execute sql...");
        for (SQLStatement item : stmtList) {
            String type = item.getClass().getSimpleName();
            if (type.toUpperCase().contains("SELECT") || type.toUpperCase().contains("SHOW")
                    || type.toUpperCase().contains("DESC") || type.toUpperCase().contains("SQLEXPLAINSTATEMENT")) {
                process.info("Execute query.");
                result = query(item.toString(), limit);
            } else if (type.toUpperCase().contains("INSERT") || type.toUpperCase().contains("UPDATE")
                    || type.toUpperCase().contains("DELETE")) {
                try {
                    process.info("Execute update.");
                    resList.add(executeUpdate(item.toString()));
                    result.setStatusList(resList);
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    process.error(e.getMessage());
                    return result;
                }
            } else {
                try {
                    process.info("Execute DDL.");
                    execute(item.toString());
                    resList.add(1);
                    result.setStatusList(resList);
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    process.error(e.getMessage());
                    return result;
                }
            }
        }
        result.success();
        return result;
    }

    @Override
    public List<SqlExplainResult> explain(String sql) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
        String current = null;
        process.info("Start check sql...");
        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, config.getType().toLowerCase());
            for (SQLStatement item : stmtList) {
                current = item.toString();
                String type = item.getClass().getSimpleName();
                sqlExplainResults.add(SqlExplainResult.success(type, current, null));
            }
            process.info("Sql is correct.");
        } catch (Exception e) {
            sqlExplainResults.add(SqlExplainResult.fail(current, LogUtil.getError(e)));
            process.error(e.getMessage());
        } finally {
            return sqlExplainResults;
        }
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }

    public List<Map<String, String>> getSplitSchemaList() {
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql = "select DATA_LENGTH,TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `Database`,TABLE_COMMENT AS COMMENT,TABLE_CATALOG AS `CATALOG`,TABLE_TYPE"
                + " AS `TYPE`,ENGINE AS `ENGINE`,CREATE_OPTIONS AS `OPTIONS`,TABLE_ROWS AS `ROWS`,CREATE_TIME,UPDATE_TIME from information_schema.tables WHERE TABLE_TYPE='BASE TABLE'";
        List<Map<String, String>> schemas = null;
        try {
            preparedStatement = conn.get().prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            schemas = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Map<String, String> map = new HashMap<>();
                for (String column : columnList) {
                    map.put(column, results.getString(column));
                }
                schemas.add(map);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
    }

    @Override
    public Set<Table> getSplitTables(List<String> tableRegList, Map<String, String> splitConfig) {
        Set<Table> set = new HashSet<>();
        List<Map<String, String>> schemaList = getSplitSchemaList();
        IDBQuery dbQuery = getDBQuery();

        for (String table : tableRegList) {
            String[] split = table.split("\\\\.");
            String database = split[0];
            String tableName = split[1];
            // 匹配对应的表
            List<Map<String, String>> mapList = schemaList.stream()
                    // 过滤不匹配的表
                    .filter(x -> contains(database, x.get(dbQuery.schemaName()))
                            && contains(tableName, x.get(dbQuery.tableName())))
                    .collect(Collectors.toList());
            List<Table> tableList = mapList.stream()
                    // 去重
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(
                                    Comparator.comparing(x -> getReValue(x.get(dbQuery.schemaName()), splitConfig) + "."
                                            + getReValue(x.get(dbQuery.tableName()), splitConfig)))),
                            ArrayList::new))
                    .stream().map(x -> {
                        Table tableInfo = new Table();
                        tableInfo.setName(getReValue(x.get(dbQuery.tableName()), splitConfig));
                        tableInfo.setComment(x.get(dbQuery.tableComment()));
                        tableInfo.setSchema(getReValue(x.get(dbQuery.schemaName()), splitConfig));
                        tableInfo.setType(x.get(dbQuery.tableType()));
                        tableInfo.setCatalog(x.get(dbQuery.catalogName()));
                        tableInfo.setEngine(x.get(dbQuery.engine()));
                        tableInfo.setOptions(x.get(dbQuery.options()));
                        tableInfo.setRows(Long.valueOf(x.get(dbQuery.rows())));
                        try {
                            tableInfo.setCreateTime(
                                    SimpleDateFormat.getDateInstance().parse(x.get(dbQuery.createTime())));
                            String updateTime = x.get(dbQuery.updateTime());
                            if (Asserts.isNotNullString(updateTime)) {
                                tableInfo.setUpdateTime(SimpleDateFormat.getDateInstance().parse(updateTime));
                            }
                        } catch (ParseException ignored) {
                            logger.warn("set date fail");

                        }
                        TableType tableType = TableType.type(isSplit(x.get(dbQuery.schemaName()), splitConfig),
                                isSplit(x.get(dbQuery.tableName()), splitConfig));
                        tableInfo.setTableType(tableType);

                        if (tableType != TableType.SINGLE_DATABASE_AND_TABLE) {
                            String currentSchemaName = getReValue(x.get(dbQuery.schemaName()), splitConfig) + "."
                                    + getReValue(x.get(dbQuery.tableName()), splitConfig);
                            List<String> schemaTableNameList = mapList.stream()
                                    .filter(y -> (getReValue(y.get(dbQuery.schemaName()), splitConfig) + "."
                                            + getReValue(y.get(dbQuery.tableName()), splitConfig))
                                                    .equals(currentSchemaName))
                                    .map(y -> y.get(dbQuery.schemaName()) + "." + y.get(dbQuery.tableName()))
                                    .collect(Collectors.toList());
                            tableInfo.setSchemaTableNameList(schemaTableNameList);
                        } else {
                            tableInfo.setSchemaTableNameList(Collections
                                    .singletonList(x.get(dbQuery.schemaName()) + "." + x.get(dbQuery.tableName())));
                        }
                        return tableInfo;
                    }).collect(Collectors.toList());
            set.addAll(tableList);

        }
        return set;
    }
}
