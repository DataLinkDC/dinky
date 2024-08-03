---
sidebar_position: 1
position: 1
id: variable_statement
title: 全局变量
---

:::info 背景

在 Dinky 中，我们可以通过全局变量的方式，将一些常用的配置信息，如：表名、表路径等，抽取出来，方便在 SQL 中复用。

此种方式，可以减少 SQL 的冗余，提高 SQL 的可读性。同时，也可以方便的进行统一的配置管理。

另: 全局变量支持多种方式, 具体详见 [全局变量](../../user_guide/register_center/global_var) 章节。
:::

## 定义变量

### 语法结构

```sql
key1 := value1;
```

### 示例

```sql
var1:=student;
select * from ${var1};
```

## 查看变量

```sql
-- 查看所有变量
SHOW FRAGMENTS;
-- 查看单个变量
SHOW FRAGMENT var1;
```



## Flink 连接配置变量

### 语法结构

```sql
CREATE TABLE table_name (
    [columns1 type1,........]
    PRIMARY KEY (pk) NOT ENFORCED
) WITH(
    [key1 = value1,........,]
    ${dorisdwd}
);
```



### 示例

```sql
CREATE TABLE DWD_INFO (
    `SID` STRING,
    `MEMO` STRING,
    PRIMARY KEY (SID) NOT ENFORCED
) WITH(
    'table.identifier' = 'dwd.DWD_INFO',
    ${dorisdwd}
);
```

Flink 连接配置如何添加变量，详见用户手册注册中心的[创建数据源](../administrator_guide/register_center/datasource_manage#创建数据源)

:::warning 注意事项

如果使用如上变量，需要在数据开发的执行配置中`开启`全局变量。

:::