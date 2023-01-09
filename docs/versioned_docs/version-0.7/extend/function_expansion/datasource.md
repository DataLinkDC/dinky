---
sidebar_position: 3
id: datasource
title: 扩展数据源
---




 Dinky 数据源遵循 SPI,可随意扩展所需要的数据源。数据源扩展可在 dlink-metadata 模块中进行可插拔式扩展。现已经支持的数据源包括如下：

   - MySQL
   - Oracle
   - SQLServer
   - PostGreSQL
   - Phoenix
   - Doris(Starrocks)
   - ClickHouse 
   - Hive ``需要的jar包: hive-jdbc-2.1.1.jar && hive-service-2.1.1.jar``

使用以上数据源,详见注册中心[数据源管理](../../administrator_guide/register_center/datasource_manage),配置数据源连接
:::tip 注意事项
  Dinky 不在对 Starorcks 进行额外扩展，Doris 和 Starorcks 底层并无差别，原则上只是功能区分。经社区测试验证，可采用 Doris 扩展连接 Starrocks。
:::
----

## 准备工作
- 本地开发环境搭建
  - 详见 [开发者本地调试手册](../../developer_guide/local_debug)

## 后端开发
- 本文以 Hive 数据源扩展为例
- 在 **dlink-metadata** 模块中， 右键**新建子模块**, 命名规则: **dlink-metadata-{数据源名称}**
- **代码层面**
  - 注意事项: 
    - 不可在父类的基础上修改代码，可以在子类中进行扩展 ,或者重写父类方法
    - 扩展数据源需要同时提交**测试用例**
  - 在此模块的 **pom.xml** 中，添加所需依赖, 需要注意的是 : 数据源本身的 ``JDBC``的 jar 不要包含在打包中 , 而是后续部署时,添加在 ``plugins`` 下
  - 需要在此模块的 **resource** 下 新建包 ``META-INF.services`` , 在此包中新建文件 ``com.dlink.metadata.driver.Driver`` 内容如下:
    - ``com.dlink.metadata.driver.数据源类型Driver``
      基础包:
```bash
顶层包名定义: com.dlink.metadata
  子包含义:
    - constant: 常量定义 目前此模块中主要定义各种动态构建的执行 SQL 
    - convert: 存放数据源的数据类型<->Java 类型的转换类 ps: 可以不定义 不定义使用 dlink-metadata-base 的默认转换 即调用父类的方法
    - driver: 存放数据源驱动类,获取元数据的主要类,类 extends AbstractJdbcDriver implements Driver 
    - query : 存放解析获取元数据的主要类,类 extends AbstractDBQuery 方法不重写 默认使用父类
```
代码如下:

HiveConstant
```java
public interface HiveConstant {

    /**
     * 查询所有database
     */
    String QUERY_ALL_DATABASE = " show databases";
    /**
     * 查询所有schema下的所有表
     */
    String QUERY_ALL_TABLES_BY_SCHEMA = "show tables";
    /**
     * 扩展信息Key
     */
    String DETAILED_TABLE_INFO = "Detailed Table Information";
    /**
     * 查询指定schema.table的扩展信息
     */
    String QUERY_TABLE_SCHEMA_EXTENED_INFOS = " describe extended `%s`.`%s`";
    /**
     * 查询指定schema.table的信息 列 列类型 列注释
     */
    String QUERY_TABLE_SCHEMA = " describe `%s`.`%s`";
    /**
     * 使用 DB
     */
    String USE_DB = "use `%s`";
    /**
     * 只查询指定schema.table的列名
     */
    String QUERY_TABLE_COLUMNS_ONLY = "show columns in `%s`.`%s`";
}

```

