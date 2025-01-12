---
position: 7
sidebar_position: 7
id: approval
title: 审核发布
---

:::info 简介
Dinky1.3增加了作业审核发布功能，可以对作业代码进行Review，只有审核通过的代码版本才允许被提交。
如需开启审核发布功能，需要在 **配置中心** > **全局配置** > **[审批 配置](./system_setting/global_settings/approval_setting)** 中开启作业上线审核开关

注意: 在 Dinky v1.3.0 版本及以上，作业上线审核开功能默认关闭，需要手动开启，否则无法使用审核发布功能
:::

### 审批列表
此处展示了需要由当前用户进行审核的工单列表，用户可以检查任务信息，对比线上代码版本与提交审核版本的差异，选择通过或驳回任务的上线工单，并留下审核意见。

[//]: # (TODO：审批列表图片，eg：https://github.com/user-attachments/assets/16c5337b-3d7a-4fac-a4c2-882177abbb9c)
[//]: # (![approval_approve_list_001]&#40;https://github.com/user-attachments/assets/16c5337b-3d7a-4fac-a4c2-882177abbb9c&#41;)
> 注意: 线上代码版本，即当前任务上一次通过审核的版本。

### 审批列表
此处展示了当前用户所提交的审核工单，用户可以选择提交、撤回或取消审批工单。

[//]: # (TODO：申请列表图片，eg：https://github.com/user-attachments/assets/97c14760-578a-41a5-8971-81ebdc8f9be5)
[//]: # (![approval_submit_list_001]&#40;https://github.com/user-attachments/assets/97c14760-578a-41a5-8971-81ebdc8f9be5&#41;)

:::tip 提示
1. 创建工单需要在数据开发页面点击创建审批按钮，审核发布页面不提供创建审批功能。
2. 撤回的工单可以被修改审批人，上线说明等信息后重新提交。
3. 被取消的工单无法被恢复。
   :::

