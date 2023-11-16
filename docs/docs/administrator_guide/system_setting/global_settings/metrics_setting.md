---
position: 6
id: metrics_setting
title: Metrics 配置
---
此配置决定监控页面JVM图的数据采集和展示
![metrics_setting](http://www.aiwenmo.com/dinky/docs/test/metrics_setting.png)

**参数配置说明:**
- **Dinky JVM Monitor 开关：** 此开关会关系到Dinky JVM Monitor，决定监控页面中的Dinky Server显示，以及JVM Metrics采集
- **Dinky JVM Metrics 采集时间粒度：** Dinky JVM Metrics 采集时间粒度，定时任务间隔触发
- **Flink Metrics 采集时间粒度：** Flink Metrics 采集时间粒度，定时任务间隔触发
- **Flink Metrics 采集时间粒度，定时任务间隔触发：** Flink Metrics 采集超时时长，定时任务间隔触发（此配置项应小于Flink Metrics 采集时间粒度）
