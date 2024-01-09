---
position: 2
sidebar_position: 2
id: datasource_manage
title: 数据源

---


:::tip 简介

数据源管理是 Dinky 管理系统中重要的功能之一，主要用于管理数据源，包括数据源的创建、编辑、删除、查看元数据、可支持自动构建
FlinkDDL 等。

目前支持的数据源类型包括：MySQL、Oracle、PostgreSQL、SQLServer、Phoenix、ClickHouse、Doris、StartRocks、Presto、Hive。

注意: 删除前会进行引用检查，如果有作业引用该数据源，将无法删除。
:::

## 数据源管理

![database_manager_list](http://pic.dinky.org.cn/dinky/docs/test/datasource00.png)

## 创建数据源

当用户使用 Dinky 做为数据开发工具时，用户首先需要进入 **注册中心 > 数据源管理**，点击 **新建** 即可。

![create_database_jdbc](http://pic.dinky.org.cn/dinky/docs/test/datasource0.png)

![create_database](http://pic.dinky.org.cn/dinky/docs/test/datasource1.png)

### 参数解读

| 参数名称       | 参数说明                                                                                                                                                                                                | 是否必填 | 默认值   |
|------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------|-------|
| 名称         | 数据源名称，自定义,建议使用英文，不超过 20 个字符，建议使用全小写.                                                                                                                                                                | 是    | 无     |
| 分组类型       | 数据源分组类型，包括来源、数仓、应用、备份、其他                                                                                                                                                                            | 否    | 无     |
| 数据源类型      | 数据源类型，目前支持以下多种数据源：<br/>            MySQL、Oracle、PostgreSQL、SQLServer、Phoenix、ClickHouse、Doris、StartRocks<br/>Presto、Hive。                                                                           | 是    | MySQL |
| url        | 数据库连接地址，如 jdbc:mysql://127.0.0.1:3306/dinky，点击输入框即可触发常用的数据库URL示例,选中后自动填充,根据实际情况填写                                                                                                                   | 是    | 无     |
| 用户名        | 连接数据库的用户名                                                                                                                                                                                           | 是    | 无     |
| 密码         | 连接数据库的密码                                                                                                                                                                                            | 是    | 无     |
| 备注         | 自定义                                                                                                                                                                                                 | 否    | 无     |
| Flink 连接配置 | 避免私密信息泄露，同时作为全局变量复用连接配置：在 FlinkSQL 中可使用 `${数据源名称}` 来加载连接配置，如 `${MySQL}`。说明：名称指的是英文唯一标识，即如图所示的名称。注意需要开启全局变量。请参考[全局变量](./global_var)                                                                  | 否    | 无     |
| Flink 连接模板 | Flink 连接模板作用是为生成 FlinkSQL DDL 而扩展的功能。其中`#{schemaName}`动态获取数据库，`#{tableName}` 动态获取表名称。其他flink相关参数请参考[Flink 官网](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/overview/) | 否    | 无     |

---
配置完成之后点击测试链接：  
![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource3.png)

## Flink 连接配置 && Flink 连接模板 Demo

以上图创建的数据源为例。

**FLink链接配置：**将一些固定参数如数据库的地址、端口、账号、密码等填在此处  
开启全局变量，使用** ${数据源名称} ** 代替数据源连接的隐私信息，*使用方法*如下：  
![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource6.png)

**Flink连接模板：**作为预设信息，在生成flinksql时无需手动填写flink的相关参数，自动填充。  
*未配置*Flink连接模板，生成的flinksql的with部分为空：  
![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource55.png)

### 两种配置方式:

#### 第一种方式:

![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource4.png)

```sql
-- Flink 连接配置: (可以放入公共参数,及敏感信息参数)
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
    ,'database-name' = '#{schemaName}'
    ,'table-name' = '#{tableName}'
```

##### 生成 FlinkSQL DDL 效果：

![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource5.png)

#### 第二种方式:

![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource44.png)

```sql
-- Flink 连接配置，同第一种方式的连接配置：
    'hostname' = 'localhost'
    ,'port' = '3306'
    ,'username' = 'root'
    ,'password' = '123456'
    ,'server-time-zone' = 'UTC'

-- Flink 连接模板: 注意引用变量的前后逗号,使用此方式作业右侧必须开启全局变量
     'connector' = 'mysql-cdc'
    ,${MySQL}
    ,'scan.incremental.snapshot.enabled' = 'true'
    ,'debezium.snapshot.mode'='latest-offset'
    ,'database-name' = '#{schemaName}'
    ,'table-name' = '#{tableName}'

```

##### 生成 FlinkSQL DDL 效果：

![create_database_jdbc_mysql](http://pic.dinky.org.cn/dinky/docs/test/datasource555.png)  
:::tip 说明
在 Dinky 1.0.0 之前的版本，动态获取库名和表名的变量格式为'${schemaName}' 和 '${tableName}',其主要场景应用于 FlinkSQL DDL 中的 with 配置中和整库同步的场景。

在 Dinky 1.0.0 之后，此处为避免与全局变量冲突，动态获取表名的变量格式为'#{schemaName}' 和 '#{tableName}',此不兼容变更对 FlinkSQL DDL 中的 with 配置无影响, 但是对于整库同步的场景需要注意修改为新的变量格式'#{schemaName}' 和 '#{tableName}' ,与此同时,在整库同步的场景下,也可以支持使用全局变量了.
:::

## 获取数据源元数据信息

在数据源管理页面，点击某一数据源的 Logo ，即可进入该数据源的详情页面

![datssource_detail](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/datasource/datssource_detail.png)

可以以表格式查询某一表数据

![datasource_search](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/datasource/datasource_search.png)

