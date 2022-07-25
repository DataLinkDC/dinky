---
sidebar_position: 2
id: job_config
title: 作业基础配置
---

Dinky 使用 Flink 社区开源版本，也完全兼容企业二次开发的 Flink 版本。本文介绍如何配置 Flink 的作业。

## 作业配置

FlinkSQL 作业配置,您可以根据具体需求配置参数，参数设置如下

|   类型   |     配置项     | 备注                                                                                                                      |
| :------: | :------------: |:------------------------------------------------------------------------------------------------------------------------|
| 作业配置 |    执行模式    | 指定 FlinkSQL 的执行模式，默认为local                                                                                              |
| 作业配置 |   Flink集群    | 此参数依据执行模式选择需要的集群实例或者集群配置                                                                                                |
| 作业配置 |    作业名称    | 默认作业名，作业名称在当前项目中必须保持 **唯一**                                                                                             |
| 作业配置 | FlinkSQL 环境  | 选择当前 FlinkSQL 执行环境，会提前执行环境语句，默认无                                                                                        |
| 作业配置 |   任务并行度   | 设置Flink任务的并行度，默认为 1                                                                                                     |
| 作业配置 | Insert 语句集  | 默认禁用，开启语句集机制，将多个 Insert 语句合并成一个JobGraph<br/> 进行提交，select 语句无效                                                           |
| 作业配置 |    全局变量    | 默认禁用，开启 FlinkSQL 全局变量，以“${}”进行调用                                                                                        |
| 作业配置 |     批模式     | 默认禁用，开启后启用 Batch Mode                                                                                                   |
| 作业配置 | SavePoint 策略 | 默认禁用，策略包括:<br/>   **最近一次**<br/>   **最早一次**<br/>   **指定一次**                                                              |
| 作业配置 |     报警组     | 报警组配置详见[报警管理](../register_center/warning)                                                                               |
| 作业配置 |    其他配置    | 其他的 Flink 作业配置，具体可选参数，详见[Flink 官网](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/)  |

:::tip 说明

 FlinkSQL 执行模式除 local 外，还包括Standalone、Yarn Session、Yarn Per-Job、Yarn Application、Kubernetes Session、Kubernetes Application

:::

## 执行配置

另外 Dinky 提供了针对 FlinkSQL 查询预览的配置，，参数设置如下

|   类型   |  配置项  |                             备注                             |
| :------: | :------: | :----------------------------------------------------------: |
| 执行配置 | 预览结果 |        默认开启，开启预览结果将同步运行并返回数据结果        |
| 执行配置 |  打印流  | 默认禁用，开启打印流将同步运行并返回含有**op**字段信息的 ChangeLog<br/> 默认不开启则返回最终结果 |
| 执行配置 | 最大行数 |                 预览数据的最大行数，默认100                  |
| 执行配置 | 自动停止 |     默认禁用，开启自动停止将在捕获最大行记录数后自动停止     |

## 保存点

Dinky 提供 FlinkSQL 在通过 **cancel** 作业停止时，自动保存**savepoint**。并生成一个保存 savepoint 的创建时间。

:::tip 说明

​    此保存点必须通过 **Flink Cancel** 完成

:::
