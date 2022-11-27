# Dlink-0.3.2 更新说明

## 前言

​ 在2021年10月24日的程序员节前夕，文末丶为大家带来了新内容 Dlink-0.3.2 。时隔两月才为大家带来新内容，在此向大家致歉。当然，开源之路一定会坚持下去，会为大家带来更多的新内容，也希望有志之士可以共建社区。

本次更新，Dlink 为大家在编写 FlinkSQL 的方向上带来了更加实用的功能——Flink SQL 自动补全。

此外，Dlink 目前不支持 `per-job` 和 `application` 的模式提交，后续将开源此功能。但在 FlinkSQL 任务定稿前的开发和调试阶段是不区分提交方式的，即 Dlink 目前可以完成作为 IDE 的开发工作，没问题后再提交到其他平台（如 StreamX）或者其他执行方式（如 `flink run` 和 `sql-client` 。

## 新功能

### 1.新增 FlinkSQL 编辑器自动补全函数及文档的功能

Dlink-0.3.2 版本上线了一个非常实用的功能——自动补全。效果如下图所示：

![image-20211023163926425](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrCRFf041g35NN7ubxP4h87Gw5B8aDXyheUhW5SvrASNmIqYK0aWJ5wpca3m04GTdUATXaxPjFPGg/0?wx_fmt=png)

我们在使用 IDEA 等工具时，提示方法并补全、生成的功能大大提升了开发效率。而 Dlink 的目标便是让 FlinkSQL 更加丝滑，所以其提供了自定义的自动补全功能。对比传统的使用 `Java` 字符串来编写 FlinkSQL 的方式，Dlink 的优势是巨大。

在文档中心，我们可以根据自己的需要扩展相应的自动补全规则，如 `UDF`、`Connector With` 等 FlinkSQL 片段，甚至可以扩展任意可以想象到内容，如注释、模板、配置、算法等。如下图所示为文档中心的编辑功能：

![image-20211023165126598](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrCRFf041g35NN7ubxP4h87SicCSZZwtenTkyzRrnuniaeITjpSP6Cqqz78GwWErlWicm7nHgI7pGe7g/0?wx_fmt=png)

具体新增规则的示例请看下文描述。

### 2.新增 set 语法来设置执行环境参数

对于一个 FlinkSQL 的任务来说，除了 sql 口径，其任务配置也十分重要。所以 Dlink-0.3.2 版本中提供了 `sql-client` 的 `set` 语法，可以通过 `set` 关键字来指定任务的执行配置（如 “ `set table.exec.resource.default-parallelism=2;` ” ），其优先级要高于 Dlink 自身的任务配置（面板右侧）。

那么长的参数一般人谁记得住？等等，别忘了 Dlink 的新功能自动补全~

示例：

配置实现输入 `parallelism` 子字符串来自动补全 `table.exec.resource.default-parallelism=` 。

![image-20211023165408037](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrCRFf041g35NN7ubxP4h87WWaWC0icIZRJBzHLZq4CIrpiabVbDP1HODLzkqOQdFYqmtOKbcib364ibA/0?wx_fmt=png)

如上图所示，在文档中心中添加一个规则，名称为 `parallelism`，填充值为 `table.exec.resource.default-parallelism=`，其他内容随意。

保存之后，来到编辑器输入 `par` 后提示情况如下图所示：

![image-20211023165809241](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrCRFf041g35NN7ubxP4h87AsyB19pwqLzZibCYnaZRyZlTibrTJ9k8YK7YqebJvUwJiau6dbM2zbt8A/0?wx_fmt=png)

选中要补全的规则后，编辑器中自动补全了 `table.exec.resource.default-parallelism=` 。

![image-20211023165924281](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrCRFf041g35NN7ubxP4h87UvNO8oxaggJSvhwsSWGTsiauMZMRS627fibBbuwJAjQJvic3WSwaBLxmg/0?wx_fmt=png)

至此，有些小伙伴发现，是不是可以直接定义 `pl2` 来自动生成 `set table.exec.resource.default-parallelism=2;` ？

当然可以的。

还有小伙伴问，可不可以定义 `pl` 生成 `set table.exec.resource.default-parallelism=;` 后，光标自动定位到 `=` 于 `;` 之间？

这个也可以的，只需要定义 `pl` 填充值为 `set table.exec.resource.default-parallelism=${1:};` ，即可实现。

所以说，只要能想象到的都可以定义，这样的 Dlink 你爱了吗？

嘘，还有点小 bug 后续修复呢。如果有什么建议及问题请及时指出哦。

## 新部署

​ 本次更新带来了新的非必要的部署变动。

### 1.新增 plugins 类加载路径用于加载 Flink 相关依赖

​ 由于 Dlink 采用关键依赖外置来管理器功能的支撑，分为自身依赖与 Flink 相关依赖，易混淆，所以在部署包新增了 `plugins` 目录用于存放 Flink 相关依赖，而 libs 下仅存放 Dlink 的自身依赖。此外 `extends` 存放暂不使用的依赖。其包结构如下所示：

