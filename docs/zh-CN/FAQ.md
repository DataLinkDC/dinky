1.Flink on Yarn HA高可用,配置hdfs依赖,无法识别HDFS高可用访问地址别名，在Perjob和application模式，提交任务，出现异常信息

![HDFS集群别名](http://www.aiwenmo.com/dinky/dev/docs/HDFS%E9%9B%86%E7%BE%A4%E5%88%AB%E5%90%8D.png)

**解决办法：**

```
#解决方案一
#添加HADOOP_HOME环境变量，修改 /etc/profile
export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
#解决方案二
auto.sh里加一行export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
```



2.出现commons-cli异常报错，需要在如下路径放入commons-cli依赖

**解决办法：**

```
下载common-cli包，需要在如下路径放置：
在Flink的下的lib
在dinky下的plugins
HDFS的/flink/lib/
```

3.依赖冲突

![dependency_conflict](http://www.aiwenmo.com/dinky/docs/zh-CN/FAQ/dependency_conflict.png)

**解决办法：**

```
添加flink-shade-hadoop-uber-3包后，请手动删除该包内部的javax.servlet 等冲突内容
```

4.连接hive异常

```shell
异常信息 Caused by: java.lang.ClassNotFoundException: org.apache.http.client.HttpClient
```

![hive_http_error](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/registerCenter/database_manager/hive_http_error.png)

**解决办法:** 

在plugins下添加以下包

```shell
httpclient-4.5.3.jar
httpcore-4.4.6.jar
```

