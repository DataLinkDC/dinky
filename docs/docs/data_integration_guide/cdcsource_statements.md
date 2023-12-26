---
sidebar_position: 1
id: cdcsource_statements
title: CDCSOURCE 整库同步

---

## 设计背景

目前通过 FlinkCDC 进行会存在诸多问题，如需要定义大量的 DDL 和编写大量的 INSERT INTO，更为严重的是会占用大量的数据库连接，对 Mysql 和网络造成压力。

Dinky 定义了 CDCSOURCE 整库同步的语法，该语法和 CDAS 作用相似，可以直接自动构建一个整库入仓入湖的实时任务，并且对 source 进行了合并，不会产生额外的 Mysql 及网络压力，支持对任意 sink 的同步，如 kafka、doris、hudi、jdbc 等等

## 原理

### source 合并

![source_merge](http://pic.dinky.org.cn/dinky/docs/zh-CN/data_integration_guide/cdcsource_statementssource_merge.png)

   面对建立的数据库连接过多，Binlog 重复读取会造成源库的巨大压力，上文分享采用了 source 合并的优化，尝试合并同一作业中的 source，如果都是读的同一数据源，则会被合并成一个 source 节点。

​    Dinky 采用的是只构建一个 source，然后根据 schema、database、table 进行分流处理，分别 sink 到对应的表。

### 元数据映射

Dinky 是通过自身的数据源中心的元数据功能捕获源库的元数据信息，并同步构建 sink 阶段 datastream 或 tableAPI 所使用的 FlinkDDL。

![meta_mapping](http://pic.dinky.org.cn/dinky/docs/zh-CN/data_integration_guide/cdcsource_statementsmeta_mapping.png)

### 多种 sink 方式

Dinky 提供了各式各样的 sink 方式，通过修改语句参数可以实现不同的 sink 方式。Dinky 支持通过 DataStream 来扩展新的 sink，也可以使用 FlinkSQL 无需修改代码直接扩展新的 sink。

![sink](http://pic.dinky.org.cn/dinky/docs/zh-CN/data_integration_guide/cdcsource_statementssink.png)

## 环境准备

### 作业配置

禁用全局变量、禁用语句集、禁用批模式。

### Flink 版本区分

目前 dlink-client-1.14 内的整库同步能力最多且主要维护，如果要使用其他 flink 版本的整库同步，如果 SQLSink 不满足需求，需要DataStreamSink 支持，请手动仿照 dlink-client-1.14 扩展相应代码实现，很简单。

### 其他 FlinkCDC 支持

目前 dlink-client-1.14 内默认实现常用的 Flink CDC，如 MysqlCDC、OracleCDC、PostgresCDC 和 SQLServerCDC，如果要使用其他 FlinkCDC，请在 Dinky 源码中仿照 MysqlCDC 进行扩展，很简单。

### 依赖上传

由于 CDCSOURCE 是 Dinky 封装的新功能，Apache Flink 源码不包含，非 Application 模式提交需要在远程 Flink 集群所使用的依赖里添加一下依赖：

```
# 将下面 Dinky根目录下 整库同步依赖包放置 $FLINK_HOME/lib下
lib/dlink-client-base-${version}.jar
lib/dlink-common-${version}.jar
plugins/flink-${flink-version}/dlink-client-${version}.jar
```

### Application 模式提交

>  目前已经支持 `application` ，需提前准备好相关jar包，或者和 `add jar`语法并用。以 `mysqlcdc-2.3.0` 和 `flink-1.14 ` 为例，需要以下 jar

* flink-shaded-guava-18.0-13.0.jar

* HikariCP-4.0.3.jar

* druid-1.2.8.jar

* dlink-metadata-mysql-0.7.2.jar

* dlink-metadata-base-0.7.2.jar

* jackson-datatype-jsr310-2.13.4.jar

* flink-sql-connector-mysql-cdc-2.3.0.jar

* dlink-client-1.14-0.7.2.jar

  ![cdcsource_example.png](http://pic.dinky.org.cn/dinky/dev/docs/cdcsource_example.png)

### 注意事项

一个 FlinkSQL 任务只能写一个 CDCSOURCE，CDCSOURCE 前可写 set、add jar 和 ddl 语句。

配置项中的英文逗号前不能加空格，需要紧随右单引号。

## 配置参数

| 配置项                         | 是否必须 | 默认值        | 说明                                                         |
| ------------------------------ | -------- | ------------- | ------------------------------------------------------------ |
| connector                      | 是       | 无            | 指定要使用的连接器                                           |
| hostname                       | 是       | 无            | 数据库服务器的 IP 地址或主机名                               |
| port                           | 是       | 无            | 数据库服务器的端口号                                         |
| username                       | 是       | 无            | 连接到数据库服务器时要使用的数据库的用户名                   |
| password                       | 是       | 无            | 连接到数据库服务器时要使用的数据库的密码                     |
| scan.startup.mode              | 否       | latest-offset | 消费者的可选启动模式，有效枚举为“initial”和“latest-offset”   |
| database-name                  | 否       | 无            | 此参数非必填                                                 |
| table-name                     | 否       | 无            | 只支持正则,示例:"test\\.student,test\\.score"，所有表示例:"test\\..*" |
| source.*                       | 否       | 无            | 指定个性化的 CDC 配置，如 source.server-time-zone 即为 server-time-zone 配置参数。 |
| checkpoint                     | 否       | 无            | 单位 ms                                                      |
| parallelism                    | 否       | 无            | 任务并行度                                                   |
| sink.connector                 | 是       | 无            | 指定 sink 的类型，如 datastream-kafka、datastream-doris、datastream-hudi、kafka、doris、hudi、jdbc 等等，以 datastream- 开头的为 DataStream 的实现方式 |
| sink.sink.db                   | 否       | 无            | 目标数据源的库名，不指定时默认使用源数据源的库名             |
| sink.table.prefix              | 否       | 无            | 目标表的表名前缀，如 ODS_ 即为所有的表名前拼接 ODS_          |
| sink.table.suffix              | 否       | 无            | 目标表的表名后缀                                             |
| sink.table.upper               | 否       | false         | 目标表的表名全大写                                           |
| sink.table.lower               | 否       | false         | 目标表的表名全小写                                           |
| sink.auto.create               | 否       | false         | 目标数据源自动建表，目前只支持 Mysql，其他可自行扩展         |
| sink.timezone                  | 否       | UTC           | 指定目标数据源的时区，在数据类型转换时自动生效               |
| sink.column.replace.line-break | 否       | false         | 指定是否去除换行符，即在数据转换中进行 REGEXP_REPLACE(column, '\\n', '') |
| sink.*                         | 否       | 无            | 目标数据源的配置信息，同 FlinkSQL，使用 ${schemaName} 和 ${tableName} 可注入经过处理的源表名 |
| sink[N].*                      | 否       | 无            | N代表为多数据源写入, 默认从0开始到N, 其他配置参数信息参考sink.*的配置. |

## 经典示例

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

### 整库同步到 Apache Doris

#### 普通同步

Doris 的 Flink 连接器参数随版本变化较大，以下为 Doris 1.2.0 版本的参数配置。

每次提交作业都需要手动修改 `'sink.sink.label-prefix' = '${schemaName}_${tableName}_1'` 的值，比如改变尾部的数值。

```sql
EXECUTE CDCSOURCE demo_doris WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'doris',
  'sink.fenodes' = '127.0.0.1:8030',
  'sink.username' = 'root',
  'sink.password' = '123456',
  'sink.doris.batch.size' = '1000',
  'sink.sink.max-retries' = '1',
  'sink.sink.batch.interval' = '60000',
  'sink.sink.db' = 'test',
  'sink.sink.properties.format' ='json',
  'sink.sink.properties.read_json_by_line' ='true',
  'sink.table.identifier' = '${schemaName}.${tableName}',
  'sink.sink.label-prefix' = '${schemaName}_${tableName}_1'
);
```

#### 字段模式演变

自动同步列新增和删除列，库表名需要与源库相同。

```sql
EXECUTE CDCSOURCE demo_doris_schema_evolution WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'datastream-doris-schema-evolution',
  'sink.fenodes' = '127.0.0.1:8030',
  'sink.username' = 'root',
  'sink.password' = '123456',
  'sink.doris.batch.size' = '1000',
  'sink.sink.max-retries' = '1',
  'sink.sink.batch.interval' = '60000',
  'sink.sink.db' = 'test',
  'sink.table.identifier' = '${schemaName}.${tableName}'
);
```

### 整库同步到 Apache Hudi

该示例为 mysql 整库同步到 Hudi 并异步到 Hive，且写入与源相同名的库，在目标表名前缀为 schema 值。其中 `${pkList}`，表示把每个表的主键字段用`.`号拼接起来，如表主键为 `cid` 和 `sid` 则表示为 `cid.sid` ，专门用于 hudi 指定recordkey.field 参数。

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

### 整库同步到 StarRocks

该示例是将 mysql 整库同步到 StarRocks 表，且写入名为 ods 的库，目标表名前缀取 `ods_` 并转小写。

```sql
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
  'sink.table-name' = '${tableName}',
  'sink.sink.properties.format' = 'json',
  'sink.sink.properties.strip_outer_array' = 'true',
  'sink.sink.max-retries' = '10',
  'sink.sink.buffer-flush.interval-ms' = '15000',
  'sink.sink.parallelism' = '1'
)
```

### 整库同步到 Mysql

该示例为将 mysql 整库同步到另一个 mysql 数据库，写入 test 库，表名前缀 `test_`，表名全小写，开启自动建表。

```sql
EXECUTE CDCSOURCE cdc_mysql WITH (
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
 'sink.url' = 'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false',
 'sink.username' = 'root',
 'sink.password' = '123456',
 'sink.sink.db' = 'test',
 'sink.table.prefix' = 'test_',
 'sink.table.lower' = 'true',
 'sink.table-name' = '${tableName}',
 'sink.driver' = 'com.mysql.jdbc.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5',
 'sink.auto.create' = 'true'
)
```

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

### 整库同步到 Kafka

#### 汇总到一个 topic

当指定 `sink.topic` 参数时，所有 Change Log 会被写入这一个 topic。

```sql
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

#### 同步到对应 topic

当不指定 `sink.topic` 参数时，所有 Change Log 会被写入对应库表名的 topic。

```sql
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

#### 使用 FlinkSQL 同步到对应 topic

```sql
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
 'sink.topic' = '${tableName}',
 'sink.properties.bootstrap.servers' = 'bigdata2:9092,bigdata3:9092,bigdata4:9092',
 'sink.key.format' = 'avro',
 'sink.value.format' = 'avro'
)
```

### 整库同步到 PostgreSQL

```sql
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
 'sink.table-name' = '${tableName}',
 'sink.driver' = 'org.postgresql.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5'
)
```

### 整库同步到 ClickHouse

```sql
EXECUTE CDCSOURCE cdc_clickhouse WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.products,bigdata\.orders',
  'sink.connector' = 'clickhouse',
  'sink.url' = 'clickhouse://127.0.0.1:8123',
  'sink.username' = 'default',
  'sink.password' = '123456',
  'sink.sink.db' = 'test',
  'sink.table.prefix' = 'test_',
  'sink.table.lower' = 'true',
  'sink.database-name' = 'test',
  'sink.table-name' = '${tableName}',
  'sink.sink.batch-size' = '500',
  'sink.sink.flush-interval' = '1000',
  'sink.sink.max-retries' = '3'
)
```

### 整库同步到 HiveCatalog

```sql
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
  'sink.catalog.name' = 'hive',
  'sink.catalog.type' = 'hive',
  'sink.default-database' = 'hdb',
  'sink.hive-conf-dir' = '/usr/local/dlink/hive-conf'
);
```

### 整库同步到 Flink Table Store (Apache Paimon)

```sql
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

### 整库同步到 DlinkCatalog

```sql
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
  'sink.catalog.name' = 'dlinkmysql',
  'sink.catalog.type' = 'dlink_mysql',
  'sink.catalog.username' = 'dlink',
  'sink.catalog.password' = 'dlink',
  'sink.catalog.url' = 'jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC',
  'sink.sink.db' = 'default_database'
);
```

### 整库同步到两个数据源

```sql
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
  'sink[0].table.identifier' = '${schemaName}.${tableName}',
  'sink[0].sink.label-prefix' = '${schemaName}_${tableName}_1',
  'sink[0].sink.enable-delete' = 'true',
  'sink[1].connector'='datastream-kafka',
  'sink[1].topic'='cdc',
  'sink[1].brokers'='127.0.0.1:9092'
)
```

## 常见问题

### 如何确认整库同步任务提交成功

查看 FlinkWeb 的 JobGraph 是否包含 Sink，如不包含则说明在构建 Sink 时出错，到 `配置中心-系统信息-Logs` 查看后台日志，寻找报错原因。

### 多并行度乱序如何解决

设置并行度为1；或者设置目标数据源的相关配置来实现最终一致性，如 Doris Sequence 列。

### 源库DDL变动怎么办

Savepoint Stop/Cancel 作业，然后从最近的 Savepoint/Checkpoint 恢复作业。如果变动过大导致任务无法从保存点正常恢复，在 CDCSOURCE 前添加 `set 'execution.savepoint.ignore-unclaimed-state' = 'true';`。

### 是否支持完整的模式演变

不支持，目前模式演变取决于 Sink 的数据源连接器能力，如 Doris 连接器支持字段级模式演变。

### No operators defined in streaming topology. Cannot execute.

jdbc 连接超时导致无法获取正确的元数据信息，可以重启 Dinky 或者升级到 0.7.2 版本。

### NoClassDefFoundError

排查依赖冲突或者缺少依赖，注意胖包的使用。

### 语法检查和血缘分析未正确显示

当前不支持，只支持作业提交。

### 源码位置

dlink-client 模块下的 cdc 里。

### 其他 cdc 和其他 sink 的支持

FlinkCDC 支持的能力都可以直接在 Dinky 上使用，可自行扩展支持；所有的 Flink SQL Connector 都可以在 CDCSOURCE 中直接使用，无需代码扩展，只需要在参数前追加 `sink.` 即可；其他特殊的 DataStream Connector 可自行扩展。

:::tip 说明

- 若有错误和疏漏请及时提出，如果有其他实践请补充，将同步更新至官网文档，多谢支持。

:::