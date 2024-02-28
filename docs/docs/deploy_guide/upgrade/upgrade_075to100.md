---
sidebar_position: 4
position: 4
id: upgrade_075to100
title: 0.7.5 升级到 1.0.0
---


:::warning 说明

1. 本文档为 Dinky v1.0.0 版本升级文档, 适用于从 0.7.5 升级到 1.0.0 版本.
2. 自 0.7.5 升级到 1.0.0 变化较大,存在部分不兼容变更,如果作业数量不多的情况下,建议重新建库部署,手动迁移任务.
3. 如果作业数量较大,建议先进行备份,然后执行升级脚本.但是升级脚本无法完全平滑升级,可能会存在部分数据丢失的情况,请谨慎操作.
4. 自 Dinky v1.0.0 之后, 默认密码为: dinky123!@#

:::

## 备份数据

> 自行备份数据,以防升级失败,导致数据丢失.

## 升级 SQL

### 使用升级脚本

```shell
# 假设你的 Dinky 部署在 /opt/dinky 目录下 

# 登录到数据库,用户名和密码请自行修改
mysql -uroot -p123455 

# 切换到 dinky 数据库
use dinky

# 导入升级脚本 请注意: 先执行 ddl.sql,再执行 dml.sql
source /opt/dinky/sql/upgrade/1.0.0_schema/mysql/dinky_ddl.sql  
source /opt/dinky/sql/upgrade/1.0.0_schema/mysql/dinky_dml.sql

# 升级完成后,请检查是否有错误信息,如果有错误信息,请根据错误信息进行处理

# 退出数据库
exit
```

### 使用数据库管理工具

1. 打开数据库管理工具,如 Navicat 或者 MySQL Workbench,连接到数据库,用户名和密码请自行修改.
2. 导入升级脚本,请注意: 先执行 ddl.sql,再执行 dml.sql.
3. 升级完成后,请检查是否有错误信息,如果有错误信息,请根据错误信息进行处理.
4. 退出数据库管理工具.

> 如需手动 cv 执行, 请自行找到对应的 SQL 语句,复制到数据库管理工具中依次执行.
