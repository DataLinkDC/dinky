---
sidebar_position: 6
id: catalog_manage
title: Catalog 管理
---

## Catalog 概述

当您使用 Flink 管理元数据时，Flink已提供了3种不同的 Catalog。具体 Flink 是如何定义 Catalog 的，详见 [Flink 官网](https://nightlies.apache.org/flink/flink-docs-master/zh/docs/dev/table/catalogs/)。
另外 Dinky 提供了一种基于 Session会话级别的 Catalog。

## Hive Catalog

### 介绍

您可以在 Dinky 中使用 FlinkSQL 在作业中编写 Hive Catalog、查看 Hive 元数据、使用 Hive Catalog。将元数据信息保存到 Hive Metastore 以后，即可在数据开发的编辑器中开发作业。

### 版本说明

| Flink 版本 |                             说明                             |
| :--------: | :----------------------------------------------------------: |
| Flink1.11  | 详见[Flink1.11](https://nightlies.apache.org/flink/flink-docs-release-1.11/dev/table/catalogs.html)说明 |
| Flink1.12  | 详见[Flink1.12](https://nightlies.apache.org/flink/flink-docs-release-1.12/dev/table/catalogs.html)说明 |
| Flink1.13  | 详见[Flink1.13](https://nightlies.apache.org/flink/flink-docs-release-1.13/dev/table/catalogs.html)说明 |
| Flink1.14  | 详见[Flink1.14](https://nightlies.apache.org/flink/flink-docs-release-1.14/dev/table/catalogs.html)说明 |
| Flink1.15  | 详见[Flink1.15](https://nightlies.apache.org/flink/flink-docs-release-1.15/dev/table/catalogs.html)说明 |



### 前提条件

已在 Hive Metastore 侧开启了 Hive Metastore 服务。
相关命令如下：

- `hive --service metastore`：开启 Hive Metastore 服务。
- `ps -ef|grep metastore`：查询 Hive Metastore 服务是否已开启