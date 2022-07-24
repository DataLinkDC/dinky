---
sidebar_position: 1
id: flinkversion
title: 扩展 Flink 版本
---




## 扩展其他版本的 Flink

Flink 的版本取决于 lib 下的 dlink-client-1.13.jar。当前版本默认为 Flink 1.13.6 API。向其他版本的集群提交任务可能存在问题，已实现 1.11、1.12、1.13、 1.14、1.15，切换版本时只需要将对应依赖在lib下进行替换，然后重启即可。

切换版本时需要同时更新 plugins 下的 Flink 依赖。