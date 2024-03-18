---
sidebar_position: 8
position: 4
id: add_file_statement
title: ADD FILE
---

:::info 背景

在某些场景下，用户不仅需要将 jar 上传到 Flink 集群中，可能还需要添加一些其他类型的文件到环境中，比如配置文件、资源文件等。

为了支持这种场景，我们在 Dinky 中新增了`ADD FILE` 语法, 原理与`ADD CUSTOMJAR`类似, 用于将文件添加到环境中, 以供 Flink 任务使用。

同样支持全模式提交。也支持从资源中心(rs:/ 协议)中获取文件。

ADD FILE 同样可以达到 `ADD CUSTOMJAR` 的效果, 但是从设计上 ADD CUSTOMJAR 更适合于添加 jar 包, ADD FILE 更适合于添加其他类型的文件。

:::

> 语法：
```sql
ADD FILE '<path_to_filename>.jar'
```


## 实战范围



当需要添加一些文件到环境中时，可以使用 ADD FILE 语法，比如：

```sql

ADD FILE '/tmp/dinky/resource/file.properties'
```

## 实战场景

> 在 Dinky v1.0.0 版本中，支持了`资源管理`,同时扩展了 rs 协议, 使得用户可以通过 rs 协议调用该 jar , 有关 `资源管理rs 协议`的使用请参考 [资源管理](../../user_guide/register_center/resource)。

> 如果使用 rs 协议访问, 请参考 [资源管理](../../user_guide/register_center/resource) 中的使用方式, 以下示例中,我们使用 rs 协议访问资源中心中的 jar 资源

eg: 假设:

1. 在 `配置中心` -> `Resource 配置` 中使用 `LOCAL` 协议 ,并设置了 `上传根路径`为 /tmp/dinky/resource
2. 在资源中心 Root 根目录下上传了一个 app.jar 文件,则该文件真实路径为 /tmp/dinky/resource/file.properties
3. 其访问方式如下:

```sql
ADD FILE 'rs:/file.properties'
```

:::tip 提示

1. 如果使用 rs 协议访问, 请注意路径,不要带上 Root 节点
2. 推荐使用 rs 协议访问, 以便于在 Flink 中支持除 k8s 模式之外的所有模式提交。
3. 此功能可以覆盖 ADD CUSTOMJAR 功能, 可自由选择使用

:::
