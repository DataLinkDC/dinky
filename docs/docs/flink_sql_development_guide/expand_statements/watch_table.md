---
sidebar_position: 5
id: watch_table
title: WATCH TABLE
---
WATCH TABLE 实现埋点功能, 可实现在dinky->表数据标签页动态查看运行时各表数据实时内容,语法结构如下:
```sql
WATCH tableName
```
表可以是source/view, 但不可以是sink表。
```sql
watch Orders;
```
