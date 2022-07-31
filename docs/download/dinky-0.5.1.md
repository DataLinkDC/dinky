---
sidebar_position: 98
title: 0.5.1 release
---

| 版本   | 二进制程序                                                                                                                | Source                                                                               |
|-------|----------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| 0.5.1 | [dlink-release-0.5.1.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/0.5.1/dlink-release-0.5.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/0.5.1.zip) |


## Dinky发行说明

Dinky 0.5.1 是一个修复版本，其中包括针对前端问题（如优化SQL编辑器性能）的优化。它还包括对0.5.0 中引入的一些重大更改的修复。

### 前端
- 修复SHOW和DESC 的查询预览失效;
- 修复作业非remote作业进行remote语法校验的问题;
- 优化菜单;
- 修复前端多处bug;
- 新增F2 全屏开发;
- 升级SpringBoot 至 2.6.3
- 修复前端 state 赋值 bug
- 修复异常预览内容溢出 bug
- 修复数据预览特殊条件下无法获取数据的 bug
- 优化SQL编辑器性能
- 修复全屏开发退出后 sql 不同步
- 优化作业配置查看及全屏开发按钮
- 新增K8S集群配置

### 后端
- 增加dlink-client-hadoop 版本定制依赖
- 优化pom及升级log4j至最新
- 升级Flink 1.14.2 到 1.14.3
- 修复Flink 1.14 提交任务报错缺类 bug
- 优化日志依赖