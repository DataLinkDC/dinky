---
position: 7
sidebar_position: 7
id: udf
title: UDF
---

:::info 简介
Dinky 在 v1.0.0 版本开始，提供了 UDF 管理的功能，可以在 Dinky 中管理 自定义的UDF，方便的进行作业所使用的UDF 管理。
:::


## UDF 注册管理

> 在 Git 项目中管理的 UDF 项目经过构建后，会自动将产物推动至 [资源中心](./resource) 的 udf 节点下集中管理

在新增时 可以直接拉取 [资源中心](./resource) 中的 UDF 产物进行添加, 实现解析 UDF 具体的函数信息,并提供方可修改函数名功能

![udf_register_list](http://pic.dinky.org.cn/dinky/docs/test/udf_register_list.png)

**注意:** 此方法没有实现自动注册给Flink 集群,后续实现在数据开发中自动解析补全UDF函数名称,并自动生成注册函数的 Flink 语句



## UDF 模板管理

> 可以在该功能中管理常用的 UDF 模板,方便的进行 UDF 函数的开发,避免重复的编写模板代码

:::warning 注意
1. 删除模版时会进行引用检查,如果有作业引用该模版,将无法删除
:::

- 支持 scala/java/python 三种语言的模板管理

![udf_template_list](http://pic.dinky.org.cn/dinky/docs/test/udf_template_list.png)
