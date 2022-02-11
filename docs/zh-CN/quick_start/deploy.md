## 单机部署

### 解压到指定目录

Dlink 不依赖任何外部的 Hadoop 或者 Flink 环境，可以单独部署在 flink、 hadoop 和 K8S 集群之外，完全解耦，支持同时连接多个不同的集群实例进行运维。

```
tar -zxvf dlink-release-0.5.0-SNAPSHOT.tar.gz
mv dlink-release-0.5.0-SNAPSHOT dlink
cd dlink
```

说明:安装目录根据自身情况而定。

### 初始化数据库

Dlink采用mysql作为后端的存储库，mysql支持5.6+。这里假设你已经安装了mysql。首先需要创建Dlink的后端数据库，这里以配置文件中默认库创建。

```
#登录mysql
mysql -uroot -proot@123
#授权并创建数据库
mysql> grant all privileges on *.* to 'dlink'@'%' identified by 'dlink' with grant option;
mysql> grant all privileges on *.* to 'dlink'@'fdw1' identified by 'dlink'  with grant option;
mysql> flush privileges;
#此处用dlink用户登录
mysql -h fdw1  -udlink -pdlink
mysql> create database dlink;
```

在dlink根目录sql文件夹下有2个sql文件，分别是dlink.sql和dlink_history.sql。如果第一次部署，可以直接将dlink.sql文件在dlink数据库下执行。（如果之前已经建立了 dlink 的数据库，那 dlink_history.sql 存放了各版本的升级 sql ，根据版本号按需执行即可） 

```
#首先登录mysql
mysql -h fdw1  -udlink -pdlink
mysql> use dlink;
mysql> source /opt/dlink/sql/dlink.sql
```

### 配置文件

创建好数据库后，就可以修改dlink连接mysql的配置文件啦，根据个人情况修改。配置文件比较简单，这里就不多说了。

```
#切换目录
cd /opt/dlink/config/
vim application.yml
```

配置文件修改好后，下一步就是安装nginx。如果已安装nginx可以忽略。因为Dlink部署需要nginx，所以先要部署nginx，配置完成后，才能正常启动。

执行完成后，接下来，部署nginx服务。

### 部署nginx

在linux，首先要配置好相应的yum库，因为在安装过程中没有配置，这里可以大概讲述下步骤，可以选择连接网络或者本地yum源都可以，这里选择连接网络方式配置。

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

说明:Dinky-0.5.0以上版本部署nginx可选

### nginx中配置dlink

如果是yum源安装的nginx，配置文件在etc下，如果是源码包安装，请自行找到配置文件

```
#切换到nginx配置目录
cd /etc/nginx/
```

vim /etc/nginx/nginx.conf打开配置文件，修改server中的内容，其中server中的内容按照如下配置即可。

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
- location / 这里可以指定为绝对路径

红色部分就是所修改的地方

![img](https://uploader.shimo.im/f/oMgh98EYnLQLfoD6.png!thumbnail?accessToken=eyJhbGciOiJIUzI1NiIsImtpZCI6ImRlZmF1bHQiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJhY2Nlc3NfcmVzb3VyY2UiLCJleHAiOjE2NDQ0ODA0NjgsImciOiJLd3Zwd0NnV2gzSFZnRDlDIiwiaWF0IjoxNjQ0NDgwMTY4LCJ1c2VySWQiOjE2NTQ4MzA4fQ.e31W51Lq59fEeB-ZQREcAIYIiJWHsQVXGRkMcyMgLFI)

配置完成后，保存退出。并重启nginx并重新加载生效

```
$systemctl restart nginx.service
$systemctl reload nginx.service
#查看nginx是否配置成功
nginx -t
$nginx -s reload
```

### 加载依赖
Dinky具备自己的 Flink 环境，该 Flink 环境的实现需要用户自己在Dinky 根目录下创建 plugins 文件夹并上传相关的 Flink 依赖，如 flink-dist, flink-table 等，具体请阅 Readme（后续的扩展依赖也放到该目录下）。当然也可在启动文件中指定 FLINK_HOME，但不建议这样做。  

Dinky当前版本的yarn的perjob与application执行模式依赖Flink-shade-hadoop去启动，需要额外添加Flink-shade-hadoop 包。

```
#创建目录
cd /opt/dlink/
mkdir plugins
```
将Flink-shade-hadoop上传到到plugins文件目录下,对于使用hadoop2 或者 hadoop 3 的均可使用  flink-shade-hadoop-3 地址如下：
```
https://mvnrepository.com/artifact/org.apache.flink/flink-shaded-hadoop-3-uber?repo=cloudera-repos
```
解压后结构如上所示，修改配置文件内容。lib 文件夹下存放 dlink 自身的扩展文件，plugins 文件夹下存放 flink 及 hadoop 的官方扩展文件（ 如果plugins下引入了flink-shaded-hadoop-3-uber 或者其他可能冲突的jar，请手动删除内部的 javax.servlet 等冲突内容）。其中 plugins 中的所有 jar 需要根据版本号自行下载并添加，才能体验完整功能，当然也可以放自己修改的 Flink 源码编译包。extends 文件夹只作为扩展插件的备份管理，不会被 dlink 加载。

请检查 plugins 下是否添加了 flink 对应版本的 flink-dist,flink-table,flink-shaded-hadoop-3-uber 等如上所示的依赖！！！

请检查 plugins 下是否添加了 flink 对应版本的 flink-dist,flink-table,flink-shaded-hadoop-3-uber 等如上所示的依赖！！！

请检查 plugins 下是否添加了 flink 对应版本的 flink-dist,flink-table,flink-shaded-hadoop-3-uber 等如上所示的依赖！！！

如果plugins下引入了flink-shaded-hadoop-3-uber 的jar，请手动删除内部的 javax.servlet 后既可以访问默认 8888 端口号（如127.0.0.1:8888），正常打开前端页面。

如果是CDH及HDP使用开源flink-shade对Dlink没有任何影响，其他用到的依赖取决于CDH或者HDP与开源版本的兼容性，需要自行根据兼容性添加依赖即可正常使用 Dlink 的所有功能。

### 启动Dlink

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

看到如下界面说明Dlink部署成功

![img](https://uploader.shimo.im/f/gdvs70SrmbWCdJRx.png!thumbnail?accessToken=eyJhbGciOiJIUzI1NiIsImtpZCI6ImRlZmF1bHQiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJhY2Nlc3NfcmVzb3VyY2UiLCJleHAiOjE2NDQ0ODA3NjAsImciOiJLd3Zwd0NnV2gzSFZnRDlDIiwiaWF0IjoxNjQ0NDgwNDYwLCJ1c2VySWQiOjE2NTQ4MzA4fQ.GmZh-wGeO5dBQ-2Cz_5EzwSsKmNJML3wX2_Tj9s8fxw)

默认用户名/密码:admin/admin

如果访问失败，请检查防火墙是否关闭、Nginx的配置是否正确



## 高可用部署

敬请期待





