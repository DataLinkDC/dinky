---

sidebar_position: 85
title: 0.7.4 release
--------------------

| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.7.4 | [dlink-release-0.7.4.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.7.4/dlink-release-0.7.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.7.4.zip) |

## Dinky发行说明

Dinky 0.7.4 是一个 bug 修复版本。

### 新功能

- 从 Flink SQL 分析自定义函数
- 在 Flink 1.16 中新增 SQLServer Connector
- CDCSOURCE 支持 Doris 自动建表

### 修复

- 修复 Mysql 自动生成DDL语句时没有数据类型的问题
- 修复 CDCSOURCE 整库同步部分问题
- 修复当主键没有在表头声明时同步数据后会发生数据错位
- 修复用户自定义函数不能在 flink1.16 和 flink 1.17 上运行
- 修复 clickhouse 的字段正确获取是否为空
- 修复构建 MySql DDL 时精度缺失问题
- 修复 mssql tinyint 类型转换异常
- 修复未知的作业 ID
- 修复从检查点恢复时会更新作业的保存点策略的问题

### 优化

- 优化 Flink 1.16 的 CDCSOURCE
- 升级 Flink 的版本
- 
### 贡献者

- @aiwenmo
- @HamaWhiteGG
- @hhxxjxsz
- @LiuHao0606
- @程胜
- @谭茁
- @xiaofan2022
- @zhengwenhao2017

