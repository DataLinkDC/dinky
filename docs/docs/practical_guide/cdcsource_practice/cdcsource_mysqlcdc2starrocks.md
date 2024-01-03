---
sidebar_position: 5
position: 5
id: cdcsource_mysqlcdc2starrocks
title: MySQLCDC 整库到 StarRocks
---



### 整库同步到 StarRocks

该示例是将 mysql 整库同步到 StarRocks 表，且写入名为 ods 的库，目标表名前缀取 `ods_` 并转小写。

```sql
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '3000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'bigdata\.products,bigdata\.orders',
  'sink.connector' = 'starrocks',
  'sink.jdbc-url' = 'jdbc:mysql://127.0.0.1:19035',
  'sink.load-url' = '127.0.0.1:18035',
  'sink.username' = 'root',
  'sink.password' = '123456',
  'sink.sink.db' = 'ods',
  'sink.table.prefix' = 'ods_',
  'sink.table.lower' = 'true',
  'sink.database-name' = 'ods',
  'sink.table-name' = '${tableName}',
  'sink.sink.properties.format' = 'json',
  'sink.sink.properties.strip_outer_array' = 'true',
  'sink.sink.max-retries' = '10',
  'sink.sink.buffer-flush.interval-ms' = '15000',
  'sink.sink.parallelism' = '1'
)
```