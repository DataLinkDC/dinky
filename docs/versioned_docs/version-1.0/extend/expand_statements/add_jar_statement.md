---
sidebar_position: 4
position: 4
id: add_jar_statement
title: ADD CUSTOMJAR
---

:::info 背景

在某些场景下，用户需要将自定义 jar 添加到 classpath，以便于在 SQL 中使用自定义 jar 中的函数。但是 Flink 原生的 ADD JAR 语法 无法在所有 Flink 版本中通用, 因此我们在 Flink SQL 中添加了 ADD CUSTOMJAR 语法，用于将用户 jar 添加到 classpath。

以达到在 Dinky 支持的所有 Flink 版本都可使用, 且不影响原生 ADD JAR 语法的目的。同时支持除 k8s 模式之外的所有模式提交。


:::

> 语法：
```sql
ADD CUSTOMJAR '<path_to_filename>.jar'
```


## 实战范围
当连接器和第三方依赖过多时，经常容易导致 jar依赖冲突；

add CUSTOMJAR 可以选择性的识别添加到服务器，做到环境隔离


## 实战场景

> 在 Dinky v1.0.0 版本中，支持了`资源管理`,同时扩展了 rs 协议, 使得用户可以通过 rs 协议调用该 jar , 有关 `资源管理rs 协议`的使用请参考 [资源管理](../../user_guide/register_center/resource)。

> 如果使用 rs 协议访问, 请参考 [资源管理](../../user_guide/register_center/resource) 中的使用方式, 以下示例中,我们使用 rs 协议访问资源中心中的 jar 资源

eg: 假设:

1. 在 `配置中心` -> `Resource 配置` 中使用 `LOCAL` 协议 ,并设置了 `上传根路径`为 /tmp/dinky/resource
2. 在资源中心 Root 根目录下上传了一个 app.jar 文件,则该文件真实路径为 /tmp/dinky/resource/app.jar
3. 其访问方式如下:

```sql
ADD CUSTOMJAR 'rs:/app.jar'
```

:::tip 提示

1. 如果使用 rs 协议访问, 请注意路径,不要带上 Root 节点
2. 推荐使用 rs 协议访问, 以便于在 Flink 中支持除 k8s 模式之外的所有模式提交。

:::
