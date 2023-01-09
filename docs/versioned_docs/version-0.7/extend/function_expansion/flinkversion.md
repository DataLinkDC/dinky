---
sidebar_position: 1
id: flinkversion
title: 扩展 Flink 版本
---




## 扩展其他版本的 Flink

**dlink-catalog-mysql**、**dlink-client**、**dlink-app**。

**lib** 目录下默认的上面三个依赖对应的 flink 版本可能和你想要使用的 flink 版本不一致，需要进入到平台的 **lib** 目录下查看具体的上面三个依赖对应的 flink 版本，
如果不一致，则需要删除 **lib** 目录下的对应的上面三个依赖包，然后从 **extends** 和 **jar** 目录下找到合适的包，拷贝到 **lib** 目录下。

比如 **lib** 目录下的 **dlink-client-1.14-0.6.7.jar** ，表示使用的 flink 版本为 1.14.x ，
如果你在 **plugins** 目录下上传的 flink 用到的 jar 包的版本不是 1.14.x ，就需要更换 **dlink-client** 包。


切换版本时需要同时更新 plugins 下的 Flink 依赖。