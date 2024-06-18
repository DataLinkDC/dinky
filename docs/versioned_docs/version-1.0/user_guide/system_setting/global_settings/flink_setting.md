---
position: 2
id: flink_setting
sidebar_position: 2
title: Flink 配置
---


当用户使用 **Application 模式**以及 **RestAPI** 时，需要在 **Flink 设置** 页面进行相关修改。

另外**Application 模式** 支持**Yarn** 和 **Kubernetes**，启用 **RestAPI** 后，Flink 任务的 savepoint,停止等操作都将会通过
JobManager 的 RestAPI 进行。

首先进入**配置中心** > **全局配置** > **Flink设置**，对参数配置进行修改即可。

![flink_setting](http://pic.dinky.org.cn/dinky/docs/test/flink_setting.jpg)

**参数配置说明:**

| 参数名称           | 参数说明                                                          |
|:---------------|:--------------------------------------------------------------|
| **使用 RestAPI** | 是否使用 RestAPI 进行任务操作，开启后，FlinkSQL 任务的 savepoint、停止等操作将通过此参数进行。 |
| **Job 提交等待时间** | 提交 Application 或 PerJob 任务时获取 Job ID 的最大等待时间，单位是秒。            |

:::warning 注意
1. **SQL 分隔符** 配置项自 Dinky1.0.0-release 开始已被删除,不支持自定义,请严格遵守 SQL 语法规范。必须以 `;\n` 作为 SQL 语句的分隔符。`\n`为隐式换行符(即: 回车)，不是明文字符串。

:::