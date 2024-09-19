---
sidebar_position: 2
id: cdcsource_mysqlcdc2print
position: 2
title: MySQLCDC 整库到 Print
---

## 前置准备
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Print，输出到控制台。
- 该示例是常用于快速简单调试, 请勿用于生产环境。

```sql showLineNumbers
EXECUTE CDCSOURCE demo_print WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'checkpoint' = '10000',
  'scan.startup.mode' = 'initial',
  'parallelism' = '1',
  'table-name' = 'test\.student,test\.score',
  'sink.connector' = 'print'
);
```