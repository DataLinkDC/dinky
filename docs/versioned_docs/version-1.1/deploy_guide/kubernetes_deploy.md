---
sidebar_position: 4
position: 4
id: kubernetes_deploy
title: kubernetes 部署
---

Kubernetes 部署目的是在 Kubernetes 集群中部署 dinky 服务，能快速、便捷在生产中部署。

## 前置条件
- Helm 3.1.0+
- Kubernetes 1.12+
  
##  安装 dinky
### 下载安装包
https://www.dinky.org.cn/download/download

### 修改配置
#### 修改镜像地址
cd dinky-release-${FLINK-VERSION}-${DINKY-VERSION}/deploy/kubernetes/helm/dinky
```yaml
image:
  repository: "dinkydocker/dinky-standalone-server"
  pullPolicy: "Always"
  tag: "1.1.0-flink1.17"
  pullSecret: ""
```
#### 内部数据源(同一个k8环境中部署mysql服务)
修改values.yaml 为mysql数据源
```yaml
mysql:
  enabled: true
  url: "172.168.1.111:31476"
  auth:
    username: "dinky"
    password: "bigdata123!@#"
    database: "dinky-dev-1-17"
```
如果数据源为postgresql，则修改如下
```yaml
postgresql:
  enabled: true
  url: "172.168.1.111:31476"
  auth:
    username: "dinky"
    password: "bigdata123!@#"
    database: "dinky-dev-1-17"
```
修改flink版本，如下修改为1.17版本
```yaml
spec:
  replicaCount: 1
  containerPort: 8888
  name: rest
  extraEnv:
    flinkVersion: "1.17"
```
例如，mysql数据源完整配置如下：
完整配置如下：
```yaml
#
#  Licensed to the Apache Software Foundation (ASF) under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The ASF licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#

timezone: "Asia/Shanghai"

nameOverride: ""
fullnameOverride: ""

image:
  repository: "dinkydocker/dinky-standalone-server"
  pullPolicy: "Always"
  tag: "1.1.0-flink1.17"
  pullSecret: ""

mysql:
  enabled: true
  url: "172.168.1.111:31476"
  auth:
    username: "dinky"
    password: "bigdata123!@#"
    database: "dinky-dev-1-17"

postgresql:
  enabled: false
  url: "172.168.1.111:31476"
  auth:
    username: "dinky"
    password: "bigdata123!@#"
    database: "dinky-dev-1-17"

externalDatabase:
  enabled: false
  type: "mysql"
  url: "10.43.2.12:3306"
  auth:
    username: "root"
    password: "Dinky@1234567!"
    database: "dinky"

externalDatabaseService:
  clusterIP: 10.43.2.12
  port: 3306

externalDatabaseEndpoints:
  ip: 172.168.1.110
  port: 3306

rbac:
  create: true

spec:
  replicaCount: 1
  containerPort: 8888
  name: rest
  extraEnv:
    flinkVersion: "1.17"

  affinity: {}
  nodeSelector: {}
  tolerations: []
  resources: {}
  # resources:
  #   limits:
  #     memory: "2Gi"
  #     cpu: "1"
  #   requests:
  #     memory: "1Gi"
  #     cpu: "500m"
  livenessProbe:
    enabled: true
    initialDelaySeconds: "90"
    periodSeconds: "30"
    timeoutSeconds: "20"
    failureThreshold: "3"
    successThreshold: "1"

  readinessProbe:
    enabled: true
    initialDelaySeconds: "90"
    periodSeconds: "30"
    timeoutSeconds: "20"
    failureThreshold: "3"
    successThreshold: "1"

ingress:
  enabled: false
  className: ""
  annotations: {}
    # kubernetes.io/ingress.class: nginx
    # kubernetes.io/tls-acme: "true"
  hosts:
    - host: demo.dinky.org.cn
      paths:
        - path: /
          pathType: ImplementationSpecific
  tls: []


service:
  ## type determines how the Service is exposed. Defaults to ClusterIP. Valid options are ExternalName, ClusterIP, NodePort, and LoadBalancer
  type: "ClusterIP"
  name: "dinky"

dinkyDefaultConfiguration:
  create: true
  append: true

dinkyServiceAccount:
  create: true
  annotations: {}
  name: "dinky"
```
注意：数据源只能启用一个，不能同时启用mysql和postgresql

