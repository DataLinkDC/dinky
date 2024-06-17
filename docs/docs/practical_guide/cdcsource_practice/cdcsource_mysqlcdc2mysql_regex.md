---
sidebar_position: 6
position: 6
id: cdcsource_mysqlcdc2mysql_regex
title: MySQLCDC 整库到 MySQL - 通过正则表达式替换、表映射路由案例 
---

## 前置准备

- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 Jdbc 的 Flink connector jar。如果提交模式为 Application/Per-Job，请确保 Jdbc connector jar 已经放置在 HDFS 中
- 请确保已经在 Flink/lib 和 dinky/extends 目录下放置了 MySQL CDC 的 Flink connector jar。 如果提交模式为 Application/Per-Job，请确保 MySQL CDC connector jar 已经放置在 HDFS 中
- 如在两方启动后才进行放置上述 jar 包，请重启 Flink 和 Dinky 服务,或者使用 Dinky 中提供的 [ADD CUSTOMJAR](../../extend/expand_statements/add_jar_statement) 功能进行加载。


## 示例一：使用  `sink.table.replace.pattern` 和 `sink.table.replace.with` 正则匹配和替换

通过正则表达式替换原表名称，并自动创建目标表名。

注意事项:
- 该示例是将 mysql 整库同步到 mysql 表，且写入名为 ods 的库，通过正则表达式匹配 `t_` 并替换成 `ods_`。
- 该示例参数中的 `#{tableName}` 为占位符，实际执行时会替换为实际表名，如 `ods_products`、`ods_orders` 等。
- 该示例 sink 中的各个参数均可根据实际情况进行调整，请按照 mysql 连接器官方文档进行配置。并请遵守整库同步的规范。

> 该示例为将 mysql 整库同步到另一个 mysql 数据库，写入 test 库，表名使用 `sink.table.replace.pattern` 和 `sink.table.replace.with` 正则匹配和替换，表名全小写，开启自动建表。

```sql showLineNumbers
EXECUTE CDCSOURCE cdc_mysql WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.t_products,bigdata\.t_orders',
 'sink.connector' = 'jdbc',
 'sink.url' = 'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false',
 'sink.username' = 'root',
 'sink.password' = '123456',
 'username' = 'root',
 'sink.sink.db' = 'test',
 -- 正则匹配表名，进行替换
 'sink.table.replace.pattern' = '^t_(.*?)',
 'sink.table.replace.with' = 'ods_$1',
 'sink.table.lower' = 'true',
 'sink.table-name' = '#{tableName}',
 'sink.driver' = 'com.mysql.jdbc.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5',.
 'sink.auto.create' = 'true'
)
```

## 示例二：使用  `sink.table.mapping-routes` 进行表名和目标表名进行映射替换 

> 使用 `sink.table.mapping-routes` 进行表名和目标表名进行映射替换。

**注意：原表和目标表名格式,`k1:v1,k2:v2` 键值对，多张表通过逗号分割。**


```sql showLineNumbers
EXECUTE CDCSOURCE cdc_mysql WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.t_example_a,bigdata\.example_b',
 'sink.connector' = 'jdbc',
 'sink.url' = 'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false',
 'sink.username' = 'root',
 'sink.password' = '123456',
 'sink.sink.db' = 'test',
 -- 映射表名 原表 t_example_a 替换成 test_example_a ，多张表通过逗号分割
 'sink.table.mapping-routes' = 't_example_a:test_example_a,example_b:t_example_b',
 'sink.table.lower' = 'true',
 'sink.table-name' = '#{tableName}',
 'sink.driver' = 'com.mysql.jdbc.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5',
 'sink.auto.create' = 'true'
)
```

## 示例三：这三个参数  `sink.table.mapping-routes` 和 `sink.table.replace.pattern` 和 `sink.table.replace.with` 可以同时搭配使用。

注意： mapping-routes 规则优先级比较高。其次是 replace.pattern 和 replace.with 规则。

```sql showLineNumbers
EXECUTE CDCSOURCE cdc_mysql WITH (
 'connector' = 'mysql-cdc',
 'hostname' = '127.0.0.1',
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'checkpoint' = '3000',
 'scan.startup.mode' = 'initial',
 'parallelism' = '1',
 'table-name' = 'bigdata\.t_example_a,bigdata\.example_b',
 'sink.connector' = 'jdbc',
 'sink.url' = 'jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false',
 'sink.username' = 'root',
 'sink.password' = '123456',
 'sink.sink.db' = 'test',
 -- 映射表名 原表 t_example_a 替换成 test_example_a ，多张表通过逗号分割
 'sink.table.mapping-routes' = 't_example_a:test_example_a,example_b:t_example_b',
 -- 正则匹配表名，进行替换
 'sink.table.replace.pattern' = '^t_(.*?)', -- replace t_example_b -> ods_example_b , test_example_a 则不会变成 ods_example_a ，因为 table.mapping-routes 优先级较高
 'sink.table.replace.with' = 'ods_$1',
 'sink.table.lower' = 'true',
 'sink.table-name' = '#{tableName}',
 'sink.driver' = 'com.mysql.jdbc.Driver',
 'sink.sink.buffer-flush.interval' = '2s',
 'sink.sink.buffer-flush.max-rows' = '100',
 'sink.sink.max-retries' = '5',
 'sink.auto.create' = 'true'
)
```