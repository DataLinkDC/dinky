#### 登录

超级管理员：admin/admin；

新增用户：默认密码 123456。

#### 集群中心

注册 Flink 集群地址时，格式为 host:port ，用英文逗号分隔。即添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081。新增和修改的等待时间较长，是因为需要检测最新的 JobManager 地址。心跳检测为手动触发，会更新集群状态与 JobManager 地址。

#### Studio

1. 在左侧目录区域创建文件夹或任务。
2. 在中间编辑区编写 FlinkSQL 。
3. 在右侧配置作业配置和执行参数。
4. Fragment 开启后，可以使用增强的 sql 片段语法：

```sql
sf:=select * from;tb:=student;
${sf} ${tb}
##效果等同于
select * from student
```

5. 内置 sql 增强语法-表值聚合：

```sql
CREATE AGGTABLE aggdemo AS
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```

6. MaxRowNum 为批流（Session模式下）执行Select时预览查询结果的最大集合长度，默认 100，最大 9999。
7. SavePoint策略支持最近一次、最早一次、指定一次三种策略。
8. Flink 共享会话共享 Catalog ，会话的使用需要在左侧会话选项卡手动创建并维护。
9. 连接器为 Catalog 里的表信息，清空按钮会销毁当前会话。
10. Local 模式主要用于语法校验、血缘分析、执行图预览等功能，当然也可执行任务，但目前版本建议请使用远程集群来执行任务。
11. 执行 SQL 时，如果您选中了部分 SQL，则会执行选中的内容，否则执行全部内容。
12. 小火箭的提交功能是异步提交当前任务已保存的 FlinkSQL 及配置到集群。由于适用于快速提交稳定的任务，所以无法提交草稿，且无法预览数据。
13. 执行信息或者历史中那个很长很长的就是集群上的 JobId 或者 APPID，任务历史可以查看执行过的任务的数据回放。
14. 草稿是无法被异步远程提交的，只能同步执行，且无法保存。
15. Studio 的布局可以随意拖动，但由于是实时计算，联动较多，请温柔些。
16. 同步执行时可以自由指定任务名，异步提交默认为作业名。
17. 支持 set 语法设置 Flink 的执行配置，其优先级大于右侧的配置。
18. 支持远程集群查看、SavePoint 及停止任务。
19. 支持自定义及上下文的 sql 函数或片段的自动补全，通过函数文档维护。
20. 支持 Flink 所有官方的连接器及插件的扩展，但需注意版本号适配。
21. 使用 IDEA 进行源码调试时，需要在 admin 及 core 下修改相应 pom 依赖的引入来完成功能的加载。
22. 支持基于 StreamGraph 的可执行 FlinkSql （Insert into）的血缘分析，无论你的 sql 有多复杂或者多 view。
23. Dlink 目前提交方式支持 Standalone 、Yarn Session、Yarn PerJob、Yarn Application，K8S 后续支持。
24. Dlink 目前对于 Flink 多版本的支持只能一个 Dlink 实例支持一个 Flink 版本，未来将开源同时支持多版本的能力。
25. 使用 Yarn PerJob、Yarn Application 需要配置集群配置，且其自动注册的集群实例需要手动点击回收。
26. 其他内容后续更新。。。

## 运行截图

> 登录页

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/login.png)

> 首页

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/welcome.png)

> Studio SQL 开发提示与补全

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/sqldev.png)

> Studio 语法和逻辑检查

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/sqlcheck.png)

> Studio 批流SELECT预览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/selectpreview.png)

> Studio 异常反馈

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/sqlerror.png)

> Studio 进程监控

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/process.png)

> Studio 执行历史

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/history.png)

> Studio 数据回放

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/datashow.png)

> Studio SavePoint 管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/savepoint.png)

> Studio 血缘分析

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/ca.png)

> Studio 函数浏览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/function.png)

> Studio 共享会话

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/session.png)

> 集群管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/cluster.png)

> 集群配置管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/clusterconfiguration.png)

> 数据源管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/db.png)
