---
position: 3
sidebar_position: 3
id: alert_group
title: 告警组
---

:::warning 触发告警前提条件

1. 只适用于 `FlinkSQL,FlinkJar` 任务
2. 作业为`已发布`状态
3. 满足告警规则,且该告警规则关联了告警模板

:::

## 告警组列表

![alert_group_list](http://pic.dinky.org.cn/dinky/docs/test/alert_group_list.png)

## 参数解读

|  字段   |                 说明                  | 是否必填 |  默认值  | 示例 |
|:-----:|:-----------------------------------:|:----:|:-----:|:--:|
| 告警组名称 |                告警组名称                |  是   |   无   | 无  |
| 告警实例  |              告警实例,可多选               |  是   |   无   | 无  |
| 是否启用  | 已启用/已禁用<br/>注意:引用状态下,数据开发任务无法关联此告警组 |  否   | false | 无  |
|  备注   |                 备注                  |  否   |   无   | 无  |