HiveTypeConvert
```java

public class HiveTypeConvert implements ITypeConvert {
    // 主要是将获取到的数据类型转换为 Java 数据类型的映射 存储在 Column 中 , 此处根据扩展的数据源特性进行修改  
    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String t = column.getType().toLowerCase().trim();
        if (t.contains("char")) {
            return ColumnType.STRING;
        } else if (t.contains("boolean")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_BOOLEAN;
            }
            return ColumnType.BOOLEAN;
        } else if (t.contains("tinyint")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_BYTE;
            }
            return ColumnType.BYTE;
        } else if (t.contains("smallint")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_SHORT;
            }
            return ColumnType.SHORT;
        } else if (t.contains("bigint")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_LONG;
            }
            return ColumnType.LONG;
        } else if (t.contains("largeint")) {
            return ColumnType.STRING;
        } else if (t.contains("int")) {
            if (column.isNullable()) {
                return ColumnType.INTEGER;
            }
            return ColumnType.INT;
        } else if (t.contains("float")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_FLOAT;
            }
            return ColumnType.FLOAT;
        } else if (t.contains("double")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_DOUBLE;
            }
            return ColumnType.DOUBLE;
        } else if (t.contains("timestamp")) {
            return ColumnType.TIMESTAMP;
        } else if (t.contains("date")) {
            return ColumnType.STRING;
        } else if (t.contains("datetime")) {
            return ColumnType.STRING;
        } else if (t.contains("decimal")) {
            return ColumnType.DECIMAL;
        } else if (t.contains("time")) {
            return ColumnType.DOUBLE;
        }
        return ColumnType.STRING;
    }

    // 转换为 DB 的数据类型 
    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "boolean";
            case BYTE:
            case JAVA_LANG_BYTE:
                return "tinyint";
            case SHORT:
            case JAVA_LANG_SHORT:
                return "smallint";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case DECIMAL:
                return "decimal";
            case INT:
            case INTEGER:
                return "int";
            case TIMESTAMP:
                return "timestamp";
            default:
                return "varchar";
        }
    }
}


```


HiveDriver
```java

public class HiveDriver extends AbstractJdbcDriver implements Driver {
    // 获取表的信息
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
    
    
//    根据库名称获取表的信息
    @Override
    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql = dbQuery.tablesSql(schemaName);
        try {
            //此步骤是为了切换数据库    与其他数据源有所区别 
            execute(String.format(HiveConstant.USE_DB, schemaName));
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return tableList;
    }

    //获取所有数据库 和 库下所有表
    @Override
    public List<Schema> getSchemasAndTables() {
        return listSchemas();
    }

    //获取所有数据库
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
                        // 获取库下的所有表 存储到schema中
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

    // 根据库名表名获取表的列信息    
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

    //获取建表的SQL
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
    public int executeUpdate(String sql) throws Exception {
        Asserts.checkNullString(sql, "Sql 语句为空");
        String querySQL = sql.trim().replaceAll(";$", "");
        int res = 0;
        try (Statement statement = conn.createStatement()) {
            res = statement.executeUpdate(querySQL);
        }
        return res;
    }

    @Override
    public JdbcSelectResult query(String sql, Integer limit) {
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
            String querySQL = sql.trim().replaceAll(";$", "");
            preparedStatement = conn.prepareStatement(querySQL);
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
                    data.put(columns.get(i).getName(), getTypeConvert().convertValue(results, columns.get(i).getName(), columns.get(i).getType()));
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
        } finally {
            close(preparedStatement, results);
            result.setRowData(datas);
            return result;
        }    }

    @Override
    public IDBQuery getDBQuery() {
        return new HiveQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new HiveTypeConvert();
    }

    // 获取驱动类
    @Override
    String getDriverClass() {
        return "org.apache.hive.jdbc.HiveDriver";
    }

    // 改数据源表的类型  此处必须
    @Override
    public String getType() {
        return "Hive";
    }
    // 改数据源表的名称  此处必须
    @Override
    public String getName() {
        return "Hive";
    }

    // 映射 Flink 的数据类型
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



```


