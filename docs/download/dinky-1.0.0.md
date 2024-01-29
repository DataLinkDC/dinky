---
sidebar_position: 83
title: 1.0.0 release
---

| Dinky 版本  | Flink 版本 | 二进制程序                                                                                                                                       | Source                                                                                    |
|-----------|----------|---------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| 1.0.0-rc4 | 1.14     | [dinky-release-1.14-1.0.0-rc4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc4/dinky-release-1.14-1.0.0-rc4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc4.zip) |
| 1.0.0-rc4 | 1.15     | [dinky-release-1.15-1.0.0-rc4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc4/dinky-release-1.15-1.0.0-rc4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc4.zip) |
| 1.0.0-rc4 | 1.16     | [dinky-release-1.16-1.0.0-rc4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc4/dinky-release-1.16-1.0.0-rc4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc4.zip) |
| 1.0.0-rc4 | 1.17     | [dinky-release-1.17-1.0.0-rc4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc4/dinky-release-1.17-1.0.0-rc4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc4.zip) |
| 1.0.0-rc4 | 1.18     | [dinky-release-1.18-1.0.0-rc4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0-rc4/dinky-release-1.18-1.0.0-rc4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0-rc4.zip) |

## Dinky-1.0.0-rc1 发行说明

### 简介

Dinky是一个基于Apache Flink的数据开发平台，敏捷地进行数据开发与部署。

### 升级说明

Dinky 1.0 是一个重构版本，对已有的功能进行重构，并新增了若干企业级功能，修复了 0.7 的一些局限性问题。 目前无法直接从 0.7 升级到
1.0，后续提供升级方案。

### 主要功能

- FlinkSQL 数据开发：自动提示补全、语法高亮、语句美化、语法校验、执行计划、MetaStore、血缘分析、版本对比等
- 支持 FlinkSQL 多版本开发及多种执行模式：Local、Standalone、Yarn/Kubernetes Session、Yarn Per-Job、Yarn/Kubernetes
  Application
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

## Dinky-1.0.0-rc2 发行说明

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

## Dinky-1.0.0-rc3 发行说明

### 新功能

- 默认 Flink 启动版本修改为 1.16
- 实现 CodeShow 组件换行按钮
- 实现 Flink 实例停止功能
- 实现删除已定义的任务监控布局

### 优化

- 获取版本方式优化为后端接口返回
- 优化CANCEL作业逻辑，对于失联作业可强制停止
- 优化注册中心部分删除时的检测引用逻辑
- 优化作业创建时可指定作业模版
- 优化Task 删除逻辑
- 优化部分前端国际化
- 优化 Dinky 进程 PID 检测逻辑
- 优化执行预览时控制台与结果 Tag 自动切换

### 修复

- 修复告警实例表单渲染问题
- 修复FlinkSQLEnv 无法检查的问题
- 修复 set 语句无法生效的问题
- 修复yarn集群配置自定义Flink，hadoop配置无效问题
- 修复Prejob模式下一些问题
- 修复运维中心检查点信息获取不到问题
- 修复Yarn Application作业结束后状态无法检测问题
- 修复yarn作业提交失败控制台日志无打印问题
- 修复获取 savepoint 列表 404 的问题
- 修复从集群配置启动的Flink 实例无法在作业配置中选择的问题
- 修复RECONNECT状态作业状态识别错误
- 修复运维中心列表结束时间为 1970-01-01 的问题
- 修复 FlinkJar 任务在提交到 PreJob 模式的问题
- 修复告警模块依赖重复引入,导致冲突
- 修复Dinky启动检测pid问题
- 修复内置Paimon 与 用户集成版本不一致时,导致冲突的问题(使用 shade 实现)
- 修复 execute jar 语法正则问题
- 修复FlinkJar 任务在 Application 模式下CheckPoint 参数不生效问题
- 修复修改 Task 作业时名称和备注信息更新错误的问题
- 修复注册数据源时密码为必填的问题

### 文档

- 添加部分数据开发相关文档
- 优化注册中心部分文档
- 删除一些弃用/错误的文档
- 调整一些文档结构
- 添加快速开始文档
- 添加部署文档

### 贡献者

@aiwenmo
@drgnchan
@gaoyan1998
@gitfortian
@gitjxm
@leechor
@leeoo
@Logout-y
@MaoMiMao
@Pandas886
@yangzehan
@YardStrong
@zackyoungh
@Zzm0809

## Dinky-1.0.0-rc4 发行说明

### 新功能

- 实现整库同步多并行度下数据有序
- 实现资源中心的 hdfs ha
- 实现配置中心全局配置的权限控制
- 实现可配置方式的告警防重发功能
- 实现DB SQL 可被 DolphinScheduler 调度
- 新增资源中心的按照配置的资源存储类型(目前实现 oss)同步目录

### 优化

- 优化 K8S的 UDF 下载逻辑
- 优化 CDC3.0相关逻辑
- 优化整库同步之分库分表
- 优化集成 LDAP逻辑
- 优化注册中心->数据源列表跳转到详情页的逻辑
- 作业配置逻辑优化(作业已发布状态下作业配置不可编辑)
- 优化数据开发中作业配置的集群实例渲染逻辑
- 优化启动脚本,使之可以配置环境变量的方式进行启动

### 修复

- 修复集群实例心跳检测不正确的问题
- 修复分隔符问题
- 修复 Jar 任务提交不能使用 set 语法的问题
- 修复 LDAP 获取用户相关信息时NPE 的问题
- 修复分配菜单权限时无法携带上一级 ID 的问题
- 修复数据开发切换 Key 时 版本历史无法正常更新的问题
- 修复 PG sql 文件的一些默认值问题
- 修复因Resource配置错误导致Dinky无法启动
- 修复权限控制默认路由跳转问题
- 修复数据开发-> 作业列表部分情况下无法折叠的问题
- 修复多线程下告警信息重复发送的问题
- 修复数据开发-> 打开作业的 tag 高度问题
- 修复集成 gitlab 时认证相关问题
- 修复运维中心作业详情的 jobmanager 日志部分情况下无法正常展示的问题
- 修复 CataLog NPE 的问题
- 修复 yarn 的 port 是 0 的问题
- 修复数据源的前端表单状态问题
- 修复 Kubeconfig 获取的问题
- 修复prejob 任务状态错误的问题
- 修复 add customjar 语法问题
- 修复一些 web 的 NPE 异常
- 修复告警实例为邮件类型时启用 ssl 的 bug
- 修复 jar 任务无法监控的问题

### 文档

- 优化分库分表的文档
- 优化常规部署文档
- 添加告警防重发相关文档
- 优化 openapi 文档
- 添加 HDFS HA 配置文档

### 其他

- 增加一些自动化 Action