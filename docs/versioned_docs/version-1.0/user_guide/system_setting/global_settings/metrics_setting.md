---
position: 6
sidebar_position: 6
id: metrics_setting
title: Metrics 配置
---

:::info 简介

Dinky1.0增加了Metrics监控功能，可以实现对 Dinky Server的JVM信息的监控

如开启Dinky JVM Monitor开关，可以在`监控`中看到 Dinky Server 的实时的 JVM 等信息

:::

![metrics_setting](http://pic.dinky.org.cn/dinky/docs/test/metrics_setting.png)

**参数配置说明:**

| 参数名称                              | 参数说明                                                            |
|:----------------------------------|:----------------------------------------------------------------|
| **Dinky JVM Monitor 开关**          | 此开关会关系到Dinky JVM Monitor，决定监控页面中的Dinky Server显示，以及JVM Metrics采集 |
| **Dinky JVM Metrics 采集时间粒度**      | Dinky JVM Metrics 采集时间粒度，定时任务间隔触发                               |
| **Flink Metrics 采集时间粒度**          | Flink Metrics 采集时间粒度，定时任务间隔触发                                   |
| **Flink Metrics 采集时间粒度，定时任务间隔触发** | Flink Metrics 采集超时时长，定时任务间隔触发（此配置项应小于Flink Metrics 采集时间粒度）      |