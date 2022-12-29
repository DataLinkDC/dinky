---
sidebar_position: 2
id: drop_statements
title:  DROP 语句
---

DROP 语句用于从当前或指定的目录中删除已注册的表/视图/函数/Catalog。

FlinkSQL 支持如下DROP 语句

- DROP CATALOG
- DROP DATABASE
- DROP TABLE
- DROP VIEW
- DROP FUNCTION

## DROP CATALOG

删除具有给定目录名称的目录

### 语法结构

```sql
DROP CATALOG [IF EXISTS] catalog_name;
```

## DROP DATABASE

删除具有给定数据库名称的数据库。如果要删除的数据库不存在，则会引发异常。

### 语法结构

```sql
DROP DATABASE [IF EXISTS] [catalog_name.]db_name [ (RESTRICT | CASCADE) ];
```

## DROP TABLE

删除具有给定表名的表。如果要删除的表不存在，则引发异常。

### 语法结构

```sql
DROP [TEMPORARY] TABLE [IF EXISTS] [catalog_name.][db_name.]table_name;
```

## DROP VIEW

删除具有目录和数据库命名空间的视图。如果要删除的视图不存在，则引发异常。

### 语法结构

```sql
DROP [TEMPORARY] VIEW  [IF EXISTS] [catalog_name.][db_name.]view_name;
```

## DROP FUNCTION

删除具有目录和数据库命名空间的目录函数。如果要删除的函数不存在，则引发异常。

### 语法结构

```sql
DROP [TEMPORARY|TEMPORARY SYSTEM] FUNCTION [IF EXISTS] [catalog_name.][db_name.]function_name;
```