# Dlink 实时计算平台——部署篇

## 环境准备

以下环境版本实测可用，缺一不可，如有其他版本的测试请告知小编。

|  环境  |   版本    |
| :----: | :-------: |
|  npm   |  7.19.0   |
| nodejs |  14.17.0  |
|  jdk   | 1.8.0_201 |
| maven  |   3.6.0   |
| lombok |  1.18.16  |
| mysql  |   8.0+    |

## 从源码编译

### 源码下载

github 地址：https://github.com/DataLinkDC/dlink

### 项目编译

```sh
mvn clean install -Dmaven.test.skip=true
```

如果编译过程无异常则需要大约五分钟编译完成，编译完成的 tar.gz 包位于 build 目录下，文件名为 dlink-release-0.3.1.tar.gz 且大小约为 148MB。

### 常见问题及解决

1. 编译 dlink-web 时出现报错终止：

   解决方式：检查 npm 和 nodejs 的版本是否与本文一致。

2. 编译报实体类缺少 get 或 set 方法：

   解决方式：检查 IDEA 是否装有 lombok 插件。

## 直接下载安装包

百度网盘链接：https://pan.baidu.com/s/13Ffhe7QaSsGXfAwqSCFdNg
提取码：0301

## 应用部署

此部署过程在 Linux 的 Centos 7 上进行，其他未测试。

### 安装 Dlink

将安装包上传至服务器相应目录并解压。

得到以下项目结构：

```java
config/ -- 配置文件
|- application.yml
lib/ -- 外部依赖及Connector
|- dlink-client-1.12-0.3.1.jar
|- dlink-connector-jdbc-0.3.1.jar
|- dlink-function-0.3.1.jar
|- dlink-metadata-clickhouse-0.3.1.jar
|- dlink-metadata-mysql-0.3.1.jar
|- dlink-metadata-oracle-0.3.1.jar
|- dlink-metadata-postgresql-0.3.1.jar
sql/
|- dinky.sql --Mysql初始化脚本
|- upgrade/ -- 各个版本升级SQL脚本
auto.sh -- 启动停止脚本
dlink-admin-0.3.1.jar -- 程序包
```

### 修改配置文件

修改数据源连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: dlink
    password: dlink
    driver-class-name: com.mysql.cj.jdbc.Driver
  application:
    name: dlink
