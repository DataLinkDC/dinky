 Dinky 数据源遵循 SPI,可随意扩展所需要的数据源。数据源扩展可在 dlink-metadata 模块中进行可插拔式扩展。现已经支持的数据源包括如下：

   - MySQL
   - Oracle
   - SQLServer
   - PostGreSQL
   - Phoenix
   - Doris(Starrocks)
   - ClickHouse 
   - Hive

使用以上数据源,请查阅注册中心[数据源管理](/zh-CN/administrator-guide/registerCenter/datasource_manage.md),配置数据源连接

**说明：** Dinky 不在对 Starorcks 进行额外扩展，Doris 和 Starorcks 底层并无差别，原则上只是功能区分。经社区测试验证，可采用 Doris 扩展连接 Starrocks。