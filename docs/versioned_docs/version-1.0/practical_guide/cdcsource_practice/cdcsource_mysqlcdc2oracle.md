---
sidebar_position: 7
position: 7
id: cdcsource_mysqlcdc2oracle
title: MySQLCDC 整库到 Oracle
---



## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Jdbc 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Jdbc connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Oracle CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 Oracle CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例

注意事项:
- 该示例是将 mysql 整库同步到 Oracle 表
- 该示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 Oracle 连接器官方文档进行配置。并请遵守整库同步的规范.


该示例将 Oracle 数据库 TEST 下所有表同步到该数据库的 TEST2下。

```sql showLineNumbers
EXECUTE CDCSOURCE cdc_oracle WITH (
 'connector' = 'oracle-cdc',
 'hostname' = '127.0.0.1',
 'port' = '1521',
 'username'='root',
 'password'='123456',
 'database-name'='ORCL',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'TEST\..*',
 'connector' = 'jdbc',
 'url' = 'jdbc:oracle:thin:@127.0.0.1:1521:orcl',
 'username' = 'root',
 'password' = '123456',
 'table-name' = 'TEST2.#{tableName}'
)
```
