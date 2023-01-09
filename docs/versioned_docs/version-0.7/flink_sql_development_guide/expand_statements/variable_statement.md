---
sidebar_position: 0
id: variable_statement
title: 变量定义
---


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