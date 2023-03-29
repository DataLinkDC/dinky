# Docker build 文档说明

## 目录说明

```shell
├── docker      -- docker build folder
│   ├── app     -- flink docker
│   ├── mysql   -- mysql docker build
│   ├── server  -- dinky-server build
│   └── web     -- dinky-web build
```

## 前后端分离部署

### 后端

maven build Dinky 后端

```shell
# build flink1.14,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.14,!web

# build flink1.15,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.15,!web
```

#### 不带 flink jar 包构建

> **推荐**
> 
> 此方法构建出的镜像，不包含 flink 的 jar 包，运行时，
> 只要将此目录 `/opt/dinky-docker/plugins` 映射至主机，
> 后续再主机上添加 flink jar 包即可

在项目根目录执行 `docker build`

```shell
docker build -t dinky-server_flink14:0.8.0 -f ./docker/server/Dockerfile ./
```

docker 运行

> 数据库地址和文件映射目录可自行修改

```shell
docker run \
-d \
--restart=always \
-p 8888:8888 \
-p 8081:8081  \
-e MYSQL_ADDR=192.168.2.21:3306 \
-e MYSQL_DATABASE=dinky \
-e MYSQL_USERNAME=dinky \
-e MYSQL_PASSWORD=dinky \
-v /opt/dinky-docker/plugins:/opt/dinky/plugins \
-v /opt/dinky-docker/logs:/opt/dinky/logs \
--name dinky-server_flink1.14 \
dinky-server_flink1.14:0.8.0
```

自行下载 flink tar 包，将 flink 文件下的 lib 目录下的 jar 包放置于主机映射目录下，
本示例的目录为 `/opt/dinky-docker/plugins/flink1.14`


#### 带 flink jar 包部署

> 此方法部署，在每次 docker build 时，都会从互联网拉取 flink 的 jar 包，
> 所以会导致 docker build 变得很慢，还是建议 Dinky 不带 flink 的 jar 包部署，
> 在 Dinky docker 运行时，进行文件映射引入 jar 包

将 `./docker/server/Dockerfile` 文件下的此位置注释移除,将 flink 的版本修改为自己需要的版本

```shell
# download flink file
ADD https://archive.apache.org/dist/flink/flink-1.14.6/flink-1.14.6-bin-scala_2.12.tgz /tmp
RUN tar zxvf /tmp/flink-1.14.6-bin-scala_2.12.tgz -C /opt
RUN cp -r /opt/flink-1.14.6/lib /opt/dinky/plugins/flink${FLINK_VERSION}
```
docker build
```shell
# 在项目根目录下执行
docker build -t dinky-server:0.8.0 -f ./docker/server/Dockerfile ./
```

运行 dinky-server

> 文件映射地址和数据库地址自行修改

```shell
docker run \
-d \
--restart=always \
-p 8888:8888 \
-p 8081:8081  \
-e MYSQL_ADDR=192.168.2.21:3306 \
-e MYSQL_DATABASE=dinky \
-e MYSQL_USERNAME=dinky \
-e MYSQL_PASSWORD=dinky \
-v /opt/dinky-docker/plugins:/opt/dinky/plugins \
-v /opt/dinky-docker/logs:/opt/dinky/logs \
--name dinky-server \
dinky-server:0.8.0
```


> Dinky 的 Flink Jar 包的引入有两种方式：
> 1. Dockerfile 中引入，此种方法会导致 Docker build 的时间加长，因为需要从网络上下载 Flink 的运行包，假如采用此方法，则需要将 `./docker/server/Dockerfile` 文件下的 `download flink file` 部分注释移除
> 2. Dockerfile 中不引入，在 dinky 部署的时候，将 Flink 等 jar 包通过文件映射的方式导入 Dinky 运行环境中

### web 部署

> 版本要求：
> * nodejs: 14.17.0+
> * npm: 7.19.0
> 
> 升级 npm 版本 `npm install npm@7.19.0 -g`
> 
> **在项目的 flink-web 目录下执行**

npm install

```shell
npm install --force
```

npm build

```shell
npm run build
```
build 完之后,web 目录下将会出现 `dist` 目录

docker build web端,在项目根目录下执行

```shell
docker run \
-d \
--restart=always \
-p 80:80 \
-e API_ORIGIN=192.168.2.21:8888\
--name dinky-web \
dinky-web:0.8.0
```
> `API_ORIGIN` 为 dinky-server 后端地址 

## 一体部署

maven build Dinky 后端

```shell
# build flink1.14,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.14

# build flink1.15,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.15
```

在项目根目录执行 docker build

```shell
docker build -t dinky-server_flink14:0.8.0 -f ./docker/Dockerfile ./
```

运行 dinky-server

> 文件映射地址和数据库地址自行修改

```shell
docker run \
-d \
--restart=always \
-p 8888:8888 \
-e MYSQL_ADDR=192.168.2.21:3306 \
-e MYSQL_DATABASE=dinky \
-e MYSQL_USERNAME=dinky \
-e MYSQL_PASSWORD=dinky \
-v /opt/dinky-docker/plugins:/opt/dinky/plugins \
-v /opt/dinky-docker/logs:/opt/dinky/logs \
--name dinky-server \
dinky-server:0.8.0
```