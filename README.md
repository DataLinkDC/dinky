# Dlink

## 简介

Dlink 为 Apache Flink 而生，让 Flink SQL 更加丝滑。它是一个 FlinkSQL Studio，可以在线开发、预览、调试 FlinkSQL，支持 Flink 官方所有语法及其增强语法，并且可以远程提交 Sql 作业到集群，无打包过程。

需要注意的是，Dlink 它更专注于 FlinkSQL 的应用，而不是 DataStream。在开发过程中您不会看到任何一句 java、scala 或者 python。所以，它的目标是基于 FlinkSQL 来实现批流一体的实时计算平台。

与此同时，Dlink 也是 DataLink 数据中台生态的核心组件。

DataLink 开源项目及社区正在建设，希望本项目可以帮助你更快发展。

## 原理

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTo5cwuZy7GSLibw5J7Lx6cicx0vXaDHqn5VrrDJ9d3hcEicbEVO77NcP6bOylC9bOpuibM08JJ8bh8XQQ/0?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTqVImq5JvQzZ7oMqpnQ2NVHdmM6Pfib63atzoWNIqK7Ue6p9KfdibV889sOcZJ1Olw3kLHLmHZiab3Tg/0?wx_fmt=png)

## 功能

注意：已完成只表明核心功能完成，不包括细节优化。

|     域     |         概要          |   进展   |
| :--------: | :-------------------: | :------: |
|   Studio   |   FlinkSQL 作业管理   |  0.1.0   |
|            |    FlinkSQL 编辑器    |  0.1.0   |
|            |       运行信息        |  0.1.0   |
|            |       查询结果        |  0.1.0   |
|            |       历史记录        |  0.1.0   |
|            |       函数浏览        |  0.1.0   |
|            |       执行配置        |  0.1.0   |
|            |    会话创建与共享     |  0.1.0   |
|            |      连接器管理       |  0.1.0   |
|            |       同步执行        |  0.1.0   |
|            |       异步提交        |  0.1.0   |
|            |     AGGTABLE 语法     |  0.2.2   |
|            |    流任务数据预览     | 0.3.0 |
|            |       任务详情        | 0.3.0 |
|            |       集群总览        | 0.3.0 |
|            |       集群任务        | 0.3.0 |
|            |      元数据查询       | 0.3.0 |
|            |     表级血缘分析      | 0.3.0 |
|            |       任务进程        | 0.3.0 |
|            |     远程任务停止      | 0.3.0 |
|            |     函数自动补全      | 敬请期待 |
|            |       任务审计        | 敬请期待 |
|            |   FlinkSQL 运行指标   | 敬请期待 |
|            |    字段级血缘分析     | 敬请期待 |
|            |    示例与技巧文档     | 敬请期待 |
|            |    FlinkSQL 执行图    | 敬请期待 |
|            |       任务恢复        | 敬请期待 |
|            |        UDF注册        | 敬请期待 |
|            |       更改对比        | 敬请期待 |
|            |   Create Table 生成   | 敬请期待 |
|            |      Insert 生成      | 敬请期待 |
|            |       SQL 翻译        | 敬请期待 |
|            |   智能 Select 模式    | 敬请期待 |
|            |    自动补全元数据     | 敬请期待 |
|            |  任务反压和倾斜提示   | 敬请期待 |
|            |          ...          | 欢迎提议 |
|  集群中心  |    集群注册与管理     |  0.1.0   |
|            |       心跳检测        |  0.1.0   |
|            |    修改与删除审计     | 敬请期待 |
|            |       集群信息        | 敬请期待 |
|            |     历史任务检索      | 敬请期待 |
|            |      启动与停止       | 敬请期待 |
|  文档中心  | FlinkSQL 函数文档管理 |  0.1.0   |
|            |   FlinkSQL 示例文档   | 敬请期待 |
|            |   FlinkSQL 调优文档   | 敬请期待 |
|  用户中心  |       用户管理        | 敬请期待 |
|            |       角色管理        | 敬请期待 |
|            |       登录授权        | 敬请期待 |
| 数据源中心 |   数据源注册与管理    | 0.3.0 |
|            |       心跳检测        | 0.3.0 |
|            |      元数据查询       | 0.3.0 |
|            |       数据查询        | 敬请期待 |
|            |       质量分析        | 敬请期待 |
|  调度中心  |   定时调度任务管理    | 敬请期待 |
|            | 依赖血缘调度任务管理  | 敬请期待 |
|            |       调度日志        | 敬请期待 |
|  监控中心  |     Flink任务监控     | 敬请期待 |
|            |       集群监控        | 敬请期待 |
|            |       每日报表        | 敬请期待 |
|  数据地图  |       检索中心        | 敬请期待 |
|            |       任务总览        | 敬请期待 |
|            |       血缘总览        | 敬请期待 |
|            |    元数据应用总览     | 敬请期待 |
|            |       集群总览        | 敬请期待 |
|  数据分析  |       简易图表        | 敬请期待 |
|            |       导入导出        | 敬请期待 |
|  Open API  |          ...          | 欢迎梳理 |
|    其他    |          ...          | 欢迎提议 |

