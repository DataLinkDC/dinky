---
position: 4
sidebar_position: 4
id: alert_template
title: 告警模版
---

:::warning 触发告警前提条件

1. 只适用于 `FlinkSQL,FlinkJar` 任务
2. 作业为`已发布`状态
3. 满足告警规则,且该告警规则关联了告警模板
:::

## 告警模版列表

> 自 v1.0.0 版本开始,扩展了告警模版的功能,支持自定义告警模版,并支持模版参数化

eg:

```markdown
- **Job Name :** <font color='gray'>${jobName}</font>
- **Job Status :** <font color='red'>${jobStatus}</font>
- **Alert Time :** ${alertTime}
- **Start Time :** ${jobStartTime}
- **End Time :** ${jobEndTime}
- **<font color='red'>${errorMsg}</font>**
  [Go toTask Web](http://${taskUrl})

```

目前支持的模版参数有:

| 参数名称                    | 说明                       | 示例                                              |
|:------------------------|:-------------------------|:------------------------------------------------|
| alertTime               | 告警触发时间                   | 2023-01-02 00:00:00                             |
| jobStartTime            | 作业开始时间                   | 2023-01-01 00:00:00                             |
| jobEndTime              | 作业结束时间                   | 2023-01-02 00:00:00                             |
| duration                | 作业持续时间/耗时                | 45s                                             |
| jobName                 | 作业名称                     | demo                                            |
| jobId                   | 作业ID                     | fe00e413b7bd3888e8906f2a42e2124f                |
| jobStatus               | 作业状态                     | FAILD                                           |
| taskId                  | 任务ID                     | 1                                               |
| jobInstanceId           | 作业实例ID                   | 2                                               |
| taskUrl                 | 任务详情地址                   | http://localhost:8000/#/devops/job-detail?id=29 |
| batchModel              | 作业类型,true 为批处理，false 为流式 | 无                                               |
| clusterName             | 在Dinky内注册的 Flink实例/集群配置  | 无                                               |
| clusterType             | 集群类型                     | 无                                               |
| clusterHost             | 集群地址                     | 无                                               |
| errorMsg                | 错误信息                     | 无                                               |
| checkpointCostTime      | checkpoint耗时             | 无                                               |
| checkpointFailedCount   | checkpoint失败次数           | 无                                               |
| checkpointCompleteCount | checkpoint完成次数           | 无                                               |
| isCheckpointFailed      | 是否checkpoint失败           | 无                                               |
| isException             | 是否异常                     | 无                                               |

