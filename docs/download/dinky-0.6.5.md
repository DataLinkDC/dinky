---
sidebar_position: 92
title: 0.6.5 release
---



| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.6.5 | [dlink-release-0.6.5.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.6.5/dlink-release-0.6.5.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.6.5.zip) |


## Dinky发行说明

Dinky 0.6.5 是一个优化修复的版本。

### 新功能

- 新增 phoenix 的 1.14 版本的 Flink 连接器
- 新增生成 FlinkSQL 时从元数据中获取空和非空
- 新增作业实例信息接口
- 新增运维中心 Flink 快照信息、JobManager 配置信息
- 新增运维中心 TaskManager 信息
- 新增作业信息选项卡
- 新增运维中心 TaskManager 的表和表单详情
- 新增作业版本管理
- 新增作业检查点历史信息
- 新增捕获 CDCSOURCE 中的列值转换异常
- 新增 TaskManager 信息
- 新增作业复制

### 修复

- 修复数据开发数据源别名是""
- 修复数据开发关闭其他页面无法关闭第一个页面
- 修复切换元数据时发生异常
- 修复 flinkLibPath 和 cluster-id 在 K8S 配置中是空
- 修复 FlinkSql 语句末尾的分号异常
- 修复 K8S 集群配置不能获取自定义配置
- 修复 yarn per-job 运行时的空指针异常
- 修复任务实例信息在强制刷新且失去连接时会清空记录的问题
- 修复 'table.local-time-zone' 参数不生效的问题
- 修复邮箱报警不能找到类 javax.mail.Address
- 修复不能校验 'show datatbases' 的问题
- 修复 setParentId 方法判空错误
- 修复 CDCSOURCE 里当 FlinkSql 字段名为关键字时不能创建 Flink 表
- 修复字段血缘分析不能处理相同字段名
- 修复集群配置页面的问题
- 修复邮件报警消息不能自定义昵称
- 修复 dlink-connector-phoenix-1.14 编译异常
- 修复 Oracle 字段可为空的识别错误
- 修复 CDCSOURCE 中 MySQL varbinary 和 binary 类型的支持

### 优化

- 优化作业树搜索结果高亮与选中背景色
- 优化多处别名是空的显示
- 优化 explainSqlRecord 代码逻辑
- 优化集群实例页面
- 优化血缘分析的刷新
- 优化作业实例信息接口
- 优化所有报警的发送信息