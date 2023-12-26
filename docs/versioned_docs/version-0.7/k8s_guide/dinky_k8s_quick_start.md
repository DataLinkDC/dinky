---
sidebar_position: 1
id: dinky_k8s_quick_start
title: Dinky快速集成k8s
---
## 镜像制作

### Dockerfile模板修改
`Dinky_HOME/config/DinkyFlinkDockerfile` 提供 Dockerfile，内置flink1.14制作方案。如需使用其他版本，请按照对应修改；
![DinkyFlinkDockerfile.png](http://pic.dinky.org.cn/dinky/dev/docs/k8s/DinkyFlinkDockerfile.png)

如果你需要自行制作容器，请跳过如下步骤，并补充 kubernetes.container.image
![k8s_container.png](http://pic.dinky.org.cn/dinky/dev/docs/k8s/k8s_container.png)

### 镜像构建
 `注册中心 -> 集群管理 -> 集群配置管理 -> 新建 -> 测试`
 ![add_k8s_conf.png](http://pic.dinky.org.cn/dinky/dev/docs/k8s/add_k8s_conf.png)
> 当配置好信息后，点击测试，大约3-5分钟左右就出现测试成功案例，此刻输入 `docker images` ，即可查看构建成功的镜像
 
![docker_images.png](http://pic.dinky.org.cn/dinky/dev/docs/k8s/docker_images.png)
#### 参数详解
* instance:  容器实例，本地：unix:///var/run/docker.sock  或者 远程：tcp://remoteIp:2375
* registry.url: hub容器地址，如：(阿里云，docker.io，harbor)
* （registry-username  registry-password）: hub登录凭证
* image-namespace: 镜像命名空间
* image-storehouse: 镜像仓库
* image-dinkyVersion: 镜像版本
* dinky远程地址： 此参数是k8s 容器与dinky通讯的地址

> 完成剩下的参数配置，即可使用 `k8s application` 模式