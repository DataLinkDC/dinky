---
sidebar_position: 2
position: 2
id: docker_deploy
title: Docker 部署
---

:::danger 注意

Dinky v1.0.0 的 Docker 镜像正在开发中，敬请期待。以下步骤目前由于镜像未发布，无法使用。请耐心等待。如您对 Docker 镜像有兴趣，欢迎加入我们的开发群，一起参与开发。

:::


## Docker 快速使用教程
本教程使用三种不同的方式通过 Docker 完成 Dinky 的部署，如果你想要快速体验，推荐使用 standalone-server 镜像， 如果你想要体验比较完整的服务，推荐使用 docker-compose 启动服务。如果你已经有自己的数据库服务 你想要沿用这些基础服务，你可以参考沿用已有的 Mysql 、 Hadoop 和 Flink  服务完成部署。

## 前置条件
- Docker 1.13.1+
- Docker Compose 1.28.0+

##  启动服务

### 使用 standalone-server 镜像
使用 standalone-server 镜像启动一个 Dinky standalone-server 容器应该是最快体验 Dinky 的方法。通过这个方式 你可以最快速的体验到 Dinky 的大部分功能，了解主要和概念和内容。

```shell
# 启动 dinky mysql 镜像服务
docker run --name dinky-mysql dinkydocker/dinky-mysql-server:0.7.2

# 启动 dinky 镜像服务
docker run --restart=always -p 8888:8888 -p 8081:8081  -e MYSQL_ADDR=dinky-mysql:3306 --name dinky --link dinky-mysql:dinky-mysql dinkydocker/dinky-standalone-server:0.7.2-flink14


#注意：如果你有 mysql 服务，请执行对应版本的 SQL 文件。假如你的 mysql地址为 10.255.7.3 端口为33006，执行命令如下

docker run --restart=always -p 8888:8888 -p 8081:8081  -e MYSQL_ADDR=10.255.7.3:3306 --name dinky dinkydocker/dinky-standalone-server:0.7.2-flink14

```

:::tip 说明
如果 `docker image` 需要加速，请把 `dinkydocker` 替换成 `registry.cn-hangzhou.aliyuncs.com/dinky`

默认用户名/密码: admin/dinky123!@# ,如需修改,请使用默认用户名/密码登录后,在`认证中心`->`用户`中修改
:::

### 环境变量
* MYSQL_ADDR ： mysql地址，如 127.0.0.1:3306
* MYSQL_DATABASE ： 数据库名
* MYSQL_USERNAME ： 用户名
* MYSQL_PASSWORD ： 密码

---
### 使用docker-compose 

#### 本地docker-compose
在开发环境,在完成mvn package的情况下
```shell
./mvnw -B clean package -Dmaven.test.skip=true -Dspotless.check.skip=true -P prod,scala-2.12,flink-all,web,fast
```

可使用
```shell
docker compose --profile standalone -f docker-compose.yml -f docker-compose.dev.yml up
```
进行docker镜像的构建及运行(/docker/.env文件配置相关环境变量).

如果需要前后端分离, 可使用
```shell
docker compose --profile ms -f docker-compose.yml -f docker-compose.dev.yml up
```
(适配自身nginx的docker/web/default.conf配置)
对于1.15上版本,需要手动将容器中/opt/diny/plugin/flink{version}/flink-table-planner-loader*.jar移除,
替换为相应版本的flink-table-planner_*.jar文件.
创建容器时,可映射到容器/opt/diny/customJar文件夹,添加自定义jar包.

欢迎补充