#### 外部数据源
修改k8s集群外mysql服务配置
```yaml
externalDatabaseEndpoints:
  ip: 172.168.1.110
  port: 3306
```
修改k8s集群内映射mysql服务配置
```yaml
externalDatabaseService:
  clusterIP: 10.43.2.12
  port: 3306
```
修改mysql服务配置
```yaml
externalDatabase:
  enabled: true
  type: "mysql"
  url: "10.43.2.12:3306"
  auth:
    username: "root"
    password: "Dinky@1234567!"
    database: "dinky"
```

完整案例如下：
```yaml
#
#  Licensed to the Apache Software Foundation (ASF) under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The ASF licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#

timezone: "Asia/Shanghai"

nameOverride: ""
fullnameOverride: ""

image:
  repository: "dinkydocker/dinky-standalone-server"
  pullPolicy: "Always"
  tag: "1.1.0-flink1.17"
  pullSecret: ""

mysql:
  enabled: false
  url: "172.168.1.111:31476"
  auth:
    username: "dinky"
    password: "bigdata123!@#"
    database: "dinky-dev-1-17"

postgresql:
  enabled: false
  url: "172.168.1.111:31476"
  auth:
    username: "dinky"
    password: "bigdata123!@#"
    database: "dinky-dev-1-17"

externalDatabase:
  enabled: true
  type: "mysql"
  url: "10.43.2.12:3306"
  auth:
    username: "root"
    password: "Dinky@1234567!"
    database: "dinky"

externalDatabaseService:
  clusterIP: 10.43.2.12
  port: 3306

externalDatabaseEndpoints:
  ip: 172.168.1.110
  port: 3306

rbac:
  create: true

spec:
  replicaCount: 1
  containerPort: 8888
  name: rest
  extraEnv:
    flinkVersion: "1.17"

  affinity: {}
  nodeSelector: {}
  tolerations: []
  resources: {}
  # resources:
  #   limits:
  #     memory: "2Gi"
  #     cpu: "1"
  #   requests:
  #     memory: "1Gi"
  #     cpu: "500m"
  livenessProbe:
    enabled: true
    initialDelaySeconds: "90"
    periodSeconds: "30"
    timeoutSeconds: "20"
    failureThreshold: "3"
    successThreshold: "1"

  readinessProbe:
    enabled: true
    initialDelaySeconds: "90"
    periodSeconds: "30"
    timeoutSeconds: "20"
    failureThreshold: "3"
    successThreshold: "1"

ingress:
  enabled: false
  className: ""
  annotations: {}
    # kubernetes.io/ingress.class: nginx
    # kubernetes.io/tls-acme: "true"
  hosts:
    - host: demo.dinky.org.cn
      paths:
        - path: /
          pathType: ImplementationSpecific
  tls: []


service:
  ## type determines how the Service is exposed. Defaults to ClusterIP. Valid options are ExternalName, ClusterIP, NodePort, and LoadBalancer
  type: "ClusterIP"
  name: "dinky"

dinkyDefaultConfiguration:
  create: true
  append: true

dinkyServiceAccount:
  create: true
  annotations: {}
  name: "dinky"
```

### 部署
将名为 dinky 的版本(release) 发布到 dinky 的命名空间中：
```shell
cd dinky-release-${FLINK-VERSION}-${DINKY-VERSION}/deploy/kubernetes/helm/dinky

helm install dinky . -n dinky
```

### 卸载
```shell
cd dinky-release-${FLINK-VERSION}-${DINKY-VERSION}/deploy/kubernetes/helm/dinky

helm uninstall dinky  -n dinky
```





