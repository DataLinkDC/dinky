---
sidebar_position: 3
position: 3
id: cdcsource_mysqlcdc2doris
title: MySQLCDC 整库到 Doris
---



### 整库同步到 Doris

#### 普通同步

Doris 的 Flink 连接器参数随版本变化较大，以下为 Doris 1.2.0 版本的参数配置。

每次提交作业都需要手动修改 `'sink.sink.label-prefix' = '${schemaName}_${tableName}_1'` 的值，比如改变尾部的数值。

```sql
EXECUTE CDCSOURCE demo_doris WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'doris',
  'sink.fenodes' = '127.0.0.1:8030',
  'sink.username' = 'root',
  'sink.password' = '123456',
  'sink.doris.batch.size' = '1000',
  'sink.sink.max-retries' = '1',
  'sink.sink.batch.interval' = '60000',
  'sink.sink.db' = 'test',
  'sink.sink.properties.format' ='json',
  'sink.sink.properties.read_json_by_line' ='true',
  'sink.table.identifier' = '${schemaName}.${tableName}',
  'sink.sink.label-prefix' = '${schemaName}_${tableName}_1'
);
```

#### 字段模式演变

自动同步列新增和删除列，库表名需要与源库相同。

```sql
EXECUTE CDCSOURCE demo_doris_schema_evolution WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'datastream-doris-schema-evolution',
  'sink.fenodes' = '127.0.0.1:8030',
  'sink.username' = 'root',
  'sink.password' = '123456',
  'sink.doris.batch.size' = '1000',
  'sink.sink.max-retries' = '1',
  'sink.sink.batch.interval' = '60000',
  'sink.sink.db' = 'test',
  'sink.table.identifier' = '${schemaName}.${tableName}'
);
```
