 Dinky数据源遵循SPI,可随意扩展所需要的数据源。数据源扩展可在dlink-metadata模块中进行可插拔式扩展。现已经支持的数据源包括如下：

   - MySQL
   - Oracle
   - SQLServer
   - PostGreSQL
   - Phoenix
   - Doris(Starrocks)
   - ClickHouse 
   - Hive

使用以上数据源,请查阅注册中心[数据源管理](/zh-CN/administrator-guide/registerCenter/datasource_manage.md),配置数据源连接

**说明：** Dinky不在对Starorcks进行额外扩展，Doris和Starorcks底层并无差别，原则上只是有些功能区分而已。在Flink Connector 上，目前Doris只支持Sink，而Starocks Source和Sink均支持。经过社区测试验证，可采用Doris的扩展连接Starrocks。