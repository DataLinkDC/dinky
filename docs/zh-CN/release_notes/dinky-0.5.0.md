## Dinky发行说明

Dinky 0.5.0版本包括对K8S的支持以及数据库,catalog的实现。它还包括对0.4.0 中引入的一些重大更改的修复。

#### 新增功能
- 支持Kubernetes Session 和 Application 模式提交任务
- 新增UDF Java方言的Local模式的在线编写、调试、动态加载
- 新增FlinkSQL 执行环境方言及其应用功能
- 新增BI选项卡的折线图、条形图、饼图
- 新增元数据查看表和字段信息
- 新增ChangLog 和 Table 的查询及自动停止实现
- 新增Mysql,Oracle,PostGreSql,ClickHouse,Doris,Java 方言
- 新增OpenAPI 的执行sql、校验sql、获取计划图、获取StreamGraph、获取预览数据、执行Jar、停止、SavePoint接口
- 新增数据源的 Sql 作业语法校验和语句执行
- 新增快捷键保存、校验、美化等
- 新增引导页
- 新增JobPlanGraph 展示
- 新增SQLServer Jdbc Connector 的实现
- 新增Local 的运行模式选择与分类
- 新增SavePoint 的 restAPI 实现
- 新增编辑器选项卡右键关闭其他和关闭所有
- 新增FlinkSQL 及 SQL 导出
- 新增集群与数据源的 Studio 管理交互
- 新增Yarn 的 Kerboros 验证
- 建立官网文档

#### 修复
- 修改项目名为 Dinky 以及图标
- 优化所有模式的所有功能的执行逻辑
- 升级各版本 Flink 依赖至最新版本以解决核弹问题
- 修复编辑集群配置测试后保存会新建的bug
- 修复登录页报错弹框
- 修复Yarn Application 解析数组异常问题
- 修复自定义Jar配置为空会导致异常的bug
- 修复任务提交失败时注册集群报错的bug
- 修复set在perjob和application模式不生效的问题
- 修复perjob和application模式的任务名无法自定义的问题
- 修复set 语法在1.11和1.12的兼容问题
- 修复血缘分析图由于前端依赖无法正常加载的问题 
