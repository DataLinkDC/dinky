## dinky-connector-pulsar

> 概要说明：
> 实现依附：https://gitee.com/apache/flink/tree/release-1.14/flink-connectors
> * Flink 官方自1.14版本支持 Flink-pulsar-connector(目前未支持 Flink-sql)
> * 在此版本前，自主实现了Flink-pulsar-connector，本次Flink-sql的实现向官方Flink-connector-pulsar对齐，更好的兼容使用，实现性能最优！
> * 就生产经验，避坑处理
> * 本次Pulsar版本使用版本：2.8.2  Flink版本：1.14.3
> * Pulsar-connector应用广泛，在消息队列的使用中,FlinkSql的开发中具有总要作用意义。

## ★详情介绍 Pulsar-SQL Connector

### Dependencies

In order to use the Pulsar connector the following dependencies are required for both projects using a build automation tool (such as Maven or SBT) and SQL Client with SQL JAR bundles.

* Maven dependency

```
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-connector-Pulsar_2.11</artifactId>
  <version>1.14.3</version>
</dependency>
```

### How to create a Pulsar table

```
CREATE TABLE source_pulsar_n(
    requestId VARCHAR,
    `timestamp` BIGINT,
    `date` VARCHAR,
    appId VARCHAR,
    appName VARCHAR,
    forwardTimeMs VARCHAR,
    processingTimeMs INT,
    errCode VARCHAR,
    userIp VARCHAR,
    b_create_time as TO_TIMESTAMP(FROM_UNIXTIME(createTime/1000,'yyyy-MM-dd HH:mm:ss'),'yyyy-MM-dd HH:mm:ss')
) WITH (
  'connector' = 'pulsar',
  'connector.version' = 'universal',
  'connector.topic' = 'persistent://dinky/dev/context.pulsar',
  'connector.service-url' = 'pulsar://pulsar-dinky-n.stream.com:6650',
  'connector.subscription-name' = 'tmp_print_detail',
  'connector.subscription-type' = 'Shared',
  'connector.subscription-initial-position' = 'Latest',
  'update-mode' = 'append',
  'format' = 'json',
  'format.derive-schema' = 'true'
);
```

### Data Type Mapping

Pulsar stores message keys and values as bytes, so Pulsar doesn’t have schema or data types. The Pulsar messages are deserialized and serialized by formats, e.g. csv, json, avro. Thus, the data type mapping is determined by specific formats. Please refer to Formats pages for more details.

### Connector Options

|                 Option                  |     Required      | Default |  Type  |                               Description                                |
|-----------------------------------------|-------------------|---------|--------|--------------------------------------------------------------------------|
| connector                               | required          | (none)  | String | Specify what connector to use, for pulsar use `'pulsar'`.                |
| connector.version                       | required          | (none)  | String | universal                                                                |
| connector.topic                         | required for sink | (none)  | String | Topic name(s) to read data from when the table is used as source         |
| connector.service-url                   | optional          | (none)  | String | The address of the pulsar                                                |
| connector.subscription-name             | required          | (none)  | String | The subscription name of the Pulsar                                      |
| connector.subscription-type             | required          | (none)  | String | A subscription model of the Pulsar【Shared、Exclusive、Key_Shared、Failover】 |
| connector.subscription-initial-position | required          | (none)  | String | initial-position[EARLIEST、LATEST、TIMESTAMP]                              |
| update-mode                             | optional          | (none)  | String | append or upsert                                                         |
| format                                  | optional          | (none)  | String | json、csv......                                                           |
| format.derive-schema                    | optional          | (none)  | String | ture or false                                                            |
|                                         |                   |         |        |                                                                          |

## 🚀 快速上手

```shell
git clone https://github.com/DataLinkDC/dinky.git
cd dinky-connector/dinky-connector-pulsar-1.14
mvn clean install -DskipTests -Dflink.version=$version
```

## 🎉 Features

* Key and Value Formats

Both the key and value part of a Pulsar record can be serialized to and deserialized from raw bytes using one of the given

* Value Format

Since a key is optional in Pulsar records, the following statement reads and writes records with a configured value format but without a key format. The 'format' option is a synonym for 'value.format'. All format options are prefixed with the format identifier.

## 👻 使用

