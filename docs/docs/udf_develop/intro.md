---
sidebar_position: 1
id: udf_intro
title: UDF 功能简介
---

## 开发目的

在使用 flink 中，难免碰到 UDF 的场景。开发、编译、打包，注册函数、测试等一系列流水线操作，这些
都由 Dinky 接管后，只需要开发、测试即可。

目前 Dinky 在对此模块进行优化调整，并有了一个雏形，现阶段支持单类开发；

Dinky 将持续打造 UDF 开发，将来规划与 git接轨，实现在线开发。
> 1. UDF 目前还在孵化阶段，请谨慎使用；
>
> 2. 支持 `Python` 、`Java` 、 `Scala` 三种代码语言
>
> 3. Flink 模式现除了 `k8s application`不支持外，其余模式均可正常使用

---

## 使用方式

1. 在 `数据开发` 里面创建作业 ， 然后选择 `Python` 、`Java` 、 `Scala`等即可
   ![create_udf_work.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/create_udf_work.png)
2. udf 这边提供模板，方便快速创建，模板具体使用请看 [udf 模板介绍](./udf_template_intro)
   ![create_udf_work2.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/create_udf_work2.png)

---

## 操作演示

![create_udf_work.gif](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/create_udf_work.gif)
 
