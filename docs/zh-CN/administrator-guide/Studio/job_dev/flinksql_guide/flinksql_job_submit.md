完成作业开发和上线后，您需要在数据开发页面启动作业至运行阶段。此外，作业在暂停或者停止后需要恢复时，需要启动作业。

## 限制说明

- 异步提交后，无法接收告警信息，而发布上线后可以接收告警信息，自动恢复作业等；
- 异步提交后，无法及时看到作业的详细情况，而发布上线可以看到作业的详细情况；

## 启动场景

- **上线后启动：** 您可以全新启动（无状态）。
- **暂停后启动：** 暂停作业时，如果你选择了Savepoint策略。系统将按照以下情况进行处理
  - 如果您选择 Savepoint 策略，在暂停前执行一次Savepoint，则作业暂停后启动恢复时，系统会从最新的Savepoint状态恢复；
  - 如果您禁用 Savepoint 策略，则作业暂停后启动恢复时，系统会从最新的Checkpoint状态恢复；
- **停止后启动：** 停止作业时，根据您选择 Savepoint 策略，作业在停止时是否执行一次Savepoint，系统将按照以下情况进行处理：
  - 如果选择 Savepoint 策略，那么作业会先进行Savepoint后进入停止流程。在作业停止后，系统会自动清除作业相关的Checkpoint信息；
  - 如果 Savepoint 策略禁用，那么作业会进入停止流程。在作业停止后，系统会自动清除作业相关的Checkpoint信息；

作业停止后启动时，您可以全新启动（无状态）；或者从所选Savepoint开始恢复作业。

## Flink作业启动操作步骤

1.首先登录Dinky数据开发控制台

2.在左侧菜单栏选择**目录 > 作业名称 > 检查当前的FlinkSql > 发布 > 上线**

或者选择**目录 > 作业名称 > 检查当前的FlinkSql > 异步提交**

作业启动异步提交如下图：

![async_submit](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/Studio/job_dev/flinksql_guide/flinksql_job_submit/async_submit.png)



作业启动发布上线如下图：

![online](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/Studio/job_dev/flinksql_guide/flinksql_job_submit/online.png)



有关发布上线的详细内容，请参考运维中心
