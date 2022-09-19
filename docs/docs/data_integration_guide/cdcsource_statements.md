---
sidebar_position: 7
id: cdcsource_statements
title: CDCSOURCE 整库同步

---

## 设计背景

目前通过 FlinkCDC 进行会存在诸多问题，如需要定义大量的 DDL 和编写大量的 INSERT INTO，更为严重的是会占用大量的数据库连接，对 Mysql 和网络造成压力。

Dinky 定义了 CDCSOURCE 整库同步的语法，该语法和 CDAS 作用相似，可以直接自动构建一个整库入仓入湖的实时任务，并且对 source 进行了合并，不会产生额外的 Mysql 及网络压力，支持对任意 sink 的同步，如 kafka、doris、hudi、jdbc 等等

## 原理

### source 合并

![source_merge](http://www.aiwenmo.com/dinky/docs/zh-CN/data_integration_guide/cdcsource_statementssource_merge.png)

   面对建立的数据库连接过多，Binlog 重复读取会造成源库的巨大压力，上文分享采用了 source 合并的优化，尝试合并同一作业中的 source，如果都是读的同一数据源，则会被合并成一个 source 节点。

​    Dinky 采用的是只构建一个 source，然后根据 schema、database、table 进行分流处理，分别 sink 到对应的表。

### 元数据映射

Dinky 是通过自身的数据源中心的元数据功能捕获源库的元数据信息，并同步构建 sink 阶段 datastream 或 tableAPI 所使用的 FlinkDDL。

![meta_mapping](http://www.aiwenmo.com/dinky/docs/zh-CN/data_integration_guide/cdcsource_statementsmeta_mapping.png)

### 多种 sink 方式

Dinky 提供了各式各样的 sink 方式，通过修改语句参数可以实现不同的 sink 方式。Dinky 支持通过 DataStream 来扩展新的 sink，也可以使用 FlinkSQL 无需修改代码直接扩展新的 sink。

![sink](http://www.aiwenmo.com/dinky/docs/zh-CN/data_integration_guide/cdcsource_statementssink.png)

## EXECUTE CDCSOURCE 基本使用

CDCSOURCE 语句用于将上游指定数据库的所有表的数据采用一个任务同步到下游系统。整库同步默认支持 Standalone、Yarn Session、Yarn Per job、K8s Session

### 说明
```
# 将下面 Dinky根目录下 整库同步依赖包放置 $FLINK_HOME/lib下
jar/dlink-client-base-${version}.jar
jar/dlink-common-${version}.jar
lib/dlink-client-${version}.jar
```


### 语法结构

```sql
EXECUTE CDCSOURCE jobname 
  WITH ( key1=val1, key2=val2, ...)
```



###  With 参数说明

WITH 参数通常用于指定 CDCSOURCE 所需参数，语法为`'key1'='value1', 'key2' = 'value2'`的键值对。

**配置项**

| 配置项            | 是否必须 | 默认值        | 说明                                                         |
| ----------------- | -------- | ------------- | ------------------------------------------------------------ |
| connector         | 是       | 无            | 指定要使用的连接器，当前支持 mysql-cdc 及 oracle-cdc         |
| hostname          | 是       | 无            | 数据库服务器的 IP 地址或主机名                               |
| port              | 是       | 无            | 数据库服务器的端口号                                         |
| username          | 是       | 无            | 连接到数据库服务器时要使用的数据库的用户名                   |
| password          | 是       | 无            | 连接到数据库服务器时要使用的数据库的密码                     |
| scan.startup.mode | 否       | latest-offset | 消费者的可选启动模式，有效枚举为“initial”和“latest-offset”   |
| database-name     | 否       | 无            | 如果table-name="test\\.student,test\\.score",此参数可选。    |
| table-name        | 否       | 无            | 支持正则,示例:"test\\.student,test\\.score"                  |
| source.*          | 否       | 无            | 指定个性化的 CDC 配置，如 source.server-time-zone 即为 server-time-zone 配置参数。 |
| checkpoint        | 否       | 无            | 单位 ms                                                      |
| parallelism       | 否       | 无            | 任务并行度                                                   |
| sink.connector    | 是       | 无            | 指定 sink 的类型，如 datastream-kafka、datastream-doris、datastream-hudi、kafka、doris、hudi、jdbc 等等，以 datastream- 开头的为 DataStream 的实现方式 |
| sink.sink.db      | 否       | 无            | 目标数据源的库名，不指定时默认使用源数据源的库名             |
| sink.table.prefix | 否       | 无            | 目标表的表名前缀，如 ODS_ 即为所有的表名前拼接 ODS_          |
| sink.table.suffix | 否       | 无            | 目标表的表名后缀                                             |
| sink.table.upper  | 否       | 无            | 目标表的表名全大写                                           |
| sink.table.lower  | 否       | 无            | 目标表的表名全小写                                           |
| sink.*            | 否       | 无            | 目标数据源的配置信息，同 FlinkSQL，使用 ${schemaName} 和 ${tableName} 可注入经过处理的源表名 |
| sink[N].*         | 否       | 无            | N代表为多目的地写入, 默认从0开始到N, 其他配置参数信息参考sink.*的配置. |

## 示例

**实时数据合并至一个 kafka topic**

```sql
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'dlink',
  'password' = 'dlink',
  'checkpoint' = '3000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector'='datastream-kafka',
  'sink.topic'='dlinkcdc',
  'sink.brokers'='127.0.0.1:9092'
)
```



**实时数据同步至对应 kafka topic**

```sql
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'dlink',
  'password' = 'dlink',
  'checkpoint' = '3000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector'='datastream-kafka',
  'sink.brokers'='127.0.0.1:9092'
)
```

**实时数据 DataStream 入仓 Doris**

```sql
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'dlink',
  'password' = 'dlink',
  'checkpoint' = '3000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'datastream-doris',
  'sink.fenodes' = '127.0.0.1:8030',
  'sink.username' = 'root',
  'sink.password' = 'dw123456',
  'sink.sink.batch.size' = '1',
  'sink.sink.max-retries' = '1',
  'sink.sink.batch.interval' = '60000',
  'sink.sink.db' = 'test',
  'sink.table.prefix' = 'ODS_',
  'sink.table.upper' = 'true',
  'sink.sink.enable-delete' = 'true'
)
```

**实时数据 FlinkSQL 入仓 Doris**

```sql
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'dlink',
  'password' = 'dlink',
  'checkpoint' = '3000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'doris',
  'sink.fenodes' = '127.0.0.1:8030',
  'sink.username' = 'root',
  'sink.password' = 'dw123456',
  'sink.sink.batch.size' = '1',
  'sink.sink.max-retries' = '1',
  'sink.sink.batch.interval' = '60000',
  'sink.sink.db' = 'test',
  'sink.table.prefix' = 'ODS_',
  'sink.table.upper' = 'true',
  'sink.table.identifier' = '${schemaName}.${tableName}',
  'sink.sink.enable-delete' = 'true'
)
```

**实时数据入湖 Hudi**

```sql
EXECUTE CDCSOURCE demo WITH (
'connector' = 'mysql-cdc',
'hostname' = '127.0.0.1',
'port' = '3306',
'username' = 'root',
'password' = '123456',
'source.server-time-zone' = 'UTC',
'checkpoint'='1000',
'scan.startup.mode'='initial',
'parallelism'='1',
'database-name'='data_deal',
'table-name'='data_deal\.stu,data_deal\.stu_copy1',
'sink.connector'='hudi',
'sink.path'='hdfs://cluster1/tmp/flink/cdcdata/${tableName}',
'sink.hoodie.datasource.write.recordkey.field'='id',
'sink.hoodie.parquet.max.file.size'='268435456',
'sink.write.precombine.field'='update_time',
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
'sink.hive_sync.enable'='true',
'sink.hive_sync.mode'='hms',
'sink.hive_sync.db'='cdc_ods',
'sink.hive_sync.table'='${tableName}',
'sink.table.prefix.schema'='true',
'sink.hive_sync.metastore.uris'='thrift://cdh.com:9083',
'sink.hive_sync.username'='flinkcdc'
)
```

同时将CDCSOURCE数据写入到Doirs和Kafka

```sql
EXECUTE CDCSOURCE jobname WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'dlink',
  'password' = 'dlink',
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
  'sink[0].table.identifier' = '${schemaName}.${tableName}',
  'sink[0].sink.enable-delete' = 'true',
  'sink[1].connector'='datastream-kafka',
  'sink[1].topic'='dlinkcdc',
  'sink[1].brokers'='127.0.0.1:9092'
)
```

:::tip 说明

- 按照示例格式书写，且一个 FlinkSQL 任务只能写一个 CDCSOURCE。
- 配置项中的英文逗号前不能加空格，需要紧随右单引号。
- 禁用全局变量、语句集、批模式。
- 目前不支持 Application 模式，后续支持。

:::
