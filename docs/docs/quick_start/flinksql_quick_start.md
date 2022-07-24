---
sidebar_position: 1
id: flinksql_quick_start
title: Flink SQL 作业快速入门
---



## 零基础上手

 Dinky 是基于 Flink 的流批一体化数据汇聚、数据同步的实时计算平台，通过阅读本文档，您将可以零基础上手实时计算平台 Dinky 。

### 创建集群配置或集群实例

首先，登录 Dlinky，选择**注册中心>>集群管理>>集群实例管理或集群配置管理**，点击**新建** Flink 集群

![](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/%E5%88%9B%E5%BB%BA%E9%9B%86%E7%BE%A4.png)

:::info 说明

  集群实例管理或集群配置管理添加集群步骤相同

  集群实例管理或集群配置管理可通过**数据开发>>快捷引导** 进行集群创建

:::

### 创建作业

选择**数据开发>>目录**，首先点击**创建目录**，点击创建好的目录右键即可创建作业

![image-20220625124837416](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/image-20220625124837416.png)



## 创建集群

### 集群实例

Dinky 推荐您在使用 Yarn Session、K8s Session、StandAlone 采用集群实例的方式注册集群。

**操作步骤**

1.可通过数据开发中的快捷引导**注册集群实例**。或者通过**注册中心中的集群管理**注册集群实例。

2.添加 Flink 集群

![image-20220625130448549](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/image-20220625130448549.png)

集群实例创建完成后，会显示在列表。

![cluserInstanceList](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/cluserInstanceList.png)



### 集群配置

Dinky 推荐您在使用 Yarn Per Job、Yarn Application、K8s Application 采用集群配置的方式注册集群。

**操作步骤**

1.可通过数据开发中的快捷引导**注册集群配置**。或者通过**注册中心中的集群管理**注册集群配置。

2.添加集群配置

![cluseterConfig1](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/cluseterConfig1.png)

![cluseterConfig2](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/cluseterConfig2.png)



 集群配置创建完成后，会显示在列表。

![cluseterConfigList](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/cluseterConfigList.png)



先关集群管理的说明，详见**用户手册中注册中心的[集群管理](./administrator_guide/register_center/cluster_manage.md)**

## 作业开发

创建集群完成后，就可进一步开发 FlinkSQL 作业

### 脚本准备

脚本选用 Flink 官网提供的 SQL 脚本，参考链接如下：

```
https://github.com/ververica/flink-sql-cookbook
#下载 flink-faker 放入$FLINK_HOME/lib下及Dlinky的plugins下
https://github.com/knaufk/flink-faker/releases
```



### FlinkSQL 作业创建

下面创建一个作业名称为**"test66"**的作业

![createJob](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/createJob.png)

创建完成后，即可在**"test66"**作业下写 SQL 及 配置作业参数

![createJob2](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/createJob2.png)



### FlinkSQL 语句编写

FlinkSQL 作业编写，分为三部分内容，分别是** SET 参数设置、DDL 语句编写、DML 语句编写**。下面以[Inserting Into Tables](https://github.com/ververica/flink-sql-cookbook/blob/main/foundations/02_insert_into/02_insert_into.md) 为例。

![SQL](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/SQL.png)

另外，FlinkSQL 的语法详见[ SQL 开发指南](./sql_development_guide/development_guide_overview.md) 

### 作业配置

当 FlinkSQL 编写完成后，即可进行作业的配置。作业配置的详细说明详见用户手册的[作业基础配置](./administrator_guide/studio/job_config.md) 

在作业配置中，您可以选择作业执行模式、Flink 集群、SavePoint策略等配置，对作业进行提交前的配置。

![job_config](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/job_config.png)

### SQL查询预览

上述 FlinkSQL 作业配置完成后，可以对 SQL 做查询预览。

点击**执行配置**，开启**打印流**，保存。点击**执行当前的SQL**。即可获取到最新结果。

![SQLView](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/SQLView.png)



### 发布运行作业

在数据写入 Sink 端时，Dlinky 提供了**异步提交** 和 **上线发布**功能，将其作业提交到远程集群

![SQLInsert](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/SQLInsert.png)



### 查看作业运行情况

当作业提交到远程集群后，您可以在**运维中心**查看作业的运行情况。

![dataopsCenter](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/dataopsCenter.png)

运维中心的说明，详见用户手册的**[运维中心](./administrator_guide/devops_center/deveops_center_intro.md)**

## 作业开发指引

![import](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/flinksql_quick_start/import.png)

