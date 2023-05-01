---

sidebar_position: 86
title: 0.7.3 release
--------------------

| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.7.3 | [dlink-release-0.7.3.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.7.3/dlink-release-0.7.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.7.3.zip) |

## Dinky发行说明

Dinky 0.7.3 是一个新功能和 bug 修复版本。

### 新功能

- 支持使用 FlinkSQL CTE 来预览数据
- 支持 Apache Flink 1.17

### 修复

- 修复获取字段血缘时未传递是否启用全局变量的参数
- 修复部署 flink k8s session 模式报错
- 修复整库同步中自动建表错误的 unsigned 语句
- 修复发布作业时的空指针异常
- 修复整库同步多个Sink的数据发送接收问题
- 修复SQL配置在特定场景下解析失败的问题
- 修复作业已删除但作业实例和历史没被删除的问题
- 修复整库同步 kafkaSink transactionalIdPrefix 的空指针异常
- 修复 postgreSql 获取 DDL 脚本错误
- 修复生成Mysql的DDL语句时普通默认值不带引号的问题

### 优化

- 优化作业监控重新连接
- 修复错别字
- 优化 MysqlJsonDebeziumDeserializationSchema 使用 flink 1.13 的 shaded 依赖
- 支持整库同步的 scan.incremental.snapshot.chunk.size 参数
- 从原始逻辑计划解析字段血缘并添加单元测试
- 升级所有的 Flink 小版本至最新版本
- 升级所有的 Flink 小版本至最新版本

### 贡献者

- @aiwenmo
- @AntChen27
- @CYGNUSDARK
- @HamaWhiteGG
- @LiuHao0606

