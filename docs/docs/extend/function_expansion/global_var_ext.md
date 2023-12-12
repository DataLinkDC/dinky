---
sidebar_position: 9
id: global_var_ext
title: 表达式变量扩展
---

:::tip

本扩展文档适用于 v1.0.0 及以上版本,可以直接在已部署服务中直接扩展,无需重新编译部署
:::

## 介绍

> 本扩展用于扩展表达式中的变量，以满足更多场景需求


## 扩展方法

找到 `部署的 dinky/dinky-loader` 目录下的 `ExpressionVariableClass` 文件, 并在其中添加


举例:

> 假设需要扩展 [`Hash 算法`](https://doc.hutool.cn/pages/HashUtil/) 相关的表达式变量

只需要在 `ExpressionVariableClass` 文件中添加如下类的全限定名即可
```text

cn.hutool.core.util.HashUtil

```
如上示例，将会扩展表达式变量中的 `Hash 算法`,你就可以在表达式中使用 `Hash 算法` 相关的表达式了

:::tip 说明
1. 请确保您的扩展类在 `dinky-loader` 中存在
2. 请确保您的扩展类在 `dinky 中已被加载(类加载机制)`
3. 请确保您的扩展类中的方法为 `public static` 修饰


考虑到多数用户通用情况下,当然也欢迎您将此次扩展贡献给社区, 请参考 [如何贡献](../../developer_guide/contribution/how_contribute) 进行 [PR](../../developer_guide/contribution/pull_request)
:::