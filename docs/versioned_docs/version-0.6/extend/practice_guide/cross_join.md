---
sidebar_position: 18
id: cross_join
title: cross join
---

## 列转行

也就是将数组展开，一行变多行，使用到 `cross join unnest()` 语句。

读取 hive 表数据，然后写入 hive 表。

### source

`source_table` 表信息如下

```sql
CREATE TABLE `test.source_table`(
  `col1` string, 
  `col2` array<string> COMMENT '数组类型的字段')
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
  'hdfs://hadoopCluster/user/hive/warehouse/test.db/source_table'
TBLPROPERTIES (
  'transient_lastDdlTime'='1659261419')
;
```

`source_table` 表数据如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/hive_to_hive_explode_source_table_data.png)

### sink

`sink_table` 表信息如下

```sql
CREATE TABLE `test.sink_table`(
  `col1` string, 
  `col2` string)
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
  'hdfs://hadoopCluster/user/hive/warehouse/test.db/sink_table'
TBLPROPERTIES (
  'transient_lastDdlTime'='1659261915')
;
```

`sink_table` 表数据如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/hive_to_hive_explode_sink_table_data.png)

### flink sql 语句

下面将使用两种方言演示如何将数组中的数据展开

#### 使用flink方言

```sql
set 'table.local-time-zone' = 'GMT+08:00';

-- 在需要读取hive或者是写入hive表时，必须创建hive catalog。
-- 创建catalog
create catalog hive with (
    'type' = 'hive',
    'hadoop-conf-dir' = '/data/soft/dlink-0.6.6/hadoop-conf',
    'hive-conf-dir' = '/data/soft/dlink-0.6.6/hadoop-conf'
)
;

use catalog hive;


insert overwrite test.sink_table
select col1, a.col
from test.source_table
cross join unnest(col2) as a (col)
;
```

#### 使用hive方言

```sql
set 'table.local-time-zone' = 'GMT+08:00';

-- 在需要读取hive或者是写入hive表时，必须创建hive catalog。
-- 创建catalog
create catalog hive with (
    'type' = 'hive',
    'hadoop-conf-dir' = '/data/soft/dlink-0.6.6/hadoop-conf',
    'hive-conf-dir' = '/data/soft/dlink-0.6.6/hadoop-conf'
)
;

use catalog hive;

load module hive;

set 'table.sql-dialect' = 'hive';

insert overwrite table test.sink_table
select col1, a.col
from test.source_table
lateral view explode(col2) a as col
;
```
