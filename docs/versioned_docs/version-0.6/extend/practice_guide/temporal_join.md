---
sidebar_position: 17
id: temporal_join
title: temporal join
---


## temporal join (时态连接)


该案例中，将 upsert kafka 主题 `order_info` 中的数据作为维表数据，然后去关联订单流水表，最后输出完整的订单流水信息数据到 kafka。

### kafka 主题 （order_water）

订单流水表读取的是 kafka `order_water` 主题中的数据，数据内容如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_order_water_data.png)

### kafka 主题 （order_info）

订单信息维表读取的是 kafka `order_info` 主题中的数据，数据内容如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_temporal_join_kafka_dim_data1.png)

### flink sql 语句

```sql
set 'table.local-time-zone' = 'GMT+08:00';
-- 如果 source kafka 主题中有些分区没有数据，就会导致水印无法向下游传播，此时需要手动设置空闲时间
set 'table.exec.source.idle-timeout' = '1 s';

-- 订单流水
CREATE temporary TABLE order_flow(
    id int comment '订单id',
    product_count int comment '购买商品数量',
    one_price double comment '单个商品价格',
    -- 定义订单时间为数据写入 kafka 的时间
    order_time TIMESTAMP_LTZ(3) METADATA FROM 'timestamp' VIRTUAL,
    WATERMARK FOR order_time AS order_time
) WITH (
    'connector' = 'kafka',
    'topic' = 'order_water',
    'properties.bootstrap.servers' = 'node01:9092,node02:9092,node03:9092',
    'properties.group.id' = 'for_source_test',
    'scan.startup.mode' = 'latest-offset',
    'format' = 'csv',
    'csv.field-delimiter' = ' '
)
;

-- 订单信息
create table order_info (
    id int PRIMARY KEY NOT ENFORCED comment '订单id',
    user_name string comment '订单所属用户',
    order_source string comment '订单所属来源',
    update_time TIMESTAMP_LTZ(3) METADATA FROM 'timestamp' VIRTUAL,
    WATERMARK FOR update_time AS update_time
) with (
    'connector' = 'upsert-kafka',
    'topic' = 'order_info',
    'properties.bootstrap.servers' = 'node01:9092,node02:9092,node03:9092',
    'key.format' = 'csv',
    'value.format' = 'csv',
    'value.csv.field-delimiter' = ' '
)
;

-- 创建连接 kafka 的虚拟表作为 sink
create table sink_kafka(
    id int PRIMARY KEY NOT ENFORCED comment '订单id',
    user_name string comment '订单所属用户',
    order_source string comment '订单所属来源',
    product_count int comment '购买商品数量',
    one_price double comment '单个商品价格',
    total_price double comment '总价格'
) with (
    'connector' = 'upsert-kafka',
    'topic' = 'for_sink',
    'properties.bootstrap.servers' = 'node01:9092,node02:9092,node03:9092',
    'key.format' = 'csv',
    'value.format' = 'csv',
    'value.csv.field-delimiter' = ' '
)
;

-- 真正要执行的任务
insert into sink_kafka
select
    order_flow.id,
    order_info.user_name,
    order_info.order_source,
    order_flow.product_count,
    order_flow.one_price,
    order_flow.product_count * order_flow.one_price as total_price
from order_flow
left join order_info FOR SYSTEM_TIME AS OF order_flow.order_time
on order_flow.id = order_info.id
;
```

flink sql 任务运行的 flink UI 界面如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_temporal_join_flink_ui.png)

查看结果写入的 kafka `for_sink` 主题的数据为

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_temporal_join_kafka_sink_data1.png)

此时新增数据到 kafka 维表主题 `order_info` 中，新增的数据如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_temporal_join_kafka_dim_data2.png)

再查看结果写入的 kafka `for_sink` 主题的数据为

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_temporal_join_kafka_sink_data2.png)

**注意**

经过测试发现，当将 upsert kafka 作为 source 时，主题中的数据必须有 **key**，否则会抛出无法反序列化数据的错误，具体如下

