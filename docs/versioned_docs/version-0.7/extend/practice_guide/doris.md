---
sidebar_position: 8
id: doris
title: Doris
---




## 背景

Apache Doris是一个现代化的MPP分析型数据库产品。仅需亚秒级响应时间即可获得查询结果，有效地支持实时数据分析。例如固定历史报表，实时数据分析，交互式数据分析和探索式数据分析等。

目前 Doris 的生态正在建设中，本文将分享如何基于 Dlink 实现 Mysql 变动数据通过 Flink 实时入库 Doris。

## 准备

老规矩，列清各组件版本：

|      组件       |     版本     |
| :-------------: | :----------: |
|      Flink      |    1.13.3    |
| Flink-mysql-cdc |    2.1.0     |
|      Doris      | 0.15.1-rc09  |
|   doris-flink   | 1.0-SNAPSHOT |
|      Mysql      |    8.0.13    |
|      Dlink      |    0.4.0     |

需要注意的是，本文的 Doris 是基于 OracleJDK1.8 和 Scala 2.11 通过源码进行编译打包的，所以所有组件的 scala 均为 2.11，此处和 Doris 社区略有不同。

## 部署

本文的 Flink 和 Doris 的部署不做描述，详情请查阅官网。

[Doris]: https://doris.apache.org/master/zh-CN/extending-doris/flink-doris-connector.html#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95	"Doris"

本文在 Dlink 部署成功的基础上进行，如需查看具体部署步骤，请阅读《flink sql 知其所以然（十六）：flink sql 开发企业级利器之 Dlink》。

Dlink 的 plugins 下添加 `doris-flink-1.0-SNAPSHOT.jar` 和 `flink-sql-connector-mysql-cdc-2.1.0.jar` 。重启 Dlink。

```java
plugins/ -- Flink 相关扩展
|- doris-flink-1.0-SNAPSHOT.jar
|- flink-csv-1.13.3.jar
|- flink-dist_2.11-1.13.3.jar
|- flink-format-changelog-json-2.1.0.jar
|- flink-json-1.13.3.jar
|- flink-shaded-zookeeper-3.4.14.jar
|- flink-sql-connector-mysql-cdc-2.1.0.jar
|- flink-table_2.11-1.13.3.jar
|- flink-table-blink_2.11-1.13.3.jar
```

当然，如果您想直接使用 FLINK_HOME 的话，可以在 `auto.sh` 文件中 `SETTING` 变量添加`$FLINK_HOME/lib` 。

## 数据表

### 学生表 （student）

```sql
-- Mysql
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `sid` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `student` VALUES (1, '小红');
INSERT INTO `student` VALUES (2, '小黑');
INSERT INTO `student` VALUES (3, '小黄');
```

### 成绩表（score）

```sql
-- Mysql
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `cid` int(11) NOT NULL,
  `sid` int(11) NULL DEFAULT NULL,
  `cls` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `score` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`cid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `score` VALUES (1, 1, 'chinese', 90);
INSERT INTO `score` VALUES (2, 1, 'math', 95);
INSERT INTO `score` VALUES (3, 1, 'english', 93);
INSERT INTO `score` VALUES (4, 2, 'chinese', 92);
INSERT INTO `score` VALUES (5, 2, 'math', 75);
INSERT INTO `score` VALUES (6, 2, 'english', 80);
INSERT INTO `score` VALUES (7, 3, 'chinese', 100);
INSERT INTO `score` VALUES (8, 3, 'math', 60);
```

### 学生成绩宽表（scoreinfo）

```sql
-- Doris
CREATE TABLE scoreinfo
(
    cid INT,
    sid INT,
    name VARCHAR(32),
    cls VARCHAR(32),
    score INT
)
UNIQUE KEY(cid)
DISTRIBUTED BY HASH(cid) BUCKETS 10
PROPERTIES("replication_num" = "1");
```



## FlinkSQL

```sql
CREATE TABLE student (
    sid INT,
    name STRING,
    PRIMARY KEY (sid) NOT ENFORCED
) WITH (
'connector' = 'mysql-cdc',
'hostname' = '127.0.0.1',
'port' = '3306',
'username' = 'test',
'password' = '123456',
'database-name' = 'test',
'table-name' = 'student');
CREATE TABLE score (
    cid INT,
    sid INT,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (
'connector' = 'mysql-cdc',
'hostname' = '127.0.0.1',
'port' = '3306',
'username' = 'test',
'password' = '123456',
'database-name' = 'test',
'table-name' = 'score');
CREATE TABLE scoreinfo (
    cid INT,
    sid INT,
    name STRING,
    cls STRING,
    score INT,
    PRIMARY KEY (cid) NOT ENFORCED
) WITH (       
'connector' = 'doris',
'fenodes' = '127.0.0.1:8030' ,
'table.identifier' = 'test.scoreinfo',
'username' = 'root',
'password'=''
);
insert into scoreinfo
select 
a.cid,a.sid,b.name,a.cls,a.score
from score a
left join student b on a.sid = b.sid
```

## 调试

### 在 Dinky 中提交

本示例采用了 yarn-session 的方式进行提交。
http://www.aiwenmo.com/dinky/docs/zh-CN/extend/practice_guide
![dinky_submit](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/practice_guide/doris/dinky_submit.png)

### 	FlinkWebUI

![flink_webui](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/practice_guide/doris/flink_webui.png)

上图可见，流任务已经成功被 Dinky 提交的远程集群了。

### Doris 查询

![doris_query](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/practice_guide/doris/doris_query.png)

上图可见，Doris 已经被写入了历史全量数据。

### 增量测试

在 Mysql 中执行新增语句：

```sql
INSERT INTO `score` VALUES (9, 3, 'english', 100);
```

Doris 成功被追加：

![doris_show_data_change](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/practice_guide/doris/doris_show_data_change.png)

### 变动测试

在 Mysql 中执行新增语句：

```sql
update score set score = 100 where cid = 1
```

Doris 成功被修改：

![doris_show_data_change_again](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/practice_guide/doris/doris_show_data_change_again.png)