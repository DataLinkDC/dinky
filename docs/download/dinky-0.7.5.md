---

sidebar_position: 84
title: 0.7.5 release
--------------------

| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.7.5 | [dlink-release-0.7.5.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.7.5/dlink-release-0.7.5.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.7.5.zip) |

## Dinky发行说明

Dinky 0.7.5 是一个 bug 修复版本。

### 新功能

- 在 flink-connector-jdbc-1.16 中添加过滤能力
- AGGTABLE 支持表值聚合函数在 Flink 1.16 中使用

### 修复

- 修复 UDF 提交时从 checkpoint 启动失败的问题
- 修复 listColumnsSortByPK 方法递归问题

### 优化

- 优化无法获取最新状态的作业显示
- 优化 Flink 作业列表查询使用枚举值

### 贡献者

- @aiwenmo
- @Wintle
- @zackyoungh


