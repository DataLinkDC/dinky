---
sidebar_position: 997
id: faq
title: FAQ
---

##  部署 FAQ

**<font color="blue" size="3.6">Q1：无法识别 hdfs 访问地址别名，提交任务报错：</font>**
```shell
Caused by: java.io.IOException: Cannot instantiate file system for URI: hdfs://nameservice/flink/lib
...
Caused by: java.lang.IllegalArgumentException: java.net.UnknownHostException: nameservice
...
```

提供 3 种方式解决这个问题：
- 升级 Dinky 至 0.6.2 及后续版本。详见：[https://github.com/DataLinkDC/dinky/issues/310](https://github.com/DataLinkDC/dinky/issues/310)

- 修改 `/etc/profile`，添加 HADOOP_HOME 环境变量
```shell
HADOOP_HOME=/opt/hadoop
```

- 编辑启动脚本 `auto.sh`，增加以下代码：
``` shell
export HADOOP_HOME=/opt/hadoop
```


**<font color="blue" size="3.6">Q2：planner 找不到或冲突，报错：</font>**
```shell
java.lang.NoClassDefFoundError: org/apache/flink/table/planner/...
```
或
```shell
org.apache.flink.table.api.TableException: Could not instantiate the executor. Make sure a planner module is on the classpath
```

1. flink-table-planner 和 flink-table-planner-loader 无法同时存在于 classpath，同时加载时会报错

2. 将 `/flink/opt` 目录下的 flink-table-planner.jar 拷贝至 `/dinky/plugins/flink`

3. 删除 `/dinky/plugins/flink` 目录下 flink-table-planner-loader.jar


**<font color="blue" size="3.6">Q3：servlet 或 gson 找不到或冲突，报错：</font>**
```shell
java.lang.NoSuchMethodError:javax.servlet....
```
或
```shell
java.lang.NoSuchMethodError:com.google.gson....
```

1. 添加 flink-shade-hadoop-uber-3 包时，删除该包内部的 javax.servlet、com.google.gson 等冲突内容

2. 添加其他依赖时，同样排除 servlet、gson 等依赖项


**<font color="blue" size="3.6">Q4：添加 cdc、kafka 等 connector 依赖，报错找不到类：</font>**
```shell
java.lang.ClassNotFoundException: org.apache.kafka.connect....
```

1. 检查 `/flink/lib` 目录和 `/dinky/plugins` 目录下有相应 jar 包

2. 检查是否有 flink-sql-connector-kafka 和 flink-connector-kafka 这种胖瘦包放在一起，一般只放胖包不放瘦包

3. 检查其他客户端依赖，如 kafka-client 包


**<font color="blue" size="3.6">Q5：连接 Hive 异常，报错：</font>**
```shell
Caused by: java.lang.ClassNotFoundException: org.apache.http.client.HttpClient
```

在 `/dinky/plugins` 下添加 httpclient-4.5.3.jar、httpcore-4.4.6.jar 依赖


**<font color="blue" size="3.6">Q6：与 CDH 集成并使用 HiveCatalog，报错：</font>**
```shell
Cause by: java.lang.IllegalArgumentException: Unrecognized Hadoop major version number: 3.1.1.7.2.8.0-224
  at org.apache.hadoop.hive.shims.ShimLoader.getMajorVersion(Shimloader.java:177) ~[flink-sql-connector-hive-2.2.0...]
```
1. 要从新编译对应 Flink 版本的源码,下载 Flink 对应版本源码，并切换到对应 hive 版本 flink-sql-connector 目录下

2. 修改 pom，添加如下信息
```xml
<!-- 添加 cloudera 仓库 -->
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

<!-- 以 flink-sql-connector-hive-2.2.0 为例，修改如下 -->
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

3. 编译成功后，将对应的 jar 包拿出来即可，分别放到 `flink/lib` 和 `dinky/plugins` 下，重启 Flink 和 Dinky


##  开发 FAQ

**<font color="blue" size="3.6">Q1：提交作业后报错 ClassCastException 类加载问题：</font>**
```shell
java.lang.ClassCastException: class org.dinky.model.Table cannot be cast to class org.dinky.model.
    Table (org.dinky.model.Table is in unnamed module of loader 'app'; org.dinky.model.
    Table is in unnamed module of loader org.apache.flink.util.ChildFirstClassLoader @66565121)
```
或
```shell
Caused by: java.lang.ClassCastException: cannot assign instance of org.apache.kafka.clients.consumer.OffsetResetStrategy to field
    org.apache.flink.connector.kafka.source.enumerator.initializer.ReaderHandledOffsetsInitializer.offsetResetStrategy of type 
    org.apache.flink.kafka.shaded.org.apache.kafka.clients.consumer.OffsetResetStrategy in instance of 
    org.apache.flink.connector.kafka.source.enumerator.initializer.ReaderHandledOffsetsInitializer
```
或
```shell
Caused by: java.lang.ArrayStoreException
	at java.lang.System.arraycopy(Native Method) ~[?:1.8.0_202]
```

1. 如果是普通作业或 stanlone 模式，在作业开发页面右下角自定义配置内加入此参数 classloader.resolve-order: parent-first

2. 如果是 application 作业在集群配置中，加入自定义参数 classloader.resolve-order: parent-first 即可


**<font color="blue" size="3.6">Q2：提交 cdc 作业后，类型转换报错：</font>**
```shell
java.lang.classCastException: java.lang.Integer cannot be cast to java.lang.Booleanat 
    org.apache.flink.table.data.GenericRowData.getBoolean(GenericRowData.java:134)at 
    org.apache.doris.flink.deserialization,converter.DorisRowConverter ,lambda$createExternalConverter$81fa9aea33 (DorisRowConverter.java:239)
at org.apache.doris.flink.deserialization.converter.DorisRowConverter.
```

1. 在 mysql 中 tinyint 会被 cdc 识别为 boolean 类型，造成转换错误

2. 添加以下参数
```shell
'jdbc.properties.tinyInt1isBit' = 'false',
'jdbc.properties.transformedBitIsBoolean' = 'false',
```

**<font color="blue" size="3.6">Q3：修改 sql 后无法从 checkpoint 启动，报错：</font>**
```shell
Cannot map checkpoint/savepoint state for operator xxx to the new program, because the operator is not available in the new program.
 If you want to allow to skip this, you can set the --allowNonRestoredState option on the CLI.
```

添加参数 execution.savepoint.ignore-unclaimed-state:true，跳过无法还原的算子


## 本地调试FAQ


Q1: 为什么不支持除了 Java8 和 Java11 以外的其他版本呢？

> A1: 因为 Flink 目前仅支持 Java8 和 Java11。

--- 

Q2: 为什么 Maven Profile 切换了不生效呢?? 提交任务时还是报各种依赖问题,Profile 像是不生效呢?????

> A2-1: 因为你没刷新 Maven Profile，导致不生效

> A2-2: 因为虽然你刷新了 Maven Profile, 没重启 Dinky 服务(不要问为什么需要重启,这是一个开发人员的基本认知),导致依赖没包含在当前已启动的服务中.

> A2-3: Profile 切的不对,注意灰色的 Profile 选项.请仔细仔细仔细仔细的看看.

> A2-4: 查看你的 IDEA 的版本,不要太旧,尽量保持在 2022.x 以上(别问为什么,上边已经说了)

> A2-5: Profile 切换加载,基于依赖的 `<scope></scope>`标签属性声明 ,如果不懂,自行百度/谷歌/CSDN/StackOverFlow/ChatGPT

--- 

Q3: 我在 IDEA 中启动 Dinky 后, 前端页面访问不了, 报错找不到页面??????

> A3-1: 可以在执行 Install 阶段勾选 `web` Profile,不然 dinky-admin/src/main/resources/ 下没有静态资源文件目录 `static`.

> A3-2: 可以单独启动前端,参考 [本地调试-启动前端](developer_guide/local_debug#启动前端) 部分

--- 

Q4: 为什么在 IDEA 中启动 Dinky 后，Profile 也加载了,我用到了一个 connector 仍然报错找不到类?????

> A4-1: Dinky 只加载了 Dinky 在开发中过程中用到的相关 Flink 依赖以及 Flink 的基本环境依赖.如报此类错误,请检查你的 pom.xml 文件,是否包含了 connector 所依赖的 jar 包

> A4-2: 如上述问题未解决,请检查你的 `dinky-flink` 模块下的与你Flink 版本一致的 `pom.xml` 文件,是否包含了 connector 所依赖的 jar 包
