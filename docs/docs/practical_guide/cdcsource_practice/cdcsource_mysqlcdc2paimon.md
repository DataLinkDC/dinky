---
sidebar_position: 12
position: 12
id: cdcsource_mysqlcdc2paimon
title: MySQLCDC 整库到 Paimon
---



### 整库同步到 Apache Paimon

```sql
EXECUTE CDCSOURCE demo WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\..*',
  'sink.connector' = 'sql-catalog',
  'sink.catalog.name' = 'fts',
  'sink.catalog.type' = 'table-store',
  'sink.catalog.warehouse'='file:/tmp/table_store'
);
```

或者

> 此方式可自动建表
```sql 

EXECUTE CDCSOURCE dinky_paimon_test
WITH
  (
    'connector' = 'mysql-cdc',
    'hostname' = '',
    'port' = '',
    'username' = '',
    'password' = '',
    'checkpoint' = '10000',
    'parallelism' = '1',
    'scan.startup.mode' = 'initial',
    'database-name' = 'dinky',
    'sink.connector' = 'paimon',
    'sink.path' = 'hdfs:/tmp/paimon/#{schemaName}.db/#{tableName}',
    'sink.auto-create' = 'true',
  );


```