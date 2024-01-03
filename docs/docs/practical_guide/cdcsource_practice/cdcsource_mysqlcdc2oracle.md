---
sidebar_position: 7
position: 7
id: cdcsource_mysqlcdc2oracle
title: MySQLCDC 整库到 Oracle
---





### 整库同步到 Oracle

该示例将 Oracle 数据库 TEST 下所有表同步到该数据库的 TEST2下。

```sql
EXECUTE CDCSOURCE cdc_oracle WITH (
 'connector' = 'oracle-cdc',
 'hostname' = '127.0.0.1',
 'port' = '1521',
 'username'='root',
 'password'='123456',
 'database-name'='ORCL',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'TEST\..*',
 'connector' = 'jdbc',
 'url' = 'jdbc:oracle:thin:@127.0.0.1:1521:orcl',
 'username' = 'root',
 'password' = '123456',
 'table-name' = 'TEST2.${tableName}'
)
```
