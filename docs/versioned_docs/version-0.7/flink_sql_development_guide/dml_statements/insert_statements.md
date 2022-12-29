---
sidebar_position: 1
id: insert_statements
title:  INSERT 语句
---

INSERT INTO 语句必须和 SELECT 子查询联用，SELECT 的数据会写入到指定目标表（Table Sink）中。

## 语法结构

```sql
INSERT { INTO | OVERWRITE } [catalog_name.][db_name.]table_name [PARTITION part_spec] [column_list] SELECT 语句
```

## 示例

```sql
-- INTO
INSERT INTO page_kafka_sink
  SELECT user, cnt, country FROM mysql_source;

-- Overwrites 
INSERT OVERWRITE hive_page PARTITION (date='2019-8-30', country='China')
  SELECT user, cnt FROM mysql_source;
```



:::warning 注意事项

- 如果在 WITH 参数里指定了某个 Sink，那么请务必自行上传相应的 Connector 程序包
- 对于读写 Kafka 的场景，推荐使用不带版本号的 `flink-connector-kafka`程序包，并将 `connector.version`参数设置为 `universal`，以获得最新的功能适配。**不建议**使用旧版本

:::
