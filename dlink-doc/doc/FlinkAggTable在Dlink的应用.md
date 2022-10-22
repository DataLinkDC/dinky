# Flink AggTable 在 Dlink 的实践

## 摘要

本文讲述了 Dlink 对 Flink 的表值聚合功能的应用与增强。增强主要在于定义了 AGGTABLE 来通过 FlinkSql 进行表值聚合的实现，以下将通过两个示例 top2 与 to_map 进行讲解。

## 准备工作

### 部署 Dlink-0.2.2

#### 获取安装包

​ 百度网盘链接：https://pan.baidu.com/s/1gHZPGMhYUcpZZgOHta3Csw
​ 提取码：0202

#### 应用结构

```java
config/ -- 配置文件
|- application.yml
lib/ -- 外部依赖及Connector
|- dlink-client-1.12.jar -- 必需
|- dlink-connector-jdbc.jar
|- dlink-function-0.2.2.jar
|- flink-connector-jdbc_2.11-1.12.4.jar
|- flink-csv-1.12.4.jar
|- flink-json-1.12.4.jar
|- mysql-connector-java-8.0.21.jar
sql/
|- dinky.sql --Mysql初始化脚本
|- upgrade/ -- 各个版本升级SQL脚本
auto.sh -- 启动停止脚本
dlink-admin.jar -- 程序包
```

#### 修改配置文件

配置数据库地址。

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: dlink
    password: dlink
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 执行与停止

```shell
# 启动
sh auto.sh start
# 停止
sh auto.sh stop
# 重启
sh auto.sh restart
# 状态
sh auto.sh status
```

### 准备测试表

#### 学生表（student）

```sql
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `sid` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `student` VALUES (1, '小明');
INSERT INTO `student` VALUES (2, '小红');
INSERT INTO `student` VALUES (3, '小黑');
INSERT INTO `student` VALUES (4, '小白');
```

#### 一维成绩表（score）

```sql
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `cid` int(11) NOT NULL,
  `sid` int(11) NULL DEFAULT NULL,
  `cls` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `score` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`cid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `score` VALUES (1, 1, 'chinese', 90);
