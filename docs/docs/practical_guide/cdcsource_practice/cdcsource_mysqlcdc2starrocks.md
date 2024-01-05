---
sidebar_position: 5
position: 5
id: cdcsource_mysqlcdc2starrocks
title: MySQLCDC 整库到 StarRocks
---


## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 StarRocks 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 StarRocks connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在 两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 StarRocks 表，且写入名为 ods 的库，目标表名前缀取 `ods_` 并转小写。
- 该示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 StarRocks 官方文档进行配置。并请遵守整库同步的规范.


```sql showLineNumbers
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '3000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'bigdata\.products,bigdata\.orders',
  'sink.connector' = 'starrocks',
  'sink.jdbc-url' = 'jdbc:mysql://127.0.0.1:19035',
  'sink.load-url' = '127.0.0.1:18035',
  'sink.username' = 'root',
  'sink.password' = '123456',
  'sink.sink.db' = 'ods',
  'sink.table.prefix' = 'ods_',
  'sink.table.lower' = 'true',
  'sink.database-name' = 'ods',
  'sink.table-name' = '#{tableName}',
  'sink.sink.properties.format' = 'json',
  'sink.sink.properties.strip_outer_array' = 'true',
  'sink.sink.max-retries' = '10',
  'sink.sink.buffer-flush.interval-ms' = '15000',
  'sink.sink.parallelism' = '1'
)
```
