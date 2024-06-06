---
sidebar_position: 12
position: 12
id: cdcsource_mysqlcdc2paimon
title: MySQLCDC 整库到 Paimon
---

## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Paimon 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Paimon connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Paimon 表
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 Paimon 连接器官方文档进行配置。并请遵守整库同步的规范.


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
  'sink.catalog.name' = 'fts',
  'sink.catalog.type' = 'table-store',
  'sink.catalog.warehouse'='file:/tmp/table_store'
);
```

或者

> 此方式可自动建表, 自动建表由 Paimon 完成, dinky 不介入

```sql showLineNumbers

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