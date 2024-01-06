---
sidebar_position: 2
position: 2
id: environment_config
title: 执行模式
---

> 本章节仅简单介绍Dinky支持的执行模式与配置方法，具体参数配置请参考[集群管理](../register_center/cluster_manage)


### Local 本地执行模式

:::tip 特点

Dinky 内置的 Flink MiniCluster,资源受限,用于语法校验等操作。

**特点：** 不需要外部 Flink 集群，资源受限。

**适用于：** 测试任务，语法校验、查看 JobPlan、查看字段级血缘、执行资源占用非常小的作业。

:::

作业配置处的`执行模式`中，勾选`Local模式`，即可使用。
![local_mode](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/local_mode.png)
### Standalone 执行模式

:::tip 特点

与外部 Standalone 集群共享资源,用于批操作和小流量任务。

Dinky 将通过 JobManager 的 Rest 端口提交 FlinkSQL 作业至外部的 Flink Standalone 集群。

**特点：** 作业资源共享，启动快，不依赖 Yarn 或 K8S。

**适用于：** 批作业、Flink OLAP 查询、资源占用小的流作业。

:::

自行根据Flink官网教程部署一个Standalone模式，并将地址其添加到`注册中心-Flink实例`中。
![create_standalone_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_standalone_cluster.png)

相关依赖添加完成后，在作业配置处的`执行模式`中，勾选`standalone模式`，并且在下方的`flink集群`选项框中勾选上方添加的集群，即可使用。
![select_standalone](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/select_standalone.png)

### Yarn Session 执行模式

:::tip 特点

与外部 Yarn Session 集群共享资源,用于批操作和小流量任务。

Dinky 将通过 JobManager 的 Rest 端口提交 FlinkSQL 作业至外部的 Flink Yarn Session 集群。

**特点：** 作业资源共享，启动快。

**适用于：** 作业资源共享，启动快，批作业、Flink OLAP 查询、资源占用小的流作业。

:::

自行根据Flink官网教程部署一个 `Flink Yarn Session`模式，并将地址其添加到`注册中心-Flink实例`中。

![create_yarn_session_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_yarn_session_cluster.png)
相关依赖添加完成后，在作业配置处的`执行模式`中，勾选`yarn session模式`，并且在下方的`flink集群`选项框中勾选上方添加的集群，即可使用。

![select_yarn_session](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/select_yarn_session.png)

### Yarn Per-Job 执行模式

:::tip 特点

每个 JobGraph 创建一个集群,资源隔离,用于资源占用大的任务。

Dinky 将通过 Yarn 来创建 Flink Yarn Per-Job 集群。

**特点：** 作业资源隔离，启动慢，每个 JobGraph 创建一个集群。

**适用于：** 资源占用较多的批作业和流作业。

:::

在 `注册中心-集群配置` 中注册 Hadoop 与 Flink 相关配置，具体配置过程见[集群管理](../register_center/cluster_manage)
的集群配置管理。

![create_yarn_per_job_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_yarn_per_job_cluster.png)

在配置完成后并且相关依赖添加完成后，在作业配置处的`执行模式`中，勾选`yarn per-job模式`，并且在下方的`flink集群`
选项框中勾选上方添加的集群，即可使用。

![select_yarn_per_job](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/select_yarn_per_job.png)
### Yarn Application 执行模式

:::tip 特点

所有 JobGraph 共享一个集群,资源隔离,节约网络资源。

Dinky 将通过 Yarn 来创建 Flink Yarn Application 集群。

**特点：** 作业资源隔离，启动慢，节约网络资源，所有 JobGraph 只创建一个集群。

**适用于：** 资源占用较多的批作业和流作业。

:::

在 `注册中心-集群配置` 中注册 Hadoop 与 Flink 相关配置，具体配置过程详见[集群管理](../register_center/cluster_manage)
的集群配置管理。

![create_yarn_application_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_yarn_application_cluster.png)

### Kubernetes Session 执行模式

:::tip 特点

与外部 Kubernetes Session 集群共享资源,动态扩容。

Dinky 将通过暴露的 NodePort 端口提交 FlinkSQL 作业至外部的 Flink Kubernetes Session 集群。

**特点：** 作业资源隔离，启动快，动态扩容。

**适用于：** 作业资源隔离，启动快，动态扩容，批作业、Flink OLAP 查询、资源占用小的流作业。

:::

根据 Flink 官网手动部署一个 Flink Kubernetes Session 集群，并暴露 **NodePort **端口， 注册到 **集群实例** 中

![create_kubernetes_session_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_kubernetes_session_cluster.png)

注册完成后，在创建作业后可在右侧作业配置处进行勾选执行模式

![select_kubernetes_session](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/select_kubernetes_session.png)

### Kubernetes Application 执行模式

:::tip 特点

每个 JobGraph 创建一个 Pod,资源隔离,动态扩容。

**特点：** 作业资源隔离，启动慢，动态扩容，节约网络资源，所有 JobGraph 只创建一个集群。

**适用于：** 作业资源隔离，启动慢，动态扩容，节约网络资源，资源占用较多的批作业和流作业。

:::

在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Kubernetes Native类型

![create_kubernetes_application_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_kubernetes_application_cluster.png)

### Kubernetes Operator 执行模式

:::tip 特点

使用 Kubernetes Operator 对 Flink 集群进行管理,可以实现集群的动态伸缩。

**特点：** 自动化管理，Operator 可以自动化管理应用程序的生命周期和运维任务；高度可扩展，Operator 允许开发者扩展 Kubernetes
的功能，以适应特定应用程序或工作负载的需求；智能决策和自愈能力，Operator
可以通过监视和分析应用程序的状态，做出智能决策以确保应用程序的健康和高可用性；自定义资源定义（CRD），Operator
使用自定义资源定义（CRD）来扩展 Kubernetes API，以支持新的自定义资源类型。

**适用于：** 作业资源隔离，启动慢，动态扩容，节约网络资源，资源占用较多的批作业和流作业。

:::

在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Kubernetes Opeartor类型

![create_kubernetes_operator_cluster](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/studio/execute_env_config/create_kubernetes_operator_cluster.png)