```

注：数据库实例名为 dlink，url 后缀参数可以根据实际数据库连接参数进行修改配置。

### 初始化数据库表

在对应数据库下执行 sql 目录下的 dinky.sql 脚本。

执行成功后，可见以下数据表：

```
dlink_catalogue
dlink_cluster
dlink_database
dlink_flink_document
dlink_history
dlink_task
dlink_task_statement
```

### 启动程序

启动 dlink 应用进程：

```sh
sh auto.sh start
```

其他命令：

```sh
# 停止
sh auto.sh stop
# 重启
sh auto.sh restart
# 状态
sh auto.sh status
```

### 运行日志

控制台输出：项目根目录下的 dlink.log 文件。

日志归档输出：项目根目录下的 logs 目录下。

### 登陆地址

服务器地址的 8888 端口号，如 127.0.0.1:8888 。

### 常见问题及解决

1. 程序启动后无法打开登录地址：

   解决方式：查看根目录下的控制台日志是否启动成功及其报错原因。

## Hello World

### 登录平台

![image-20210831193245415](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVKhSI29yWICmAIGMHmkwCXvezvNE7Hsjsf0xDyOiasLHp3N6TEXChiaXA/0?wx_fmt=png)

登录账号和密码在配置文件中设置，默认为 admin/admin。

### 查看主页

![image-20210831193512112](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVLY1QNWbQLb2Jkj8NPCTibfGH2ejd1djrxKkXeABicNplqmrCovutsvlQ/0?wx_fmt=png)

从主页查看当前版本号与更新日志。

### 注册集群

![image-20210831194054400](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVDgX4ibvdowLNlL08F4HT18hMubmXHZVGV2wBrWVibBuMzJ2CC7vINKyA/0?wx_fmt=png)

进入集群中心进行远程集群的注册。点击新建按钮配置远程集群的参数。图中示例配置了一个 Flink on Yarn 的高可用集群，其中 JobManager HA 地址需要填写集群中所有有可能被作为 JobManager 的 RestAPI 物理地址，多个地址间使用英文逗号分隔。表单提交时可能需要较长时间的等待，因为那时 dlink 正在努力的计算当前活跃的 JobManager 地址。

![image-20210831194540072](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVqPeqdt8VWMfoUJmugB1iamy6zQQGibXGicFFVfQW8gcolicUZic4az1Ts7g/0?wx_fmt=png)

保存成功后，页面将展示出当前的 JobManager 地址以及被注册集群的版本号，状态为正常时表示可用。

注意：只有具备 JobManager 实例的 Flink 集群才可以被成功注册到 dlink 中。

如状态异常时，请检查被注册的 Flink 集群地址是否能正常访问，默认端口号为8081，可能更改配置后发生了变化，查看位置为 Flink Web 的 JobManager 的 Configuration 中的 rest 相关属性。

### 执行 Hello World

万物都具有 Hello World 的第一步，当然 dlink 也是具有的。我们选取了基于 datagen 的流查询作为第一行 Flink Sql。具体如下：

```sql
CREATE TABLE Orders (
    order_number BIGINT,
    price        DECIMAL(32,2),
    buyer        ROW<first_name STRING, last_name STRING>,
    order_time   TIMESTAMP(3)
) WITH (
  'connector' = 'datagen',
  'rows-per-second' = '1'
);
select order_number,price,order_time from Orders
```

点击 Flink Sql Studio 进入开发页面：

![image-20210831200202188](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVkarvjH7WXrwJZq3cpQVvpVGNATnmNIoOUUib49fmWk0QFiaJAibnZZoHg/0?wx_fmt=png)

在中央的脚本编辑器中粘贴 Flink Sql，左边作业配置的 Flink 集群选中上文注册的测试集群，执行配置勾选远程执行，最后点击右上方三角形的开始按钮来执行语句。

### 执行历史

语句执行后，可以从下方历史选项卡中查看任务的执行状态，包含初始化、成功、失败等。

![image-20210831201159447](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVLtYcRQ9Micl9YC8P0tNgBdWKM94MEjJEKr3wicJP3DcbbCZoib1wZWLAQ/0?wx_fmt=png)

如上图所示，语句已经成功被执行。

### 预览数据

![image-20210831200900774](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVO8jEvGURW39DKU2b95rJQHDQY6Xq43Npd17XeHHtkqOyhK07jQ3EgA/0?wx_fmt=png)

点击结果选项卡的获取最新数据可以查看语句的执行结果，此处如果是 Insert 语句则无结果可查看。由于前文最大预览数为100，所以只记录了前 100 条结果。当然也可以点击历史中的对应任务的预览数据查看该执行结果。

![image-20210831201427838](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVicYbwNbGJ7ibmSbeCJSPiam7Du0dSECpvt0LDBavaEibafd1VMrkQFycxQ/0?wx_fmt=png)

### 管理进程

点击进程选项卡，选中已注册的集群，可以查看该集群的作业执行状况。

![image-20210831201615631](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVJbL7joDbibicnAkZSGiangAzwo5gw0ap17f2DUicE7RqicgOzRhu6rNjnHQ/0?wx_fmt=png)

点击操作栏中的停止按钮即可停止该流作业。

![image-20210831201919558](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTq0iaMiaGaJRmDDSWetZugQlVquWBlPgeicuKzgacXjwmGl1ThCBIrZP2t6e7eRLwBfstSA73IO066AQ/0?wx_fmt=png)

由上图可见，被提交的 Hello World 任务已经在被注册的集群上成功执行，且通过进程管理成功停止作业。

## 扩展组件

### 扩展连接器

dlink 兼容 Flink 官方所有的 connector 及其自定义 connector 。只需要将连接器所需要的 jar 包上传到 dlink 根目录下的 lib 目录下，然后重启 dlink 即可生效。

例如，当扩展 kafka 的连接器时，只需要把 flink-json-1.12.5.jar 、flink-connector-kafka_2.11-1.12.5.jar 以及 flink-sql-connector-kafka_2.11-1.12.5.jar 加入到 lib 目录下即可；当扩展 mysql 的连接器时，则需要把 flink-connector-jdbc_2.11-1.12.5.jar 和 mysql-connector-java-8.0.21.jar 加入到 lib 目录下即可。

此外，在 dlink-connector-jdbc.jar 中实现了基于 flink-connector-jdbc 的 Oracle 和 ClickHouse 的连接器，在引入flink-connector-jdbc_2.11-1.12.5.jar、ojdbc6-11.2.0.3.jar、clickhouse-jdbc-0.2.6.jar 后即可连接 Oracle 和 ClickHouse。

注：上述 jar 的版本号可替换为其他版本号。

### 扩展 UDF

dlink 兼容 Flink 官方的 UDF。只需要把 UDF 打包为依赖然后加入到 lib 目录下，重启后生效。

此外，dlink 提供了表值聚合函数——AGGTABLE：

```sql
CREATE AGGTABLE aggdemo AS
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```

以及语法糖——语句片段：

```sql
sf:=select * from;
tb:=student;
${sf} ${tb}
## 效果等同于
select * from student
```

### 扩展元数据

dlink 提供了元数据功能，扩展其他元数据的实现，需要基于 SPI 扩展，可见源码示例，打包后加入 lib 目录下，重启生效。

## 更多精彩

本文简简单单地带来了 dlink 的初次部署与体验的具体步骤，此外它还具备大量的新特性与功能来辅助 Flink Sql 开发与运维，如作业管理、共享会话、血缘分析、函数文档、数据源管理、元数据中心以及 SQL 编辑器的代码高亮、自动补全、语法逻辑校验等各种辅助功能。未来还将提供依赖调度等更多功能。

后续将带来《Dlink 实时计算平台使用手册——功能篇》、《Dlink 实时计算平台使用手册——技巧篇》、《Dlink 实时计算平台使用手册——原理篇》。

## 未来

Dlink 0.4.0 将于 0.3.0 功能完善后提上日程，主要包含企业级应用功能如时间调度、依赖调度、数据地图等。

Dlink 将紧跟 Flink 官方社区发展，为推广及发展 Flink 的应用而奋斗，打造 FlinkSQL 的最佳搭档的形象。

与此同时，DataLink 数据中台将同步发展，未来将提供开源的企业级数据中台解决方案。

## 交流

欢迎您加入社区交流分享与批评，也欢迎您为社区贡献自己的力量。

QQ社区群：**543709668**，申请备注 “ Dlink ”，不写不批

微信社区群：

公众号：DataLink数据中台
