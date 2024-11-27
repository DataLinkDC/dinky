---
sidebar_position: 1
id: quick_experience
title: 快速开始
---

## Docker 快速开始

如果您是第一次接触 Dinky，我们推荐您使用 Docker 快速体验 Dinky 的功能。

### 独立启动Dinky服务

如果你本地已经安装了 docker，执行以下命令可以一键安装：

```shell
# 拉取镜像
docker pull dinkydocker/dinky-standalone-server:1.1.0-flink1.17
# 运行程序
docker run -p 8888:8888 \
 --name dinky dinkydocker/dinky-standalone-server:1.1.0-flink1.17
```

:::tip 注意
默认使用h2作为数据库，开箱即用，您不需要额外附加数据库。仅限于快速体验，
生产环境请使用MySQL或Postgres，更多参数配置请参考[Docker部署](../../deploy_guide/docker_deploy)
章节获取详细内容，
:::

Docker启动成功后，在浏览器里输入地址http://ip:8888，看到以下界面，说明Dinky启动成功。
> 初始账户    
> 用户名: admin   
> 密码 :dinky123!@#

![login](https://pic.dinky.org.cn/dinky/docs/zh-CN//2024-11-22/fast-guide-login.png)



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

![](https://pic.dinky.org.cn/dinky/docs/zh-CN//2024-11-22/fast-guide-preview.png)

### 预览查询结果

点击右上角 `预览按钮`，会启动local集群并执行任务，下方控制台会实时显示运行日志，提交成功后会切换到`结果选项卡`，点击 `获取最新数据` ，即可查看 Select 语句的执行结果。

![](https://pic.dinky.org.cn/dinky/docs/zh-CN//2024-11-22/fast-guide-preview-result.png)
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
![](https://pic.dinky.org.cn/dinky/docs/zh-CN//2024-11-22/fast-guide-devops.png)
找到我们的作业，点击**详情按钮**，即可查看作业的运行状态，日志，监控等信息。
![](https://pic.dinky.org.cn/dinky/docs/zh-CN//2024-11-22/fast-guide-job-detail.png)


## Nginx 配置
### Dinky 1.0-1.1版本
Dinky使用了SSE技术作为日志推流，如果您使用了nginx代理，需要配置Nginx支持SSE，否则默认Nginx配置会导致大量连接异常，造成页面极其卡顿，
需要在Nginx配置文件中添加以下配置：

```shell
proxy_buffering off;
proxy_cache off;
proxy_read_timeout 86400s;
proxy_send_timeout 86400s;
```
### Dinky 1.2以后版本
Dinky使用了Websocket技术作为日志推流，如果您使用了nginx代理，需要配置Nginx支持Websocket，否则无法使用控制台等功能
需要在Nginx配置文件中添加以下配置：

> 注意，以下为参考配置，并非强制要求标准配置，请根据你的自身情况进行修改

```shell
    location /api/ws/global {
        proxy_pass ${你的后端地址};
        proxy_read_timeout 300s;
        proxy_send_timeout 300s;
        proxy_set_header  Host $http_host;
        proxy_set_header  X-Real-IP  $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header  X-Forwarded-Proto $scheme;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $connection_upgrade;
    }

    location /ws/sql-gateway/ {
        proxy_pass ${你的后端地址};
        proxy_read_timeout 300s;
        proxy_send_timeout 300s;
        proxy_set_header  Host $http_host;
        proxy_set_header  X-Real-IP  $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header  X-Forwarded-Proto $scheme;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $connection_upgrade;
    }

```
在Http节点加入配置支持websocket
```shell
    map $http_upgrade $connection_upgrade {
        default upgrade;
        ''      close;
    }
```

## 写在最后
至此，您已经了解了基础DInky使用流程，但Dinky的能力远不止于此，您可以继续阅读其他文档，了解更多Dinky的功能，尽享Dinky为你带来的丝滑开发体验
