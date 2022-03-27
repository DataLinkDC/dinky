数据开发是基于React JavaScript库开发的SQL交互式界面工具。目前在Dinky中支持的方言有：
 - FlinkSQL
 - FlinkSQLEnv
 - MySQL
 - PostgreSQL
 - Oracle
 - SQLServer
 - Phoenix
 - Hive
 - Doris(Starrocks)
 - Clickhouse
 - java 

**FlinkSQL支持**

 FlinkSQL作为Flink上的SQL计算引擎,结合Dinky中的数据开发。使得FlinkSQL开发更加简化,不需要写一行Java或者Scala代码。
 只要FlinkSQL本身支持的语法，SQL-Client支持的语法，Dinky数据开发上也完全支持。

**Java支持**

当写一些UDF及UDTF函数时,可以在Studio上编写代码,并可以在local模式下测试，并最终发布。当前Java只适合一些UDF及UDTF函数。

**数据库支持**

目前支持的数据库,都可以在数据开发上做ETL及通过OpenAPI做周期性调度。

 

 