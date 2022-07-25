---
sidebar_position: 2
id: job_instance_status
title: 作业实例状态
---

如果您已经提交作业或者发布上线作业，可通过运维中心查看和修改作业实例状态。默认显示当前实例，切换后显示历史实例。

**当前实例**

![current_instance](http://www.aiwenmo.com/dinky/docs/administrator_guide/devops_center/job_manage/job_instance_status/current_instance.png)

**历史实例**

![history_instance](http://www.aiwenmo.com/dinky/docs/administrator_guide/devops_center/job_manage/job_instance_status/history_instance.png)

## 实例状态

运行信息为您展示作业的实时运行信息。您可以通过作业的状态来分析、判断作业的状态是否健康、是否达到您的预期。**Task状态** 为您显示作业各状态的数量。Task存在以下11种状态，均为 Flink 作业状态

- 已创建
- 初始化
- 运行中
- 已完成
- 异常中
- 已异常
- 已暂停
- 停止中
- 停止
- 重启中
- 未知

作业提交或者发布后，可看到作业实例的详情信息。

:::tip 说明

如果作业长时间处于初始化状态而未发生改变时，一般是后台发生了异常，却没有被 Dinky 捕捉到，需要自行查看 log 来排查问题。
目前 Per-Job 和 Application 作业在停止时会被识别为 **未知** 状态。如果网络受限或者集群已被手动关闭，作业也会被识别为 **未知**。

:::

## 作业实例信息

作业实例详细包含配置信息及运行状态和时间，各字段的含义

| 字段名称 |                                                         说明                                                          |
| :------: |:-------------------------------------------------------------------------------------------------------------------:|
|  作业名  |                                               创建的作业名称，即pipeline.name                                                |
| 生命周期 |                                                开发中<br/> 已发布<br/> 已上线                                                |
| 运行模式 | Standalone<br/>Yarn Session<br/>Yarn Per-job<br/>Yarn Application<br/>Kubernetes Session<br/>Kubernetes Application |
| 集群实例 |                                                  手动或自动注册的 Flink 集群                                                  |
|  作业ID  |                                                    Flink 作业的 JID                                                    |
|   状态   |                                                        实例状态                                                         |
| 开始时间 |                                                      作业创建时的时间                                                       |
|   耗时   |                                                       作业运行的时长                                                       |

:::tip 说明

如果作业状态有问题，可以进入作业信息后点击刷新按钮强制刷新作业实例状态。

:::

## 修改作业状态

1.在运维中心，单击**点击目标作业名**

2.单击**作业总览**，进入作业详情页面

3.根据需要单击以下按钮，修改作业状态

![Modify_instance](http://www.aiwenmo.com/dinky/docs/administrator_guide/devops_center/job_manage/job_instance_status/Modify_instance.png)

其中，每个按钮含义如下表所示

|    操作名称    |             说明              |
| :------------: |:---------------------------:|
|    重新启动    |           作业只重新启动           |
|    停止    |            作业只停止            |
|    重新上线    |       作业重新启动，并且从保存点恢复       |
|      下线      |    作业触发 SavePoint 并同时停止     |
| SavePoint 触发 | 作业触发 SavePoint 操作，创建一个新的保存点 |
| SavePoint 暂停 |   作业触发 SavePoint 操作，并暂停作业   |
| SavePoint 停止 |   作业触发 SavePoint 操作，并停止作业   |
|    普通停止    |            作业只停止            |