HiveQuery
```java


public class HiveQuery extends AbstractDBQuery {
    // 获取所有数据库的查询SQL 需要与 常量类(HiveConstant) 联合使用
    @Override
    public String schemaAllSql() {
        return HiveConstant.QUERY_ALL_DATABASE;
    }

    // 获取库下的所有表的SQL 需要与 常量类(HiveConstant) 联合使用
    @Override
    public String tablesSql(String schemaName) {
        return HiveConstant.QUERY_ALL_TABLES_BY_SCHEMA;
    }
    
    // 动态构建获取表的所有列的SQL 需要与 常量类(HiveConstant) 联合使用
    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(HiveConstant.QUERY_TABLE_SCHEMA, schemaName, tableName);
    }
    
    // 获取所有数据库的 result title
    @Override
    public String schemaName() {
        return "database_name";
    }

    // 获取建表语句的 result title
    @Override
    public String createTableName() {
        return "createtab_stmt";
    }

    // 获取所有表的 result title
    @Override
    public String tableName() {
        return "tab_name";
    }
    
    // 获取表注释的 result title
    @Override
    public String tableComment() {
        return "comment";
    }

    // 获取列名的 result title
    @Override
    public String columnName() {
        return "col_name";
    }

    // 获取列的数据类型的  result title
    @Override
    public String columnType() {
        return "data_type";
    }

    // 获取列注释的 result title
    @Override
    public String columnComment() {
        return "comment";
    }

}

```

- 以上代码开发完成 在此模块下进行测试  建议使用单元测试进行测试 , 如您 PR , 则测试用例必须提交,同时需要注意敏感信息的处理

- 通过仅仅上述的代码可知 Dinky 的代码扩展性极强 , 可根据数据源的特性随时重写父类的方法 , 进而轻松实现扩展




- 如果上述过程 测试没有问题 需要整合进入项目中 , 请看以下步骤:
- 打包相关配置 修改如下:
  - 在 **dlink-core** 模块的 **pom.xml** 下 , 找到扩展数据源相关的依赖 `放在一起方便管理` 并且新增如下内容:
```xml
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>模块名称</artifactId>
            <scope>${scope.runtime}</scope>
        </dependency>
``` 
  - 在 **dlink** 根下 **pom.xml** 中 ,新增如下内容:
```xml
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>模块名称</artifactId>
            <version>${project.version}</version>
        </dependency>
```

  - 在 **dlink-assembly** 模块中 , 找到 ``package.xml`` 路径: **/dlink-assembly/src/main/assembly/package.xml** , 新增如下内容:
```xml
        <fileSet>
            <directory>${project.parent.basedir}/dlink-metadata/模块名称/target
            </directory>
            <outputDirectory>lib</outputDirectory>
            <includes>
                <include>模块名称-${project.version}.jar</include>
            </includes>
        </fileSet>
  ```

至此 如您的代码没有问题的话 后端代码扩展就已经完成了 !

----

## 前端开发
- **dlink-web** 为 Dinky 的前端模块
- 扩展数据源相关表单所在路径: `dlink-web/src/pages/DataBase/`
  - 修改 `dlink-web/src/pages/DataBase/components/DBForm.tsx` 的 **const data** 中 添加如下:
eg:
```shell
         {
           type: 'Hive', 
         },
```
如下图:
![extened_datasource_dbform](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/function_expansion/datasource/extened_datasource_dbform.jpg)

注意: ``此处数据源类型遵照大驼峰命名规则``

  - 添加数据源logo图片
    - 路径: `dlink-web/public/database/`
    - logo 图下载参考: [https://www.iconfont.cn](https://www.iconfont.cn) 
    - logo 图片存放位置: `dlink-web/public/database`   
  - 修改 `dlink-web/src/pages/DataBase/DB.ts` , 添加如下:
eg:
```shell
    case 'hive':  
      imageUrl += 'hive.png'; # 需要和添加的图片名称保持一致
      break;
```
如下图:
![extened_datasource_datasourceform](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/function_expansion/datasource/extened_datasource_datasourceform.jpg)
   - 创建数据源相关表单属性在: `dlink-web/src/pages/DataBase/components/DataBaseForm.tsx` 此处无需修改

----

:::info 说明
至此 , 基于 Dinky 扩展数据源完成 , 如您也有扩展需求 ,请参照如何 [[Issuse]](../../developer_guide/contribution/issue)  和如何  [[提交 PR]](../../developer_guide/contribution/pull_request)
:::
