---

sidebar_position: 83
title: 1.0.0 release
--------------------

| 版本        | 二进制程序                                                                                                                             | Source                                                                                    |
|-----------|-----------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| 1.0.0-rc1 | [dinky-release-1.0.0-rc1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc1/dinky-release-1.0.0-rc1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc1.zip) |

## Dinky发行说明

### 简介

Dinky是一个基于Apache Flink的数据开发平台，敏捷地进行数据开发与部署。

### 升级说明

Dinky 1.0 是一个重构版本，对已有的功能进行重构，并新增了若干企业级功能，修复了 0.7 的一些局限性问题。 目前无法直接从 0.7 升级到 1.0，后续提供升级方案。

### 主要功能

- FlinkSQL 数据开发：自动提示补全、语法高亮、语句美化、语法校验、执行计划、MetaStore、血缘分析、版本对比等
- 支持 FlinkSQL 多版本开发及多种执行模式：Local、Standalone、Yarn/Kubernetes Session、Yarn Per-Job、Yarn/Kubernetes Application
- 支持 Apache Flink 生态：Connector、FlinkCDC、Paimon 等
- 支持 FlinkSQL 语法增强：整库同步、执行环境、全局变量、语句合并、表值聚合函数、加载依赖、行级权限、提交Jar等
- 支持 FlinkCDC 整库实时入仓入湖：多库输出、自动建表、模式演变、分库分表
- 支持 SQL 作业开发及元数据浏览：ClickHouse、Doris、Hive、Mysql、Oracle、Phoenix、PostgreSql、Presto、SqlServer、StarRocks 等
- 支持 Flink 实时在线调试预览 TableData、ChangeLog、Operator、 Catalog、UDF
- 支持 Flink 作业自定义监控统计分析、自定义告警规则。
- 支持实时任务运维：上线下线、作业信息（支持获取 checkpoint）、作业日志、版本信息、作业快照、监控、SQL 血缘、告警记录等
- 支持实时作业报警及报警组：钉钉、微信企业号、飞书、邮箱、短信等
- 支持自动托管的 SavePoint/CheckPoint 恢复及触发机制：最近一次、最早一次、指定一次等
- 支持多种资源管理：集群实例、集群配置、数据源、告警、文档、全局变量、Git项目、UDF、系统配置等
- 支持企业级管理：租户、用户、角色、菜单、令牌、数据权限


### 部分新功能

- 新增首页看板
- 数据开发支持代码提示
- 支持实时打印 Flink 表数据
- 控制台支持实时打印任务提交 log
- 支持 Flink CDC 3.0 整库同步
- 支持自定义告警规则，自定义告警信息模板
- 运维中心全面改版
- k8s 以及 k8s operator 支持
- 支持代理 Flink webui 访问
- 支持 Flink 任务监控
- 支持 Dinky jvm 监控
- 新增资源中心功能，并扩展了 rs 协议
- 新增 Git UDF/JAR 项目托管，及整体构建流程
- 支持全模式自定义 jar 提交
- openapi 支持自定义参数提交
- 权限系统升级，支持租户，角色，token，菜单权限
- LDAP 认证支持
- 数据开发页面新增小工具功能
- 支持推送依赖任务至 DolphinScheduler

### 贡献者

在此感谢参与 1.0.0 建设的贡献者们，详情见：https://github.com/DataLinkDC/dinky/graphs/contributors


