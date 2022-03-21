package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.constant.HiveConstant;
import com.dlink.metadata.convert.HiveTypeConvert;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.HiveQuery;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiveDriver extends AbstractJdbcDriver implements Driver {


//    @Override
//    public Table getTable(String schemaName, String tableName) {
//        List<Table> tables = listTables(schemaName);
//        Table table = null;
//        for(Table item : tables){
//            if(Asserts.isEquals(item.getName(),tableName)){
//                table = item;
//            }
//        }
//        if(Asserts.isNotNull(table)) {
//            List<Column> columnList = new ArrayList<>();// 接收排除 Detailed Table Information 之后的 Column对象
//            List<Column> columnListWithExt = listColumns(schemaName, table.getName()); //获取所有的 Column对象
//
//            Column columnExtInfoToTable = columnListWithExt.get(columnListWithExt.size() - 1); //获取 Detailed Table Information 下方解析该值 并赋值给Table的属性
//            String extenedInfo = columnExtInfoToTable.getType(); //获取 Detailed Table Information 的值
//            /**
//             * 解析 Detailed Table Information 开始
//             */
//
//            System.out.println(extenedInfo);
//
//            /**
//             * 解析 Detailed Table Information 结束
//             */
//
//
//            for (int i = 0; i < columnListWithExt.size(); i++) {
//                Column columnExt = columnListWithExt.get(i);
//                if (!columnExt.getName().contains(HiveConstant.DETAILED_TABLE_INFO)){// 排除 Detailed Table Information
//                    Column columnBean = new Column();
//                    columnBean.setName(columnExt.getName());
//                    columnBean.setType(columnExt.getType());
//                    columnBean.setComment(columnExt.getComment());
//                    columnList.add(columnBean);
//                }
//            }
//            table.setColumns(columnList);
//        }
//        return table;
//    }


    @Override
    public Table getTable(String schemaName, String tableName) {
        List<Table> tables = listTables(schemaName);
        Table table = null;
        for (Table item : tables) {
            if (Asserts.isEquals(item.getName(), tableName)) {
                table = item;
                break;
            }
        }
        if (Asserts.isNotNull(table)) {
            table.setColumns(listColumns(schemaName, table.getName()));
        }
        return table;
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
    public List<Schema> getSchemasAndTables() {
        return listSchemas();
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
                    if (execute(String.format(HiveConstant.USE_DB, schemaName))) {
                        schema.setTables(listTables(schema.getName()));
                    }
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
    public List<Column> listColumns(String schemaName, String tableName) {
        List<Column> columns = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String tableFieldsSql = dbQuery.columnsSql(schemaName, tableName);
        try {
            preparedStatement = conn.prepareStatement(tableFieldsSql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            Integer positionId = 1;
            while (results.next()) {
                Column field = new Column();
                if (StringUtils.isEmpty(results.getString(dbQuery.columnName()))) {
                    break;
                } else {
                    if (columnList.contains(dbQuery.columnName())) {
                        String columnName = results.getString(dbQuery.columnName());
                        field.setName(columnName);
                    }
                    if (columnList.contains(dbQuery.columnType())) {
                        field.setType(results.getString(dbQuery.columnType()));
                    }
                    if (columnList.contains(dbQuery.columnComment()) && Asserts.isNotNull(results.getString(dbQuery.columnComment()))) {
                        String columnComment = results.getString(dbQuery.columnComment()).replaceAll("\"|'", "");
                        field.setComment(columnComment);
                    }
                    field.setPosition(positionId++);
                    field.setJavaType(getTypeConvert().convert(field));
                }
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
    public String getCreateTableSql(Table table) {
        StringBuilder createTable = new StringBuilder();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String createTableSql = getDBQuery().createTableSql(table.getSchema(), table.getName());
        try {
            preparedStatement = conn.prepareStatement(createTableSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                createTable.append(results.getString(getDBQuery().createTableName())).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return createTable.toString();
    }

    @Override
    public IDBQuery getDBQuery() {
        return new HiveQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new HiveTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "org.apache.hive.jdbc.HiveDriver";
    }

    @Override
    public String getType() {
        return "Hive";
    }

    @Override
    public String getName() {
        return "Hive";
    }


    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        HashMap<String, String> map = new HashMap<>();
        map.put("BOOLEAN", "BOOLEAN");
        map.put("TINYINT", "TINYINT");
        map.put("SMALLINT", "SMALLINT");
        map.put("INT", "INT");
        map.put("VARCHAR", "STRING");
        map.put("TEXY", "STRING");
        map.put("INT", "INT");
        map.put("DATETIME", "TIMESTAMP");
        return map;
    }
}
