---
sidebar_position: 4
position: 4
id: cdcsource_mysqlcdc2hudi
title: MySQLCDC 整库到 Hudi
---

## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Hudi 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Hudi connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Hudi 表
- 该示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等。
- 该示例参数中的 `#{schemaName}` 为占位符，实际执行时会替换为实际库名，如 `test` 等。
- 该示例参数中的 `#{pkList}` 为占位符，实际执行时会替换为实际表的主键字段，如 `cid.sid` 等。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 Hudi 连接器官方文档进行配置。并请遵守整库同步的规范.


> 该示例为 mysql 整库同步到 Hudi 并异步到 Hive，且写入与源相同名的库，在目标表名前缀为 schema 值。其中 `#{pkList}`
，表示把每个表的主键字段用`.`号拼接起来，如表主键为 `cid` 和 `sid` 则表示为 `cid.sid` ，专门用于 hudi 指定recordkey.field
参数。

```sql showLineNumbers
EXECUTE CDCSOURCE demo_hudi WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '10000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'database-name'='bigdata',
 'table-name'='bigdata\.products,bigdata\.orders',
 'sink.connector'='hudi',
 'sink.path'='hdfs://nameservice1/data/hudi/#{tableName}',
 'sink.hoodie.datasource.write.recordkey.field'='#{pkList}',
 'sink.hoodie.parquet.max.file.size'='268435456',
 'sink.write.tasks'='1',
 'sink.write.bucket_assign.tasks'='2',
 'sink.write.precombine'='true',
 'sink.compaction.async.enabled'='true',
 'sink.write.task.max.size'='1024',
 'sink.write.rate.limit'='3000',
 'sink.write.operation'='upsert',
 'sink.table.type'='COPY_ON_WRITE',
 'sink.compaction.tasks'='1',
 'sink.compaction.delta_seconds'='20',
 'sink.compaction.async.enabled'='true',
 'sink.read.streaming.skip_compaction'='true',
 'sink.compaction.delta_commits'='20',
 'sink.compaction.trigger.strategy'='num_or_time',
 'sink.compaction.max_memory'='500',
 'sink.changelog.enabled'='true',
 'sink.read.streaming.enabled'='true',
 'sink.read.streaming.check.interval'='3',
 'sink.hive_sync.skip_ro_suffix' = 'true', 
 'sink.hive_sync.enable'='true',
 'sink.hive_sync.mode'='hms',
 'sink.hive_sync.metastore.uris'='thrift://bigdata1:9083',
 'sink.hive_sync.db'='qhc_hudi_ods',
 'sink.hive_sync.table'='#{tableName}',
 'sink.table.prefix.schema'='true'
)
```