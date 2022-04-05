## 说明

- SQL 编辑器编辑的 FlinkSQL 作业，当前仅支持 Flink1.11、Flink1.12、Flink1.13、Flink1.14 版本的语法。
- FlinkSQL 支持的上下游存储，请参考 [上下游存储]()

## FlinkSQL 操作步骤


1.进入 Dinky 的 Data Studio

2.在左侧菜单栏，右键 **目录**

3.新建目录或作业，请参考作业运维中的 **[作业管理](/zh-CN/administrator_guide/studio/job_devops/job_manage.md)**

4.在新建文件的对话框，填写作业信息

|  参数  | 说明                                                                                     | 备注                                                         |
|:----:|:---------------------------------------------------------------------------------------| :----------------------------------------------------------- |
| 作业名称 | 作业名称在当前项目中必须保持 **唯一**                                                                  |                                                              |
| 作业类型 | 流作业和批作业均支持以下作业类型：<br/>  FlinkSQL：支持**SET、DML、DDL**语法<br/>  FlinkSQLEnv：支持**SET、DDL**语法 | FlinkSQLEnv 场景适用于所有作业<br/>的SET、DDL语法统一管理的场景，<br/>当前FlinkSQLEnv 在SQL编辑器的语<br/>句限制在1000行以内 |

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

![](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/studio/job_dev/flinksql_guide/flinksql_job_dev/job_dev.png)

6.在作业开发页面右侧 **执行配置**，填写配置信息

|  类型  | 配置项          | 备注                                                                                                                       |
|:----:|:-------------|:-------------------------------------------------------------------------------------------------------------------------|
| 作业配置 | 执行模式         | 区别请参考:[作业托管概述](/zh-CN/administrator_guide/studio/job_dev/job_hosting.md)                                                 |
| 作业配置 | 集群实例         | Standalone 和 Session 执行模式需要选择集群实例，请参考：[集群实例管理](/zh-CN/administrator_guide/register_center/cluster_manage?id=集群实例管理)      |
| 作业配置 | 集群配置         | Per-Job 和 Application 执行模式需要选择集群配置，请参考：[集群配置管理](/zh-CN/administrator_guide/register_center/cluster_manage?id=集群配置管理)     |
| 作业配置 | FlinkSQL 环境  | 选择已创建的 FlinkSQLEnv，如果没有则不选                                                                                               |
| 作业配置 | 任务并行度        | 指定作业级任务并行度，默认为 1                                                                                                         |
| 作业配置 | Insert 语句集   | 默认禁用，开启后将 SQL编辑器中编写的多个 Insert 语句合并为一个 JobGraph 进行提交                                                                      |
| 作业配置 | 全局变量         | 默认禁用，开启后可以使用数据源连接配置变量、自定义变量等                                                                                             |
| 作业配置 | 批模式          | 默认禁用，开启后启用 Batch Mode                                                                                                    |
| 作业配置 | SavePoint 策略 | 默认禁用，策略包括:<br/>   **最近一次**<br/>   **最早一次**<br/>   **指定一次**                                                               |
| 作业配置 | 报警组          | 报警组配置请参考[告警管理](docs/zh-CN/administrator-guide/registerCenter/warning.md)                                                 |
| 作业配置 | 其他配置         | 其他的 Flink 作业配置，具体可选参数，请参考 [Flink 官网](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/) |

作业配置如下图：

![job_config](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/studio/job_dev/flinksql_guide/flinksql_job_dev/job_config.png)

**注意：** 请及时手动保存作业信息，以免丢失