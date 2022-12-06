---
sidebar_position: 5
id: metadata
title: 元数据管理
---


当用户查看或者使用数据源中的 DDL 或者 DML ，可通过元数据中心获取数据源中的相关 DDL 或者 DML。目前元数据中心包含：

- **表信息**

- **字段信息**

- **SQL 生成**

其中在SQL生成中又包括：

- **FlinkDDL 语句**
- **SELECT 语句**
- **SQLDDL 语句**

首先进入**数据开发**中的**元数据**，选择已经配置好的数据源，会出现数据源对应的schema。

![metadata_page](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/metadata/metadata_page.png)


出现以上 schema 后，查看 schema 下的表，**右键单击 schema 下的表**即可看到表信息，字段信息及 SQL 生成。

### 表信息
![metadata_table_info](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/metadata/metadata_table_info.png)

### 字段信息
![metadata_columns_info](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/metadata/metadata_columns_info.png)

### SQL生成
![metadata_gen_flinkddl](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/metadata/metadata_gen_flinkddl.png)

![metadata_gen_selectsql](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/metadata/metadata_gen_selectsql.png)

![metadata_gen_createtable_sql](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/metadata/metadata_gen_createtable_sql.png)


**如何配置数据源详见** [数据源管理](../register_center/datasource_manage)。



