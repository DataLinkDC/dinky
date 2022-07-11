# 自定义实现的catalog
## 目标
用于保存 flink sql创建的元数据

- 实现了 database table view 的 create ，alter， drop，list，get 操作。
- partition相关操作: 根据文档，只有再文件系统的connector才需要这个功能。
>例如 在 hdfs 上，其作用是在文件系统上根据分区字段创建目录，列出已有的分区目录，修改分区目录等。根据评估，如果是应对流数据，暂时不需要实现该功能。
如果是针对hdfs，则使用hive catalog来作为 catalog 更好
如果是其他fs类的支持，需要适配各种环境，不利于做到通用性。
故这个部分未进行实现。

- 对各类 Statistics 也未进行实现，因为流数据无法获取相关的条数，数据大小等。

## 用法
1. 将打好包的jar放到flink lib目录
2. 引入mysql driver，需要支持 com.mysql.cj.jdbc.Driver 这个暂时是写死的。
3. 使用mycatalog.sql 初始化meta库所需的表。
4. 在flink conf目录下创建配置文件 mysql-catalog.properties
5. 根据你的环境启动sql client 或者在代码中使用 flink sql来创建catalog，注册catalog。

> flink sql:
```roomsql 
create catalog mycatalog 
    with(
        'type'='dlink_mysql_catalog'
        );
        
use catalog mycatalog;
```

> java
```java 
        DlinkMysqlCatalog catalog =
                new DlinkMysqlCatalog(
                        "myCatalog");
        tableEnv.registerCatalog(DlinkMysqlCatalogFactoryOptions.IDENTIFIER, catalog);
        tableEnv.useCatalog(DlinkMysqlCatalogFactoryOptions.IDENTIFIER);
```
## 注意
> 在dlink mysql catalog的实现中，对默认数据库进行了限制，不允许用户自定义默认数据库名称。
todo: 在yaml文件中定义 catalog 的方法。


