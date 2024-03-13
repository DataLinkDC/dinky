---
sidebar_position: 1
position: 1
id: upgrade_overview
title: 版本升级介绍
---

:::caution 注意
1. 自 Dinky 0.7.5 开始进行升级教程, 后续版本更新将在此文档及子文档中进行更新。
2. 所有版本的升级 SQL 在部署目录下的 sql/upgrade 目录下, 升级 SQL 按照版本号进行排序, 升级 SQL 目录命名规则为 0.7.5_schema/mysql
3. 自 Dinky v1.0.0 开始支持 PostgreSQL,作为后端数据存储库实现, 升级 SQL 目录命名规则为 1.0.0_schema/postgresql
4. 注意: Dinky v1.0.0 刚支持 PostgreSQL,此数据库类型不存在升级脚本,直接执行`sql/dinky-pg.sql`即可)
5. 升级时请注意,先执行 `{version}_schema/mysql`/`{version}_schema/postgresql` 下的 `dinky_ddl.sql`,再执行 `{version}_schema/mysql`/`{version}_schema/postgresql` 下的 `dinky_dml.sql`
:::

### 版本升级列表

- [0.7.5 升级到 1.0.0](upgrade_075to100)