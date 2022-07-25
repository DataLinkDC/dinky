---
sidebar_position: 2
id: deploy
title: 部署
---


## Linux 单机部署

### 解压到指定目录

Dinky 不依赖任何外部的 Hadoop 或者 Flink 环境，可以单独部署在 flink、 hadoop 和 K8S 集群之外，完全解耦，支持同时连接多个不同的集群实例进行运维。

```
tar -zxvf dlink-release-{version}.tar.gz
mv dlink-release-{version} dlink
cd dlink
```

### 初始化数据库

Dinky 采用 mysql 作为后端的存储库，mysql 支持 5.7+。这里假设你已经安装了 mysql 。首先需要创建 Dinky 的后端数据库，这里以配置文件中默认库创建。

```
#登录mysql
mysql -uroot -proot@123
#授权并创建数据库
mysql> grant all privileges on *.* to 'dlink'@'%' identified by 'dlink' with grant option;
mysql> grant all privileges on *.* to 'dlink'@'fdw1' identified by 'dlink'  with grant option;
mysql> flush privileges;
#此处用 dlink 用户登录
mysql -h fdw1  -udlink -pdlink
mysql> create database dlink;
```

在 Dinky 根目录 sql 文件夹下有 2 个 sql 文件，分别是 dlink.sql 和 dlink_history.sql。如果第一次部署，可以直接将 dlink.sql 文件在 dlink 数据库下执行。（如果之前已经建立了 dlink 的数据库，那 dlink_history.sql 存放了各版本的升级 sql ，根据版本号及日期按需执行即可） 

```
#首先登录 mysql
mysql -h fdw1  -udlink -pdlink
mysql> use dlink;
mysql> source /opt/dlink/sql/dlink.sql
```

### 配置文件

创建好数据库后，修改 Dinky 连接 mysql 的配置文件。

```
#切换目录
cd /opt/dlink/config/
vim application.yml
```

### 部署 nginx（可选）

在 linux，首先要配置好相应的 yum 库，因为在安装过程中没有配置，这里可以大概讲述下步骤，可以选择连接网络或者本地 yum 源都可以，这里选择连接网络方式配置。

```
#下载yum源
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
#清除缓存
yum makecache
#接下来安装nginx
yum -y install epel-release
yum -y install nginx
sudo systemctl enable nginx
sudo service nginx start
sudo service nginx reload
nginx -v
#最后查看进程看是否正常启动
ps -ef|grep nginx
```

说明: Dinky-0.5.0 以上版本部署 nginx 为可选

### nginx中配置 Dinky（可选）

如果是 yum 源安装的 nginx，配置文件在 etc 下，如果是源码包安装，请自行找到配置文件

```
#切换到nginx配置目录
cd /etc/nginx/
```

vim /etc/nginx/nginx.conf 打开配置文件，修改 server 中的内容，其内容按照如下配置即可。

```
  server {
        listen       9999;
        #listen       [::]:80;
        server_name  bigdata3;
        root         /usr/share/nginx/html;
                gzip on;
     		gzip_min_length 1k;
		gzip_comp_level 9;
		gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
		gzip_vary on;
		gzip_disable "MSIE [1-6]\.";

        # Load configuration files for the default server block.
        include /etc/nginx/default.d/*.conf;
        
        location / {
            root   html;
            index  index.html index.htm;
			try_files $uri $uri/ /index.html;
        }
        error_page 404 /404.html;
        location = /404.html {
        }

        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
        }
        
        location ^~ /api {
            proxy_pass http://192.168.0.0:8888;
            proxy_set_header   X-Forwarded-Proto $scheme;
            proxy_set_header   X-Real-IP         $remote_addr;
        }
    }
```

修改内容:

-  listen 监听端口；
-  存放html路径；
-  location / 这里可以指定为绝对路径

配置完成后，保存退出。并重启 nginx 并重新加载生效

```
$systemctl restart nginx.service
$systemctl reload nginx.service
#查看nginx是否配置成功
nginx -t
$nginx -s reload
```

### 加载依赖

Dinky 需要具备自身的 Flink 环境，该 Flink 环境的实现需要用户自己在 Dinky 根目录下创建 plugins 文件夹并上传相关的 Flink 依赖，如 flink-dist, flink-table 等，具体见下文。当然也可在启动文件中指定 FLINK_HOME，但不建议这样做。  

