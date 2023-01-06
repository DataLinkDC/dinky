---
sidebar_position: 94
title: 0.6.3 release
---



| 版本    | 二进制程序                                                                                                                | Source                                                                               |
|-------|----------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| 0.6.3 | [dinky-release-0.6.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/0.6.3/dinky-release-0.6.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/0.6.3.zip) |


## Dinky发行说明

Dinky 0.6.3 是一个新增功能和修复的版本。主要是新增了 CDC 整库实时入仓入湖的功能。

### 新功能

- 新增 CDC 整库实时同步至 kafka 的一个 topic
- 新增 CDC 整库实时同步至 kafka 对应 topic
- 新增 CDC 整库实时入仓 doris
- 新增 CDC 整库实时入湖 hudi
- 新增 CDC 整库同步表名规则
- 新增 CDC 整库实时 sql sink
- 新增 MysqlCDC 整库同步配置扩展
- 新增 CDC 整库同步主键注入
- 新增 Flink 1.15.0 的支持

### 修复

- 修复当作业停止时作业状态错误的 bug
- 修复 Oracle 不支持的字符集
- 修复 Clickhouse 元数据无法展示的 bug
- 修复元数据查询切换 bug
- 修复整库同步 Hudi 无法构建 Schema 的 bug
- 修复数据源单元测试的 name 缺失 bug
- 修复默认分隔符为 ';\r\n|;\n' 来解决 windows 和 mac 同时兼容
- 修复批任务无法正确提交 yarn application 的 bug
- 修复修改作业名失败的 bug
- 修复一些错误文档连接
- 修复获取作业执行计划被执行两次的 bug

### 优化

- 优化 Java 流的释放
- 优化 CDC 整库实时入库 doris
- 优化同一节点下无法启动多个进程实例的问题
- 优化微信告警的发送标题
- 优化启动文件编码及禁用环境变量