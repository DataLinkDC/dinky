---
sidebar_position: 2
id: dbql_quick_start
title: DB SQL 作业快速入门
---

## 零基础上手

 Dinky 当前支持的外部数据源详见[扩展数据源](./extend/function_expansion/datasource.md)

### 创建数据源

选择**注册中心>>数据源管理>>新建**，假设您连接Doris。

![createSource](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/createSource.png)

![createSource1](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/createSource1.png)

![createSource2](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/createSource2.png)

测试创建成功后，显示如下

![createSourceList](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/createSourceList.png)

### 创建作业

点击**数据开发>>目录>>右键**，出现创建作业菜单。作业类型选择**Doris**

![createdsjob](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/createdsjob.png)

### 作业配置

作业创建完成后，在最右侧会出现数据源，选择连接的**数据源**

![sourcellink](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/sourcellink.png)

### ETL 作业编写

外部数据源可以创建 DDL、DML语句对其进行ETL开发。

![ETL](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/ETL.png)

### 作业提交运行

当 ETL 开发结束 或者做即席查询时，可以点击**保存>>语法检查>>运行当前的SQL** 将 SQL 提交。

![runjob](http://www.aiwenmo.com/dinky/docs/zh-CN/quick_start/dbql_quick_start/runjob.png)