```sql
-- Pulsar多集群形式，
-- 此处分 n、b 两个集群

--声明数据源
CREATE TABLE source_pulsar_n(
    requestId VARCHAR,
    `timestamp` BIGINT,
    `date` VARCHAR,
    appId VARCHAR,
    appName VARCHAR,
    forwardTimeMs VARCHAR,
    processingTimeMs INT,
    errCode VARCHAR,
    userIp VARCHAR,
    createTime BIGINT,
    b_create_time as TO_TIMESTAMP(FROM_UNIXTIME(createTime/1000,'yyyy-MM-dd HH:mm:ss'),'yyyy-MM-dd HH:mm:ss')
) WITH (
  'connector' = 'pulsar',
  'connector.version' = 'universal',
  'connector.topic' = 'persistent://dinky/dev/context.pulsar',
  'connector.service-url' = 'pulsar://pulsar-dinky-n.stream.com:6650',
  'connector.subscription-name' = 'tmp_print_detail',
  'connector.subscription-type' = 'Shared',
  'connector.subscription-initial-position' = 'Latest',
  'update-mode' = 'append',
  'format' = 'json',
  'format.derive-schema' = 'true'
);


CREATE TABLE source_pulsar_b(
    requestId VARCHAR,
    `timestamp` BIGINT,
    `date` VARCHAR,
    appId VARCHAR,
    appName VARCHAR,
    forwardTimeMs VARCHAR,
    processingTimeMs INT,
    errCode VARCHAR,
    userIp VARCHAR,
    createTime BIGINT,
  b_create_im_time as TO_TIMESTAMP(FROM_UNIXTIME(createTime/1000,'yyyy-MM-dd HH:mm:ss'),'yyyy-MM-dd HH:mm:ss')
) WITH (
  'connector' = 'pulsar',
  'connector.version' = 'universal',
  'connector.topic' = 'persistent://dinky/dev/context.pulsar',
  'connector.service-url' = 'pulsar://pulsar-dinky-b.stream.com:6650',
  'connector.subscription-name' = 'tmp_print_detail',
  'connector.subscription-type' = 'Shared',
  'connector.subscription-initial-position' = 'Latest',
  'update-mode' = 'append',
  'format' = 'json',
  'format.derive-schema' = 'true'
);

-- 合并数据源
create view pulsar_source_all AS
select
      requestId ,
      `timestamp`,
      `date`,
      appId,
      appName,
      forwardTimeMs,
      processingTim,
      errCode,
      userIp,
      b_create_time
from source_pulsar_n
union all
select
      requestId ,
      `timestamp`,
      `date`,
      appId,
      appName,
      forwardTimeMs,
      processingTim,
      errCode,
      userIp,
      b_create_time
from source_pulsar_b;

-- 创建 sink
create table sink_pulsar_result(
    requestId VARCHAR,
    `timestamp` BIGINT,
    `date` VARCHAR,
    appId VARCHAR,
    appName VARCHAR,
    forwardTimeMs VARCHAR,
    processingTimeMs INT,
    errCode VARCHAR,
    userIp VARCHAR
) with (
  'connector' = 'print'
);

-- 执行逻辑
-- 查看 pulsar主题明细数据
insert into sink_pulsar_result
select 
      requestId ,
      `timestamp`,
      `date`,
      appId,
      appName,
      forwardTimeMs,
      processingTim,
      errCode,
      userIp,
      b_create_time
from pulsar_source_all;

```

### 介绍

与Kafka对比

| 对比方面 |                         Kafka                          |                               Pulsar                               |
|------|--------------------------------------------------------|--------------------------------------------------------------------|------|
| 模型概念 | producer – topic – consumer group – consumer           | producer – topic -subsciption- consumer                            | Stri |
| 消费模式 | 主要集中在流(Stream) 模式, 对单个partition是独占消费, 没有共享(Queue)的消费模式 | 提供了统一的消息模型和API. 流(Stream) 模式 – 独占和故障切换订阅方式 ; 队列(Queue)模式 – 共享订阅的方式 |
| 消息确认 | 使用偏移量 offset for sink                                  | 使用专门的cursor管理. 累积确认和kafka效果一样; 提供单条或选择性确认                          |
| 消息保留 | 根据设置的保留期来删除消息, 有可能消息没被消费, 过期后被删除, 不支持TTL               | 消息只有被所有订阅消费后才会删除, 不会丢失数据,. 也运行设置保留期, 保留被消费的数据 . 支持TTL              |

根本区别：Apache Pulsar和Apache Kafka之间的根本区别在于Apache Kafka是以分区为存储中心，而Apache Pulsar是以Segment为存储中心

性能对比：Pulsar性能比Kafka强许多，速度是Kafka的五倍，延迟降低了40%

### Pulsar补充介绍（消息体）

消息队列的读写......

核心概念
3.1 Messages（消息）

#### Value / data payload：

消息携带的数据，所有 Pulsar 的消息携带原始 bytes，但是消息数据也需要遵循数据 schemas。

#### Key：

消息可以被 Key 打标签。这可以对 topic 压缩之类的事情起作用。

#### Properties：

可选的，用户定义属性的 key/value map。

#### Producer name：

生产消息的 producer 的名称（producer 被自动赋予默认名称，但你也可以自己指定。）

#### Sequence ID：

在 topic 中，每个 Pulsar 消息属于一个有序的序列。消息的 sequence ID 是它在序列中的次序。

#### Publish time：

消息发布的时间戳

#### Event time：

可选的时间戳，应用可以附在消息上，代表某个事件发生的时间，例如，消息被处理时。如果没有明确的设置，那么 event time 为0。

#### TypedMessageBuilder：

它用于构造消息。您可以使用TypedMessageBuilder设置消息属性，比如消息键、消息值。设置TypedMessageBuilder时，将键设置为字符串。如果您将键设置为其他类型，例如，AVRO对象，则键将作为字节发送，并且很难从消费者处取回AVRO对象。

### Subscriptions（订阅模式）

* 1 Exclusive（独占模式）
* 2 Failover（灾备模式）
* 3 Shared（共享模式）
* 4 Key_Shared（Key 共享模式）

