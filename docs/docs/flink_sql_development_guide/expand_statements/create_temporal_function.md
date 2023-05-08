---
sidebar_position: 1
id: create_temporal_function
title: CREATE TEMPORAL FUNCTION
---
CREATE TEMPORAL FUNCTION 实现在sql中定义[Defining a Temporal Table Function](https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/dev/table/concepts/temporal_table_function/#defining-a-temporal-table-function:~:text=pure%20SQL%20DDL.-,Defining%20a%20Temporal%20Table%20Function,-%23)
功能,语法结构如下:
```sql
CREATE TEMPORAL [TEMPORARY|TEMPORARY SYSTEM] FUNCTION  
    [IF NOT EXISTS] [catalog_name.][db_name.]function_name  
    AS SELECT update_time, target FROM tableName
```
其中`update_time`列为版本时间属性列,`target` 是一个或多个键值列, `talbeName`表示版本表名。
```sql
create temporal temporary function 
    IF NOT EXISTS rates 
    as select update_time, currency from currency_rates;

```
该语句等价table API示例：
```java
    TemporalTableFunction rates = tEnv
        .from("currency_rates")
        .createTemporalTableFunction("update_time", "currency");
     
    tEnv.createTemporarySystemFunction("rates", rates);    
```
