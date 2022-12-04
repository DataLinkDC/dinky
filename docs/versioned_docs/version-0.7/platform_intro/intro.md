---
sidebar_position: 0
title: Dinky 简介
---

## 介绍

实时即未来，Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑，并致力于实时计算平台建设。

Dinky 基于 Apache Flink 进行扩展 ，增强 Flink 的应用与体验，探索流式数仓。即站在巨人肩膀上创新与实践，Dinky 在未来批流一体的发展趋势下潜力无限。

最后，Dinky 的发展皆归功于 Apache Flink 等其他优秀的开源项目的指导与成果。

## 由来

Dinky（原 Dlink）：

1.Dinky 英译为 “ 小巧而精致的 ” ，最直观的表明了它的特征：轻量级但又具备复杂的大数据开发能力。

2.为 “ Data Integrate No Knotty ” 的首字母组合，英译 “ 数据整合不难 ”，寓意 “ 易于建设批流一体平台及应用 ”。

3.从 Dlink 改名为 Dinky 过渡平滑，更加形象的阐明了开源项目的目标，始终指引参与者们 “不忘初心，方得始终 ”。

## 特点

一个 **开箱即用** 、**易扩展** ，以 **Apache Flink** 为基础，连接 **OLAP** 和 **数据湖** 等众多框架的 **一站式** 实时计算平台，致力于 **流批一体** 和 **湖仓一体** 的建设与实践。

其主要目标如下：
- 沉浸式 FlinkSQL 数据开发：自动提示补全、语法高亮、语句美化、在线调试、语法校验、执行计划、MetaStore、血缘分析、版本对比等
- 支持 FlinkSQL 多版本开发及多种执行模式：Local、Standalone、Yarn/Kubernetes  Session、Yarn Per-Job、Yarn/Kubernetes  Application
- 多版本Flink支持(1.11、1.12、1.13、1.14、1.15、16)，多版本scala支持(2.11、2.22)
- 支持 Apache Flink 生态：Connector、FlinkCDC、Table Store 等
- 支持 FlinkSQL 语法增强：表值聚合函数、全局变量、执行环境、语句合并、整库同步、共享会话等
- 支持 FlinkCDC 整库实时入仓入湖、多库输出、自动建表
- 支持 SQL 作业开发：ClickHouse、Doris、Hive、Mysql、Oracle、Phoenix、PostgreSql、SqlServer、StarRocks 等
- 支持实时在线调试预览 Table、 ChangeLog、统计图
- 支持 Flink Catalog、数据源元数据在线查询及管理
- 支持实时任务运维：上线下线、作业信息、集群信息、作业快照、异常信息、数据地图、数据探查、历史版本、报警记录等
- 支持作为多版本 FlinkSQL Server 以及 OpenApi 的能力
- 支持实时作业报警及报警组：钉钉、微信企业号、飞书、邮箱等
- 支持自动托管的 SavePoint/CheckPoint 恢复及触发机制：最近一次、最早一次、指定一次等
- 支持多种资源管理：集群实例、集群配置、Jar、数据源、报警组、报警实例、文档、用户、系统配置等
- 支持 (JAVA & SCALA &PYTHON) UDF在线开发
- 支持k8s Application 自动化部署 以及 k8s operator
- 更多隐藏功能等待小伙伴们探索

----- 
## 架构
![Dinky](http://www.aiwenmo.com/dinky/docs/zh-CN/concept_architecture/architecture/dinky.png)

## 组成部分

**`JobManager`**

JobManager 作为 Dinky 的作业管理的统一入口，负责 Flink 的各种作业执行方式及其他功能的调度。

**`Executor`**

Executor 是 Dinky 定制的 FlinkSQL 执行器，来模拟真实的 Flink 执行环境，负责 FlinkSQL 的 Catalog 管理、UDF管理、全局变量管理、配置管理、语句集管理、语法校验、生成 JobGraph、本地执行、远程提交、SELECT 及 SHOW 预览等核心功能。

**`Interceptor`**

Interceptor 是 Dinky 的 Flink 执行拦截器，负责对其进行变量解析、UDF注册、整库同步、SET 和 AGGTABLE 等增强语法解析。

**`Gateway`**

Gateway 是 Dinky 自己定制的 Gateway，负责进行基于 Yarn 与 K8S 环境的任务提交与管理，主要有 Yarn Per-Job 和 Yarn/K8S Application 的 FlinkSQL 提交、停止、SavePoint 以及配置测试，而 User Jar 目前只开放了 Yarn-Application 的提交。

**`Flink SDK`**

Dinky 主要通过调用 flink-client 和 flink-table 模块进行二次开发。

**`Yarn SDK`**

Dinky 通过调用 flink-yarn 模块进行二次开发。

**`Flink API`**

Dinky 也支持通过调用 Flink JobManager 的 RestAPI 对任务进行管理等操作，系统配置可以控制开启和停用。

**`Local`**

Dinky 自身的 Flink 环境，通过 plugins 下的 Flink 依赖进行构建，主要用于语法校验和逻辑检查、生成 JobPlan 和 JobGraph、字段血缘分析等功能。
注意：目前请不要用该模式执行或提交流作业，将会无法关闭，需要重启进程才可。

**`Standalone`**

Dinky 通过已注册的 Flink Standalone 集群实例可以对远程集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。

**`Yarn-Session`**

Dinky 通过已注册的 Flink Yarn Session 集群实例可以对远程集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。

**`Yarn-Per-Job`**

Dinky 通过已注册的集群配置来获取对应的 YarnClient 实例，然后将 Local 模式解析生成的 JobGraph 与 Configuration 提交至 Yarn 来创建 Flink Per-Job 应用。

**`Yarn-Applicatio`n**

Dinky 通过已注册的集群配置来获取对应的 YarnClient 实例。对于 User Jar，将 Jar 相关配置与 Configuration 提交至 Yarn 来创建 Flink-Application 应用；对于 Flink SQL，Dinky 则将作业 ID 及数据库连接配置作为 Main 入参和 dlink-app.jar 以及 Configuration 提交至 Yarn 来创建 Flink-Application 应用。

**`Kubernetes-Session`**

Dinky 通过已注册的 Flink Kubernetes Session 集群实例可以对远程集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。
注意需要暴露 NodePort。

**`Kubernetes-Application`**

Dinky 通过已注册的集群配置来获取对应的 FlinkKubeClient 实例。对于 Flink SQL，Dinky 则将作业 ID 及数据库连接配置作为 Main 入参和定制的 dlink-app.jar 镜像以及 Configuration 提交至 Yarn 来创建 Flink-Application 应用。
注意需要自己打包 dlink-app 镜像，具体见文章。
