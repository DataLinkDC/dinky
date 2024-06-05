---
sidebar_position: 1
position: 1
id: studio_overview
title: 数据开发功能简介
---

:::info 简介
本章节主要介绍 Dinky 数据开发的基本设计理念，以及如何使用 Dinky 数据开发功能。
:::

## 数据开发引导

### 数据开发功能介绍

Dinky 数据开发功能主要包括：

- 基于 普通 DB 的SQL 的数据开发与调试和运行
- 基于 Flink SQL/Flink Jar 任务的开发与调试和运行
- 基于 Python/Java/Scala 的 UDF 开发与调试和运行
- 根据作业类型自动渲染数据开发界面,使得页面交互更加友好,开发更加高效

## 创建作业

Dinky支持创建多种类型作业，以满足不同需求，主要分为 Flink类型作业，Jdbc类型作业，其他类型作业(UDF)，如图，下面开始介绍各种类型作业使用教程

![](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/create_task.png)

#### FlinkSql作业

此作业类型用于开发**FlinkSQL**，与 **FlinkCDC整库同步**作业。

#### FlinkJar作业

用于运行**自定义jar包**，对于非Flink sql作业，使用原生flink代码开发的jar包，可以通过dinky的`execute jar`语法进行提交与管理

#### FlinkSqlEnv作业类型

这是一个比较特殊的作业类型，对于sql作业开发，我们总是不可避免的需要一些通用参数需求，或者一些通用代码片段等，除了使用全局变量以外
我们还可以通过创建一个`FlinkSqlEnv`类型作业把代码写在里面供其他任务引用，以此避免重复编写一些通用的语句，提高开发效率。

#### Jdbc作业

此作业类型用于执行**Jdbc sql语句**，目前支持多种数据库，如：Mysql,ClickHouse、Doris 等,需要提前在配置中心进行数据源注册。

#### 其他类型作业

目前支持编写**UDF**类型的作业，如：Python、Java、Scala 等。

:::tip 说明

Dinky将多种类型作业编写全部SQL化，并拓展了语法，不同类型作业语法并不通用，在创建作业时请注意不要创建错误类型的作业。
:::