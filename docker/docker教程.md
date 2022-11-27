# docker教程
## 镜像构建（可选）
基于Dinky每次发布的[release](http://www.dlink.top/download/download)构建：
1. 在当前目录下新建`dinky-release`目录
2. 解压release包，将里面的文件复制到`dinky-release`目录下
```bash
dinky-release
- config
- jar
- lib
- plugins
- sql
- auto.sh
```
3. 执行docker镜像构建命令，参考命令如下：
```bash
docker build --tag ylyue/dinky:0.7.0 .
docker tag ylyue/dinky:0.7.0 registry.cn-beijing.aliyuncs.com/yue-open/dinky:0.7.0 # 可选
```

[👉已构建的dockerhub仓库](https://hub.docker.com/r/ylyue/dinky)

## 镜像发布（可选）
1. 推送到dockerhub公共仓库
```bash
docker push ylyue/dinky:0.7.0
```
2. 推送到阿里云私有仓库（可选）
```bash
docker push registry.cn-beijing.aliyuncs.com/yue-open/dinky:0.7.0
```

[👉已构建的dockerhub仓库](https://hub.docker.com/r/ylyue/dinky)

## 镜像部署
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

### 其他
Dinky与Flink：
- 此镜像仓库只是让你部署起了Dinky开发平台，因此你任然还需部署flink集群
- flink集群部署参考[flink官方文档](https://nightlies.apache.org/flink/flink-docs-release-1.15/zh/docs/deployment/resource-providers/standalone/docker/)
