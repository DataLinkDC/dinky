---
sidebar_position: 8
id: extend_statement
title: 扩展语法
---

 Dinky 在 FlinkSQL 的基础上新增扩展语法，用于一些参数变量及表值聚合，注册的参数变量及表值聚合可用于 SQL 查询。

  Dinky 当前支持如下扩展语法：

   - 定义变量；
   - 查看变量；
   - 定义表值聚合； 



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

## 表值聚合

Dinky 对 Flink 的表值聚合功能的应用与增强。增强主要在于定义了 AGGTABLE 来通过 FlinkSql 进行表值聚合的实现。

### 语法结构

```sql
CREATE AGGTABLE agg_name AS
SELECT [columns1,columns2,columns3,.....]
FROM table_name 
GROUP BY columns1,......
AGG BY columns2,.....;
```

### 示例

```sql
jdbcconfig:='connector' = 'jdbc',
    'url' = 'jdbc:mysql://127.0.0.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',
    'username'='dlink',
    'password'='dlink',;
CREATE TABLE student (
    sid INT,
    name STRING,
    PRIMARY KEY (sid) NOT ENFORCED
) WITH (
    ${jdbcconfig}
    'table-name' = 'student'
);
CREATE TABLE score (
    cid INT,
    sid INT,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (
    ${jdbcconfig}
    'table-name' = 'score'
);
CREATE TABLE scoretop2 (
    cls STRING,
    score INT,
    `rank` INT,
    PRIMARY KEY (cls,`rank`) NOT ENFORCED
) WITH (
    ${jdbcconfig}
    'table-name' = 'scoretop2'
);
CREATE AGGTABLE aggscore AS 
SELECT cls,score,rank
FROM score
GROUP BY cls
AGG BY TOP2(score) as (score,rank);

insert into scoretop2
select 
b.cls,b.score,b.`rank`
from aggscore b
```



