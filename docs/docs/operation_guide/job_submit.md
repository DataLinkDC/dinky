---
sidebar_position: 3
id: job_submit
title: 作业提交
---




## 功能说明

- **执行当前的 SQL：** 提交执行未保存的作业配置，并可以同步获取 SELECT、SHOW 等执行结果，常用于 Local、Standalone、Session 执行模式；
- **异步提交：** 提交执行最近保存的作业配置，可用于所有执行模式；
- **发布** 发布当前作业的最近保存的作业配置，发布后无法修改；
- **上线** 提交已发布的作业配置，可触发报警；
- **下线** 停止已上线的作业，并触发 SavePoint；
- **停止** 只停止已提交的作业；
- **维护** 使已发布的作业进入维护状态，可以被修改；
- **注销** 标记作业为注销不可用状态。

## 常用场景

- **查看 FlinkSQL 执行结果：** 执行当前的 SQL。
- **提交作业：** 异步提交。
- **上线作业：** SavePoint 最近一次 + 上线。
- **下线作业：** 下线。
- **升级作业：** 下线后上线。
- **全新上线作业：** SavePoint 禁用 + 上线。

## Flink作业启动操作步骤

1.首先登录Dinky数据开发控制台

2.在左侧菜单栏选择**目录 > 作业名称 > 检查当前的FlinkSql > 发布 > 上线**

或者选择**目录 > 作业名称 > 检查当前的FlinkSql > 异步提交**

作业启动异步提交如下图：

![async_submit](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/studio/job_dev/flinksql_guide/flinksql_job_submit/async_submit.png)

作业启动发布上线如下图：

![online](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/studio/job_dev/flinksql_guide/flinksql_job_submit/online.png)

:::info 信息
有关发布上线的详细内容，详见用户手册的运维中心
:::