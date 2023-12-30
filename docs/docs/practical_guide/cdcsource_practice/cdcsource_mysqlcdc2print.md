---
sidebar_position: 2
id: cdcsource_mysqlcdc2print
position: 2
title: MySQLCDC 整库到 Print
---

### 整库同步到 Print

常用于快速简单调试。

```sql
EXECUTE CDCSOURCE demo_print WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'print'
);
```