---
sidebar_position: 2
position: 2
id: job_details
title: 任务详情
---


:::info 信息

在此页面内,可以直观的看到 Flink 任务的详细信息.

并且可以在点击某一Dag 节点后,查看该节点的详细信息. 

以及支持读取该算子的 CheckPoint 信息(注意: 该功能需要 Flink 任务开启 CheckPoint 功能)
:::


## 头部功能区域

![header_btns](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/header_btns.png)

> 头部按钮功能区放置了一些常用的操作按钮, 以便于您快速的进行操作.包括:

- `编辑`: 注意: 此按钮并非修改任务信息的功能,而是在某些情况下任务失联,无法连接任务关联的集群,需要重新映射集群信息,以便于 Dinky 侧能够正确的连接到远端 Flink 集群,以便重新获取任务详细信息.
- `刷新`: 手动刷新当前任务实例的信息
- `FlinkWebUI`: 跳转到远端 Flink 任务的 WebUI 页面
- `重新启动`: 重新启动当前任务实例,并生成新的任务实例,并非在当前任务实例上进行重启
- `智能停止`: 触发此操作后,会在当前任务实例上触发 SavePoint 操作,并在 SavePoint 成功后,停止当前任务实例(注意: 该操作后台会检测当前任务是否配置了 SavePoint 路径,如果没有配置,则不会触发 SavePoint 操作,直接停止任务实例,由 Flink 端调用 SavePoint+Stop 操作)
- `SavePoint 触发`: 触发此操作后,会在当前任务实例上触发 SavePoint 操作 (注意: 此操作不会停止当前任务实例,请确保该任务配置了 SavePoint 路径)
- `SavePoint 暂停`: 暂停当前任务实例的 SavePoint 操作
- `SavePoint 停止`: 触发此操作后,会在当前任务实例上触发 SavePoint 操作,并在 SavePoint 成功后,停止当前任务实例(注意: 此操作会停止当前任务实例,请确保该任务配置了 SavePoint 路径)
- `普通停止`: 停止当前任务实例,并不会触发 SavePoint 操作


## 作业信息

![job_detail_info](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_detail_info.png)

如上图所示, 该 Tag 展示了 Flink 任务的基本信息, 包含了 Dinky 侧定义的一些配置, 以及远端 Flink 任务的运行状态, Dag 执行图, 任务的 输入输出记录等信息.

:::tip 提示

- 在配置信息展示区域,可以点击 `Flink实例` 的值,直接跳转至该任务使用的 Flink 实例

:::


## 作业日志

![job_logs](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_logs.png)

如上图所示, 该 Tag 展示了 Flink 任务的日志信息, 包含 Root Exception, 以及 Flink JobManager 和 TaskManager 的日志信息.以便于您快速的定位问题.

## 版本信息

![job_version](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_version.png)

如上图所示, 该 Tag 展示了 Flink 任务的历史版本信息, 包含了 Flink 任务的历史版本, 以及每个版本的详细信息,此功能在每次任务执行`发布`时,会自动记录当前任务的版本信息,以便于您快速的回滚到某个版本.点击左侧列表中的 `版本号` 即可查看该版本的详细信息.
:::warning 注意
注意: 此功能提供删除历史版本的功能,但是请谨慎操作,删除后无法恢复.
:::
## 作业快照

![job_checkpoint_list](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_checkpoint_list.png)

如上图所示, 该 Tag 展示了 Flink 任务的 CheckPoint 信息, 包含了 CheckPoint 列表和 SavePoint 列表, 以及每个 CheckPoint 的详细信息.

:::tip 提示
在 CheckPoint 列表中,可以点击 `此处恢复` 按钮,触发从该 CheckPoint 恢复任务的操作,并停止当前任务实例,启动新的任务实例
:::


## 监控 

![job_metrics_list](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_metrics_list.png)

如上图所示, 该 Tag 展示了 Flink 任务的监控信息, 包含了 Flink 任务的监控指标, 以及每个指标的详细信息.此功能需要再任务运行时,自行配置 Flink 任务的监控指标.

:::tip 拓展
此功能产生的监控数据 由 Apache Paimon 进行存储,存储方式为`本地文件存储`, 并由后端通过 SSE(Server-Sent Events) 技术进行实时推送,以便于前端实时展示监控数据.
:::

:::warning 注意
此功能必须要在任务状态为 `运行中` 时才能正常使用,否则 `任务监控配置` 按钮则不会出现,无法新增监控指标和实时刷新监控指标,但是该任务下已经定义的监控指标,依然可以正常查看.
:::

## SQL 血缘

![job_lineage](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_lineage.png)

如上图所示, 该 Tag 展示了 Flink 任务的 SQL 血缘信息,可以直观的看到 Flink 任务的 SQL 血缘关系,以及字段之间的最终关系.
:::tip RoadMap
目前 SQL 血缘功能还在开发中,后续会支持更多的功能,包括: 
- [ ] 连接线展示其转换逻辑关系
- [ ] 血缘关系图的导出
- [ ] 血缘关系图的搜索
- [ ] 全局血缘影响分析
- ...
:::

## 告警记录

![job_alert_list](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/devops_center/job_alert_list.png)

如上图所示, 该 Tag 展示了 Flink 任务的告警信息,可以直观的看到 Flink 任务的告警信息,以及告警的详细信息.

:::warning 注意

任务告警只有当 任务的生命周期为 `已发布`时,且满足告警策略,并关联了告警组,当任务某个指标满足告警策略时,会触发告警,并在此处展示.

:::