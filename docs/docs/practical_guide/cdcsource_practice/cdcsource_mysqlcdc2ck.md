---
sidebar_position: 10
position: 10
id: cdcsource_mysqlcdc2ck
title: MySQLCDC 整库到 ClickHouse
---




### 整库同步到 ClickHouse

```sql
EXECUTE CDCSOURCE cdc_clickhouse WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
  'sink.connector' = 'clickhouse',
  'sink.url' = 'clickhouse://127.0.0.1:8123',
  'sink.username' = 'default',
  'sink.password' = '123456',
  'sink.sink.db' = 'test',
  'sink.table.prefix' = 'test_',
  'sink.table.lower' = 'true',
  'sink.database-name' = 'test',
  'sink.table-name' = '${tableName}',
  'sink.sink.batch-size' = '500',
  'sink.sink.flush-interval' = '1000',
  'sink.sink.max-retries' = '3'
)
```