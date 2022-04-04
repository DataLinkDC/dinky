下面为您介绍Dinky托管FlinkSQL作业开发的一些限制和操作步骤。

## **限制说明**

- SQL编辑器编辑的FlinkSQL作业，当前仅支持Flink1.11、Flink1.12、Flink1.13、Flink1.14版本。
- FlinkSQL支持的上下游存储，请参考开发参考中的[上下游存储]

## FlinkSQL操作步骤

为了方便您编写和管理Flink SQL作业，提高作业开发效率，Dinky为您提供FlinkSQL的一些功能，包括元数据中心和SQL编辑器等。

1.首先登录Dinky数据开发控制台

2.在左侧菜单栏，单击**目录**

3.新建目录和作业，请参考作业运维中的**[作业管理](/zh-CN/administrator-guide/Studio/job_devops/job_manage.md)**

4.在新建文件的对话框，填写作业信息

|   参数   | 说明                                                         | 备注                                                         |
| :------: | :----------------------------------------------------------- | :----------------------------------------------------------- |
| 文件名称 | 作业的名称：作业名称在当前项目中必须保持**唯一**             |                                                              |
| 文件类型 | 流作业和批作业均支持以下文件类型：<br/>  FlinkSQL:支持**SET、DML、DDL**语法<br/>  FlinkSQLEnv:支持**SET、DDL**语法 | FlinkSQLEnv 场景适用于所有作业<br/>的SET、DDL语法统一管理的场景，<br/>当前FlinkSQLEnv 在SQL编辑器的语<br/>句限制在1000行以内 |

5.在作业开发 SQL 编辑器，编写 DDL 和 DML 代码

示例代码如下：

```sql
--创建源表datagen_source
CREATE TABLE datagen_source(
  id  BIGINT,
  name STRING
) WITH (
  'connector' = 'datagen'
);
--创建结果表blackhole_sink
CREATE  TABLE blackhole_sink(
   id  BIGINT,
   name STRING
) WITH (
  'connector' = 'blackhole'
);
--将源表数据插入到结果表
INSERT INTO blackhole_sink
SELECT
   id  BIGINT,
   name STRING
from datagen_source;
```

新建作业如下图：

![](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/Studio/job_dev/flinksql_guide/flinksql_job_dev/job_dev.png)

6.在作业开发页面右侧，单击**执行配置**，填写配置信息

|   参数   | 说明           | 备注                                                         |
| :------: | :------------- | :----------------------------------------------------------- |
| 作业配置 | 执行模式       | 选择作业需要部署的集群，<br/>支持以下三种集群模式：<br/>  **Per-job 集群**<br/>  **Session 集群**<br/>  **Application 集群**<br/> 这3种集群的区别请参考:[作业托管概述](/zh-CN/administrator-guide/Studio/job_dev/job_hosting.md) |
| 作业配置 | Flink 集群配置 | Flink 集群配置请参考:注册中心的<br/> [集群管理](/zh-CN/administrator-guide/registerCenter/cluster_manage.md) |
| 作业配置 | FlinkSQL 环境  | 选择创建的FlinkSQLEnv文件，如果<br/>没有则不选               |
| 作业配置 | 任务并行度     | SET优先于作业配置                                            |
| 作业配置 | Insert 语句集  | 默认禁用，开启后在SQL编辑器中<br/>编写多个 Insert 语句       |
| 作业配置 | 全局变量       | 默认禁用                                                     |
| 作业配置 | 批模式         | 默认禁用，开启后 FlinkSQL为离线计算                          |
| 作业配置 | SavePoint 策略 | 默认禁用，策略包括:<br/>   **最近一次**<br/>   **最早一次**<br/>   **指定一次** |
| 作业配置 | 报警组         | 报警组配置请参考：<br/>注册中心的[告警管理](docs/zh-CN/administrator-guide/registerCenter/warning.md) |
| 作业配置 | 其他配置       | SET优先于作业配置，具体可选参数<br/> 请参考 [Flink 官网](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/) |

作业配置如下图：

![job_config](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/Studio/job_dev/flinksql_guide/flinksql_job_dev/job_config.png)

7.单击**保存**。

8.单击**验证**。

9.单击**上线**。

完成作业开发和语法检查后，即可发布作业并上线作业。