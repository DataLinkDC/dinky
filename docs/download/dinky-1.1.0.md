---
sidebar_position: 79
title: 1.1.0 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                           | Source                                                                                |
|----------|----------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.1.0    | 1.14     | [dinky-release-1.14-1.1.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.14-1.1.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.1.0.zip) |
| 1.1.0    | 1.15     | [dinky-release-1.15-1.1.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.15-1.1.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.1.0.zip) |
| 1.1.0    | 1.16     | [dinky-release-1.16-1.1.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.16-1.1.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.1.0.zip) |
| 1.1.0    | 1.17     | [dinky-release-1.17-1.1.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.17-1.1.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.1.0.zip) |
| 1.1.0    | 1.18     | [dinky-release-1.18-1.1.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.18-1.1.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.1.0.zip) |
| 1.1.0    | 1.19     | [dinky-release-1.19-1.1.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.1.0/dinky-release-1.19-1.1.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.1.0.zip) |

## Dinky-1.1.0 发行说明

### 升级说明

:::warning 重要
v1.1.0 版本有不兼容变更,请参考如下不兼容变更列表,合理选择升级方式
:::

### 不兼容变更
- v1.1.0 支持表结构自动升级框架(flyway),使用截止到v1.0.2的表结构/数据为默认基础版本,如果你的版本未在 v1.0.2+ 则必须先按照官网中的升级教程进行升级到 v1.0.2 的表结构,如你的版本为 v1.0.2+ 则可以直接升级,程序会自动执行,且不会影响历史数据,如你是全新部署,请忽略该事项
- 由于 flink-cdc 贡献至 apache 基金会导致新版本包名变更,无法做兼容变更,在 dinky-v1.1.0 及以上版本,dinky 将使用新的包名依赖引入,因此要求你的 flink-cdc 的依赖则必须升级至 flink-cdc v3.1+,否则无法使用
- 移除打包时的 scala版本区分,只采用 scala-2.12 进行开发,不再支持scala-1.11.x


### 新功能
- 新增 flyway 表结构升级框架
- 任务目录支持灵活排序
- 实现任务级别的权限控制,并支持不同权限控制策略
- 优化添加租户时自动添加管理员用户关联
- 新增任务提交卡死状态下可以直接杀死该进程功能
- 支持 k8s 部署 dinky
- 实现数据预览
- 新增数据开发中支持 UDF 注入配置
- 添加整库同步功能(cdcsource) sink 端的表名称映射，正则匹配修改映射
- 新增 Dashboard 页面
- 新增 Paimon 数据源类型
- 新增 SQL-Cli

### 修复
- 修改k8s的account.name值问题&添加deleteCluster时Conf初始化问题
- 修复 flink-cdc 在 application 模式下丢失sql的问题
- 修复复制任务时,任务创建时间未重置问题
- 修复任务列表定位问题
- 解决提交 Jar 任务时用户 Jars 中的自定义类无法编译的问题
- 修复 企微-app模式告警信息不正确的问题
- 修复 flink-1.19 无法提交任务的问题
- 修复启动脚本无法支持 jdk11 的问题
- 修复集群实例无法删除的问题
- 修复 UDF 在 Flink SQL 任务中找不到类的问题
- 修复数据开发页面大小改变时的状态未更新问题
- 修复自定义配置中定义 Flink 无法获取最新高可用地址的问题
- 修复通过手动配置rest.address和rest.port无法识别问题

### 优化
- 优化资源配置中的提示词
- 优化 mysql 数据源类型的 DDL 生成逻辑
- 优化一些前端依赖问题,并优化一些前端提示信息
- 优化资源中心的复制路径功能,支持dinky 内多种应用场景
- 优化监控功能,使用 dinky 的配置中心中监控功能开关来控制整体 dinky 内的所有监控
- 优化一些前端判断逻辑

### 重构
- 将告警规则移动至注册中心下的告警路由下
- 移除 paimon 作为存储监控介质,改为 sqllite,并不强依赖于 hadoop-uber 包(hadoop 环境下除外),并支持定期清理
- 重构监控页面,移除部分内置服务监控


### 文档
- 增加 k8s 部署 dinky 文档
- 优化 docker 部署文档
- 添加整库同步功能(cdcsource) sink 端的表名称映射等相关文档




