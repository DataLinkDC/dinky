---
sidebar_position: 6
position: 6
id: execute_jar
title: EXECUTE JAR
---

:::info 背景

该功能是为了解决在执行 JAR 类型任务时，去除各种繁杂交互, 以及统一任务提交方式, 使得用户可以更加方便的提交任务。

:::

## 语法结构

```sql

EXECUTE JAR WITH (
'uri'='<jar_path>.jar', -- 该参数 必填
'main-class'='<main_class>', -- 该参数 必填
'args'='<args>', -- 主类入参 该参数可选 
'parallelism'='<parallelism>', -- 任务并行度 该参数可选
'savepoint-path'='<savepoint_path>' --任务保存点路径 该参数可选
);

```

## Demo:

```sql
EXECUTE JAR WITH (
'uri'='rs:/jar/flink/demo/SocketWindowWordCount.jar',
'main-class'='org.apache.flink.streaming.examples.socket',
'args'=' --hostname localhost ',
'parallelism'='',
'savepoint-path'=''
);
```
:::warning 注意
1. 以上示例中, uri 参数使用了 rs 协议, 请参考 [资源管理](../../user_guide/register_center/resource) 中 rs 协议使用方式
2. 以上示例中, uri 的值为 rs:/jar/flink/demo/SocketWindowWordCount.jar, 该值为资源中心中的资源路径, 请确保资源中心中存在该资源,请忽略资源中心 Root 节点(该节点为虚拟节点)
:::