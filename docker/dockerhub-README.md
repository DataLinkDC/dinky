## 介绍
Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑
- 一站式 FlinkSQL & SQL DataOps
- 基于 Apache Flink 二次开发，无侵入，开箱即用
- 实时即未来，批流为一体

Dinky 是一个开箱即用的一站式实时计算平台，以 Apache Flink 为基础，连接 OLAP 和数据湖等众多框架，致力于流批一体和湖仓一体的建设与实践。

## 镜像仓库说明
这是一个由ylyue发起的开源共建仓库，基于Dinky每次发布的[release](http://www.dlink.top/download/download)构建，在使用此镜像仓库前，
建议你先拥有[Dinky脚本启动方案](http://www.dlink.top/docs/build_deploy/deploy)的踩坑经验，因为此仓库提供的镜像，仅是提供了docker部署方案需要的镜像，而你该踩的坑一个都少不了。

阿里云国内加速地址：registry.cn-beijing.aliyuncs.com/yue-open/dinky

- [👉gitee源码](https://gitee.com/DataLinkDC/Dinky)
- [👉github源码](https://github.com/DataLinkDC/dlink)
- [👉官网](http://www.dlink.top/)

## 使用教程
### docker部署参考命令
```bash
docker run -it --name=dinky -p8888:8888 \ 
 -v /opt/pvc/local/dinky/config:/opt/dinky-release/config \
 registry.cn-beijing.aliyuncs.com/yue-open/dinky:0.7.0
```

docker run -it --name=dinky -p8888:8888 -v /opt/pvc/local/dinky/config:/opt/dinky-release/config ylyue/dinky:0.7.0


<font color=red>**注意：**</font>
1. 上面的部署命令并不能让你成功的启动dinky，因为你还需修改dinky的配置文件，如数据库信息（配置文件路径详见：**挂载点**）
```
spring:
  datasource:
    url: jdbc:mysql://你的数据库地址/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: 你的数据库账号
    password: 你的数据库密码
```
2. 你的数据库仍需初始化，创建dinky所需要的表（因为dinky暂未实现自动初始化表）
  - 表的初始化`SQL文件`位于dinky release解压包中，[👉你可以在这里获得](http://www.dlink.top/download/download)

> mysql数据库的初始化脚本：
> - [👉gitee releases界面](https://gitee.com/DataLinkDC/Dinky/releases)下载对应版本的releases包，获得Mysql初始化脚本
> - [👉Dinky官网releases界面](http://www.dlink.top/docs/build_deploy/deploy)下载对应版本的releases包，获得Mysql初始化脚本
> - mysql需自行部署8.x版本，参考：[👉Centos docker Mysql8 安装与初始化配置](https://blog.csdn.net/u013600314/article/details/80521778?spm=1001.2014.3001.5502)

### 环境变量与挂载点
当前dinky版本，暂不支持`-e`环境变量注入，你可以选择将`/opt/dinky-release/config`路径下的配置文件挂载出来更改。
- `/opt/dinky-release/jar`
- `/opt/dinky-release/lib`
- `/opt/dinky-release/plugins`
上述挂载点你也可以根据需求，选择性的挂载出来进行数据的持久化处理与修改。

## 其他
### 文档版本
当前文档版本0.7.x
- [👉0.6.x](https://github.com/DataLinkDC/dlink/blob/dev/docker/docker教程-0.6.x.md)

### Dinky与Flink部署关系
- 此镜像仓库只是让你部署起了Dinky开发平台，因此你任然还需部署flink集群
- flink集群部署参考[flink官方文档](https://nightlies.apache.org/flink/flink-docs-release-1.15/zh/docs/deployment/resource-providers/standalone/docker/)

### 感谢
感谢Dinky的作者aiwenmo无私开源！！！

欢迎前往，官方QQ社区群：543709668，一起讨论，一起开源共建。
