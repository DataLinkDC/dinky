---
position: 1
sidebar_position: 1
id: cluster_manage
title: 集群
---

# 集群管理

:::tip 提示

在 Dinky 中，将 Flink 运行模式拆分为了 `Flink实例` 和 `集群配置` 两个概念。请按需选择使用。

如果您不清楚 Flink
各个运行模式的区别，请参考 [Flink 官方文档](https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/deployment/resource-providers/standalone/overview/)

:::

## Flink实例

> Flink实例是指已经启动的 Flink 集群

> Flink实例仅可以注册模式为 Local, Standalone, Yarn Session, Kubernetes Session 的 Flink 实例


:::warning 注意

1. 在 Flink 实例中，仅可以注册模式为 Local, Standalone, Yarn Session, Kubernetes Session 的 Flink 实例
2. 在 Flink 实例中，区分了自动注册和手动注册两种方式，手动启动的 Flink 实例需要手动注册, 由`集群配置/application模式`
   任务启动后的 Flink 实例会自动注册
3. 自动注册的集群无需关心注册过多的情况, 系统会定时清理过期的 自动注册的 Flink 实例,手动注册的无影响
4. 手动注册的集群需要手动管理 Flink 实例, 如需删除 Flink 实例，请鼠标悬浮某一 Flink 实例，点击删除按钮即可删除 Flink 实例
5. 提供手动进行心跳检测
6. 所有Flink 实例删除前会进行引用检测,如果有引用,则无法删除(此为避免实际运行中的任务关联到该Flink 实例,从而导致一系列问题)
7. 手动注册的集群可以直接删除(前提是未被使用),如果自动注册的集群如果状态为`健康`需要先停止 Flink 实例,然后再删除 Flink 实例.

如需查看 自动注册 和 手动注册 下的 Flink 实例，请点击切换按钮进行查看
:::

### 手动注册

