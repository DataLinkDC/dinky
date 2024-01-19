---
sidebar_position: 9
position: 9
id: cdcsource_mysqlcdc2pg
title: MySQLCDC 整库到 PostgreSQL
---


## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Jdbc 的 PostgreSQL connector jar。如果提交模式为 Application/Per-Job，请确保 PostgreSQL connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Doris 表，且写入名为 ods 的库，目标表名前缀取 `test__` 并转小写。
- 该示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 Doris 连接器官方文档进行配置。并请遵守整库同步的规范.

```sql showLineNumbers
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
 'sink.table-name' = '#{tableName}',
 'sink.driver' = 'org.postgresql.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5'
)
```