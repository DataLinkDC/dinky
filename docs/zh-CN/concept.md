<big>**JobManager**</big>

JobManager 作为 Dinky 的作业管理的统一入口，负责 Flink 的各种作业执行方式及其他功能的调度。

<big>**Executor**</big>

Executor 是 Dinky 定制的 FlinkSQL 执行器，来模拟真实的 Flink 执行环境，负责 FlinkSQL 的 Catalog 管理、UDF管理、片段管理、配置管理、语句集管理、语法校验、逻辑验证、计划优化、生成 JobGraph、本地执行、远程提交、SELECT 及 SHOW 预览等核心功能。

<big>**Interceptor**</big>

Interceptor 是 Dinky 的 Flink 执行拦截器，负责对其进行片段解析、UDF注册、SET 和 AGGTABLE 等增强语法解析。

<big>**Gateway**</big>

Gateway 并非是开源项目 flink-sql-gateway，而是 Dinky 自己定制的 Gateway，负责进行基于 Yarn 环境的任务提交与管理，主要有Yarn-Per-Job 和 Yarn-Application  的 FlinkSQL 提交、停止、SavePoint 以及配置测试，而 User Jar 目前只开放了 Yarn-Application 的提交。

<big>**Flink SDK**</big>

Dinky 主要通过调用 flink-client 和 flink-table 模块进行二次开发。

<big>**Yarn SDK**</big>

Dinky 通过调用 flink-yarn 模块进行二次开发。

<big>**Flink API**</big>

Dinky 也支持通过调用 JobManager 的 RestAPI 对任务进行管理等操作，系统配置可以控制开启和停用。

<big>**Local**</big>

Dinky 自身的 Flink 环境，通过 plugins 下的 Flink 依赖进行构建，主要用于语法校验和逻辑检查、生成 JobPlan 和 JobGraph、字段血缘分析等功能。
注意：目前请不要用该模式执行或提交流作业，将会无法关闭，需要重启进程才可。

<big>**Standalone**</big>

Dinky 通过已注册的 Flink Standalone 集群实例可以对远程集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。

<big>**Yarn-Session**</big>

Dinky 通过已注册的 Flink Yarn Session 集群实例可以对远程集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。

<big>**Yarn-Per-Job**</big>

Dinky 通过已注册的集群配置来获取对应的 YarnClient 实例，然后将 Local 模式解析生成的 JobGraph 与 Configuration 提交至 Yarn 来创建 Flink Per-Job 应用。

<big>**Yarn-Application**</big>

Dinky 通过已注册的集群配置来获取对应的 YarnClient 实例。对于 User Jar，将 Jar 相关配置与 Configuration 提交至 Yarn 来创建 Flink-Application 应用；对于 Flink SQL，Dinky 则将作业 ID 及数据库连接配置作为 Main 入参和 dlink-app.jar 以及 Configuration 提交至 Yarn 来创建 Flink-Application 应用。

<big>**Kubernetes-Session**</big>

Dinky 通过已注册的 Flink Kubernetes Session 集群实例可以对远程集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。
注意需要暴露 NodePort。

<big>**Kubernetes-Application**</big>

Dinky 通过已注册的集群配置来获取对应的 FlinkKubeClient 实例。对于 Flink SQL，Dinky 则将作业 ID 及数据库连接配置作为 Main 入参和定制的 dlink-app.jar 镜像以及 Configuration 提交至 Yarn 来创建 Flink-Application 应用。
注意需要自己打包 dlink-app 镜像，具体见文章。