```java
config/--配置文件
    |-application.yml
    extends/--扩展
    |-clickhouse-jdbc-0.2.6.jar
    |-dlink-client-1.11.jar
    |-dlink-client-1.13.jar
    |-dlink-client-1.14.jar
    |-dlink-flink-shaded-hadoop-3-uber.jar
    |-flink-sql-connector-hbase-1.4_2.11-1.12.5.jar
    |-flink-sql-connector-hbase-2.2_2.11-1.12.5.jar
    |-flink-sql-connector-kafka_2.11-1.12.5.jar
    |-ojdbc8-12.2.0.1.jar
    lib/--外部依赖及Connector
    |-dlink-client-1.12.jar--必需
    |-dlink-connector-jdbc.jar
    |-dlink-function.jar
    |-dlink-metadata-clickhouse.jar
    |-dlink-metadata-mysql.jar
    |-dlink-metadata-oracle.jar
    |-dlink-metadata-postgresql.jar
    plugins/
    |-flink-connector-jdbc_2.11-1.12.5.jar
    |-flink-csv-1.12.5.jar
    |-flink-json-1.12.5.jar
    |-mysql-connector-java-8.0.21.jar
    sql/
    |-dinky.sql--Mysql初始化脚本
    |-upgrade/--各个版本升级SQL脚本
    auto.sh--启动停止脚本
    dlink-admin.jar--程序包
```

### 2.新增 Nginx 的部署方式

​ Dlink 是一个基于 SpringBoot 框架的 React 应用，所以有两种部署方式。

​ 此前的方式为将 React 的打包资源放在了 dlink-admin 的 static 目录下，依据 SpringBoot Web 机制自动加载，所以访问 8888 端口号即可打开应用页面。

​ 本次更新提供了另一种更加可靠的部署方式—— Nginx 部署。

​ 将 dist 并上传至 nginx 的 html 文件夹下，修改 nginx 配置文件并重启。添加内容如下：

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

​ 1.server.listen 填写前端访问端口

​ 2.proxy_pass 填写后端地址如 http://127.0.0.1:8888

​ 3.重启 Nginx。

​ 4.后续只更新前端资源时，不需要重启 Nginx。

## 新改动

### 1.优化 Flink 多版本间的切换问题

​ Dlink 主要原理是基于 flink-client 来提交 FlinkSQL 到远程集群，其在提交的过程时会进行 sql 到 执行图的转换工作，所以该功能会强依赖 Flink 的源码与版本，使其在切换 Flink 版本时会出现类不存在或方法不存在的问题。本次更新对从 `CatalogManager` 获取表字段的逻辑进行了下沉处理，新提供了 `List<String> getFieldNamesFromCatalogManager(CatalogManager catalogManager, String catalog, String database, String table)` 的静态方法用于解决不同版本在获取字段时逻辑不一致的问题。

​ 当然，之所以说是多版本间的切换而非多版本的兼容，是因为 Dlink 的一个进程实例只支持一种 Flink 版本，多版本需要启动多个 Dlink 实例或者切换 dlink-client 依赖后重启。后续将开源单实例兼容多版本 Flink 。

### 2.新增 dlink-extends 模块用于扩展依赖打包

​ Dlink 支持 Flink 社区的绝大多数插件或依赖，例如各种 Connector。于是新增了 dlink-extends 模块用于打包自己需求的依赖，当然从各大网址下载也是完全可以的。

​ 注意：在引入 Hadoop 的依赖时，会因为与 SpringBoot 产生冲突（如 Servlet-api 等）而导致无法正常打开网页，此时如果不想解决依赖冲突的难题，可以通过 Nginx 前后端分离的模式部署 Dlink 从而避免该问题。

### 3.优化所有的新增功能其别名未填则默认为名称

​ 该功能的优化源于 Github 的用户 zhu-mingye 所贡献的测试与提议。主要描述为在新增一个 Flink 集群时如果未指定别名则会在提交表单时出现异常信息，同时 Flink 集群简略表格显示别名的值为空。对此进行了底层的改进，对于已拥有 alias 的对象可以在新增写入数据库且值为空时自动补填 name。

### 4.优化注册 Flink 集群时的连接测试与异常日志打印

​ 有很多试用者反映 Dlink 在注册 Flink 集群时，尽管可以注册成功，但日志会打印 UnKnownHostException 等异常。所以对 UnKnownHostException 产生原因追究并处理，新版本已解决。

### 5.升级 Flink 1.13.2 为1.13.3

​ 仅升级 dlink-client 的 pom 依赖 Flink的版本 1.13.2 为 1.13.3。此外无改动。

### 6.新增 Flink 1.14.0 的支持

​ 十月金秋，Flink 社区发布了 1.14.0 版本，并带来了诸多特性，其特性本文不再赘述，如有需求可关注“Flink 中文社区”查看。与此同时，Dlink 也扩展了 flink-client-1.14 来支持最新版本的 Flink。

### 7.修复血缘分析缩进树图渲染导致页面崩溃的 bug

​ 由于最新的 ant-design/charts 依赖中 `IndentedTreeGraph` 出现了一些 bug 导致 `edgeStyle` 参数方法中的`graph.findById(item.target.id).getModel()` 无法正确返回对象，所以暂时将原有的根据血缘表中字段数占比而渲染粗细不同的关系连接线功能去除来避免该问题的发生。

## 安装包获取

百度网盘链接: https://pan.baidu.com/s/1OJNK_7Un_IZzUybELluyog

提取码: iv6j

