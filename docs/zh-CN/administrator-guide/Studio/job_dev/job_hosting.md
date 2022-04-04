Dinky 做为一站式的实时计算平台，可以托管数据库及 Flink。全托管的 Flink 集群模式支持 local、Session、Per-job 和 Application 四种模式，四种模式有如下区别：

- **local:** Flink自带，无需任何配置，一个简单的 Mini 集群；
- **Session 集群：** 多个作业可以复用相同的JM，可以提高JM资源利用率。因此适用于大量占用资源比较小或任务启停比较频繁的小并发作业，可以有效节约资源开销。Yarn 和 K8S 均适用；
- **Per-job 集群:** 作业之间资源隔离，每个作业都需要一个独立的JM，因为小任务JM的资源利用率较低，因此适用于占用资源比较大或持续稳定运行的作业。Yarn 适用；
- **Application 集群:** 在 Application 模式下，每个作业创建一个集群，这些作业会被视为属于同一个应用，在同一个集群中执行（如果在 Per-Job 模式下，就会启动多个集群）。可见，Application 模式本质上是 Session 和 Per-Job 模式的折衷。Yarn 和 K8S 均适用；

Dinky 上如何托管 Flink 集群，请参考[集群管理](/zh-CN/administrator-guide/registerCenter/cluster_manage.md)

其他数据库的托管请参考[数据源管理](/zh-CN/administrator-guide/registerCenter/datasource_manage.md)