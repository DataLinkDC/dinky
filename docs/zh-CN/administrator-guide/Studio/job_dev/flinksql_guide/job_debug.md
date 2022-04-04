您可以选择使用 Session 集群在开发测试环境对作业调试，如作业运行、检查结果等。提升开发效率。调试可以包含多个 SELECT 或 INSERT 的复杂作业。配置 Session 集群请参考注册中心中[集群管理](docs/zh-CN/administrator-guide/registerCenter/cluster_manage.md)的集群实例管理。

## FlinkSQL作业调试步骤

1.首先登录 Dinky 数据开发控制台

2.点击**目录 > 新建目录 > 新建作业**

3.填写完作业信息后，单击**确认**，作业类型必须选择 FlinkSQL

4.分别注册 Source 表和 Sink 表

分别编写源表和结果表 SQL 代码后，单击**执行当前的SQL**

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

5.单击**保存**

6.单击**语法检查**

7.单击**执行当前的SQL**

8.配置调试数据

如果你想要实时预览数据，如计算count(1)统计等。可以点击**执行配置**

|  配置项  |                  说明                  |
| :------: | :------------------------------------: |
| 实时预览 |                默认开启                |
|  打印流  | 默认禁用，如果您想要实时打印数据可开启 |

确定好调试数据后，单击**执行当前的SQL**，会在SQL编辑器下方显示结果

