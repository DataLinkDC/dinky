---
sidebar_position: 6
id: flinktaskmonitor
title: flink 任务运行监控 
---




## 说明

本文章主要说明对运行过程中的 flink 任务的监控，采用 **prometheus+pushgateway+grafana** 方式，为非平台内部监控方案。

## 前提

公司的服务器已经安装了 prometheus 和 pushgateway 网关服务，如果为安装，需要运维人员进行安装，或是自行安装。

## 介绍

Flink 提供的 Metrics 可以在 Flink 内部收集一些指标，通过这些指标让开发人员更好地理解作业或集群的状态。
由于集群运行后很难发现内部的实际状况，跑得慢或快，是否异常等，开发人员无法实时查看所有的 Task 日志。 Metrics 可以很好的帮助开发人员了解作业的当前状况。

Flink 官方支持 Prometheus，并且提供了对接 Prometheus 的 jar 包，很方便就可以集成。
在 FLINK_HEME/plugins/metrics-prometheus 目录下可以找到 **flink-metrics-prometheus** 包。


## 配置步骤

###  修改Flink配置

修改 **flink-conf.yaml** 文件，该文件和集群配置有关，如果是 Standalone，Yarn Session 和 Kubernetes Session 模式，则需要修改启动本地集群或 session 集群时的 flink 目录下的 flink-conf.yaml 文件。
如果是 Yarn Per-job、Yarn Application 和 Kubernetes Application 模式，则需要修改创建集群时指定的 flink 配置目录下的 flink-conf.yaml 文件，
同时需要将上面提到的 **flink-metrics-prometheus** 上传到平台的 **plugins** 目录和 **hdfs** 上的 **flink lib** 目录下。

```shell
vim flink-conf.yaml
```

添加如下配置：

```yaml
##### 与 Prometheus 集成配置 #####
metrics.reporter.promgateway.class: org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter
# PushGateway 的主机名与端口号
metrics.reporter.promgateway.host: node01
metrics.reporter.promgateway.port: 9091
# Flink metric 在前端展示的标签（前缀）与随机后缀
metrics.reporter.promgateway.jobName: flink-application
metrics.reporter.promgateway.randomJobNameSuffix: true
metrics.reporter.promgateway.deleteOnShutdown: false
metrics.reporter.promgateway.interval: 30 SECONDS
```

之后启动 flink 任务，然后就可以通过 grafana 来查询 prometheus 中网关的数据了。

### grafana 监控方案

https://grafana.com/search/?term=flink&type=dashboard

通过上面网站搜索 flink ，我们就可以找到其他用户共享的他们使用的 flink 任务监控方案，从结果中找到自己喜欢的监控方案，下载对应的 json 文件后，上传到 grafana 即可实现对任务的监控。