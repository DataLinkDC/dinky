---
sidebar_position: 1
id: quick_experience
title: 快速体验
---
## Docker 快速体验

通过 dinky-mysql-server 和 dinky-standalone-server 镜像快速体验 Flink 实时计算平台。

### 环境准备

需要 `Docker 1.13.1+`。

### 启动 dinky-mysql-server 镜像

启动该镜像提供 Dinky 的 Mysql 业务库能力。

```sh
docker run --name dinky-mysql dinkydocker/dinky-mysql-server:0.7.2
```

见以下内容证明启动成功：

```java
2023-03-08T12:04:23.520202Z 0 [Note] mysqld: ready for connections.
Version: '5.7.41'  socket: '/var/run/mysqld/mysqld.sock'  port: 3306  MySQL Community Server (GPL)
```

### 启动 dinky-standalone-server 镜像

启动该镜像提供 Dinky 实时计算平台。

```sh
docker run --restart=always -p 8888:8888 -p 8081:8081  -e MYSQL_ADDR=dinky-mysql:3306 --name dinky --link dinky-mysql:dinky-mysql dinkydocker/dinky-standalone-server:0.7.2-flink14
```

见以下内容证明启动成功：

```java
Dinky pid is not exist in /opt/dinky/run/dinky.pid
FLINK VERSION : 1.14
........................................Start Dinky Successfully........................................
........................................Restart Successfully........................................
```

:::tip 说明
如果 `docker image` 需要加速，请把 `dinkydocker` 替换成 `registry.cn-hangzhou.aliyuncs.com/dinky`
:::

## 入门示例

### 创建作业

`IP:8888` 地址打开平台并 `admin/admin` 登录，创建 `功能示例` 目录，创建 `HelloWorld` 的 FlinkSQL 作业。

执行模式选择 `Local` 并输入以下语句：

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

![image-20230308222328163](http://pic.dinky.org.cn/dinky/docs/zh-CN/quick_start/docker/helloword.png)

### 调试查询

点击 `执行按钮`（执行当前的SQL），下方切换至 `结果` 选项卡，点击 `获取最新数据` ，即可查看 Select 语句的执行结果。

![image-20230308222416050](http://pic.dinky.org.cn/dinky/docs/zh-CN/quick_start/docker/selecttable.png)

## 结束语

至此，FlinkSQL 的丝滑之旅已经开始，还不赶快邀请朋友一起探索这奇妙的 Dinky 实时计算平台。更多用法请看官网文档（ www.dlink.top ）。