---
sidebar_position: 6
id: catalog_manage
title: Catalog 管理
---

## Catalog 概述

当您使用 Flink 管理元数据时，Flink已提供了四种不同的 Catalog：
- GenericInMemoryCatalog
- JdbcCatalog
- HiveCatalog
- 用户自定义的Catalog

在实际的开发过程当中，如果是采用 Flink 原生的 Catalog。具体 Flink 是如何定义 Catalog 的，详见 Flink 官网。详见 [Flink 官网](https://nightlies.apache.org/flink/flink-docs-master/zh/docs/dev/table/catalogs/)。

另外 Dinky 提供了二种基于Catalog ：
- Session会话级别的 Catalog
- Mysql Catalog
  如果在 Dinky 实时计算平台开发，推荐采用 Flink 原生的 HiveCatalog 或者 Dinky 的 Mysql Catalog。

## Hive Catalog

### 介绍

您可以在 Dinky 中使用 FlinkSQL 在作业中编写 Hive Catalog、查看 Hive 元数据、使用 Hive Catalog。将元数据信息保存到 Hive Metastore 以后，即可在数据开发的编辑器中开发作业。

### 说明

**依赖说明**
```
# FLINK_HOME/lib及 $DINKY_HOME/plugins下
flink-sql-connector-hive-x.x.x_x.xx-x.x.x.jar
```

**版本说明**

| Flink 版本 |                             说明                             |
| :--------: | :----------------------------------------------------------: |
| Flink1.11  | 详见[Flink1.11](https://nightlies.apache.org/flink/flink-docs-release-1.11/dev/table/catalogs.html)说明 |
| Flink1.12  | 详见[Flink1.12](https://nightlies.apache.org/flink/flink-docs-release-1.12/dev/table/catalogs.html)说明 |
| Flink1.13  | 详见[Flink1.13](https://nightlies.apache.org/flink/flink-docs-release-1.13/dev/table/catalogs.html)说明 |
| Flink1.14  | 详见[Flink1.14](https://nightlies.apache.org/flink/flink-docs-release-1.14/dev/table/catalogs.html)说明 |
| Flink1.15  | 详见[Flink1.15](https://nightlies.apache.org/flink/flink-docs-release-1.15/dev/table/catalogs.html)说明 |

### 前提条件

已在 Hive Metastore 侧开启了 Hive Metastore 服务。
相关命令如下：

- `hive --service metastore`：开启 Hive Metastore 服务。
- `ps -ef|grep metastore`：查询 Hive Metastore 服务是否已开启

## Mysql Catalog

### 介绍

您可以在 Dinky FlinkSQL 作业中配置 Mysql Catalog、查看 Mysql 元数据、使用 Mysql Catalog。将元数据信息保存到 Mysql 以后，在作业中无需再显式声明 DDL 语句，直接三段式引用元数据即可。

### 前提条件

必须在 Dinky 元数据库创建 Mysql Catalog所使用的元数据表，Mysql Catalog 持久化目前默认的Catalog为my_catalog,默认的FlinkSQLEnv为DefaultCatalog。元数据表解释如下：

|          元数据表          |    表中文名称    |
| :------------------------: | :--------------: |
|     metadata_database      | 元数据schema信息 |
|       metadata_table       | 元数据table信息  |
| metadata_database_property |  schema属性信息  |
|  metadata_table_property   |  table属性信息   |
|      metadata_column       |    数据列信息    |
|     metadata_function      |     UDF信息      |

### 说明

```
# Mysql Catalog依赖放置 $FLINK_HOME/lib下
dlink-catalog-mysql-1.1x-0.6.x.jar
```

### 创建 Mysql Catalog 语法

对于 mysql catalog 除前提条件中提到的用默认的 DefaultCatalog 外，那么可以新建一个作业通过create创建catalog。语法如下：

```
create catalog my_catalog with(
   'type' = 'dlink_mysql', 
   'username' = 'dlink', 
   'password' = 'dlink', 
   'url' = 'jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true' 
   );
use catalog my_catalog;
```

目前 mysql catalog 固定是my_catalog，不能自定义一个catalog。如果在重新创建表，需要在建表之前，将 Flink 任务停止后，再次初始化建表脚本。