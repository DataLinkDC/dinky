---
position: 2
sidebar_position: 2
id: datasource_manage
title: 数据源

---

## 数据源管理

![database_manager_list](http://www.aiwenmo.com/dinky/docs/test/datasource00.png)

## 创建数据源

当用户使用 Dinky 做为数据开发工具时，用户首先需要进入 **注册中心 > 数据源管理**，点击 **新建** 即可。

![create_database_jdbc](http://www.aiwenmo.com/dinky/docs/test/datasource0.png)

![create_database](http://www.aiwenmo.com/dinky/docs/test/datasource1.png)

**名称：** 唯一标识，自定义

**分组类型：** 包括来源、数仓、应用、备份、其他，不选默认Default

**数据源类型：** 目前支持以下多种数据源   
- OLTP：MySQL,Oracle,PostgreSQL,SQLServer,Phoenix
- OLAP：ClickHouse,Doris,StartRocks,Presto
- DataWarehouse/DataLake：Hive

**url：** 数据库连接地址，如 jdbc:mysql://127.0.0.1:3306/dlink，点击输入框有常用的数据库URL示例
![create_database](http://www.aiwenmo.com/dinky/docs/test/datasource2.png)

**用户名：** 连接数据库的用户名

**密码：** 连接数据库的密码

**备注：** 自定义

**Flink 连接配置：** 避免私密信息泄露，同时作为全局变量复用连接配置，在FlinkSQL中可使用`${数据源名称}` 来加载连接配置，如`${MySQL}`。说明：名称指的是英文唯一标识，即如图所示的名称。注意需要开启全局变量（原片段机制）。更多参数请参考[全局变量](./global_var.md)

**Flink 连接模板：** Flink 连接模板作用是为生成 FlinkSQL DDL 而扩展的功能。其中`#{schemaName}`动态获取数据库，`#{tableName}` 动态获取表名称。其他flink相关参数请参考[Flink 官网](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/overview/)


---
填完之后点击测试链接：  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource3.png)





## Flink 连接配置 && Flink 连接模板 Demo  
以上图创建的数据源为例。

**FLink链接配置：**将一些固定参数如数据库的地址、端口、账号、密码等填在此处  
开启全局变量，使用** ${数据源名称} ** 代替数据源连接的隐私信息，*使用方法*如下：  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource6.png)

**Flink连接模板：**作为预设信息，在生成flinksql时无需手动填写flink的相关参数，自动填充。  
*未配置*Flink连接模板，生成的flinksql的with部分为空：  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource55.png)

### 两种配置方式:
#### 第一种方式:  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource4.png)

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

##### 生成flinksql效果：  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource5.png)


#### 第二种方式:  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource44.png)

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

##### 生成flinksql效果：  
![create_database_jdbc_mysql](http://www.aiwenmo.com/dinky/docs/test/datasource555.png)  
:::tip 说明
此处为避免与全局变量冲突，动态获取表名的变量格式为'#{schemaName}' 和 '#{tableName}'!
:::


## 查询数据

dinky除了可以开发flink任务之外，也支持常见的数据库任务，在数据开发页面选择创建相对应的数据库任务，右侧选定数据源即可查询数据库数据。

