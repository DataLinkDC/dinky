---
sidebar_position: 13
position: 13
id: cdcsource_mysqlcdc2multiplesink
title: MySQLCDC 整库到 多 Sink
---


## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Kafka 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Kafka connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Doris 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Doris connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Doris 表 和 Kafka topic
- 下述示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等,用于指定 Kafka topic 名称/目标表名。
- 下述示例参数中的 `#{schemaName}` 为占位符，实际执行时会替换为实际库名，如 `test` 等。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 Kafka 连接器官方文档进行配置。并请遵守整库同步的规范.


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
  'sink[0].table.identifier' = '#{schemaName}.#{tableName}',
  'sink[0].sink.label-prefix' = '#{schemaName}_#{tableName}_1',
  'sink[0].sink.enable-delete' = 'true',
  'sink[1].connector'='datastream-kafka',
  'sink[1].topic'='cdc',
  'sink[1].brokers'='127.0.0.1:9092'
)
```