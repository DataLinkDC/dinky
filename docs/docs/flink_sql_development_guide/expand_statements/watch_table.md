---
sidebar_position: 5
id: print_table
title: PRINT TABLE
---
PRINT TABLE 实现埋点功能, 可实现在dinky->表数据标签页动态查看运行时各表数据实时内容,语法结构如下:
```sql
PRINT tableName
```
表可以是source/view, 但不可以是sink表。
```sql
print Orders;
```
