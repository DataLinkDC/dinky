---

sidebar_position: 83
title: 1.0.0 release
--------------------

| 版本        | 二进制程序                                                                                                                             | Source                                                                                    |
|-----------|-----------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| 1.0.0-rc2 | [dinky-release-1.0.0-rc2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc2/dinky-release-1.0.0-rc2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc2.zip) |

## Dinky-rc1发行说明

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


## Dinky-rc2发行说明

### 修复

- [Fix-2739] 修复 auto.sh 的 CLASS_PATH 中缺少 extends 路径的问题
- [Fix-2740] 修复发布/下线后作业列表生命周期状态值没有重新渲染的问题
- [Fix] 修复 Flink 1.18 set 语法不工作并产生 null 错误
- [Fix] 修复提交历史的保存点机制问题
- [Fix] 修复打印 Flink 表的问题
- [Fix] 修复 Catalog 中创建视图的问题
- [Fix] 修复 Flink application 不会抛出异常
- [Fix] 修复报警选项不正确
- [Fix] 修复作业生命周期的问题
- [Fix-2754] 修复集群配置中 k8s 的 YAML 无法显示
- [Fix-2756] 修复运维中心作业列表耗时格式化错误
- [Fix-2777] 修复 Flink dag 提示框问题
- [Fix-2782] 修复 checkpoint 的路径未找到
- [Fix] 修复向海豚调度推送作业时节点位置错误
- [Fix-2806] 修复当 set 配置中包含单引号时作业参数未生效的问题
- [Fix-2811] 升级 jmx_prometheus_javaagent 到 0.20.0 来解决一些 CVE
- [Fix-2814] 修复 checkpoint 展示问题
- [Fix] 修复在使用 ADDJAR 语法是 Flink catalog 没有生效
- [Fix] 修复运维中心的一些问题
- [Fix-2832] 修复 h2 驱动程序无默认打包问题
- [Fix] 修复 sql 的问题
- [Fix] 修复作业实例始终处于运行中状态
- [Fix-2843] 修复 Yarn Application 提交任务失败后缺少日志打印
- [Fix] 修复 h2 中的 udf 问题
- [Fix-2823] 修复作业配置不能渲染 yarn prejob 集群
- [Fix] 修复 URL 拼错导致请求失败
- [Fix-2855] 修复 savepoint 表参数问题
- [Fix-2776] 修复多用户登录时出现相同令牌值插入错误的问题

### 优化

- [Improve] 改进从 execute pipeline 命令中提取 yaml
- [Optimization] 添加作业配置项的键宽度
- [Optimization] 在 PrintNetSink 中添加 dinky port 配置
- [Improve] 优化查询作业目前树
- [Optimization-2773] 优化数据源目录树有两个滚动条
- [Optimization-2822] 优化 Metrics 页面提示
- [Optimization] 优化 Flink on yarn 的 app 提交
- [Optimization] 优化 Explainer 类使用建造者模式来构建结果
- [Optimization] 优化文档管理
- [Optimization] 通过 SPI 来实现 operator
- [Improve] 优化文档表单弹出层
- [Optimization-2757] 优化 Flink 实例的类型渲染
- [Optimization-2755] 优化数据源详情搜索框
- [Optimization] 为 DinkyClassLoader 添加资源实现

### 文档

- [Document] 完善注册中心的集群实例列表文档
- [Document] 完善注册中心的告警文档
- [Document] 完善注册中心的Git项目文档
- [Document] 完善快速开始的 k8s 文档
- [Document] 修改域名
- [Document] 完善注册中心和认证中心的文档
- [Document] 完善贡献者开发文档
- [Document] 在 CDCSOURCE 添加参数描述 debezium.*
- [Document-2830] 更新官网下载
- [Document] 修改官网文档结构

### 贡献者

@aiwenmo
@gaoyan1998
@gitfortian
@leeoo
@leechor
@stdnt-xiao
@yangzehan
@zackyoungh
@Zzm0809