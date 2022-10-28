---
sidebar_position: 91
title: 0.6.6 release
---

| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.6.6 | [dlink-release-0.6.6.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.6.6/dlink-release-0.6.6.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.6.6.zip) |

## Dinky发行说明

Dinky 0.6.6 是一个优化修复的版本。

:::warning 注意
此版本有表结构变更 <br/>
需要执行 **sql/upgrade/0.6.6_schema/mysql/dinky_ddl.sql** 文件
:::

### 新功能

- 新增 DevOps 的作业历史版本列表
- 新增历史版本对比
- 新增 Flink MySql CataLog,在 FlinkSQLEnv 中添加默认的 mysql CataLog
- 新增 1.13版本 的 Doris 连接 隐藏列 **_DORIS_DELETE** 的处理
- 新增 dlink-connect-pulsar
- 新增 **运维中心** 作业从**指定 CheckPoint 恢复**
- 新增元数据功能
- 新增 Flink 元数据信息和列详细信息
- 升级了 Flink1.15.0 到 Flink1.15.1
- 新增作业导入和导出任务json文件
- 新增运维中心的任务 savepoints 列表

### 修复

- 修复 flink-Connector-Phoenix 并更新 PhoenixDynamicTableFactory
- 修复作业实例导致 OOM，更新 Flink 版本并修复 Flink 和 CDC 版本兼容性错误
- 修复任务实例的耗时解析错误问题
- 修复 catalog SPI bug 和 sql bug
- 修复运维中心检查点等信息获取错误
- 修复在 SQLSinkBuilder 中捕获 tranateToPlan 异常
- 修复 application 模式提交失败
- 修复删除告警实例后,告警组下实例仍存在 导致告警错误的问题
- 修复循环依赖问题
- 修复删除未引用的类
- 修复 jobhistory 字段 null 值的问题

### 优化

- 优化从 api 移除敏感信息(密码)， 修正已删除的用户登录信息显示不正确的问题
- 修正已删除的用户登录信息显示不正确的问题
- 优化检查点页面
- 优化 无法获取 flinksql 执行图时的返回提示信息
