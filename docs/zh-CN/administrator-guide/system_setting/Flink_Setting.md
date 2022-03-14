
当用户使用Application模式以及RestAPI时，需要在Flink设置页面进行相关修改。

另外Application模式支持Yarn和Kubernetes，启用RestAPI后，Flink任务的savepoint,停止等操作都将会通过JobManager的RestAPI进行。

首先进入系统设置中的Flink设置，对参数配置进行修改即可。

![image-20220314221926706](http://www.aiwenmo.com/dinky/dev/docs/image-20220314221926706.png)

**参数配置说明:** 

- **提交FlinkSQL的Jar文件路径:** 此参数是为了引入Dinky中提交Application模式的jar包文件，需要上传到相应的HDFS路径，jar包在dinky解压根目录下的jar文件夹下；
- **提交FlinkSQL的Jar的主类入参：** 默认为空，不需要修改，配合提交FlinkSQL的Jar文件路径使用；
- **提交FlinkSQL的Jar的主类：** 默认com.dlink.app.MainApp,不需要修改，配合提交FlinkSQL的Jar文件路径使用；
- **使用RestAPI:** 默认开启,开启后FlinkSQL停止等操作通过此参数进行；
- **FlinkSQL语句分隔符:** 默认是分号，即";"。多个语句间可以用分号隔开；