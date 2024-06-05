---
sidebar_position: 12
position: 12
id: cdcsource_mysqlcdc2dinkycatalog
title: MySQLCDC 整库到 DinkyCatalog
---



### 整库同步到 Dinky Catalog

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
  'sink.catalog.name' = 'dlinkmysql',
  'sink.catalog.type' = 'dlink_mysql',
  'sink.catalog.username' = 'dlink',
  'sink.catalog.password' = 'dlink',
  'sink.catalog.url' = 'jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC',
  'sink.sink.db' = 'default_database'
);
```
