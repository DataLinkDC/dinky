---

sidebar_position: 5
id: overview
title: Overview
---------------

## 概述

Dinky 作为 [Apache Flink](https://github.com/apache/flink) 的 FlinkSQL 的实时计算平台，具有以下核心特点。

- **支持 Flink 原生语法、连接器、UDF 等：** 几乎零成本将 Flink 作业迁移至 Dinky。
- **增强 FlinkSQL 语法：** 表值聚合函数、全局变量、CDC多源合并、执行环境、语句合并等。
- **支持 Flink 多版本：** 支持作为多版本 FlinkSQL Server 的能力以及 OpenApi。
- **支持外部数据源的 DB SQL 操作：** 如 ClickHouse、Doris、Hive、Mysql、Oracle、Phoenix、PostgreSql、Presto、SqlServer、StarRocks 等。
- **支持实时任务运维：** 作业上线下线、作业信息、集群信息、作业快照、异常信息、作业日志、数据地图、即席查询、历史版本、报警记录等。

## 管理控制台介绍

Dinky 实时计算平台开发模块包括 **数据开发**、**运维中心**、**注册中心** 和 **系统设置** 四大模块。

### 数据开发

数据开发包括作业管理、作业配置和运维管理等

![data_ops](http://www.aiwenmo.com/dinky/docs/zh-CN/overview/data_ops.png)

### 运维中心

![devops_center](http://www.aiwenmo.com/dinky/docs/zh-CN/overview/devops_center.png)

### 注册中心

注册中心包括集群管理、Jar管理、数据源管理、报警管理和文档管理

![register_center](http://www.aiwenmo.com/dinky/docs/zh-CN/overview/register_center.png)

### 系统设置

系统设置包括用户管理和Flink设置

![system_settings](http://www.aiwenmo.com/dinky/docs/zh-CN/overview/system_settings.png)
