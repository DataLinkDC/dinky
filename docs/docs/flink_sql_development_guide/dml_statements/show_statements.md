---
sidebar_position: 2
id: show_statements
title:  SHOW 语句
---

SHOW 语句用于列出所有目录，或列出当前目录中的所有数据库，或列出当前目录和当前数据库中的所有表/视图，或显示当前目录和数据库，或显示指定表的创建语句，或列出当前目录和当前数据库中的所有函数，包括系统函数和用户定义函数，或仅列出当前目录和当前数据库中的用户定义函数。

FlinkSQL 支持如下 SHOW 语句:

- SHOW CATALOGS 
- SHOW CURRENT CATALOG 
- SHOW DATABASES 
- SHOW CURRENT DATABASE 
- SHOW TABLES 
- SHOW CREATE TABLE
- SHOW VIEWS 
- SHOW FUNCTIONS 

## SHOW CATALOGS

显示所有 Catalog。

```sql
SHOW CATALOGS;
```

## SHOW CURRENT CATALOG

显示当前  Catalog。

```sql
SHOW CURRENT CATALOG;
```

## SHOW DATABASES

显示当前目录中的所有数据库。

```sql
SHOW DATABASES;
```

## SHOW CURRENT DATABASE

显示当前数据库。

```sql
SHOW CURRENT DATABASE;
```

## SHOW TABLES

显示当前 Catalog 和当前数据库中的所有表。

```sql
SHOW TABLES;
```

## SHOW CREATE TABLE

显示指定表的创建表语句。

```sql
SHOW CREATE TABLE [catalog_name.][db_name.]table_name;
```

## SHOW VIEWS 

显示当前 Catalog 和当前数据库中的所有视图。

```sql
SHOW VIEWS;
```

## SHOW FUNCTIONS 

显示当前 Catalog 和当前数据库中的所有函数，包括系统函数和用户定义函数。

```sql
SHOW [USER] FUNCTIONS;
```

:::tip 说明

  USER 只显示当前 Catalog 和当前数据库中的用户定义函数。

:::