---
sidebar_position: 2
id: remote_link_k8s
title: 远程连接 k8s 集群
---

## 文件准备
1. 获取 .kube/config，并放在本地机器上面 

> 通常执行 cat ~/.kube/config 指令，可以获得该文件的内容。
> 
> 如果是 kubeadm 或者基于 kubeadm （例如 kuboard-spray）的安装工具安装的 Kubernetes 集群，请在控制节点执行 cat /etc/kubernetes/admin.conf
> 
> 如果是 k3s 请执行 cat /etc/rancher/k3s/k3s.yaml

:::warning 注意事项

部分 kubernetes 集群（例如 Amazon AKS）因为 kubeconfig 文件的内容不同于 kubeadm 安装的集群，暂不支持使用 kubeconfig。

以上的内容参考来自：[Kuboard](https://kuboard.cn/)
:::
0