```log
[INFO] [2022-07-31 21:18:22][org.apache.flink.runtime.executiongraph.ExecutionGraph]Source: order_info[5] (2/8) (f8b093cf4f7159f9511058eb4b100b2e) switched from RUNNING to FAILED on bbc9c6a6-0a76-4efe-a7ea-0c00a19ab400 @ 127.0.0.1 (dataPort=-1).
java.io.IOException: Failed to deserialize consumer record due to
	at org.apache.flink.connector.kafka.source.reader.KafkaRecordEmitter.emitRecord(KafkaRecordEmitter.java:56) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.kafka.source.reader.KafkaRecordEmitter.emitRecord(KafkaRecordEmitter.java:33) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.base.source.reader.SourceReaderBase.pollNext(SourceReaderBase.java:143) ~[flink-connector-base-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.api.operators.SourceOperator.emitNext(SourceOperator.java:385) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.runtime.io.StreamTaskSourceInput.emitNext(StreamTaskSourceInput.java:68) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.runtime.io.StreamOneInputProcessor.processInput(StreamOneInputProcessor.java:65) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.runtime.tasks.StreamTask.processInput(StreamTask.java:519) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.runtime.tasks.mailbox.MailboxProcessor.runMailboxLoop(MailboxProcessor.java:203) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.runtime.tasks.StreamTask.runMailboxLoop(StreamTask.java:804) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.runtime.tasks.StreamTask.invoke(StreamTask.java:753) ~[flink-streaming-java-1.15.1.jar:1.15.1]
	at org.apache.flink.runtime.taskmanager.Task.runWithSystemExitMonitoring(Task.java:948) ~[flink-runtime-1.15.1.jar:1.15.1]
	at org.apache.flink.runtime.taskmanager.Task.restoreAndInvoke(Task.java:927) ~[flink-runtime-1.15.1.jar:1.15.1]
	at org.apache.flink.runtime.taskmanager.Task.doRun(Task.java:741) ~[flink-runtime-1.15.1.jar:1.15.1]
	at org.apache.flink.runtime.taskmanager.Task.run(Task.java:563) ~[flink-runtime-1.15.1.jar:1.15.1]
	at java.lang.Thread.run(Thread.java:748) ~[?:1.8.0_311]
Caused by: java.io.IOException: Failed to deserialize consumer record ConsumerRecord(topic = order_info, partition = 0, leaderEpoch = 0, offset = 7, CreateTime = 1659273502239, serialized key size = 0, serialized value size = 18, headers = RecordHeaders(headers = [], isReadOnly = false), key = [B@2add8ff2, value = [B@2a633689).
	at org.apache.flink.connector.kafka.source.reader.deserializer.KafkaDeserializationSchemaWrapper.deserialize(KafkaDeserializationSchemaWrapper.java:57) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.kafka.source.reader.KafkaRecordEmitter.emitRecord(KafkaRecordEmitter.java:53) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	... 14 more
Caused by: java.io.IOException: Failed to deserialize CSV row ''.
	at org.apache.flink.formats.csv.CsvRowDataDeserializationSchema.deserialize(CsvRowDataDeserializationSchema.java:162) ~[flink-csv-1.15.1.jar:1.15.1]
	at org.apache.flink.formats.csv.CsvRowDataDeserializationSchema.deserialize(CsvRowDataDeserializationSchema.java:47) ~[flink-csv-1.15.1.jar:1.15.1]
	at org.apache.flink.api.common.serialization.DeserializationSchema.deserialize(DeserializationSchema.java:82) ~[flink-core-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.connectors.kafka.table.DynamicKafkaDeserializationSchema.deserialize(DynamicKafkaDeserializationSchema.java:119) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.kafka.source.reader.deserializer.KafkaDeserializationSchemaWrapper.deserialize(KafkaDeserializationSchemaWrapper.java:54) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.kafka.source.reader.KafkaRecordEmitter.emitRecord(KafkaRecordEmitter.java:53) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	... 14 more
Caused by: org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.exc.MismatchedInputException: No content to map due to end-of-input
 at [Source: UNKNOWN; line: -1, column: -1]
	at org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:59) ~[flink-shaded-jackson-2.12.4-15.0.jar:2.12.4-15.0]
	at org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext.reportInputMismatch(DeserializationContext.java:1601) ~[flink-shaded-jackson-2.12.4-15.0.jar:2.12.4-15.0]
	at org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectReader._initForReading(ObjectReader.java:358) ~[flink-shaded-jackson-2.12.4-15.0.jar:2.12.4-15.0]
	at org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectReader._bindAndClose(ObjectReader.java:2023) ~[flink-shaded-jackson-2.12.4-15.0.jar:2.12.4-15.0]
	at org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectReader.readValue(ObjectReader.java:1528) ~[flink-shaded-jackson-2.12.4-15.0.jar:2.12.4-15.0]
	at org.apache.flink.formats.csv.CsvRowDataDeserializationSchema.deserialize(CsvRowDataDeserializationSchema.java:155) ~[flink-csv-1.15.1.jar:1.15.1]
	at org.apache.flink.formats.csv.CsvRowDataDeserializationSchema.deserialize(CsvRowDataDeserializationSchema.java:47) ~[flink-csv-1.15.1.jar:1.15.1]
	at org.apache.flink.api.common.serialization.DeserializationSchema.deserialize(DeserializationSchema.java:82) ~[flink-core-1.15.1.jar:1.15.1]
	at org.apache.flink.streaming.connectors.kafka.table.DynamicKafkaDeserializationSchema.deserialize(DynamicKafkaDeserializationSchema.java:119) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.kafka.source.reader.deserializer.KafkaDeserializationSchemaWrapper.deserialize(KafkaDeserializationSchemaWrapper.java:54) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	at org.apache.flink.connector.kafka.source.reader.KafkaRecordEmitter.emitRecord(KafkaRecordEmitter.java:53) ~[flink-connector-kafka-1.15.1.jar:1.15.1]
	... 14 more
```