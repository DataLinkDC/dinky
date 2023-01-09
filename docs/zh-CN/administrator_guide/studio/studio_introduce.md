FlinkSQL Studio是基于React JavaScript库开发的SQL交互式界面工具。目前在Dinky中支持的方言有：
 - FlinkSQL
 - MySQL
 - PostgreSQL
 - Oracle
 - SQLServer
 - Phoenix
 - Doris
 - Clickhouse
 - java 

在FlinkSQL Studio除支持以上方言外,还可以在上面创建作业，提交作业及配置作业等。在作业提交后，可以通过OpenAPI对所在作业进行调度。
 
**FlinkSQL支持**

 FlinkSQL作为Flink上的SQL计算引擎,结合Dinky中的Studio。使得FlinkSQL开发更加简化,不需要写一行Java或者Scala代码。
 只要FlinkSQL本身支持的语法，SQL-Client支持的语法，Dinky Studio上也完全支持。

**Java支持**

当写一些UDF及UDTF函数时,可以在Studio上编写代码,并可以在local模式下测试，并最终发布。当前Java只适合一些UDF及UDTF函数。

**数据库支持**

目前支持的数据库,都可以在Studio上做ETL及通过OpenAPI做周期性调度。

 
 
 
 