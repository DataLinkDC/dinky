---
sidebar_position: 1
id: docker_deploy
title: Docker部署
---

## Docker 快速使用教程
本教程使用三种不同的方式通过 Docker 完成 DInky 的部署，如果你想要快速体验，推荐使用 standalone-server 镜像， 如果你想要体验比较完成的服务，推荐使用 docker-compose 启动服务。如果你已经有自己的数据库或者 Zookeeper 服务 你想要沿用这些基础服务，你可以参考沿用已有的 Mysql 、 Hadoop 和 Flink  服务完成部署。

## 前置条件
- Docker 1.13.1+
- Docker Compose 1.28.0+

##  启动服务

### 使用 standalone-server 镜像
> 使用 standalone-server 镜像启动一个 Dinky standalone-server 容器应该是最快体验 Dinky 的方法。通过这个方式 你可以最快速的体验到 Dinky 的大部分功能，了解主要和概念和内容。

```shell
## 启动 dinky mysql 镜像服务
docker run --name dinky-mysql registry.cn-hangzhou.aliyuncs.com/dinky/dinky-mysql-server:0.7.0

# 启动 dinky 镜像服务
docker run --restart=always -p 8888:8888 -p 8081:8081  -e MYSQL_ADDR=dinky-mysql:3306 --name dinky --link dinky-mysql:dinky-mysql registry.cn-hangzhou.aliyuncs.com/dinky/dinky-standalone-server:0.7.0-flink14
```
> 注意：如果你有 mysql 服务，请执行对应版本的 SQL 文件。假如你的 mysql地址为 10.255.7.3 端口为33006，执行命令如下

```shell
docker run --restart=always -p 8888:8888 -p 8081:8081  -e MYSQL_ADDR=10.255.7.3:33006 --name dinky registry.cn-hangzhou.aliyuncs.com/dinky/dinky-standalone-server:0.7.0-flink14
```



