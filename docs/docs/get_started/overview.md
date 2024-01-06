---

sidebar_position: 1
id: overview
title: Dinky介绍
---------

## 概述

实时即未来，Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑。

Dinky 是一个开箱即用、易扩展，以 Apache Flink 为基础，连接 OLAP 和数据湖等众多框架的一站式实时计算平台，致力于流批一体和湖仓一体的探索与实践。
致力于简化Flink任务开发，提升Flink任务运维能力，降低Flink入门成本，提供一站式的Flink任务开发、运维、监控、报警、调度、数据管理等功能。

最后，Dinky 的发展皆归功于 Apache Flink 等其他优秀的开源项目的指导与成果。


## 特性

- 沉浸式 FlinkSQL 数据开发：自动提示补全、语法高亮、语句美化、在线调试、语法校验、执行计划、Catalog支持、血缘分析等
- Flink SQL语法增强，如 CDC任务，jar任务，实时打印表数据，实时数据预览，全局变量增强，语句合并、整库同步等
- 适配 FlinkSQL 多种执行模式：Local、Standalone、Yarn/Kubernetes  Session、Yarn Per-Job、Yarn/Kubernetes  Application
- 增强 Flink 生态拓展：Connector、FlinkCDC、Table Store 等
- 支持 FlinkCDC 整库实时入仓入湖、多库输出、自动建表、模式演变
- 支持 Flink Java / Scala / Python UDF 开发与自动提交
- 支持 SQL 作业开发：ClickHouse、Doris、Hive、Mysql、Oracle、Phoenix、PostgreSql、Presto、SqlServer、StarRocks 等
- 支持实时在线调试预览 Table、 ChangeLog、统计图和 UDF
- 支持 Flink Catalog、Dinky内置Catalog增强，数据源元数据在线查询及管理
- 支持自动托管的 SavePoint/CheckPoint 恢复及触发机制：最近一次、最早一次、指定一次等
- 支持实时任务运维：作业信息、集群信息、作业快照、异常信息、历史版本、报警记录等
- 支持作为多版本 FlinkSQL Server 以及 OpenApi 的能力
- 支持实时作业报警及报警组：钉钉、微信企业号、飞书、邮箱等
- 支持多种资源管理：集群实例、集群配置、数据源、报警组、报警实例、文档、系统配置等
- 支持企业级管理功能：多租户、用户、角色、命名空间等
- 更多隐藏功能等待小伙伴们探索

## 未来计划