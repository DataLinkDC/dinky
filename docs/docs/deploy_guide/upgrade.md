---
sidebar_position: 4
position: 4
id: upgrade
title: 升级
---


:::info 说明

本文档仅从 0.7.5 开始进行升级教程, 后续版本更新将在此文档中进行更新。

:::

:::caution 注意

1. 所有版本的升级 SQL 在部署目录下的 sql/upgrade 目录下, 升级 SQL 按照版本号进行排序, 升级 SQL 目录命名规则为 0.7.5_schema/mysql
2. 自 Dinky v1.0.0 开始支持 PostgreSQL,作为后端数据存储库实现, 升级 SQL 目录命名规则为 1.0.0_schema/postgresql
3. 注意: Dinky v1.0.0 刚支持 PostgreSQL,此数据库类型不存在升级脚本,直接执行`sql/dinky-pg.sql`即可)
4. 升级时请注意,先执行 `{version}_schema/mysql`/`{version}_schema/postgresql` 下的 `dinky_ddl.sql`,再执行 `{version}_schema/mysql`/`{version}_schema/postgresql` 下的 `dinky_dml.sql`
:::

## 0.7.5 升级到 1.0.0

> 注意: 自 0.7.5 升级到 1.0.0 变化较大,存在部分不兼容变更,如果作业数量不多的情况下,建议重新建库部署,手动迁移任务.如果作业数量较大,建议先进行备份,然后手动迁移任务.
    
> 快速梳理中,敬请期待,欢迎参与贡献,请参考[贡献指南](../developer_guide/contribution/how_contribute)。

```shell


```
