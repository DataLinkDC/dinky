---
sidebar_position: 13
position: 13
id: cdcsource_mysqlcdc2multiplesink
title: MySQLCDC 整库到 多 Sink
---





### 整库同步到两个数据源

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
  'table-name' = 'test\.student,test\.score',
  'sink[0].connector' = 'doris',
  'sink[0].fenodes' = '127.0.0.1:8030',
  'sink[0].username' = 'root',
  'sink[0].password' = 'dw123456',
  'sink[0].sink.batch.size' = '1',
  'sink[0].sink.max-retries' = '1',
  'sink[0].sink.batch.interval' = '60000',
  'sink[0].sink.db' = 'test',
  'sink[0].table.prefix' = 'ODS_',
  'sink[0].table.upper' = 'true',
  'sink[0].table.identifier' = '${schemaName}.${tableName}',
  'sink[0].sink.label-prefix' = '${schemaName}_${tableName}_1',
  'sink[0].sink.enable-delete' = 'true',
  'sink[1].connector'='datastream-kafka',
  'sink[1].topic'='cdc',
  'sink[1].brokers'='127.0.0.1:9092'
)
```