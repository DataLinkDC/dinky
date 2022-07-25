---
sidebar_position: 6
id: hive
title: Hive
---



## 前言

最近有很多小伙伴问，Dinky 如何连接 Hive 进行数据开发？

关于 Dinky 连接 Hive 的步骤同 Flink 的 `sql-client ` ，只不过它没有默认加载的配置文件。下文将详细讲述对 Hive 操作的全过程。

## 准备工作

由于搭建 Hive 的开发环境会涉及到重多组件和插件，那其版本对应问题也是至关重要，它能帮我们避免很多不必要的问题，当然小版本号之间具备一定的兼容性。

我们先来梳理下本教程的各个组件版本：

|   组件   |  版本  |
|:------:| :----: |
| Dinky  | 0.3.2  |
| Flink  | 1.12.4 |
| Hadoop | 2.7.7  |
|  Hive  | 2.3.6  |
| Mysql  | 8.0.15 |

再来梳理下本教程的各个插件版本：

|     所属组件      |            插件            |         版本          |
|:-------------:| :------------------------: | :-------------------: |
|     Dinky     |        dlink-client        |         1.12          |
| Dinky & Flink |  flink-sql-connector-hive  |   2.3.6_2.11-1.12.3   |
| Dinky & Flink | flink-shaded-hadoop-3-uber | 3.1.1.7.2.8.0-224-9.0 |

## 部署扩展

部署扩展的工作非常简单（前提是 Dlink 部署完成并成功连接 Flink 集群，相关部署步骤请查看《Dlink实时计算平台——部署篇》），只需要把 `flink-sql-connector-hive-2.3.6_2.11-1.12.3.jar` 和 `flink-shaded-hadoop-3-uber-3.1.1.7.2.8.0-224-9.0.jar` 两个插件分别加入到 Dlink 的 plugins 目录与 Flink 的 lib 目录下即可，然后重启二者。当然，还需要放置 `hive-site.xml`，位置自定义，Dlink 可以访问到即可。

## 创建 Hive Catalog

已知，Hive 已经新建了一个数据库实例 `hdb` ，创建了一张表 `htest`，列为 `name` 和 `age`，存储位置默认为 `hdfs:///usr/local/hadoop/hive-2.3.9/warehouse/hdb.db` 。（此处为何 2.3.9 呢，因为 `flink-sql-connector-hive-2.3.6_2.11-1.12.3.jar` 只支持到最高版本 2.3.6，小编先装了个 2.3.9 后装了个 2.3.6，尴尬 > _ < ~）

```sql
CREATE CATALOG myhive WITH (
    'type' = 'hive',
    'default-database' = 'hdb',
    'hive-conf-dir' = '/usr/local/dlink/hive-conf'
);
-- set the HiveCatalog as the current catalog of the session
USE CATALOG myhive;
select * from htest
```

在 Dinky 编辑器中输入以上 sql ，创建 Hive Catalog，并查询一张表。

其中，`hive-conf-dir` 需要指定 `hive-site.xml` 的路径，其他同 Flink 官方解释。

执行查询后（记得选中执行配置的预览结果），可以从查询结果中查看到 htest 表中只有一条数据。（这是正确的，因为小编太懒了，只随手模拟了一条数据）

此时可以使用 FlinkSQL 愉快地操作 Hive 的数据了。

## 使用 Hive Dialect

很熟悉 Hive 的语法以及需要对 Hive 执行其自身特性的语句怎么办？

同 Flink 官方解释一样，只需要使用 `SET table.sql-dialect=hive` 来启用方言即可。注意有两种方言 `default` 和 `hive` ，它们的使用可以随意切换哦~

```sql
CREATE CATALOG myhive WITH (
    'type' = 'hive',
    'default-database' = 'hdb',
    'hive-conf-dir' = '/usr/local/dlink/hive-conf'
);
-- set the HiveCatalog as the current catalog of the session
USE CATALOG myhive;
-- set hive dialect
SET table.sql-dialect=hive;
-- alter table location
alter table htest set location 'hdfs:///usr/htest';
-- set default dialect
SET table.sql-dialect=default;
select * from htest;
```

上述 sql 中添加了 Hive Dialect 的使用，FlinkSQL 本身不支持 `alter table .. set location ..` 的语法，使用 Hive Dialect 则可以实现语法的切换。本 sql 内容对 htest 表进行存储位置的改变，将其更改为一个新的路径，然后再执行查询。

由上图可见，被更改过 location 的 htest 此时查询没有数据，是正确的。

然后将 location 更改为之前的路径，再执行查询，则可见原来的那条数据，如下图所示。

## 总结

由上所知，Dlink 以更加友好的交互方式展现了 Flink 集成 Hive 的部分功能，当然其他更多的 Hive 功能需要您自己在使用的过程中去体验与挖掘。

目前，Dlink 支持 Flink 绝大多数特性与功能，集成与拓展方式与 Flink 官方文档描述一致，只需要在 Dlink 的 plugins 目录下添加依赖即可。