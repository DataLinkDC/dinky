---
sidebar_position: 2
id: glossary
title: 词汇术语
---

由于 StreamSQL与传统 SQL 在语法上存在一定的差别，在您学习 FlinkSQL 语法之前，首先需要了解在 SQL中定义的一些关键字。

FlinkSQL常用术语如下

|    词汇术语     |                             说明                             |
| :-------------: | :----------------------------------------------------------: |
|    Source 端    |                 为 FlinkSQL 持续提供输入数据                 |
|     Sink 端     |               为 FlinkSQL 处理结果输出的目的地               |
|     Schema      |         表示一个表的结构信息，例如各个列名、列类型等         |
|    时间模式     | FlinkSQL 处理数据时获取的时间戳，目前支持 Event Time、Processing Time 两种模式 |
|   Event Time    | Event Time 时间模式下，时间戳由输入数据的字段提供，可以用 WATERMARK FOR 语句<br/>指定该字段并启用 Event Time 时间模式 |
|    Watermark    | 表示一个特定的时间点，在该时间点之前的所有数据已经得到妥善处理。<br/>Watermark 由系统自动生成,你可以通过WATERMARK FOR columnName AS <watermark_strategy_expression>定义。 |
| Processing Time | Processing Time 时间模式下，时间戳由系统自动生成并添加到数据源中（以`PROCTIME`命名，SELECT *时不可见，使用时必须显式指定）。它以每条数据被系统处理的时间作为时间戳 |
|     计算列      | 计算列是一个使用 `column_name AS computed_column_expression` 语法生成的虚拟列。它由使用同一表中其他列的非查询表达式生成，并且不会在表中进行物理存储. |
|    时间窗口     | 目前系统支持 TUMBLE、HOP、Session、CUMULATE三种时间窗口,具体详见[时间窗口](https://nightlies.apache.org/flink/flink-docs-master/docs/dev/table/sql/queries/window-tvf/) |
|    SQL Hints    | SQL hints 是和 SQL 语句一起使用来改变执行计划的,常用在动态表的查询中，详见[SQL hints](https://nightlies.apache.org/flink/flink-docs-master/docs/dev/table/sql/queries/hints/) |



:::warning 注意事项

  **Flink提供了几种常用的watermark策略**

​      1.严格意义上递增的时间戳,发出到目前为止已观察到的最大时间戳的水印。时间戳小于最大时间戳的行不会迟到。  WATERMARK FOR rowtime_column AS rowtime_column  

​      2.递增的时间戳,发出到目前为止已观察到的最大时间戳为负1的水印。时间戳等于或小于最大时间戳的行不会迟到。  WATERMARK FOR rowtime_column AS rowtime_column - INTERVAL '0.001' SECOND。

​      3.有界时间戳(乱序) 发出水印，它是观察到的最大时间戳减去指定的延迟，例如，WATERMARK FOR rowtime_column AS rowtime_column-INTERVAL'5'SECOND是5秒的延迟水印策略。
​      WATERMARK FOR rowtime_column AS rowtime_column - INTERVAL 'string' timeUnit.

:::