---
sidebar_position: 4
id: job_debug
title: 作业调试
---




可以选择使用 Standalone 或 Session 集群在开发测试环境对作业调试，如作业运行、检查结果等。配置 Standalone 或 Session 集群请参考注册中心中[集群管理](../administrator_guide/register_center/cluster_manage)的集群实例管理。

也可以调试普通的 DB SQL 作业。

## FlinkSQL作业调试步骤

1.进入 Data Studio

2.点击 **目录 > 新建目录 > 新建作业**

3.填写完作业信息后，单击 **确认**，作业类型选择 FlinkSQL

4.编写完整的 FlinkSQL 语句，包含 CREATE TABLE 等

示例代码如下：

```sql
--创建源表datagen_source
CREATE TABLE datagen_source(
  id  BIGINT,
  name STRING
) WITH (
  'connector' = 'datagen'
);
--将源表数据插入到结果表
SELECT
   id  BIGINT,
   name STRING
from datagen_source
```

5.单击**保存**

6.单击**语法检查**

7.配置**执行配置**

| 配置项  |           说明            |
|:----:|:-----------------------:|
| 预览结果 | 默认开启，可预览 FlinkSQL 的执行结果 |
| 打印流  |  默认禁用，开启后将展示 ChangeLog  |
| 最大行数 |  默认 100，可预览的执行结果最大的记录数  |
| 自动停止 |   默认禁用，开启后达到最大行数将停止作业   |

**注意：** 预览聚合结果如 count 等操作时，关闭打印流可合并最终结果。

8.单击**执行当前的SQL**

9.**结果** 或者 **历史 > 预览数据** 可以手动查询最新的执行结果

## FlinkSQL 预览结果的必要条件

1.执行模式必须是 Local、Standalone、Yarn Session、Kubernetes Session 其中的一种；

2.必须关闭 **Insert 语句集**；

3.除 SET 和 DDL 外，必须只提交一个 SELECT 或 SHOW 或 DESC 语句；

4.必须开启 **预览结果**；

5.作业必须是提交成功并且返回 JID，同时在远程集群可以看到作业处于 RUNNING 或 FINISHED 状态；

6.Dinky 重启后，之前的预览结果将失效

## DB SQL 调试

选择对应数据源，并书写其 sql 执行即可。