## 部署

### 版本

抢先体验( main 主支)：dlink-0.3.2

稳定版本( 0.3.1 分支)：dlink-0.3.1

### 从安装包开始

```
config/ -- 配置文件
|- application.yml
lib/ -- 外部依赖及Connector
|- dlink-client-1.12.jar -- 必需
|- dlink-connector-jdbc.jar
|- dlink-function.jar
|- flink-connector-jdbc_2.11-1.12.5.jar
|- flink-csv-1.12.5.jar
|- flink-json-1.12.5.jar
|- mysql-connector-java-8.0.21.jar
sql/ 
|- dlink.sql --Mysql初始化脚本
auto.sh --启动停止脚本
dlink-admin.jar --程序包
```

解压后结构如上所示，修改配置文件内容。

在Mysql数据库中创建数据库并执行初始化脚本。

执行以下命令管理应用。

```shell
sh auto.sh start
sh auto.sh stop
sh auto.sh restart
sh auto.sh status
```

### 从源码编译

#### 项目目录

```java
dlink -- 父项目
|-dlink-admin -- 管理中心
|-dlink-client -- Client 中心
| |-dlink-client-1.12 -- Client-1.12 实现
| |-dlink-client-1.13 -- Client-1.13 实现
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
|-dlink-function -- 函数中心
|-dlink-metadata -- 元数据中心
| |-dlink-metadata-base -- 元数据基础组件
| |-dlink-metadata-clickhouse -- 元数据- clickhouse 实现
| |-dlink-metadata-mysql -- 元数据- mysql 实现
| |-dlink-metadata-oracle -- 元数据- oracle 实现
| |-dlink-metadata-postgresql -- 元数据- postgresql 实现
|-dlink-web -- React 前端
```

#### 编译打包

以下环境版本实测编译成功：

|         环境          |   版本   |
| :-------------------: | :------: |
|         npm          |  7.19.0  |
|       node.js        |  14.17.0 |
|         jdk          | 1.8.0_201|
|        maven         |  3.6.0   |
|       lombok         |  1.18.16 |
|        mysql         |   5.7+   |

```shell
mvn clean install -Dmaven.test.skip=true
```

如果前端编译 umi 报错时：npm install -g umi

#### 扩展Connector及UDF

将 Flink 集群上已扩展好的 Connector 和 UDF 直接放入 Dlink 的 lib 下，然后重启即可。
定制 Connector 过程同 Flink 官方一样。

#### 扩展Metadata

遵循SPI。

#### 扩展其他版本的Flink

Flink 的版本取决于 lib 下的 dlink-client-1.12.jar。
当前版本默认为 Flink 1.12.4 API。
向其他版本的集群提交任务可能存在问题，已实现 1.11、1.12、1.13，切换版本时只需要将对应依赖在lib下进行替换，然后重启即可。

## 使用手册

### 基础使用

#### 登录

当前版本用户名和密码在配置文件中配置。

#### 集群中心

注册 Flink 集群地址时，格式为 host:port ，用英文逗号分隔。即添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081。
新增和修改的等待时间较长，是因为需要检测最新的 JobManager 地址。
心跳检测为手动触发，会更新集群状态与 JobManager 地址。

#### Studio

1. 在左侧目录区域创建文件夹或任务。
2. 在中间编辑区编写 FlinkSQL 。
3. 在右侧配置执行参数。
4. Fragment 开启后，可以使用增强的 sql 片段语法：
```sql
sf:=select * from;tb:=student;
${sf} ${tb}
##效果等同于
select * from student
```
5. 内置 sql 增强语法-表值聚合：
```sql
CREATE AGGTABLE aggdemo AS
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```
6. MaxRowNum 为批流执行Select时预览查询结果的最大集合长度，默认 100，最大 9999。
7. SavePointPath 当前版本属于非 Jar 提交，暂不可用。
8. Flink 共享会话共享 Catalog 。
9. 连接器为 Catalog 里的表信息，清空按钮会销毁当前会话。
10. Local 模式请使用少量测试数据，真实数据请使用远程集群。
11. 执行 SQL 时，如果您选中了部分 SQL，则会执行选中的内容，否则执行全部内容。
12. 小火箭的提交功能是异步提交当前任务已保存的 FlinkSQL 及配置到集群。无法提交草稿。
13. 执行信息或者历史中那个很长很长的就是集群上的 JobId。
14. 草稿是无法被异步远程提交的，只能同步执行。
15. 灰色按钮代表近期将实现。
16. 同步执行时可以自由指定任务名，异步提交默认为作业名。
#### 使用技巧

