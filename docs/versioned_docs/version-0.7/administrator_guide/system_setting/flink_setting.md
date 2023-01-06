---
position: 2
id: flink_setting
title: Flink 设置
---




当用户使用 **Application 模式**以及 **RestAPI** 时，需要在 **Flink 设置** 页面进行相关修改。

另外**Application 模式** 支持**Yarn** 和 **Kubernetes**，启用 **RestAPI** 后，Flink 任务的 savepoint,停止等操作都将会通过 JobManager 的 RestAPI 进行。

首先进入**系统设置**中的**Flink设置**，对参数配置进行修改即可。

![flink_setting](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/system_setting/flink_setting/flink_setting.png)

**参数配置说明:**

- **提交 FlinkSQL 的 Jar文件路径:** 此参数是为了引入 Dinky 中提交 **Application 模式**的 jar包文件，
  - **服务器部署方式:** 需要上传到相应的HDFS路径，jar包在 Dinky 解压根目录下的jar文件夹下；eg: hdfs:///dlink/jar/dlink-app-${dlink-version}-jar-with-dependencies.jar
  - **本地调试方式:** 需要本地 install后 将其设置为eg: $idea_work_dir/dlink/dlink-app/target/dlink-app-${dlink-version}-jar-with-dependencies.jar
- **提交 FlinkSQL 的 Jar 的主类入参：** 默认为空，不需要修改，配合提交FlinkSQL的Jar文件路径使用；
- **提交 FlinkSQL 的 Jar 的主类：** 默认 com.dlink.app.MainApp,不需要修改，配合提交FlinkSQL的Jar文件路径使用；
- **使用 RestAPI:** 默认开启,开启后 FlinkSQL 停止等操作通过此参数进行；
- **FlinkSQL 语句分隔符:** 默认是分号，即";"。多个语句间可以用分号隔开； 此项支持自定义 eg: **;\r\n**