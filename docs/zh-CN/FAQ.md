Flink on Yarn HA高可用,配置hdfs依赖,无法识别HDFS高可用访问地址别名，在Perjob和application模式，提交任务，出现异常信息

![HDFS集群别名](http://www.aiwenmo.com/dinky/dev/docs/HDFS%E9%9B%86%E7%BE%A4%E5%88%AB%E5%90%8D.png)

**解决办法：**

```
#解决方案一
#添加HADOOP_HOME环境变量，修改 /etc/profile
export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
#解决方案二
auto.sh里加一行export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
```



出现commons-cli异常报错，需要在如下路径放入commons-cli依赖

**解决办法：**

```
下载common-cli包，需要在如下路径放置：
在Flink的下的lib
在dinky下的plugins
HDFS的/flink/lib/
```



