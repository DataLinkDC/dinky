---
sidebar_position: 1
id: quick_experience
title: 快速体验
---
## Docker 快速开始

:::danger 注意

Dinky v1.0.0 的 Docker 镜像正在开发中，敬请期待。以下步骤目前由于镜像未发布，无法使用。请耐心等待。如您对 Docker 镜像有兴趣，欢迎加入我们的开发群，一起参与开发。

:::

如果您是第一次接触 Dinky，我们推荐您使用 Docker 快速体验 Dinky 的功能。

### 环境准备

需要 `Docker 1.13.1+`。

### 启动 dinky-standalone-server 镜像

启动该镜像提供 Dinky 实时计算平台。

```sh
docker run -p 8888:8888 -p 8081:8081 --name dinky dinkydocker/dinky-standalone-server:1.0.0-flink16
```
参数解释：
> docker需要开放容器内 8888 ，8081 端口到宿主机，8888为后端服务端口，8081为前端服务端口

> 默认使用h2作为数据库，开箱即用，您不需要额外附加数据库。仅限于快速体验，生产环境请使用MySQL或Postgres，具体请参考Docker部署章节

Docker启动成功后，在浏览器里输入地址http://ip:8081，看到以下界面，说明Dinky启动成功。
![login](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-login.png)

默认登录账号：admin

默认登录密码：admin

## 创建你的第一个Flink任务
Dinky支持多种任务开发，本章节会指导你如何使用Local模式快速创建一个FlinkSQL任务。
### 创建作业

登录Dinky后，进入数据开发页面，点击**目录**，右键新建作业，选择**FlinkSQL**作业类型，填写作业名称，点击**确定**。

右侧作业配置页面，执行模式选择**Local**模式，并行度选择**1**。

输入以下Flink语句：

```sql
CREATE TABLE Orders (
    order_number BIGINT,
    price        DECIMAL(32,2),
    buyer        ROW<first_name STRING, last_name STRING>,
    order_time   TIMESTAMP(3)
) WITH (
  'connector' = 'datagen',
  'rows-per-second' = '1',
  'number-of-rows' = '50'
);
select order_number,price,first_name,last_name,order_time from Orders 
```

![](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-preview.png)

### 预览查询结果

点击右上角 `预览按钮`，会启动local集群并执行任务，下方控制台会实时显示运行日志，提交成功后会切换到`结果选项卡`，点击 `获取最新数据` ，即可查看 Select 语句的执行结果。

![](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-preview-result.png)
:::tip 说明
预览功能只支持select语句查询结果(目前不支持Application与Prejob预览功能)，如果您是正常的带有insert的FlinkSql作业，请点击`执行按钮`
:::

### 任务提交
预览功能仅适用于debug，方便开发时查看数据，对于线上作业，我们需要使用`执行按钮`提交任务到集群。

修改FlinkSql语句
```sql
--创建源表datagen_source
CREATE TABLE datagen_source(
  id  BIGINT,
  name STRING
) WITH (
  'connector' = 'datagen'
);
--创建结果表blackhole_sink
CREATE  TABLE blackhole_sink(
   id  BIGINT,
   name STRING
) WITH (
  'connector' = 'blackhole'
);
--将源表数据插入到结果表
INSERT INTO blackhole_sink
SELECT
   id  ,
   name 
from datagen_source;
```
点击提交按钮，即可提交任务到集群
### 作业运维
任务提交成功后，我们可以进入运维中心页面。
![](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-devops.png)
找到我们的作业，点击**详情按钮**，即可查看作业的运行状态，日志，监控等信息。
![](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-job-detail.png)

## 写在最后
至此，您已经了解了基础DInky使用流程，但Dinky的能力远不止于此，您可以继续阅读其他文档，了解更多Dinky的功能，尽享Dinky为你带来的丝滑开发体验