INSERT INTO `score` VALUES (2, 1, 'math', 100);
INSERT INTO `score` VALUES (3, 1, 'english', 95);
INSERT INTO `score` VALUES (4, 2, 'chinese', 98);
INSERT INTO `score` VALUES (5, 2, 'english', 99);
INSERT INTO `score` VALUES (6, 3, 'chinese', 99);
INSERT INTO `score` VALUES (7, 3, 'english', 100);
```

#### 前二排名表（scoretop2）

```sql
DROP TABLE IF EXISTS `scoretop2`;
CREATE TABLE `scoretop2`  (
  `cls` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `score` int(11) NULL DEFAULT NULL,
  `rank` int(11) NOT NULL,
  PRIMARY KEY (`cls`, `rank`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```

#### 二维成绩单表（studentscore）

```sql
DROP TABLE IF EXISTS `studentscore`;
CREATE TABLE `studentscore`  (
  `sid` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `chinese` int(11) NULL DEFAULT NULL,
  `math` int(11) NULL DEFAULT NULL,
  `english` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```

### 问题提出

#### 输出各科成绩前二的分数

要求输出已有学科排名前二的分数到scoretop2表中。

#### 输出二维成绩单

要求将一维成绩表转化为二维成绩单，其中不存在的成绩得分为0，并输出至studentscore表中。

## Dlink 的 AGGTABLE

​ 本文以 Flink 官方的 Table Aggregate Functions 示例 Top2 为例进行比较说明，传送门 https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/table/functions/udfs/#table-aggregate-functions

### 官方 Table API 实现

```java
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.*;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.util.Collector;
import static org.apache.flink.table.api.Expressions.*;

// mutable accumulator of structured type for the aggregate function
public static class Top2Accumulator {
  public Integer first;
  public Integer second;
}

// function that takes (value INT), stores intermediate results in a structured
// type of Top2Accumulator, and returns the result as a structured type of Tuple2<Integer, Integer>
// for value and rank
public static class Top2 extends TableAggregateFunction<Tuple2<Integer, Integer>, Top2Accumulator> {

  @Override
  public Top2Accumulator createAccumulator() {
    Top2Accumulator acc = new Top2Accumulator();
    acc.first = Integer.MIN_VALUE;
    acc.second = Integer.MIN_VALUE;
    return acc;
  }

  public void accumulate(Top2Accumulator acc, Integer value) {
    if (value > acc.first) {
      acc.second = acc.first;
      acc.first = value;
    } else if (value > acc.second) {
      acc.second = value;
    }
  }

  public void merge(Top2Accumulator acc, Iterable<Top2Accumulator> it) {
    for (Top2Accumulator otherAcc : it) {
      accumulate(acc, otherAcc.first);
      accumulate(acc, otherAcc.second);
    }
  }

  public void emitValue(Top2Accumulator acc, Collector<Tuple2<Integer, Integer>> out) {
    // emit the value and rank
    if (acc.first != Integer.MIN_VALUE) {
      out.collect(Tuple2.of(acc.first, 1));
    }
    if (acc.second != Integer.MIN_VALUE) {
      out.collect(Tuple2.of(acc.second, 2));
    }
  }
}

TableEnvironment env = TableEnvironment.create(...);

// call function "inline" without registration in Table API
env
  .from("MyTable")
  .groupBy($("myField"))
  .flatAggregate(call(Top2.class, $("value")))
  .select($("myField"), $("f0"), $("f1"));

// call function "inline" without registration in Table API
// but use an alias for a better naming of Tuple2's fields
env
  .from("MyTable")
  .groupBy($("myField"))
  .flatAggregate(call(Top2.class, $("value")).as("value", "rank"))
  .select($("myField"), $("value"), $("rank"));

// register function
env.createTemporarySystemFunction("Top2", Top2.class);

// call registered function in Table API
env
  .from("MyTable")
  .groupBy($("myField"))
  .flatAggregate(call("Top2", $("value")).as("value", "rank"))
  .select($("myField"), $("value"), $("rank"));
```

### Dlink FlinkSql 实现

#### 示例

```sql
CREATE AGGTABLE aggdemo AS 
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```

#### 优势

​ 可以通过 FlinkSql 来实现表值聚合的需求，降低了开发与维护成本。

#### 缺点

​ 语法固定，示例关键字必须存在并进行描述，where 可以加在 FROM 和 GROUP BY 之间。

## Dlink 本地实现各科成绩前二

​ 本示例通过 Dlink 的本地环境进行演示实现。

### 进入Dlink

![image-20210615115042539](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGIAFicLZ3bwSawOianJQnNWuKAvZJ3Bb00DiaBxtxvnXgToGibPAwMFhs6A/0?wx_fmt=png)

​ 只有版本号大于等于 0.2.2-rc1 的 Dlink 才支持本文 AGGTABLE 的使用。

### 编写 FlinkSQL

```sql
jdbcconfig:='connector' = 'jdbc',
    'url' = 'jdbc:mysql://127.0.0.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',
    'username'='dlink',
    'password'='dlink',;
CREATE TABLE student (
    sid INT,
    name STRING,
    PRIMARY KEY (sid) NOT ENFORCED
) WITH (
    ${jdbcconfig}
    'table-name' = 'student'
);
CREATE TABLE score (
    cid INT,
    sid INT,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (
    ${jdbcconfig}
    'table-name' = 'score'
);
CREATE TABLE scoretop2 (
    cls STRING,
    score INT,
    `rank` INT,
    PRIMARY KEY (cls,`rank`) NOT ENFORCED
) WITH (
    ${jdbcconfig}
    'table-name' = 'scoretop2'
);
CREATE AGGTABLE aggscore AS 
SELECT cls,score,rank
FROM score
GROUP BY cls
AGG BY TOP2(score) as (score,rank);

insert into scoretop2
select 
b.cls,b.score,b.`rank`
from aggscore b
```

​ 本 Sql 使用了 Dlink 的增强特性 Fragment 机制，对 jdbc的配置进行了定义。

### 维护 FlinkSQL 及配置

![image-20210615115521967](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGeibmfcst4hHVTqzFmX6LvBXqgPTFcCOWHuIxEcbNHgfnUc0mhPm1eFw/0?wx_fmt=png)

​ 编写 FlinkSQL ，配置开启 Fragment 机制，设置 Flink 集群为本地执行。点击保存。

### 同步执行INSERT

![image-20210615115714713](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGApFiacyxkKERLE9FhsteTeTovcjTQHiaPKcxY6YqSukkVYZWVFGxPJibQ/0?wx_fmt=png)

​ 点击同步执行按钮运行当前编辑器中的 FlinkSQL 语句集。弹出提示信息，等待执行完成后自动关闭并刷新信息和结果。

​ 当前版本使用异步提交功能将直接提交任务到集群，Studio 不负责执行结果的记录。提交任务前请保存 FlinkSQL 和配置，否则将提交旧的语句和配置。

### 执行反馈

![image-20210615115913647](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGL7Wv8Tefsn0h1USWf2VLXB2Tb3yx4K2QksiaFplehnrvz25cE0nQnlA/0?wx_fmt=png)

​ 本地执行成功，“0_admin” 为本地会话，里面存储了 Catalog。

### 同步执行SELECT查看中间过程

![image-20210615120129426](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGXkEXFib5ic21kOemq6ib8kWAdLCBicicjBxU9oibmaSs4Hru8EccxKe5z0dg/0?wx_fmt=png)

​ 由于当前会话中已经存储了表的定义，此时直接选中 select 语句点击同步执行可以重新计算并展示其计算过程中产生的结果，由于 Flink 表值聚合操作机制，该结果非最终结果。

### 同步执行SELECT查看最终结果

![image-20210615121542233](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkG5mNQFZp4YIuwIrh6cJteFIwsbomibSk32hWbFqlt887F9lee9NYT8fQ/0?wx_fmt=png)

​ 在草稿的页面使用相同的会话可以共享 Catalog，此时只需要执行 select 查询 sink 表就可以预览最终的统计结果。

### 查看Mysql表的数据

![image-20210615120738413](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGerEdvQLXGNqfm7KZT7ARaNBV0mlrUdah69JAB3miaBFBgUU3iaaowcLg/0?wx_fmt=png)

​ sink 表中只有五条数据，结果是正确的。

## Dlink 远程实现二维成绩单

​ 本示例通过 Dlink 控制远程集群来实现。

​ 远程集群的 lib 中需要上传 dlink-function.jar 。

### 编写FlinkSQL

```sql
jdbcconfig:='connector' = 'jdbc',
    'url' = 'jdbc:mysql://127.0.0.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',
    'username'='dlink',
    'password'='dlink',;
CREATE TABLE student (
    sid INT,
    name STRING,
    PRIMARY KEY (sid) NOT ENFORCED
) WITH (
     ${jdbcconfig}
    'table-name' = 'student'
);
CREATE TABLE score (
    cid INT,
    sid INT,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (
     ${jdbcconfig}
    'table-name' = 'score'
);
CREATE TABLE studentscore (
    sid INT,
    name STRING,
    chinese INT,
    math INT,
    english INT,
    PRIMARY KEY (sid) NOT ENFORCED
) WITH (
     ${jdbcconfig}
    'table-name' = 'studentscore'
);
CREATE AGGTABLE aggscore2 AS 
SELECT sid,data
FROM score
GROUP BY sid
AGG BY TO_MAP(cls,score) as (data);

insert into studentscore
select 
a.sid,a.name,
cast(GET_KEY(b.data,'chinese','0') as int),
cast(GET_KEY(b.data,'math','0') as int),
cast(GET_KEY(b.data,'english','0') as int)
from student a
left join aggscore2 b on a.sid=b.sid 
```

​ 本实例通过表值聚合将分组后的多行转单列然后通过 GET_KEY 取值的思路来实现。同时，也使用了 Fragment 机制。

### 同步执行

![image-20210615131731449](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGxHX5T3C2vr2CF9LicZicBnGZOYmpXVq343zYFPjXsae0icQ1mTVWcsugQ/0?wx_fmt=png)

​ 与示例一相似，不同点在于需要更改集群配置为 远程集群。远程集群的注册在集群中心注册，Hosts 需要填写 JobManager 的地址，HA模式则使用英文逗号分割可能出现的地址，如“127.0.0.1:8081,127.0.0.2:8081,127.0.0.3:8081”。心跳监测正常的集群实例即可用于任务执行或提交。

### Flink UI

![image-20210615131931183](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGCSGp5fSGaRz0PgvFlEmWSRdiaZZHbmicvYWXnLzoNL3HWEc3mL1W2jPA/0?wx_fmt=png)

​ 打开集群的 Flink UI 可以发现刚刚提交的批任务，此时可以发现集群版本号为 1.12.2 ，而 Dlink 默认版本为 1.12.4 ，所以一般大版本内可以互相兼容。

### 查看Mysql表的数据

![image-20210615132004925](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrkkX1Jsib7GxQY7tpiciaNdkGc9NX5IzQ6Kog5oYPiaaELmCYzh3vpdUaK40hNuFPrlAWY1jlZd7QbtQ/0?wx_fmt=png)

​ 查看 Mysql 表的最终数据，发现存在四条结果，且也符合问题的要求。

