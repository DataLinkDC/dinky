---
position: 2
id: datasource_manage
title: 数据源管理
---
## 数据源管理列表

![database_manager_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/datasource_manage/database_manager_list.png)

## 创建数据源

当用户使用 Dinky 做为数据开发工具时，用户首先需要进入 **注册中心 > 数据源管理**，点击 **新建** 即可。

![create_database_jdbc](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/datasource_manage/create_database_jdbc.png)

![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/datasource_manage/create_database_jdbc_mysql.png)

**名称：** 输入英文唯一标识

**别名：** 自定义，默认同名称

**分组类型：** 包括来源、数仓、应用、备份、其他

**url：** 数据库连接地址，如 jdbc:mysql://127.0.0.1:3306/dlink

**用户名：** 连接数据库的用户名

**密码：** 连接数据库的密码

**Flink 连接配置：** 避免私密信息泄露，同时作为全局变量复用连接配置，在FlinkSQL中可使用 **${名称}** 来加载连接配置，如 **${ods}**。说明：名称指的是英文唯一标识，即如图所示的名称。注意需要开启全局变量（原片段机制）

**Flink 连接模板：** Flink 连接模板作用是为生成 FlinkSQL DDL 而扩展的功能。其中 **${schemaName}** 动态获取数据库，**${tableName}** 动态获取表名称。更多参数请参考[Flink 官网](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/overview/)

**注释：** 自定义

**是否启用：** 默认禁用，需要开启

Flink 连接配置 && Flink 连接模板 配置Demo: (以上图创建的`本地`数据源为例)

```sql
,第一种方式:
-- Flink 连接配置: (可以放入公共参数,及其敏感信息参数)
     'hostname' = 'localhost'
    ,'port' = '3306'
    ,'username' = 'root'
    ,'password' = '123456'
    ,'server-time-zone' = 'UTC'

-- Flink 连接模板: 
     'connector' = 'mysql-cdc'
    ,'hostname' = 'localhost'
    ,'port' = '3306'
    ,'username' = 'root'
    ,'password' = '123456'
    ,'server-time-zone' = 'UTC'
    ,'scan.incremental.snapshot.enabled' = 'true'
    ,'debezium.snapshot.mode'='latest-offset'  
    ,'database-name' = '${schemaName}'
    ,'table-name' = '${tableName}'

第二种方式:
-- Flink 连接配置: 同第一种方式的连接配置

-- Flink 连接模板: 注意引用变量的前后逗号,使用此方式作业右侧必须开启全局变量
     'connector' = 'mysql-cdc'
    ,${本地}
    ,'scan.incremental.snapshot.enabled' = 'true'
    ,'debezium.snapshot.mode'='latest-offset'
    ,'database-name' = '${schemaName}'
    ,'table-name' = '${tableName}'

以上配置完成后可在 数据开发->左侧点击 元数据->选中当前创建的数据源 -> 展开库 -> 右键单击 表名 -> 点击 SQL生成 -> 查看FlinkDDL 即可看到成果
```

当前数据库统一使用如上定义的参数名称配置数据源连接。当前支持的数据源详见 [扩展数据源](../../extend/function_expansion/datasource)。
