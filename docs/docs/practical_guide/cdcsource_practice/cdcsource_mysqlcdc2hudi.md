---
sidebar_position: 4
position: 4
id: cdcsource_mysqlcdc2hudi
title: MySQLCDC 整库到 Hudi
---



### 整库同步到 Apache Hudi

该示例为 mysql 整库同步到 Hudi 并异步到 Hive，且写入与源相同名的库，在目标表名前缀为 schema 值。其中 `${pkList}`
，表示把每个表的主键字段用`.`号拼接起来，如表主键为 `cid` 和 `sid` 则表示为 `cid.sid` ，专门用于 hudi 指定recordkey.field
参数。

```sql
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
 'sink.path'='hdfs://nameservice1/data/hudi/${tableName}',
 'sink.hoodie.datasource.write.recordkey.field'='${pkList}',
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
 'sink.hive_sync.table'='${tableName}',
 'sink.table.prefix.schema'='true'
)
```