:::warning  注意事项
Dinky 当前版本的 yarn 的 perjob 与 application 执行模式依赖 flink-shade-hadoop ，如果你的 Hadoop 版本为 2+ 或 3+，需要额外添加 flink-shade-hadoop-uber-3 包，请手动删除该包内部的 javax.servlet 等冲突内容。
当然如果你的 Hadoop 为 3+ 也可以自行编译对于版本的 dlink-client-hadoop.jar 以替代 uber 包，
:::

```
#创建目录
cd /opt/dlink/
mkdir plugins
```
将 flink-shade-hadoop 上传到到 plugins 文件目录下，使用  flink-shade-hadoop-3 地址如下：
```
https://mvnrepository.com/artifact/org.apache.flink/flink-shaded-hadoop-3-uber?repo=cloudera-repos
```

如果是 CDH 及 HDP 使用开源 flink-shade 对 Dinky 没有任何影响，其他用到的依赖取决于 CDH 或者 HDP 与开源版本的兼容性，需要自行根据兼容性添加依赖即可正常使用 Dinky 的所有功能。

最终项目根目录如下，仅供参考：

```shell
config/ -- 配置文件
|- application.yml
extends/ -- 扩展
|- dlink-client-1.11.jar -- 适配 Flink1.11.x
|- dlink-client-1.12.jar -- 适配 Flink1.12.x
|- dlink-client-1.14.jar -- 适配 Flink1.14.x
|- dlink-client-1.15.jar -- 适配 Flink1.15.x
html/ -- 前端编译产物
jar/ 
  |- dlink-app-1.11.jar -- dlink application 模式提交 sql 用到的 jar 适配 Flink1.11.x
  |- dlink-app-1.12.jar -- dlink application 模式提交 sql 用到的 jar 适配 Flink1.12.x
  |- dlink-app-1.13.jar -- dlink application 模式提交 sql 用到的 jar 适配 Flink1.13.x
  |- dlink-app-1.14.jar -- dlink application 模式提交 sql 用到的 jar 适配 Flink1.14.x
  |- dlink-app-1.15.jar -- dlink application 模式提交 sql 用到的 jar 适配 Flink1.15.x
  |- dlink-client-base.jar  -- 整库同步场景下需要的包 
  |- dlink-common.jar -- 整库同步场景下需要的包
lib/ -- 内部组件
|- dlink-alert-dingtalk.jar 
|- dlink-alert-email.jar 
|- dlink-alert-feishu.jar 
|- dlink-alert-wechat.jar 
|- dlink-client-1.13.jar  -- 适配 Flink1.13.x,默认
|- dlink-catalog-mysql.jar -- dlink 的 catalog实现 
|- dlink-connector-jdbc.jar
|- dlink-function.jar
|- dlink-metadata-clickhouse.jar
|- dlink-metadata-doris.jar
|- dlink-metadata-hive.jar
|- dlink-metadata-mysql.jar
|- dlink-metadata-oracle.jar
|- dlink-metadata-phoenix.jar
|- dlink-metadata-postgresql.jar
|- dlink-metadata-sqlserver.jar
plugins/
|- flink-connector-jdbc_2.11-1.13.6.jar
|- flink-csv-1.13.6.jar
|- flink-dist_2.11-1.13.6.jar
|- flink-json-1.13.6.jar
|- flink-shaded-hadoop-3-uber-3.1.1.7.2.1.0-327-9.0.jar
|- flink-shaded-zookeeper-3.4.14.jar
|- flink-table-blink_2.11-1.13.6.jar
|- flink-table_2.11-1.13.6.jar
|- mysql-connector-java-8.0.21.jar
sql/ 
|- dlink.sql -- Mysql初始化脚本
|- dlink_history.sql -- Mysql各版本及时间点升级脚本
|- dlinkmysqlcatalog.sql -- dlink 的 catalog 表 SQL 脚本
auto.sh --启动停止脚本
dlink-admin.jar --主程序包
```

### 启动 Dinky

```
#启动
$sh auto.sh start
#停止
$sh auto.sh stop
#重启
$sh auto.sh restart
#查看状态
$sh auto.sh status
```

默认用户名/密码: admin/admin

:::tip 说明
   Dinky 部署需要 MySQL5.7 以上版本
   
   Dinky 不依赖于 Nginx， Nginx 可选
:::





