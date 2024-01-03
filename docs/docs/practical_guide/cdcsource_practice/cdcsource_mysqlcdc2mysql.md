---
sidebar_position: 6
position: 6
id: cdcsource_mysqlcdc2mysql
title: MySQLCDC 整库到 MySQL
---



### 整库同步到 Mysql

该示例为将 mysql 整库同步到另一个 mysql 数据库，写入 test 库，表名前缀 `test_`，表名全小写，开启自动建表。

```sql
EXECUTE CDCSOURCE cdc_mysql WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
 'sink.connector' = 'jdbc',
 'sink.url' = 'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false',
 'sink.username' = 'root',
 'sink.password' = '123456',
 'sink.sink.db' = 'test',
 'sink.table.prefix' = 'test_',
 'sink.table.lower' = 'true',
 'sink.table-name' = '${tableName}',
 'sink.driver' = 'com.mysql.jdbc.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5',
 'sink.auto.create' = 'true'
)
```

### 整库同步到 Mysql with debezium.skipped.operations


```sql
EXECUTE CDCSOURCE cdc_mysql WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
 'debezium.skipped.operations'='d',
 'sink.connector' = 'jdbc',
 'sink.url' = 'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false',
 'sink.username' = 'root',
 'sink.password' = '123456',
 'sink.sink.db' = 'test',
 'sink.table.prefix' = 'test_',
 'sink.table.lower' = 'true',
 'sink.table-name' = '${tableName}',
 'sink.driver' = 'com.mysql.jdbc.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5',
 'sink.auto.create' = 'true'
)
```