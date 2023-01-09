---
sidebar_position: 2
id: agg_table_statement
title: AggTable
---

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