![cluster_instance_m_list](http://pic.dinky.org.cn/dinky/docs/test/cluster_instance_m_list.png)

### 自动注册

![cluster_instance_a_list](http://pic.dinky.org.cn/dinky/docs/test/cluster_instance_a_list.png)

### 参数解读

| 参数              | 说明                                                                          | 是否必填 |  默认值  |      示例值      |
|-----------------|-----------------------------------------------------------------------------|:----:|:-----:|:-------------:|
| 集群名称            | 集群名称, 用于区分不同集群                                                              |  是   |   无   | flink-session |
| 集群别名            | 集群别名, 用于区分不同集群, 如不填默认同集群名称                                                  |  否   | 同集群名称 | flink-session |
| 集群类型            | 集群类型, 目前支持 Local, Standalone, Yarn Session, Kubernetes Session              |  是   |   无   |  Standalone   |
| JobManager 高可用地址 | 添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101 |  是   |   无   |
| 备注              | 备注, 用于备注集群信息                                                                |  否   |   无   | flink-session |

## 集群配置

> 集群配置是指 预先定义的 Flink 集群配置, 用于提交 Flink 作业时,会自动启动 Flink 集群

> 集群配置仅可以注册模式为 Yarn Per-Job, Yarn Application, Kubernetes Application 的 Flink 实例

:::warning 注意

1. 在集群配置中，仅可以注册模式为 Yarn(可用于Yarn Per-Job, Yarn Application)，Kubernetes Native，Kubernetes Operator的
   Flink 配置
2. 在此模式中支持可以以配置方式 启动一个 YarnSession/KubernetesSession 的 Flink实例, 启动成功后,会自动注册到 `Flink实例`
   中
3. 支持手动心跳检测
4. 所有集群配置删除前会进行引用检测,如果有引用,则无法删除(此为避免实际运行中的任务关联到该Flink 配置,从而导致一系列问题)

注意: 在集群配置中,如果使用Yarn 模式,需要有 Hadoop 与 Flink 集成的 jar 包,如果使用Kubernetes
模式,需要有Flink运行时的jar包,否则会导致启动失败,如遇到依赖加载问题,请查看详细日志进行排查
:::

### 集群配置列表

![cluster_config_list](http://pic.dinky.org.cn/dinky/docs/test/cluster_config_list.png)

### 参数解读

- 基本配置-公共配置

| 参数     | 说明                                                     | 是否必填 | 默认值 |    示例值     |
|--------|--------------------------------------------------------|:----:|:---:|:----------:|
| 类型     | 集群类型, 目前支持 Yarn, Kubernetes Native，Kubernetes Operator |  是   |  无  |    Yarn    |
| 集群配置名称 | 集群配置名称, 用于区分不同集群配置                                     |  是   |  无  | flink-yarn |
| 备注     | 描述信息                                                   |  否   |  无  |            |
| 是否启用   | 标志是否启用该集群配置,如果不启用，则该集群配置不会在数据开发集群列表下拉框中显示              |  是   |  无  |            |

- 提交 FlinkSQL 配置项 (Application 模式必填)-公共配置

| 参数       | 说明                                                                                                       | 是否必填 | 默认值 |                                                                                        示例值                                                                                         |
|----------|----------------------------------------------------------------------------------------------------------|:----:|:---:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Jar 文件路径 | 指定 Jar 文件路径,如果该集群配置用于提交 Application 模式任务时 则必填<br/>需要包含以下文件：dinky-app-{version}-jar-with-dependencies.jar |  否   |  无  | hdfs:///dinky/dinky-app-1.16-1.0.0-SNAPSHOT-jar-with-dependencies.jar <br/>请注意: 如果使用 Kubernetes 模式,路径需要指定为: local:///dinky/dinky-app-1.16-1.0.0-SNAPSHOT-jar-with-dependencies.jar |

- Flink 预设配置（高优先级）-公共配置

| 参数              | 说明                 | 是否必填 | 默认值 |           示例值            |
|-----------------|--------------------|:----:|:---:|:------------------------:|
| JobManager 内存   | JobManager 内存大小!   |  否   |  无  |            1g            |
| TaskManager 内存  | TaskManager 内存大小!  |  否   |  无  |            1g            |
| TaskManager 堆内存 | TaskManager 堆内存大小! |  否   |  无  |            1g            |
| 插槽数             | 插槽数量               |  否   |  无  |            2             |
| 保存点路径           | 对应SavePoint目录      |  否   |  无  | hdfs:///flink/savepoint  |
| 检查点路径           | 对应CheckPoint目录     |  否   |  无  | hdfs:///flink/checkpoint |

--- 

#### Yarn 模式

- Hadoop 配置

| 参数                 | 说明                                                                  | 是否必填 | 默认值 | 示例值 |
|--------------------|---------------------------------------------------------------------|:----:|:---:|:---:|
| Hadoop 配置文件路径      | 指定配置文件路径（末尾无/），需要包含以下文件：core-site.xml,hdfs-site.xml,yarn-site.xml ！ |  是   |  无  |     |
| Hadoop 自定义配置（高优先级） | Hadoop 的其他参数配置                                                      |  否   |  无  |     |

- Flink 配置

| 参数                | 说明                                        | 是否必填 | 默认值 |        示例值        |
|-------------------|-------------------------------------------|:----:|:---:|:-----------------:|
| Flink Lib 路径      | 指定 lib 的 hdfs 路径（末尾无/），需要包含 Flink 运行时的依赖！ |  是   |  无  | hdfs:///flink/lib |
| Flink 配置文件路径      | 仅指定到文件夹，dinky会自行读取文件夹下的配置文件，k8s模式下此参数可选填  |  是   |  无  |  /opt/flink/conf  |
| Flink 自定义配置（高优先级） | Flink 的其他参数配置                             |  否   |  无  |                   |

---

#### Kubernetes Native 模式

- Kubernetes 配置

| 参数                   | 说明                                       | 是否必填 | 默认值 |       示例值       |
|----------------------|------------------------------------------|:----:|:---:|:---------------:|
| 暴露端口类型               | 指定暴露端口类型, 目前支持 NodePort, ClusterIP       |  是   |  无  |    NodePort     | 
| Kubernetes 命名空间      | 指定 Kubernetes 命名空间                       |  是   |  无  |      flink      |
| Kubernetes 提交账号      | 指定 Kubernetes 提交账号                       |  是   |  无  |      flink      |
| Flink 镜像地址           | 指定 Flink 镜像地址                            |  是   |  无  |  flink:1.16.0   |
| JobManager CPU 配置    | 指定 JobManager CPU 配置                     |  否   |  无  |      1000m      |
| TaskManager CPU 配置   | 指定 TaskManager CPU 配置                    |  否   |  无  |      1000m      |
| Flink 配置文件路径         | 仅指定到文件夹，dinky会自行读取文件夹下的配置文件，k8s模式下此参数可选填 |  否   |  无  | /opt/flink/conf |
| 自定义配置                | Flink 的其他参数配置                            |  否   |  无  |                 |
| K8s KubeConfig       | 指定 K8s KubeConfig,支持从本地上传并加载             |  否   |  无  |                 |
| Default Pod Template | 指定 Default Pod Template,支持从本地上传并加载       |  否   |  无  |                 |
| JM Pod Template      | 指定 JobManager Pod Template,支持从本地上传并加载    |  否   |  无  |                 |
| TM Pod Template      | 指定 TaskManager Pod Template,支持从本地上传并加载   |  否   |  无  |                 |

---

#### Kubernetes Operator 模式

- Kubernetes 配置

> 注意: 与 Kubernetes Native 模式不同的是, Kubernetes Operator 模式下, 需要指定 Flink 版本而不需要指定暴露端口类型,
> 其他配置项都一致,以下仅列出不同的配置项

| 参数       | 说明                                   | 是否必填 | 默认值 |  示例值   |
|----------|--------------------------------------|:----:|:---:|:------:|
| Flink 版本 | 指定 Flink 版本,支持 Flink1.15 - Flink1.18 |  是   |  无  | 1.16.0 |

---

