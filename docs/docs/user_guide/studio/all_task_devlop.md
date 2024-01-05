---
sidebar_position: 2
position: 2
id: all_task_devlop
title: 作业开发
---

## 创建作业

Dinky支持创建多种类型作业，以满足不同需求，主要分为 Flink类型作业，Jdbc类型作业，其他类型作业(UDF)，如图，下面开始介绍各种类型作业使用教程

![image-20231220140415854](http://pic.dinky.org.cn/dinky/docs/test/202312201404899.png)

#### FlinkSql作业

此作业类型用于开发**FlinkSQL**，与 **FlinkCDC整库同步**作业。

#### FlinkJar作业

用于运行**自定义jar包**，对于非Flink sql作业，使用原生flink代码开发的jar包，可以通过dinky的`exec jar`语法进行提交与管理

#### FlinkSqlEnv作业类型

这是一个比较特殊的作业类型，对于sql作业开发，我们总是不可避免的需要一些通用参数需求，或者一些通用代码片段等，除了使用全局变量以外
我们还可以通过创建一个`FlinkSqlEnv`类型作业把代码写在里面供其他任务引用，以此避免重复编写一些通用的语句，提高开发效率，具体见下面作业配置说明。

[//]: # (![image-20231220141323767]&#40;http://pic.dinky.org.cn/dinky/docs/test/202312201413831.png&#41;)

[//]: # (可在此处对FlinkSqlEnv作业创建的catalog进行选择)

[//]: # (![image-20231220141416725]&#40;http://pic.dinky.org.cn/dinky/docs/test/202312201414788.png&#41;)

#### Jdbc作业

此作业类型用于执行**Jdbc sql语句**，目前支持多种数据库，如：Mysql,ClickHouse、Doris 等,需要提前在配置中心进行数据源注册。

#### 其他类型作业

目前支持编写**UDF**类型的作业，如：Python、Java、Scala 等。

:::tip 说明

Dinky将多种类型作业编写全部SQL化，并拓展了语法，不同类型作业语法并不通用，在创建作业时请注意不要创建错误类型的作业。

:::

### 作业配置

![image-20231220112839608](http://pic.dinky.org.cn/dinky/docs/test/202312201128666.png)

该面板仅在 FlinkSQL 与 Flink Jar 类型作业需要配置，您可以根据具体需求配置参数，参数设置如下

| 是否必填 |     配置项      | 备注                                                                                                                                                                   |
|:----:|:------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  是   |     执行模式     | 指定 FlinkSQL 的执行模式，默认为local，支持以下几种运行模式**<br />Local<br />Standalone<br />Yarn / Yarn Session<br />Yarn Prejob / Yarn Application<br />K8s Application / K8s Session** |
|  是   |   Flink集群    | 除Local模式外，Standalone与Session模式需要选择对应的集群实例，Application与PreJob模式需要选择对应的集群配置，具体配置方法参考注册中心内容                                                                             |
|  否   | FlinkSQL 环境  | 选择当前 FlinkSQL 执行环境或Catalog，默认无，请参考Flink Env作业或Catalog章节                                                                                                              |
|  是   |    任务并行度     | 设置Flink任务的并行度，默认为 1                                                                                                                                                  |
|  否   |     全局变量     | 默认禁用，开启 FlinkSQL 全局变量，以“${}”进行调用                                                                                                                                     |
|  否   |     批模式      | 默认禁用，开启后启用 Batch Mode                                                                                                                                                |
|  否   | SavePoint 策略 | 默认禁用，策略包括:<br/>   **最近一次**<br/>   **最早一次**<br/>   **指定一次**                                                                                                           |
|  否   |     报警组      | 报警组配置详见[报警管理](../register_center/alert/alert_overview)                                                                                                               |
|  否   |     其他配置     | 其他的 Flink 作业配置，具体可选参数，详见[Flink 官网](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/)                                               |

**使用Local模式的作业运行在那里？**

Dinky内置了一个Flink MiniCluster，提交Local模式作业后，Dinky会启动一个单机Flink MiniCluster，作业被运行在此集群上，
该集群资源受限，仅适用于快速体验或临时测试Flink任务,不建议在生产环境使用，生产作业请使用其他模式。

**其他配置的列表里没有我需要的参数怎么办？**

为防止误输入，错误输入，Dinky提供了常用的Flink配置，如果您需要的参数不存在，可以修改dinky安装目录下的 `dinky-loader/FlinkConfClass`
文件，
新增一行为该参数所在flink源码中的全路径类名，重启dinky即可。

除此之外，您还可以在 `注册中心-->文档-->新建文档` 中添加您需要的配置,类型选择
Flink参数，添加完成后即可在配置列表中找到刚刚添加的Flink参数，具体请参考注册中心文档管理模块

### 保存点

Dinky 提供 FlinkSQL 在通过 **智能停止** 作业时，自动触发**savepoint**。也可以在运维中心手动触发，触发成功后会保存结果并记录在这里

![image-20231220114448998](http://pic.dinky.org.cn/dinky/docs/test/202312201144048.png)

### 版本历史

在创建作业后，点击`发布`会自动创建一个历史版本，用于记录历史并回退

![image-20231220134050332](http://pic.dinky.org.cn/dinky/docs/test/202312201340401.png)

单机历史版本即可查看当前版本与所选版本的代码差异

![image-20231220134119789](http://pic.dinky.org.cn/dinky/docs/test/202312201341853.png)

可点击此处回滚版本

![image-20231220134324847](http://pic.dinky.org.cn/dinky/docs/test/202312201343903.png)

### 运行作业

单击左上角`执行`按钮即可提交任务到指定集群，提交为同步操作，运行过程下方控制台会实时打印执行日志，如果作业运行失败，请参考日志内容进行定位。
:::tip 说明

请勿将Select语句作为FlinkSQL作业提交，Select语句请使用预览功能，详见下方预览功能章节

:::
![](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-preview.png)

### 作业预览

在Flink
Sql开发过程中，我们经常需要select查看数据，Dinky提供了预览功能，可以在开发过程中实时查看数据，对于select语句，点击右上角`预览`
即可。

同时你也可以对预览功能进行配置，如下图

![image-20231220114031011](http://pic.dinky.org.cn/dinky/docs/test/202312201140092.png)

参数设置如下

| 是否必填 | 配置项  |                             备注                              |
|:----:|:----:|:-----------------------------------------------------------:|
|  否   | 预览结果 |                   默认开启，开启预览结果将同步运行并返回数据结果                   |
|  否   | 打印流  | 默认禁用，开启打印流将同步运行并返回含有**op**字段信息的 ChangeLog<br/> 默认不开启则返回最终结果 |
|  否   | 最大行数 |                       预览数据的最大行数，默认100                       |
|  否   | 自动停止 |                 默认禁用，开启自动停止将在捕获最大行记录数后自动停止                  |
