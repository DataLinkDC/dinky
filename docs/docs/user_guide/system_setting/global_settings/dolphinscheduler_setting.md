---
position: 4
sidebar_position: 4
id: dolphinscheduler_setting
title: Dolphinscheduler 配置
---


:::info 简介

为了方便用户使用 Dinky 与 DolphinScheduler 进行集成，Dinky 提供了 DolphinScheduler 相关配置。

在 Dinky V1.0.0 版本开始，提供了 DolphinScheduler 页面化配置功能，可以在页面上修改配置，修改后会自动生效，无需重启 Dinky 服务。

:::

如果用户想要将Dinky开发好的Flink任务推送到Dolphinscheduler。

### 1.生成海豚调度令牌

访问Dolphinscheduler系统，安全中心>令牌管理，创建令牌
:::warning 注意
设置令牌的失效时间，默认失效时间=创建时间，创建即失效，记得更改。
:::

### 2.在Dinky配置Dolphinscheduler相关参数

![dolphin_setting](http://pic.dinky.org.cn/dinky/docs/test/dolphin_setting.jpg)

**参数配置说明:**

| 参数名称                   | 参数说明                                                                                                                     |  
|------------------------|--------------------------------------------------------------------------------------------------------------------------| 
| 是否启用 DolphinScheduler  | 如果启用，Dinky 会将开发好的任务推送到 DolphinScheduler 系统中，并在 DolphinScheduler 系统中创建项目<br/>请最后修改此配置,因为启用时会自动创建项目.如果下边的几项配置不正确,会导致创建项目失败 |  
| DolphinScheduler 地址    | 能正常访问的DolphinScheduler 地址                                                                                                |       
| DolphinScheduler Token | 步骤1中在DolphinScheduler系统中生成的令牌                                                                                            |
| DolphinScheduler 项目名   | DolphinScheduler系统中的项目名，如果不存在，在修改 `是否启用 DolphinScheduler`配置为true后，会自动创建                                                  |

### 3.推送任务

将 Dinky 开发好的 Flink 任务推送到海豚调度：数据开发页，作业右上角依次点击 **保存** -> **上线** -> **推送** 按钮即可

### 4.访问Dolphinscheduler

可以看到与配置的 **DolphinScheduler 项目名** 同名的项目
![dolphin](http://pic.dinky.org.cn/dinky/docs/test/dolphin3.png)
**项目管理** > **项目名称** > **工作流定义**，可以看到dinky推送过来的工作流，依次点击右边**上线**，**运行** 按钮即可。
:::warning 注意
如果报错提示租户相关问题，可能是海豚调度的项目对应的账户没有关联租户，需要在海豚调度安全中心创建租户后，对用户进行分配租户。
:::