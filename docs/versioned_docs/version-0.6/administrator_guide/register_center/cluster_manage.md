---
position: 1
id: cluster_manage
title: 集群管理
---


提交 FlinkSQL 作业时，首先要保证安装了 Flink 集群。Flink 当前支持的集群模式包括：

- Standalone 集群
- Yarn 集群
- Kubernetes 集群

对于以上的三种集群而言，Dinky 为用户提供了两种集群管理方式，一种是集群实例管理，一种是集群配置管理。

:::tip 说明

- Standalone 集群适用于既可以做为查询使用，又可以将作业异步提交到远程集群
- Yarn Session 和 Kubernetes Session 适用于既可以做为查询使用，又可以将作业异步提交到远程集群
- Yarn Per-job，Yarn Application 和 Kubernetes Application 适用于异步提交

:::

## 集群实例管理

集群实例管理适用于 Standalone，Yarn Session 和 Kubernetes Session 这三种集群实例的注册。

对于已经注册的集群实例，您可以对集群实例做编辑、删除、搜索、心跳检测和回收等。

### 注册集群实例

**注册中心 > 集群管理 > 集群实例管理 > 新建**

![cluster_manager_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/cluster_manage/cluster_manager_list.png)

![create_flink_cluster](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/cluster_manage/create_flink_cluster.png)

**参数配置说明：** 

- **名称：** 自定义(必填)
- **别名：** 自定义，默认同名称
- **类型：** 支持 Standalone，Yarn Session 和 Kubernetes Session 三种类型的集群提交任务，其他类型的集群只能查看作业信息
- **JobManager HA地址：** JobManager 的 RestAPI 地址，当 HA 部署时，将可能出现的多个 RestAPI 地址均写入，且采用英文逗号隔开
- **注释：** 自定义



## 集群配置管理

集群配置管理适用于 Yarn Per-job、Yarn Application 和 Kubernetes Application 这三种类型配置。

对于已经注册的集群配置，您可以对集群配置做编辑、删除和搜索等。

### 集群配置

单击**注册中心 > 集群管理 > 集群配置管理 > 新建 **

![create_cluster_config_1](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/cluster_manage/create_cluster_config_1.png)

![create_cluster_config_2](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/cluster_manage/create_cluster_config_2.png)

**参数配置说明:**

- **类型：** 支持 Flink on Yarn 和 Flink on Kubernetes
- **hadoop 配置**
  - **配置文件路径：** hadoop 配置文件路径，指定配置文件路径（末尾无/），需要包含以下文件：core-site.xml,hdfs-site.xml,yarn-site.xml
  - **自定义配置（高优先级，目前不生效，请跳过）**
    - **ha.zookeeper.quorum：** zookeeper 访问地址
    - **其他配置：** hadoop 的其他参数配置（默认不填写）
- **Flink 配置**
  - **lib 路径：** 指定 lib 的 hdfs 路径（末尾无/），需要包含 Flink 运行时的依赖
  - **配置文件路径：** 指定 flink-conf.yaml 的具体路径（末尾无/），必填
  - **自定义配置（高优先级）：** Flink参数配置
- **基本配置**
  - **标识:** 唯一英文标识（必填）
  - **名称:** 自定义，默认同标识
  - **注释:** 自定义
  - **是否启用:** 默认禁用，需要开启

## 查看集群信息

创建集群后可在**集群实例管理**后者**集群配置**中查看集群信息。

集群信息相关字段含义如下：

|      字段      |                             说明                             |
| :------------: | :----------------------------------------------------------: |
|      名称      |                         名称是唯一的                         |
|      别名      |                            自定义                            |
|      类型      | Standalone<br/>Yarn Session<br/>Yarn Per-job<br/>Yarn Application<br/>Kubernetes Session<br/>Kubernetes Application |
| JobManager地址 |                         Rest API地址                         |
|      版本      |                          Flink 版本                          |
|      状态      |                        正常<br/> 异常                        |
|    是否启用    |                      已启用<br/> 已禁用                      |
|    注册方式    |                        手动<br/> 自动                        |
|  最近更新时间  |                       集群信息修改时间                       |
|      操作      |                    对集群做编辑、删除操作                    |

:::warning 注意事项

   当非Session类作业提交和发布后，作业会成为一个集群实例而存在

:::

