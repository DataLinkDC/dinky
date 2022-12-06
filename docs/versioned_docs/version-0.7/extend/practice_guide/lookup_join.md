---
sidebar_position: 15
id: lookup_join
title: lookup join
---



## lookup join

该例中，将 mysql 表作为维表，里面保存订单信息，之后去关联订单流水表，最后输出完整的订单流水信息数据到 kafka。

### kafka 主题 （order_water）

订单流水表读取的是 kafka `order_water` 主题中的数据，数据内容如下

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_order_water_data.png)

### mysql 表 （dim.order_info）

**表结构**

```sql
CREATE TABLE `order_info` (
  `id` int(11) NOT NULL COMMENT '订单id',
  `user_name` varchar(50) DEFAULT NULL COMMENT '订单所属用户',
  `order_source` varchar(50) DEFAULT NULL COMMENT '订单所属来源',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

**数据**

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_lookup_join_mysql_mysql_data1.png)

### flink sql 语句

```sql
set 'table.local-time-zone' = 'GMT+08:00';

-- 订单流水
CREATE temporary TABLE order_flow(
    id int comment '订单id',
    product_count int comment '购买商品数量',
    one_price double comment '单个商品价格',
    -- 一定要添加处理时间字段，lookup join 需要该字段
    proc_time as proctime()
) WITH (
    'connector' = 'kafka',
    'topic' = 'order_water',
    'properties.bootstrap.servers' = 'node01:9092,node02:9092,node03:9092',
    'properties.group.id' = 'for_source_test',
    'scan.startup.mode' = 'latest-offset',
    'format' = 'csv',
    'csv.field-delimiter' = ' '
)
;

-- 订单信息
create table order_info (
    id int PRIMARY KEY NOT ENFORCED comment '订单id',
    user_name string comment '订单所属用户',
    order_source string comment '订单所属来源'
) with (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://node01:3306/dim?useSSL=false',
    'table-name' = 'order_info',
    'username' = 'root',
    'password' = 'root'
)
;

-- 创建连接 kafka 的虚拟表作为 sink
create table sink_kafka(
    id int PRIMARY KEY NOT ENFORCED comment '订单id',
    user_name string comment '订单所属用户',
    order_source string comment '订单所属来源',
    product_count int comment '购买商品数量',
    one_price double comment '单个商品价格',
    total_price double comment '总价格'
) with (
    'connector' = 'upsert-kafka',
    'topic' = 'for_sink',
    'properties.bootstrap.servers' = 'node01:9092,node02:9092,node03:9092',
    'key.format' = 'csv',
    'value.format' = 'csv',
    'value.csv.field-delimiter' = ' '
)
;

-- 真正要执行的任务
insert into sink_kafka
select
    a.id,
    b.user_name,
    b.order_source,
    a.product_count,
    a.one_price,
    a.product_count * a.one_price as total_price
from order_flow as a
-- 一定要添加 for system_time as of 语句，否则读取 mysql 的子任务会被认为是有界流，只读取一次，之后 mysql 维表中变化后的数据无法被读取
left join order_info for system_time as of a.proc_time as b
on a.id = b.id
;
```

flink sql 任务运行之后，flink UI 界面显示为

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_lookup_join_mysql_flink_ui.png)

最后查看写入 kafka 中的数据为

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_lookup_join_mysql_sink_kafka_data1.png)

此时，修改 mysql 中的数据，修改之后为

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_lookup_join_mysql_mysql_data2.png)

再查看写入 kafka 中的数据为

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_lookup_join_mysql_sink_kafka_data2.png)

**其他**

如果 kafka 中的订单流数据中的某个订单 id 在维表 mysql 中找不到，而且 flink sql 任务中使用的是 left join 连接，
则匹配不到的订单中的 user_name 和 product_count 字段将为空字符串，具体如下图所示

![img.png](http://www.aiwenmo.com/dinky/docs/zh-CN/sql_development_guide/example/kafka_lookup_join_mysql_sink_kafka_data3.png)