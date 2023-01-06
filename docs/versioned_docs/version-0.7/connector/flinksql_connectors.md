---
sidebar_position: 1
id: flinksql_connectors
title: FlinkSQL Connectors
---



Flink做为实时计算引擎，支持非常丰富的上下游存储系统的 Connectors。并从这些上下系统读写数据并进行计算。对于这些 Connectors 在 Flink 中统一称之为数据源(Source) 端和 目标(Sink) 端。

- 数据源(Source)指的是输入流计算系统的上游存储系统来源。在当前的 FlinkSQL 模式的作业中，数据源可以是消息队列 Kafka、数据库 MySQL 等。
- 目标（Sink）指的是流计算系统输出处理结果的目的地。在当前的流计算  FlinkSQL  模式的作业中，目标端可以是消息队列 Kafka、数据库 MySQL、OLAP引擎 Doris、ClickHouse 等。

Dinky 实时计算平台支持支持 Flink 1.11、Flink 1.12、Flink 1.13、Flink 1.14 和 Flink 1.15 五个版本，对应的版本支持所有开源的上下游存储系统具体Connector信息详见Flink开源社区：

- [Flink1.11](https://nightlies.apache.org/flink/flink-docs-release-1.11/dev/table/connectors/)
- [Flink1.12](https://nightlies.apache.org/flink/flink-docs-release-1.12/dev/table/connectors/)
- [Flink1.13](https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/connectors/table/overview/)
- [Flink1.14](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/connectors/table/overview/)
- [Flink1.15](https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/connectors/table/overview/)


另外非 Flink 官网支持的 Connector 详见 github：

- [Flink-CDC](https://github.com/ververica/flink-cdc-connectors/releases/)
- [Hudi](https://github.com/apache/hudi/releases)
- [Iceberg](https://github.com/apache/iceberg/releases)
- [Doris](https://github.com/apache/incubator-doris-flink-connector/tags)
- [Starrocks](https://github.com/StarRocks/flink-connector-starrocks/releases)
- [ClickHouse](https://github.com/itinycheng/flink-connector-clickhouse)
- [Pulsar](https://github.com/streamnative/pulsar-flink/releases)
