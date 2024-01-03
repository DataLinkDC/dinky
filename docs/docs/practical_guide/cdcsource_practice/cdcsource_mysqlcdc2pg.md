---
sidebar_position: 9
position: 9
id: cdcsource_mysqlcdc2pg
title: MySQLCDC 整库到 PostgreSQL
---



### 整库同步到 PostgreSQL

```sql
EXECUTE CDCSOURCE cdc_postgresql WITH (
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
 'sink.url' = 'jdbc:postgresql://127.0.0.1:5432/test',
 'sink.username' = 'test',
 'sink.password' = '123456',
 'sink.sink.db' = 'test',
 'sink.table.prefix' = 'test_',
 'sink.table.lower' = 'true',
 'sink.table-name' = '${tableName}',
 'sink.driver' = 'org.postgresql.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5'
)
```