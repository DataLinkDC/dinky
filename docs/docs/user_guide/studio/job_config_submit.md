---
sidebar_position: 1
id: job_config_submit
title: 作业与环境管理
---

## 作业相关配置

### 作业类型

:::tip 说明

在创建作业时，有三种Flink Job Type，分别为：`FlinkSql`、`FlinkJar`、`FlinkSqlEnv`，下方介绍三种作业类型的作用

:::

![image-20231220140415854](http://pic.dinky.org.cn/dinky/docs/test/202312201404899.png)

#### FlinkSql作业类型

此作业类型用于**开发FlinkSQL作业**，并能够对作业进行相关配置，如保存点结束/恢复运行，历史版本控制等，提交至外部集群运行。

![image-20231220140531477](http://pic.dinky.org.cn/dinky/docs/test/202312201405535.png)

#### FlinkJar作业类型

此作业用于**执行jar包**，可携带参数进行提交运行，可通过dinky对jar作业进行管理

![image-20231220140911995](http://pic.dinky.org.cn/dinky/docs/test/202312201409057.png)

#### FlinkSqlEnv作业类型

此作业是用于**创建catalog**，便于在执行FlinkSQL作业时对FLinkSQL环境进行选择

![image-20231220141323767](http://pic.dinky.org.cn/dinky/docs/test/202312201413831.png)

可在此处对FlinkSqlEnv作业创建的catalog进行选择

![image-20231220141416725](http://pic.dinky.org.cn/dinky/docs/test/202312201414788.png)

### 作业配置

:::tip 说明

在创建作业后，可在右侧进行作业配置，配置项解释见下方

:::

![image-20231220112839608](http://pic.dinky.org.cn/dinky/docs/test/202312201128666.png)

FlinkSQL 作业配置,您可以根据具体需求配置参数，参数设置如下

|   类型   |     配置项     | 备注                                                         |
| :------: | :------------: | :----------------------------------------------------------- |
| 作业配置 |    执行模式    | 指定 FlinkSQL 的执行模式，默认为local                        |
| 作业配置 |   Flink集群    | 此参数依据执行模式选择需要的集群实例或者集群配置             |
| 作业配置 |    作业名称    | 默认作业名，作业名称在当前项目中必须保持 **唯一**            |
| 作业配置 | FlinkSQL 环境  | 选择当前 FlinkSQL 执行环境，会提前执行环境语句，默认无       |
| 作业配置 |   任务并行度   | 设置Flink任务的并行度，默认为 1                              |
| 作业配置 | Insert 语句集  | 默认禁用，开启语句集机制，将多个 Insert 语句合并成一个JobGraph<br/> 进行提交，select 语句无效 |
| 作业配置 |    全局变量    | 默认禁用，开启 FlinkSQL 全局变量，以“${}”进行调用            |
| 作业配置 |     批模式     | 默认禁用，开启后启用 Batch Mode                              |
| 作业配置 | SavePoint 策略 | 默认禁用，策略包括:<br/>   **最近一次**<br/>   **最早一次**<br/>   **指定一次** |
| 作业配置 |     报警组     | 报警组配置详见[报警管理](../register_center/alert/alert_overview)         |
| 作业配置 |    其他配置    | 其他的 Flink 作业配置，具体可选参数，详见[Flink 官网](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/) |

:::tip 说明

FlinkSQL 执行模式除 local 外，还包括Standalone、Yarn Session、Yarn Per-Job、Yarn Application、Kubernetes Session、Kubernetes Application、Kubenetes Operator

:::

### 预览配置

:::tip 说明

在创建作业后，可在右侧进行预览配置，若作业是查询结果，在作业运行后可在左下方进行结果预览，配置项解释见下方

:::

![image-20231220114031011](http://pic.dinky.org.cn/dinky/docs/test/202312201140092.png)



另外 Dinky 提供了针对 FlinkSQL 查询预览的配置，，参数设置如下

|   类型   |  配置项  |                             备注                             |
| :------: | :------: | :----------------------------------------------------------: |
| 执行配置 | 预览结果 |        默认开启，开启预览结果将同步运行并返回数据结果        |
| 执行配置 |  打印流  | 默认禁用，开启打印流将同步运行并返回含有**op**字段信息的 ChangeLog<br/> 默认不开启则返回最终结果 |
| 执行配置 | 最大行数 |                 预览数据的最大行数，默认100                  |
| 执行配置 | 自动停止 |     默认禁用，开启自动停止将在捕获最大行记录数后自动停止     |

### 保存点配置

:::tip 说明

​    Dinky 提供 FlinkSQL 在通过 **cancel** 作业停止时，自动保存**savepoint**。并生成一个保存 savepoint 的创建时间。此保存点必须通过 **Flink Cancel** 完成。保存点结束方式在运维中心的作业中，通过Saveponit结束，需要提前配置保存点。

:::

![image-20231220114448998](http://pic.dinky.org.cn/dinky/docs/test/202312201144048.png)



### 历史版本配置

:::tip 说明

在创建作业后，编写完作业代码，在发布-上下线后即可在右侧历史版本处查看并切换历史版本

:::

![image-20231220134050332](http://pic.dinky.org.cn/dinky/docs/test/202312201340401.png)

单机历史版本即可查看当前版本与所选版本的代码差异

![image-20231220134119789](http://pic.dinky.org.cn/dinky/docs/test/202312201341853.png)

可点击此处回滚版本

![image-20231220134324847](http://pic.dinky.org.cn/dinky/docs/test/202312201343903.png)

## 环境相关配置

### 前置说明

> 需要注意，如果使用额外依赖包，如：`mysql-cdc-connector`，那么此依赖需要添加一份到勾选提交作业运行模式的`flink/lib`下，并且需要在`dinky/plugins/flink版本号/`下也添加一份，添加完成后重启dinky和flink集群。另外一种方式是通过dinky的`add jar`语法，详细用法见dinky官网

### Local 本地执行模式

:::tip 特点

Dinky 内置的 Flink MiniCluster,资源受限,用于语法校验等操作。

需要注意，Local模式流任务的作业无法关闭，如果提交了，需要重启Dinky

**特点：** 不需要外部 Flink 集群，资源受限。

**适用于：** 语法校验、查看 JobPlan、查看字段级血缘、执行资源占用非常小的批作业。

:::

Local环境运行需要在dinky的安装目录下`plugins/flink 版本号`中添加对应版本需要使用的jar依赖，可以将本地flink安装目录lib下的所有内容复制一份到此目录下，如下图。

![image-20231116092612440](http://pic.dinky.org.cn/dinky/docs/test/202311160926573.png)

相关依赖添加完成后在作业配置处的`执行模式`中，勾选`Local模式`，即可使用。

![image-20231116091702701](http://pic.dinky.org.cn/dinky/docs/test/202311160917749.png)



### Standalone 执行模式

:::tip 特点

与外部 Standalone 集群共享资源,用于批操作和小流量任务。

Dinky 将通过 JobManager 的 Rest 端口提交 FlinkSQL 作业至外部的 Flink Standalone 集群。

**特点：** 作业资源共享，启动快，不依赖 Yarn 或 K8S。

**适用于：** 批作业、Flink OLAP 查询、资源占用小的流作业。

:::

自行根据Flink官网教程部署一个Standalone模式，并将地址其添加到`注册中心-Flink实例`中。

![image-20231116092905314](http://pic.dinky.org.cn/dinky/docs/test/202311160929362.png)

相关依赖添加完成后，在作业配置处的`执行模式`中，勾选`standalone模式`，并且在下方的`flink集群`选项框中勾选上方添加的集群，即可使用。

![image-20231116093642228](http://pic.dinky.org.cn/dinky/docs/test/202311160936280.png)

### Yarn Session 执行模式

:::tip 特点

与外部 Yarn Session 集群共享资源,用于批操作和小流量任务。

Dinky 将通过 JobManager 的 Rest 端口提交 FlinkSQL 作业至外部的 Flink Yarn Session 集群。

**特点：** 作业资源共享，启动快。

**适用于：** 作业资源共享，启动快，批作业、Flink OLAP 查询、资源占用小的流作业。

:::

自行根据Flink官网教程部署一个 `Flink Yarn Session`模式，并将地址其添加到`注册中心-Flink实例`中。

![image-20231116095708329](http://pic.dinky.org.cn/dinky/docs/test/202311160957398.png)

相关依赖添加完成后，在作业配置处的`执行模式`中，勾选`yarn session模式`，并且在下方的`flink集群`选项框中勾选上方添加的集群，即可使用。

![image-20231116095621922](http://pic.dinky.org.cn/dinky/docs/test/202311160956970.png)

### Yarn Per-Job 执行模式

:::tip 特点

每个 JobGraph 创建一个集群,资源隔离,用于资源占用大的任务。

Dinky 将通过 Yarn 来创建 Flink Yarn Per-Job 集群。

**特点：** 作业资源隔离，启动慢，每个 JobGraph 创建一个集群。

**适用于：** 资源占用较多的批作业和流作业。

:::

在 `注册中心-集群配置` 中注册 Hadoop 与 Flink 相关配置，具体配置过程见[集群管理](../register_center/cluster_manage) 的集群配置管理。

![image-20231116100208363](http://pic.dinky.org.cn/dinky/docs/test/202311161002408.png)

在配置完成后并且相关依赖添加完成后，在作业配置处的`执行模式`中，勾选`yarn per-job模式`，并且在下方的`flink集群`选项框中勾选上方添加的集群，即可使用。

![image-20231116100506987](http://pic.dinky.org.cn/dinky/docs/test/202311161005023.png)

### Yarn Application 执行模式

:::tip 特点

所有 JobGraph 共享一个集群,资源隔离,节约网络资源。

Dinky 将通过 Yarn 来创建 Flink Yarn Application 集群。

**特点：** 作业资源隔离，启动慢，节约网络资源，所有 JobGraph 只创建一个集群。

**适用于：** 资源占用较多的批作业和流作业。

:::

在 `注册中心-集群配置` 中注册 Hadoop 与 Flink 相关配置，具体配置过程详见[集群管理](../register_center/cluster_manage) 的集群配置管理。

![](http://pic.dinky.org.cn/dinky/docs/test/202312201032576.png)

将 `/dinky根目录/jar/dinky-app-版本号-xxx-with-dependencies.jar` 上传到 **配置中心 > 全局配置 > Resource 配置** 中对应文件系统的路径中。

- HDFS

![](http://pic.dinky.org.cn/dinky/docs/test/202312201034498.png)

- OSS

![](http://pic.dinky.org.cn/dinky/docs/test/202312201035898.png)

### Kubernetes Session 执行模式

:::tip 特点

与外部 Kubernetes Session 集群共享资源,动态扩容。

Dinky 将通过暴露的 NodePort 端口提交 FlinkSQL 作业至外部的 Flink Kubernetes Session 集群。

**特点：** 作业资源隔离，启动快，动态扩容。

**适用于：** 作业资源隔离，启动快，动态扩容，批作业、Flink OLAP 查询、资源占用小的流作业。

:::

根据 Flink 官网手动部署一个 Flink Kubernetes Session 集群，并暴露 **NodePort **端口， 注册到 **集群实例** 中

![](http://pic.dinky.org.cn/dinky/docs/test/202312201036450.png)

注册完成后，在创建作业后可在右侧作业配置处进行勾选执行模式

![](http://pic.dinky.org.cn/dinky/docs/test/202312201036304.png)

### Kubernetes Application 执行模式

:::tip 特点

每个 JobGraph 创建一个 Pod,资源隔离,动态扩容。

**特点：** 作业资源隔离，启动慢，动态扩容，节约网络资源，所有 JobGraph 只创建一个集群。

**适用于：** 作业资源隔离，启动慢，动态扩容，节约网络资源，资源占用较多的批作业和流作业。

:::

在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Kubernetes Native类型

![image-20231220103914938](http://pic.dinky.org.cn/dinky/docs/test/202312201039000.png)

将 `/dinky根目录/jar/dinky-app-版本号-xxx-with-dependencies.jar` 打包添加到对应版本的Flink镜像中lib目录下，并且如果是非本地部署（如docker，k8s部署的dinky），需要把`~/.kube/config`拷贝进容器，物理机部署默认读取的是此路径下k8s配置。配置完成后即可创建作业后在作业配置处勾选此模式，此模式为单个任务为一个集群实例。

![image-20231220104551759](http://pic.dinky.org.cn/dinky/docs/test/202312201045824.png)

具体如何注册 Kubernetes Application 的集群配置，详见[集群管理](http://localhost:3000/docs/next/administrator_guide/register_center/cluster_manage) 的集群配置管理。

### Kubernetes Operator 执行模式

:::tip 特点

使用 Kubernetes Operator 对 Flink 集群进行管理,可以实现集群的动态伸缩。

**特点：** 自动化管理，Operator 可以自动化管理应用程序的生命周期和运维任务；高度可扩展，Operator 允许开发者扩展 Kubernetes 的功能，以适应特定应用程序或工作负载的需求；智能决策和自愈能力，Operator 可以通过监视和分析应用程序的状态，做出智能决策以确保应用程序的健康和高可用性；自定义资源定义（CRD），Operator 使用自定义资源定义（CRD）来扩展 Kubernetes API，以支持新的自定义资源类型。

**适用于：** 作业资源隔离，启动慢，动态扩容，节约网络资源，资源占用较多的批作业和流作业。

:::

在 **注册中心 > 集群管理 > 集群配置管理** 中注册 Kubernetes Opeartor类型

![image-20231220110234858](http://pic.dinky.org.cn/dinky/docs/test/202312201102913.png)

如何注册 Kubernetes Application 的集群配置，详见[集群管理](http://www.dlink.top/docs/next/administrator_guide/register_center/cluster_manage) 的集群配置管理。

## 作业提交

本章节为您介绍如何提交 FlinkSQL 作业至集群。Dinky 提供了三种提交作业的方式，分别是：

- 调试当前作业
- 运行当前作业
- 上线下线发布

各提交作业的支持的执行模式及说明如下

| SQL类型  |   提交方式   |                           执行模式                           | 说明                                                        |
| :------: | :----------: | :----------------------------------------------------------: | :---------------------------------------------------------- |
| FlinkSQL | 调试当前作业 | local<br/> Standalone<br/> Yarn Session<br/>  Kubernetes Session | 适用场景:`调试作业`                                         |
| FlinkSQL | 运行当前作业 | Standalone<br/> Yarn Session<br/> Yarn Per-job<br/> Yarn Application<br/> Kubernetes Session<br/> Kubernetes Application<br/> Kubernetes Operator<br/> | 适用场景:`远程提交作业至集群`                               |
| FlinkSQL | 上线下线发布 | Standalone<br/> Yarn Session<br/> Yarn Per-job<br/> Yarn Application<br/> Kubernetes Session<br/> Kubernetes Application<br/> Kubernetes Operator<br/> | 适用场景:<br/>1.`远程发布作业至集群`<br/>2.`维护及监控作业` |

#### 调试当前SQL

![image-20231220142013475](http://pic.dinky.org.cn/dinky/docs/test/202312201420534.png)

#### 执行当前SQL

![image-20231220142100063](http://pic.dinky.org.cn/dinky/docs/test/202312201421115.png)

#### 上线下线发布

![image-20231220142222119](http://pic.dinky.org.cn/dinky/docs/test/202312201422171.png)





