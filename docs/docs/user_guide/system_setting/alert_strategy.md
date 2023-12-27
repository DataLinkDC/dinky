---
position: 4
id: alert_strategy
sidebar_position: 4
title: 告警策略
---

:::info
告警策略是指在满足告警规则的前提下，告警模板的触发条件。

默认情况下,系统内置了一些告警策略,需要注意的是,内置的告警策略不可删除,可以修改告警模版,但是规则不可修改

如果想要使用自定义的告警策略,可以在告警策略页面进行创建,并且可以删除自定义的告警策略
:::

## 内置告警策略列表

![alert_strategy](http://www.aiwenmo.com/dinky/docs/test/alert_strategy.png)

### 参数解读

| 参数    | 说明                                                     | 默认值 |
|-------|--------------------------------------------------------|-----| 
| 名称    | 告警策略名称 ,需要保证唯一性                                        | 无   |
| 触发条件  | 1. 任意规则: 任意规则满足触发条件,触发告警 <br/> 2. 所有规则: 所有规则全部满足才会触发告警 | 无   |
| 触发规则	 | 详见下表[触发规则](#触发规则)                                      | 无   | 

### 触发规则

| 规则名称          | 说明                                                                                                                                                                                        | 可选规则              |
|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------| 
| 作业 ID         | 指 Flink 作业的 ID                                                                                                                                                                            | =                 |
| 运行时间          | Flink 作业运行时间,即耗时                                                                                                                                                                          | >,<,=,<=,>=,!= ,= |
| 作业状态          | Flink 作业状态,可对如下状态值判断:<br/> FINISHED<br/>RUNNING<br/>FAILED<br/>CANCELED<br/>INITIALIZING<br/>RESTARTING<br/>CREATED<br/>FAILING<br/>SUSPENDED<br/>CANCELLING<br/>RECONNECTING<br/>UNKNOWN | =                 |
| 执行模式          | local<br/>yarn-session<br/>yarn-per-job<br/>yarn-application<br/>kubernetes-session<br/>kubernetes-per-job<br/>kubernetes-application                                                     | =                 |                                                                                                                  | 无   |                                                                                                                             | 无   |
| 批模式           | 可选值:<br/> True,False                                                                                                                                                                      | =                 |                                                                                                                    | 无   |                                                                                                                             | 无   |
| CheckPoint 时间 | 自定义时间                                                                                                                                                                                     | >,<, =            |
| CheckPoint 失败 | 可选值:<br/> True,False                                                                                                                                                                      | =                 | 
| 任务产生异常        | 可选值:<br/> True,False                                                                                                                                                                      | =                 |                                                                                                                     | 无   |


## 自定义告警策略

### 创建自定义的告警策略

点击创建告警策略按钮,弹出创建告警策略的弹窗

![alert_rules](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/system_setting/alert_template/alert_rules.png)

注意: 如需添加自定义告警模版, 请点击 **告警模版下拉框**中的**新建告警模版** 按钮,跳转至告警模版页面, 或者直接在 *
*[告警模版](../register_center/alert/alert_template)** 页面创建告警模版
