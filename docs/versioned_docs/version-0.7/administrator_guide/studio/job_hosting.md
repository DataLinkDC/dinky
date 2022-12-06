---
sidebar_position: 1
id: job_hosting
title: 作业概述
---




Dinky 做为一站式的实时计算平台，可以托管 Flink 和数据库的作业。

## FlinkSQL

### Local

Dinky 内置的 Flink MiniCluster，如果提交任务至 Local 模式则将在 Dinky 内部运行该作业。

**特点：** 不需要外部 Flink 集群，资源受限。

**适用于：** 语法校验、查看 JobPlan、查看字段级血缘、执行资源占用非常小的批作业。

:::warning 注意事项

   请不要提交流任务至 Local，如果提交了，你将无法关闭它，只能重启 Dinky。

:::

### Standalone

Dinky 将通过 JobManager 的 Rest 端口提交 FlinkSQL 作业至外部的 Flink Standalone 集群。

**特点：** 作业资源共享，启动快，不依赖 Yarn 或 K8S。

**适用于：** 批作业、Flink OLAP 查询、资源占用小的流作业。

### Yarn Session

Dinky 将通过 JobManager 的 Rest 端口提交 FlinkSQL 作业至外部的 Flink Yarn Session 集群。

**特点：** 作业资源共享，启动快。

**适用于：** 作业资源共享，启动快，批作业、Flink OLAP 查询、资源占用小的流作业。

:::tip 说明

  需要手动启动 Yarn Session 集群并注册到 Dinky 的集群实例，详见[集群管理](../register_center/cluster_manage)。

:::

### Yarn Per-Job

Dinky 将通过 Yarn 来创建 Flink Yarn Per-Job 集群。

**特点：** 作业资源隔离，启动慢，每个 JobGraph 创建一个集群。

**适用于：** 资源占用较多的批作业和流作业。

:::tip 说明

   需要在 Dinky 的集群配置中注册相关的 Hadoop 和 Flink 配置，详见[集群管理](../register_center/cluster_manage)。

:::

### Yarn Application

Dinky 将通过 Yarn 来创建 Flink Yarn Application 集群。

**特点：** 作业资源隔离，启动慢，节约网络资源，所有 JobGraph 只创建一个集群。

**适用于：** 资源占用较多的批作业和流作业。

:::tip 说明

   需要在 Dinky 的集群配置中注册相关的 Hadoop 和 Flink 配置，详见[集群管理](../register_center/cluster_manage)。

:::

### Kubernetes Session

Dinky 将通过暴露的 NodePort 端口提交 FlinkSQL 作业至外部的 Flink Kubernetes Session 集群。

**特点：** 作业资源隔离，启动快，动态扩容。

**适用于：** 作业资源隔离，启动快，动态扩容，批作业、Flink OLAP 查询、资源占用小的流作业。

:::tip 说明

  需要在 Dinky 的集群配置中注册相关的 Kubernetes 和 Flink 配置，详见[集群管理](../register_center/cluster_manage)。

:::

### Kubernetes Application

Dinky 将通过 dlink-app 镜像创建的 Flink Kubernetes Application 集群。

**特点：** 作业资源隔离，启动慢，动态扩容，节约网络资源，所有 JobGraph 只创建一个集群。

**适用于：** 作业资源隔离，启动慢，动态扩容，节约网络资源，资源占用较多的批作业和流作业。

:::tip 说明

   需要在 Dinky 的集群配置中注册相关的 Kubernetes 和 Flink 配置，详见[集群管理](../register_center/cluster_manage)。

:::

## DB SQL

Dinky 将把 sql 提交到对应的数据源执行。

**适用于：** 原生 SQL 查询、执行。

:::tip 说明

  需要在数据源中心注册数据库，详见[数据源管理](../register_center/datasource_manage)

:::

