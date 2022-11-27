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
docker部署参考命令：
```bash
docker run -it --name=dinky -p8888:8888 \ 
 -e spring.datasource.url=jdbc:mysql://localhost:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true \ 
 -e spring.datasource.username=root \ 
 -e spring.datasource.password=11eb441842a9491c90168c6f76c2eed4 \ 
 -v /opt/docker/dinky/plugins:/opt/dinky/plugins \
 -v /opt/docker/dinky/lib:/opt/dinky/lib \
 -v /opt/docker/dinky/jar:/opt/dinky/jar \
 registry.cn-beijing.aliyuncs.com/yue-open/dinky:0.6.4-flink1.15
```

环境变量与挂载点：
- SpringBoot标准项目，`-e`可以用于替换[application.yml](https://gitee.com/DataLinkDC/Dinky/blob/0.6.4/dlink-admin/src/main/resources/application.yml)文件中的配置
- `/opt/dinky/plugins`挂载点，用于挂载Flink SQL开发中需要依赖的jar包
- `/opt/dinky/lib`挂载点（非必须），用于挂载Dinky内部组件，当你需要时再挂载出来
- `/opt/dinky/jar`挂载点（非必须），用于挂载dlink application模式提交sql用到的jar，当你需要时再挂载出来

mysql数据库的初始化脚本：
- [👉gitee releases界面](https://gitee.com/DataLinkDC/Dinky/releases)下载对应版本的releases包，获得Mysql初始化脚本
- [👉Dinky官网releases界面](http://www.dlink.top/docs/build_deploy/deploy)下载对应版本的releases包，获得Mysql初始化脚本
- mysql需自行部署8.x版本，参考：[👉Centos docker Mysql8 安装与初始化配置](https://blog.csdn.net/u013600314/article/details/80521778?spm=1001.2014.3001.5502)

版本号0.6.4-flink1.15：
- `0.6.4`代表Dinky版本号
- `flink1.15`代表Flink版本号，即默认提供了flink1.15的相关默认依赖，你任然可以替换`plugins、lib、jar`挂载点的相关依赖包，使之支持Flink其他版本，如：flink:1.15-scala_2.12.15

Dinky与Flink：
- 此镜像仓库只是让你部署起了Dinky开发平台，因此你任然还需部署flink集群
- flink集群部署参考[flink官方文档](https://nightlies.apache.org/flink/flink-docs-release-1.15/zh/docs/deployment/resource-providers/standalone/docker/)

## 感谢
感谢Dinky的作者aiwenmo无私开源！！！

欢迎前往，官方QQ社区群：543709668，一起讨论，一起开源共建。
