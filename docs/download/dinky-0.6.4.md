---
sidebar_position: 93
title: 0.6.4 release
---



| 版本    | 二进制程序                                                                                                                | Source                                                                               |
|-------|----------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| 0.6.4 | [dlink-release-0.6.4.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/0.6.4/dlink-release-0.6.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/0.6.4.zip) |


## Dinky发行说明

Dinky 0.6.4 是一个优化修复的版本。

### 新功能

- 新增整库同步表名参数支持换行和列支持按主键优先排序
- 新增整库同步日志输出
- 新增钉钉报警的 @mobile 配置
- 新增任务的 StreamGraph 导出为 JSON
- 新增任务的 API 接口示例页面
- 新增数据开发帮助页面
- 新增普通 SQL 的字段血缘
- 新增作业监听池来解决频繁写库的问题
- 新增非 FlinkSQL 作业的导出 StreamGraphPlan 的按钮隐藏
- 新增数据源的删除按钮
- 新增整库同步的 jdbc 配置和升级 flinkcdc 版本

### 修复

- 修复刷新作业监控页面时的抖动问题
- 修复 Flink Oracle Connector 不能转换 CLOB 到 String 的问题
- 修复切换任务时保存点未同步刷新的问题
- 修复 ClusterClient 接口不通版本的兼容性问题
- 修复 MySQL 类型转换精度信息是空的问题
- 修复初始化函数的冗余操作
- 修复整库同步的 decimal 问题
- 修复获取作业计划失败的问题
- 修复整库同步 OracleCDC number 不能被转换为 Long 的问题
- 修复微信企业号报警测试按钮后台错误的问题
- 修复当切换作业 tab 时无法正确保存修改的作业配置的问题
- 修复数据源和元数据不能展示别名的问题
- 修复作业重命名后 tab 未更新的问题
- 修复 K8S 集群配置的 FlinkLibPath 是空的问题
- 修复整库同步 String 无法转换为 Timestamp 的问题

### 优化

- 优化初始化 sql
- 优化打包
- 优化移除 preset-ui
- 优化 MySQL 字段类型转换