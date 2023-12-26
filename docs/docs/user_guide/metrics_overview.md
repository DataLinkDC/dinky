---
position: 6
sidebar_position: 6
id: metrics_overview
title: 监控
---

:::info 简介
Dinky1.0增加了Metrics监控功能，可以实现对 Dinky Server的 JVM 信息的监控 
如需要查看 Dinky Server 的实时的 JVM 等信息，需要再 **配置中心** > **全局配置** > **[Metrics 配置](../system_setting/global_settings/metrics_setting)** 中开启 Dinky JVM Monitor 开关

注意: 在 Dinky v1.0.0 版本及以上，Metrics 监控功能默认关闭，需要手动开启，否则无法正常监控

:::


### Dinky Server 监控


![metrics_overview_dinky](http://pic.dinky.org.cn/dinky/docs/test/metrics_overview_dinky.png)


### Flink 监控

> 此功能 主要展示在 **运维中心** > **任务详情** > **监控** 中 定义的监控指标


![metrics_flink](http://pic.dinky.org.cn/dinky/docs/test/metrics_flink.png)