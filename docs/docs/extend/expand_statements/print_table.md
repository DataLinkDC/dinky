---
sidebar_position: 5
position: 5
id: print_table
title: PRINT TABLE
---

:::info 背景
在 Dinky 中, 我们可以通过 `PRINT TABLENAME`语句来查看表数据实时内容。

此功能可以实现在 Dinky->`表数据`标签页动态查看运行时各表数据实时内容, 以便于调试SQL语句或者查看中间表过程数据。
:::

## 语法结构
PRINT TABLE 实现埋点功能, 语法结构如下:
```sql
PRINT tableName
```

### Demo

```sql
CREATE TABLE Orders (
    order_number BIGINT,
    price        DECIMAL(32,2),
    buyer        STRING,
    seller       STRING,
    order_time   TIMESTAMP(3),
    WATERMARK FOR order_time AS order_time - INTERVAL '5' SECOND
) WITH (
    'connector' = 'kafka',
    'topic' = 'Orders',
    'properties.bootstrap.servers' = 'localhost:9092',
    'properties.group.id' = 'testGroup',
    'scan.startup.mode' = 'latest-offset',
    'format' = 'csv'
);

print Orders;
```
:::warning 注意

PRINT 的表可以是 source/view, 但不可以是 sink 表。

:::
