---
position: 4
sidebar_position: 4
id: row_limits
title: 行权限
---

:::info 简介

行权限场景主要是为了解决角色场景下，不同角色之间的数据隔离问题。通过添加“行权限”规则，可使指定范围的角色和用户仅能查看指定范围的行数据。
:::

通过添加“行权限”规则，可使指定范围的角色和用户仅能查看指定范围的行数据。例如用户 A 仅能查看数据集“ '班级'字段=高二 ”的数据。但是只在只在flinksql中生效。

![row_limits_add](http://pic.dinky.org.cn/dinky/docs/test/row_limits_add.png)

如上图所示，表名为flink中的表名，表达式只需要填where后的语句即可。



## 实现原理

本功能基于 [FlinkSQL的行级权限解决方案](https://github.com/HamaWhiteGG/flink-sql-security/blob/dev/docs/row-filter/README.md) 实现，具体原理如下：

1. 在检查提交时系统会解析传入的 SQL，获取 InputTable 和 OutputTable 两个数据集。
2. 系统通过对远端权限服务的查询，获取该用户下绑定的 RBAC 权限。 
3. 根据获取到的 RBAC 权限，系统会得到对应的表的权限信息。
4. 系统将通过重写 SQL，将原本 SQL 查询字段的过滤条件 追加到 WHERE 条件中，从而实现数据的过滤。
5. 重写后的 SQL 会被提交到 Flink 作业中执行。
