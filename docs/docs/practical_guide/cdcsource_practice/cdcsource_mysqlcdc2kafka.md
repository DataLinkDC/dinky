---
sidebar_position: 8
position: 8
id: cdcsource_mysqlcdc2kafka
title: MySQLCDC 整库到 Kafka
---


## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Kafka 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Kafka connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Kafka topic
- 下述示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等,用于指定 Kafka topic 名称。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 Kafka 连接器官方文档进行配置。并请遵守整库同步的规范.

### 汇总到一个 topic

当指定 `sink.topic` 参数时，所有 Change Log 会被写入这一个 topic。

```sql showLineNumbers
EXECUTE CDCSOURCE cdc_kafka_one WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
 'sink.connector'='datastream-kafka',
 'sink.topic'='cdctest',
 'sink.brokers'='bigdata2:9092,bigdata3:9092,bigdata4:9092'
)
```

### 同步到对应 topic

当不指定 `sink.topic` 参数时，所有 Change Log 会被写入对应库表名的 topic。

```sql showLineNumbers
EXECUTE CDCSOURCE cdc_kafka_mul WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
 'sink.connector'='datastream-kafka',
 'sink.brokers'='bigdata2:9092,bigdata3:9092,bigdata4:9092'
)
```

### 使用 FlinkSQL 同步到对应 topic

```sql showLineNumbers
EXECUTE CDCSOURCE cdc_upsert_kafka WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
 'sink.connector' = 'upsert-kafka',
 'sink.topic' = '#{tableName}',
 'sink.properties.bootstrap.servers' = 'bigdata2:9092,bigdata3:9092,bigdata4:9092',
 'sink.key.format' = 'avro',
 'sink.value.format' = 'avro'
)
```
