---
sidebar_position: 3
position: 3
id: upgrade_101to102
title: 1.0.1 升级到 1.0.2
---

:::warning 说明

1. 本文档为 Dinky v1.0.1 升级到 v1.0.2 的升级文档,其他版本请自行参考对应版本的升级文档.切记如果连续版本中含有表结构变更,不可跨版本升级
2. 请根据实际情况选择对应的升级文档.
3. 升级前请先备份数据,以防升级失败导致数据丢失.(建议)
:::

## 备份数据

> 自行备份数据,以防升级失败,导致数据丢失.

## 升级 SQL

### 使用升级脚本

#### MySQL
```shell
# 假设你的 Dinky 部署在 /opt/dinky 目录下 
# 登录到数据库,用户名和密码请自行修改

# 切换到 dinky 数据库
use dinky

# 导入升级脚本 请注意: 先执行 ddl.sql,再执行 dml.sql
source /opt/dinky/sql/upgrade/1.0.2_schema/mysql/dinky_ddl.sql  
source /opt/dinky/sql/upgrade/1.0.2_schema/mysql/dinky_dml.sql

# 升级完成后,请检查是否有错误信息,如果有错误信息,请根据错误信息进行处理

# 退出数据库
exit
```
#### PostgresSQL
```shell
# 假设你的 Dinky 部署在 /opt/dinky 目录下 

# 登录到数据库,用户名和密码请自行修改
# 切换到 dinky 数据库
use dinky

# 导入升级脚本 请注意: 先执行 ddl.sql,再执行 dml.sql
先执行 /opt/dinky/sql/upgrade/1.0.2_schema/postgre/dinky_ddl.sql  
再执行 /opt/dinky/sql/upgrade/1.0.2_schema/postgre/dinky_dml.sql

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
