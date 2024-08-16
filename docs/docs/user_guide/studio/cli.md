---
sidebar_position: 1
position: 1
id: cli
title: 连接Fink SQL Cli
---


![1.png](https://imgos.cn/2024/08/16/66beafa63a49a.png)


# 开始
## 部署flink
为了简化教程，本次我们之间部署单机Flink版本，实际使用过程中可自行连接你自己的集群
> dinky 1.1.0版本目前无法支持flink 1.19连接 cli，1.1.1已修复


1. 下载flink
```bash
wget https://archive.apache.org/dist/flink/flink-1.17.2/flink-1.17.2-bin-scala_2.12.tgz
```
2. 解压flink
```bash
tar -zxvf flink-1.17.2-bin-scala_2.12.tgz
```
3. 启动flink
```bash
cd flink-1.17.2
./bin/start-cluster.sh 

Starting cluster.
Starting standalonesession daemon on host DESKTOP-S65S3F5.
Starting taskexecutor daemon on host DESKTOP-S65S3F5.
```

## 准备依赖
连接flink sql cli需要两个jar包
```bash
-rw-r--r-- 1 root root 931K Aug  7 20:43 flink-sql-client-1.17.2.jar
-rw-r--r-- 1 root root 206K Aug  7 20:43 flink-sql-gateway-1.17.2.jar
```
他们在flink安装目录 opt文件夹下面，属于可选组件，所以dinky docker内默认没有集成，我们需要放进去

执行以下命令建立目录并放置我们的依赖
```bash
cd ..
mkdir /data
mkdir /data/flink
cp flink-1.17.2/opt/flink-sql-client-1.17.2.jar /data/flink
cp flink-1.17.2/opt/flink-sql-gateway-1.17.2.jar /data/flink
```
## 部署Dinky
dinky 1.1.0发布了docker包，我们可以通过docker 快速部署
```bash
# 下载镜像
docker pull dinkydocker/dinky-standalone-server:1.1.0-flink1.17
# 启动服务
docker run --restart=always -p 8888:8888 \
  --name dinky \
  -v /data/flink:/opt/dinky/customJar/ \ 
  dinkydocker/dinky-standalone-server:1.1.0-flink1.17
```
参数解释
> `-p 8888:8888 `
>
> dinky默认8888端口
>
> `-v /data/flink:/opt/dinky/customJar/`
>
> docker版dinky为我们预留了自定义依赖入口，只需要把我们刚刚建立的目录映射到容器内/opt/dinky/customJar/路径即可

访问http://localhost:8888/ 即可看见dinky页面

默认账号：admin

默认密码：dinky123!@#


![login](http://pic.dinky.org.cn/dinky/docs/zh-CN//fast-guide-login.png)

## 注册集群
登录Dinky之后，依次点击`注册中心`，`集群实例`，`新建`

集群名称随便填，类型选择Standalone，JobManager地址填写我们刚刚启动的Flink地址`localhost:8081` ，然后点击保存。

![2.png](https://imgos.cn/2024/08/16/66beb076774b1.png)

保存成功后，如图所示，显示状态正常即可，如果状态异常，则表示集群地址填写有误，请检查

![3.png](https://imgos.cn/2024/08/16/66beb0a7ca956.png)
## 连接SQL CLI

回到数据开发页面，点击左侧SQL CLI按钮，在弹出页面选择我们刚刚添加的集群

![4.png](https://imgos.cn/2024/08/16/66beb0db805f1.png)

点击连接

铛铛铛！连接成功！然后我们就可以在cli内尽情玩耍啦。

![1.png](https://imgos.cn/2024/08/16/66beafa63a49a.png)
