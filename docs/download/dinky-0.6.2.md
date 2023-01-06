---
sidebar_position: 95
title: 0.6.2 release
---



| 版本    | 二进制程序                                                                                                                | Source                                                                               |
|-------|----------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| 0.6.2 | [dinky-release-0.6.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/0.6.2/dinky-release-0.6.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/0.6.2.zip) |


## Dinky发行说明

Dinky 0.6.2 是一个优化和修复的版本。

### 新功能

- 新增 飞书报警
- 新增 邮件报警
- 新增 报警实例测试功能
- 新增 docker 镜像文件
- 新增 Remote mode（Standalone、Yarn Session、Kubernetes Session）从 SavePoint 恢复作业
- 新增 Oracle CDC 多源合并
- 新增 版本为 0.6.2

### 优化

- 优化 检查 FlinkSQL 不通过时返回全部的异常信息
- 优化 Hive 的 pom
- 优化 httpclient 的依赖
- 优化 报警、数据源、线程任务的 SPI 机制
- 优化 CDC 多源合并
- 优化 运维中心始终显示 FlinkWebUI 按钮
- 优化 集群实例显示 FlinkWebUI 按钮

### 修复

- 修复 Integer 类型判断方式从 “==” 改为 “equals”
- 修复 Flink 的 hadoop_conf 配置未生效
- 修复 报警测试的最新配置未生效
- 修复 飞书报警使用 “@all” 时会触发异常
- 修复 install 项目失败
- 修复 Hive 构建 sql 的异常
- 修复 特殊字符导致全局变量替换失败
- 修复 不支持查看执行图 sql 的展示异常
- 修复 CDC 多源合并 Remote 提交的任务同步问题
- 修复 修改集群配置后显示 “新增成功”
- 修复 Flink Oracle Connector 读取 Date 和 Timestamp 类型的异常
- 修复 MybatsPlus 实体类 boolean 默认为 false 导致业务错误的异常
- 修复 修复系统配置的 sql 分隔符默认为 “;\r\n”
- 修复 任务失败导致的循环问题
