---
sidebar_position: 11
position: 11
id: cdcsource_mysqlcdc2hivecatalog
title: MySQLCDC 整库到 HiveCatalog
---





### 整库同步到 HiveCatalog

```sql showLineNumbers
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
  'sink.catalog.name' = 'hive',
  'sink.catalog.type' = 'hive',
  'sink.sink.db' = 'hdb',
  'sink.catalog.hive-conf-dir' = '/usr/local/dlink/hive-conf'
);
```