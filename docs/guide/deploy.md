### 从安装包开始

Dlink 可以部署在 Flink 及 Hadoop 集群之外的任意节点，只要网络通就可；部署在集群内部也可。

```
config/ -- 配置文件
|- application.yml
extends/ -- 扩展
|- dlink-client-1.11.jar
|- dlink-client-1.12.jar
|- dlink-client-1.14.jar
html/ -- 前端编译产物，用于Nginx
jar/ -- dlink application模式提交sql用到的jar
lib/ -- 内部组件
|- dlink-client-1.13.jar -- 必需
|- dlink-connector-jdbc.jar
|- dlink-function.jar
|- dlink-metadata-clickhouse.jar
|- dlink-metadata-mysql.jar
|- dlink-metadata-oracle.jar
|- dlink-metadata-postgresql.jar
plugins/
|- flink-connector-jdbc_2.11-1.13.3.jar
|- flink-csv-1.13.3.jar
|- flink-dist_2.11-1.13.3.jar
|- flink-json-1.13.3.jar
|- flink-shaded-hadoop-3-uber-3.1.1.7.2.1.0-327-9.0.jar
|- flink-shaded-zookeeper-3.4.14.jar
|- flink-table-blink_2.11-1.13.3.jar
|- flink-table_2.11-1.13.3.jar
|- mysql-connector-java-8.0.21.jar
sql/ 
|- dlink.sql --Mysql初始化脚本
auto.sh --启动停止脚本
dlink-admin.jar --程序包
```

解压后结构如上所示，修改配置文件内容。lib 文件夹下存放 dlink 自身的扩展文件，plugins 文件夹下存放 flink 及 hadoop 的官方扩展文件。其中 plugins 中的所有 jar 需要根据版本号自行下载并添加，才能体验完整功能，当然也可以放自己修改的 Flink 源码编译包。当然，如果您硬要使用 FLINK_HOME 的话，可以在 `auto.sh` 文件中 `SETTING` 变量添加`$FLINK_HOME/lib` 。extends 文件夹只作为扩展插件的备份管理，不会被 dlink 加载。

在Mysql数据库中创建数据库并执行初始化脚本。

执行以下命令管理应用。

```shell
sh auto.sh start
sh auto.sh stop
sh auto.sh restart
sh auto.sh status
```

前端 Nginx 部署：
将 html 文件夹上传至 nginx 的 html 文件夹下，修改 nginx 配置文件并重启。如果 Nginx 和 Dlink 在同一节点，则可以指定静态资源的绝对路径到 Dlink 的 html 目录。

```shell
    server {
        listen       9999;
        server_name  localhost;

		# gzip config
		gzip on;
		gzip_min_length 1k;
		gzip_comp_level 9;
		gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
		gzip_vary on;
		gzip_disable "MSIE [1-6]\.";

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
			try_files $uri $uri/ /index.html;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        location ^~ /api {
            proxy_pass http://127.0.0.1:8888;
            proxy_set_header   X-Forwarded-Proto $scheme;
            proxy_set_header   X-Real-IP         $remote_addr;
        }
    }
```

1. server.listen 填写前端访问端口，例如 9999
2. proxy_pass 填写后端地址如 http://127.0.0.1:8888
3. 将 html 文件夹下打包好的前端资源上传到 nginx 的 html 文件夹中
4. 重载配置文件：nginx -s reload
5. 直接访问前端端口，例如 9999

### 从源码编译

#### 项目目录

```java
dlink -- 父项目
|-dlink-admin -- 管理中心
|-dlink-app -- Application Jar
|-dlink-assembly -- 打包配置
|-dlink-client -- Client 中心
| |-dlink-client-1.11 -- Client-1.11 实现
| |-dlink-client-1.12 -- Client-1.12 实现
| |-dlink-client-1.13 -- Client-1.13 实现
| |-dlink-client-1.14 -- Client-1.14 实现
|-dlink-common -- 通用中心
|-dlink-connectors -- Connectors 中心
| |-dlink-connector-jdbc -- Jdbc 扩展
|-dlink-core -- 执行中心
|-dlink-doc -- 文档
| |-bin -- 启动脚本
| |-bug -- bug 反馈
| |-config -- 配置文件
| |-doc -- 使用文档
| |-sql -- sql脚本
|-dlink-executor -- 执行中心
|-dlink-extends -- 扩展中心
|-dlink-function -- 函数中心
|-dlink-gateway -- Flink 网关中心
|-dlink-metadata -- 元数据中心
| |-dlink-metadata-base -- 元数据基础组件
| |-dlink-metadata-clickhouse -- 元数据- clickhouse 实现
| |-dlink-metadata-mysql -- 元数据- mysql 实现
| |-dlink-metadata-oracle -- 元数据- oracle 实现
| |-dlink-metadata-postgresql -- 元数据- postgresql 实现
|-dlink-web -- React 前端
|-docs -- 官网 md
```

#### 编译打包

以下环境版本实测编译成功：

|  环境   |   版本    |
| :-----: | :-------: |
|   npm   |  7.19.0   |
| node.js |  14.17.0  |
|   jdk   | 1.8.0_201 |
|  maven  |   3.6.0   |
| lombok  |  1.18.16  |
|  mysql  |   5.7+    |

```shell
mvn clean install -Dmaven.test.skip=true
```
