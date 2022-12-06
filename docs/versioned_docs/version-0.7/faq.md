---
sidebar_position: 14
id: faq
title: FAQ
---

1.Flink on Yarn HA高可用,配置hdfs依赖,无法识别HDFS高可用访问地址别名，在Perjob和application模式，提交任务，出现异常信息

![HDFS集群别名](http://www.aiwenmo.com/dinky/dev/docs/HDFS%E9%9B%86%E7%BE%A4%E5%88%AB%E5%90%8D.png)

**解决办法：**

- 方案一

升级 Dinky 至 0.6.2 及后续版本。
详见：[https://github.com/DataLinkDC/dlink/issues/310](https://github.com/DataLinkDC/dlink/issues/310)
- 方案二

添加HADOOP_HOME环境变量，修改 /etc/profile
``` shell
export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
```

- 方案三

auto.sh 里加一行
``` shell
export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
```

2.出现 commons-cli 异常报错，需要在如下路径放入 commons-cli 依赖

**解决办法：**

下载common-cli包，需要在如下路径放置：
- Flink 的 lib
- dinky 的 plugins
- HDFS 的 /flink/lib/

3.依赖冲突

![dependency_conflict](http://www.aiwenmo.com/dinky/docs/zh-CN/faq/dependency_conflict.png)

**解决办法：**

如果添加 flink-shade-hadoop-uber-3 包后，请手动删除该包内部的javax.servlet 等冲突内容

4.连接hive异常

``` java
Caused by: java.lang.ClassNotFoundException: org.apache.http.client.HttpClient
```

![hive_http_error](http://www.aiwenmo.com/dinky/docs/zh-CN/faq/hive_http_error.png)

**解决办法:** 

在plugins下添加以下包

```
 httpclient-4.5.3.jar
 httpcore-4.4.6.jar
```
5.找不到javax/ws/rs/ext/MessageBodyReader类
![image](https://user-images.githubusercontent.com/40588644/166678799-13450726-6b89-4a04-9911-0ad0b11cf4dd.png)
**解决办法:**
在plugins下添加以下包
- javax.ws.rs-api-2.0.jar


6.在 Flink 中，如果与 CDH 集成并使用 HiveCatalog，必须要从新编译对应 Flink 版本的源码，在使用中如果不编译，会报如下错误：

![cdh_flink_sql_hive_connector_error](http://www.aiwenmo.com/dinky/docs/zh-CN/faq/cdh_flink_sql_hive_connector_error.jpg)

**解决办法:** 

  1.首先下载 Flink 对应版本源码，并切换到对应 hive 版本flink-sql-connector目录下

  2.修改pom，添加如下信息

   ```java
#添加cloudera 仓库
<repositories>
    <repository>
        <id>cloudera</id>
        <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
        <name>Cloudera Repositories</name>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
# 以flink-sql-connector-hive-2.2.0,修改如下
<dependency>
	<groupId>org.apache.hive</groupId>
	<artifactId>hive-exec</artifactId>
	<version>2.1.1-cdh6.3.2</version>
	<exclusions>
		<exclusion>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
		</exclusion>
		<exclusion>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
		</exclusion>
		<exclusion>
			<groupId>org.pentaho</groupId>
			<artifactId>pentaho-aggdesigner-algorithm</artifactId>
		</exclusion>
	</exclusions>
</dependency>
   ```

 编译成功后，将对应的jar包拿出来即可，分别放到 flink/lib和dinky/plugins下。重启 Flink 和 Dlinky 即可。

