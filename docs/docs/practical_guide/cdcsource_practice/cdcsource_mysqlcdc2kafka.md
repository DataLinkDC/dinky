---
sidebar_position: 8
position: 8
id: cdcsource_mysqlcdc2kafka
title: MySQLCDC 整库到 Kafka
---




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
