---
sidebar_position: 3
id: job_submit
title: 作业提交
---

本文为您介绍如何提交 FlinkSQL 作业至集群，以及 DB SQL提交至数据库。Dinky 提供了三种提交作业的方式，分别是：

- 执行当前SQL
- 异步提交
- 上线下线发布

各提交作业的支持的执行模式及说明如下

| SQL类型  |   提交方式   |                           执行模式                           | 说明                                                        |
| :------: | :----------: | :----------------------------------------------------------: | :---------------------------------------------------------- |
| FlinkSQL | 执行当前SQL  | local<br/> Standalone<br/> Yarn Session<br/>  Kubernetes Session | 适用场景:`调试作业`                                         |
| FlinkSQL |   异步提交   | Standalone<br/> Yarn Session<br/> Yarn Per-job<br/> Yarn Application<br/> Kubernetes Session<br/> Kubernetes Application | 适用场景:`远程提交作业至集群`                               |
| FlinkSQL | 上线下线发布 | Standalone<br/> Yarn Session<br/> Yarn Per-job<br/> Yarn Application<br/> Kubernetes Session<br/> Kubernetes Application | 适用场景:<br/>1.`远程发布作业至集群`<br/>2.`维护及监控作业` |
|  DB SQL  | 执行当前SQL  |                              -                               | 适用场景:`调试作业`                                         |
|  DB SQL  |   异步提交   |                              -                               | 适用场景:`提交作业至数据库`                                 |



**执行当前SQL如下**

![execute_current_sql](http://www.aiwenmo.com/dinky/docs/administrator_guide/studio/job_ops/job_submit/execute_current_sql.png)



**异步提交**

![async_commit](http://www.aiwenmo.com/dinky/docs/administrator_guide/studio/job_ops/job_submit/async_commit.png)



**上线下线发布**

![publish](http://www.aiwenmo.com/dinky/docs/administrator_guide/studio/job_ops/job_submit/publish.png)



:::tip 说明

- FlinkSQL 执行当前SQL与异步提交区别在于执行当前SQL适合调试作业，而异步提交适合提交作业到集群；
- FlinkSQL 异步提交与上线下线发布，异步提交无法接收告警，而上线下线发布可以接收告警并可以维护作业和在运维中心监控作业；
- DB SQL 执行当前SQL与异步提交区别在于执行当前SQL适合调试作业并可以看到错误信息，而异步提交只显示作业提交失败；

:::
