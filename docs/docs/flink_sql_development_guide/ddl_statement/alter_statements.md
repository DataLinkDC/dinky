---
sidebar_position: 3
id: alter_statements
title:  ALTER 语句
---

ALTER 语句用于修改 Catalog 中已注册的表/视图/函数定义。

FlinkSQL 支持如下 ALTER 语句

- ALTER  TABLE
- ALTER  VIEW
- ALTER  FUNCTION

## ALTER  TABLE

将给定的表名重命名为另一个新的表名。

```sql
ALTER TABLE [catalog_name.][db_name.]table_name RENAME TO new_table_name;
```

## ALTER  VIEW

将给定视图重命名为同一目录和数据库中的新名称。

```sql
ALTER VIEW [catalog_name.][db_name.]view_name RENAME TO new_view_name;
```

## ALTER  FUNCTION

使用新的标识符和可选的语言标记更改目录函数。如果目录中不存在函数，则引发异常。

```sql
ALTER [TEMPORARY|TEMPORARY SYSTEM] FUNCTION 
  [IF EXISTS]  [catalog_name.][db_name.]function_name 
  AS '函数类全名' [LANGUAGE JAVA|SCALA|Python]
```

