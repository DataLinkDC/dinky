---
sidebar_position: 2
id: env_config
title: 环境配置
---




## Flink 环境配置

### Local

只需要在 Dinky 根目录下的 plugins 文件夹下添加 Flink lib 与 connector 等 Jar 即可。

### Standalone 集群

根据 Flink 官网手动部署一个 Flink Standalone 集群，并注册到 **集群实例** 中。

如何注册 Standalone 集群，详见[集群管理](../register_center/cluster_manage) 的集群实例管理。

### Yarn Session 集群

根据 Flink 官网手动部署一个 Flink Yarn Session 集群，并注册到 **集群实例** 中。

如何注册 Yarn Session 集群，详见[集群管理](../register_center/cluster_manage)的集群实例管理。

### Yarn Per-Job 集群

在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Hadoop 与 Flink 相关配置。

如何注册 Yarn Per-Job 的集群配置，详见[集群管理](../register_center/cluster_manage) 的集群配置管理。

### Yarn Application 集群

1.在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Hadoop 与 Flink 相关配置；

2.将 dlink-app.jar 上传到 **系统设置 > Flink 设置** 中的 **提交 FlinkSQL 的 Jar 文件路径** 的 hdfs 配置地址。

如何注册 Yarn Application 的集群配置，详见[集群管理](../register_center/cluster_manage) 的集群配置管理。

### Kubernetes Session 集群

根据 Flink 官网手动部署一个 Flink Kubernetes Session 集群，并暴露 **NodePort**， 注册到 **集群实例** 中。

如何注册 Kubernetes Session 集群， 详见[集群管理](../register_center/cluster_manage) 的集群实例管理。

### Kubernetes Application 集群

1.在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Kubernetes 与 Flink 相关配置；

2.将 dlink-app.jar 打包成完整的 Flink 镜像，在 **系统设置 > Flink 设置** 中的 **提交 FlinkSQL 的 Jar 文件路径** 的配置 dlink-app.jar 的 local 地址。

如何注册 Kubernetes Application 的集群配置，详见[集群管理](../register_center/cluster_manage) 的集群配置管理。

## 其他数据源环境配置

手动部署外部数据源，然后注册到 **数据源管理** 。

如何注册外部数据源，详见[数据源管理](../register_center/datasource_manage)。当前 Dinky 支持的数据源详见功能扩展中的[扩展数据源](../../extend/function_expansion/datasource)