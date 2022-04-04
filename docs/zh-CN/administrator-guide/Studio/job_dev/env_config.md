## Flink 环境配置

### Session 集群

Session 集群适用于非生产环境的开发测试环境，您可以使用 Session 集群模式部署或调试作业，提高作业 Job Manager 的资源使用率。

如何创建 Session 集群，请参考注册中心[集群管理](/zh-CN/administrator-guide/registerCenter/cluster_manage.md)的集群实例管理

### Per-job 集群

Per-job 集群适用于生产环境，您可以使用 Per-job 集群模式部署或提交作业

如何创建 Session 集群，请参考注册中心[集群管理](/zh-CN/administrator-guide/registerCenter/cluster_manage.md)的集群配置管理

### Application 集群

Per-job 集群适用于生产环境，您可以使用 Per-job 集群模式部署或提交作业

如何创建 Session 集群，请参考注册中心[集群管理](/zh-CN/administrator-guide/registerCenter/cluster_manage.md)的集群配置管理


### 说明

如果是 Session 集群有如下建议：

- 对于单并发的小作业，建议整个集群的作业总数不超过100个；
- 对于复杂作业，建议单作业最大并发数不超过512，64个并发的中等规模作业单集群不多于32个。否则可能会出现心跳超时等问题影响集群稳定性。此时，您需要增大心跳间隔和心跳超时时间；
- 如果您需要同时运行更多的任务，则需要增加 Session 集群的资源配置；

## 其他数据源环境配置

Dinky 虽然是在 Flink 基础之上开发而言，为了让用户一站式开发，扩展了额外的数据源。极大的方便了用户进行 FlinkSQL 的编写。

如何创建外部数据源，请参考注册中心的[数据源管理](/zh-CN/administrator-guide/registerCenter/datasource_manage.md)。当前 Dinky 支持的数据源请参考功能扩展中的[扩展数据源](/zh-CN/extend/datasource.md)