1.[Flink AggTable 在 Dlink 的实践](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/FlinkAggTable%E5%9C%A8Dlink%E7%9A%84%E5%BA%94%E7%94%A8.md)
2.[Dlink 概念原理与源码扩展介绍](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5%E4%B8%8E%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E8%AF%A6%E8%A7%A3.md)
3.[Dlink 实时计算平台——部署篇](https://github.com/DataLinkDC/dlink/blob/dev/dlink-doc/doc/Dlink%E5%AE%9E%E6%97%B6%E8%AE%A1%E7%AE%97%E5%B9%B3%E5%8F%B0%E2%80%94%E2%80%94%E9%83%A8%E7%BD%B2%E7%AF%87.md)

#### 常见问题及解决

（=。=）~ 敬请期待。

## 技术栈

[Apache Flink](https://github.com/apache/flink)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[ant-design-pro](https://github.com/ant-design/ant-design-pro)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[SpringBoot]()

## 交流与贡献

欢迎您加入社区交流分享，也欢迎您为社区贡献自己的力量。

在此有意向参与代码及文档贡献或积极测试者可以私信我加入 Dlink Contributors 群聊进一步了解。
dlink将正式开始社区积极的发展阶段，社区的主旨是开放、交流、创新、共赢，dlink的核心理念是创新，即不受思想约束地勇于尝试。
datalink本就是一个创新型的解决方案，而不是模仿别人的思路按部就班，一味模仿对于社区及所有人的发展意义并不大，积极创新才可能独树一帜，并为大家带来更大的利益。
无论您是否已经建成了自己的FlinkSQL平台或者数据中台，相信它一定会在创新的方向上为您带来些许启发。
在此非常感谢大家的支持~

QQ社区群：**543709668**，申请备注 “ Dlink ”，不写不批哦

微信社区群：添加微信号 wenmo_ai 邀请进群，申请备注 “ Dlink ”，不写不批哦

公众号：[DataLink数据中台](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

邮箱：aiwenmo@163.com

## 运行截图

> 登录页

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTo5cwuZy7GSLibw5J7Lx6cicxd9xkibGuLQiahOhU9ncGTamPViaIeRNlmH5rMmDgDaaLkXl9ibjDjBECwA/0?wx_fmt=png)

> 首页

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvTKz0dcibiavX4ZHA1wSHUvXLlsRvcghHKOhLmIMicJWlnp61L2gxyJEwg/0?wx_fmt=png)

> Studio 任务提交

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cv1oWyfwgHbcYQGEyS0xg8SVVArEmPXVWVSQk2AGWO0cnh9C3ZtyXeJg/0?wx_fmt=png)

> Studio 语法逻辑检查

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvhpib8mBVribEEEUacvddKxL28xwjWicwIoJ78YTGLgtqZ2dKWfAFOckTw/0?wx_fmt=png)

> Studio 批流预览

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvJ1roopzpX0zSyC2gP1p3a7fZykXqn90k38wOjARrR9DHiajbQAldEQA/0?wx_fmt=png)

> Studio 异常反馈

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cv7Ut058hld87PWgamiayRMz0X4eF8SkROnXGquVq5wc3OzkPf8tlWmGw/0?wx_fmt=png)

> Studio 进程监控

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvQKRDlwya3rEaJzzhVohZRTponJXnf4iaZ85Q8Vic8iaLLvcTIQJrJOZOQ/0?wx_fmt=png)

> Studio 执行历史

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvDuLY9K4xalvoytxAlNjoR6Upf1v167rGicaPaAfIhibCGcEhOvzI7V1A/0?wx_fmt=png)

> Studio 数据回放

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvLlyAyRYXPPkDrSYuKN6DB13wcHbLZ2qibbewQgXibeaWH8zOLq0lQyAQ/0?wx_fmt=png)

> Studio 血缘分析

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvmrA22mmuFJ6QzAG5fEAMSbmMHXFsODzzrzOrFz3eGd7pEicN6fQupwg/0?wx_fmt=png)

> Studio 函数浏览

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvo3bmeQGbM3qMibOkbNGl6Uj8OibyR5CkOWp86YYlD6LDhZCX3VZLz9cA/0?wx_fmt=png)

> Studio 共享会话

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvp0AcrZPNAuoe9yeHJLl1ztj7NUoMJx0I4GQwTYaQYI8Ldp6PWzhShQ/0?wx_fmt=png)

> 集群注册

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cv2010EVibf5rvht2nQVu7dAIoUMGgwh7PUWk9HWUjgyy2emSLGCqkdeQ/0?wx_fmt=png)

> 数据源注册

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvsFzqzCuMlFDrKYyBcuUQbEeicfJfHiaJk1e9Znfv2WvcAduSUsW02nsQ/0?wx_fmt=png)

> 元数据查询

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvwh7ES41AWxmujZtg0icuvzZc2WGRROgLv77devjaJ4p18X2Yv1ibklTA/0?wx_fmt=png)

