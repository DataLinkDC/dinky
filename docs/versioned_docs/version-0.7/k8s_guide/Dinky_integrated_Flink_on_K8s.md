---
sidebar_position: 3
id: Dinky_integrated_Flink_on_K8s
title: Dinky集成Flink on K8S
---

# Dinky集成Flink on K8S

## Dinky集成K8S Session模式

> Session 模式是一种需要**提前分配集群资源**并通过共享的 Session 集群来运行 Flink 任务的部署模式
>
> 该模式部署方便，并且比较灵活在添加删除新的Flink任务时不用再启动或关闭Flink集群，**此模式不适合生产环境使用**

下面是两种Flink On K8S Session部署模式，**两种模式任选一种**

- 第一种是通过Flink官网文档部署
- 第二种是通过单个FlinkDeployment文件部署

### 【方式一】：Flink官网文档部署Session集成Dinky

#### 部署Flink

需要几个配置文件，在flink官网[ standalone session 文档](https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/standalone/kubernetes/#session-%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E5%AE%9A%E4%B9%89)处可以找到，注意这里的版本，切换至自己对于**大版本号**，如：`1.16\1.17`

![image-20230922160212040](http://pic.dinky.org.cn/dinky/docs/test/202312201518540.png) 

Session模式需要几个配置文件，在官网文档可以找到，也可以点击下方超链接跳转，然后新建文件，把文件改成对应名称将官网复制的内容填入进来

![image-20230922160614885](http://pic.dinky.org.cn/dinky/docs/test/202312201518305.png)

- [通用集群资源定义](https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/standalone/kubernetes/#%E9%80%9A%E7%94%A8%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E5%AE%9A%E4%B9%89)：
  - `flink-configuration-configmap.yaml`  【flink-conf.yml配置文件】，如需要更改分配资源需在此处配置
  - `jobmanager-service.yaml` 【jobmanager通信服务配置文件】
  - `jobmanager-rest-service.yaml` 【用于暴露rest端口以供外部访问的配置文件】

- [Session集群资源定义](https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/standalone/kubernetes/#session-%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E5%AE%9A%E4%B9%89)：
  - `jobmanager-session-deployment-non-ha.yaml` 【jobmanager核心配置文件，non-ha是 非高可用配置文件】，此处不能更改replicas副本数，否则会报错，如需高可用选带ha的配置文件，并自行配置[高可用](https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/ha/overview/)
  - `taskmanager-session-deployment.yaml` 【taskmanager核心配置文件】此处可根据需求更改tm的副本数


将五个文件都在本地新建，并将官网内容复制到各个文件中

```sh
touch flink-configuration-configmap.yaml
touch jobmanager-service.yaml
touch jobmanager-rest-service.yaml
touch jobmanager-session-deployment-non-ha.yaml
touch taskmanager-session-deployment.yaml
```

![image-20230922161756584](http://pic.dinky.org.cn/dinky/docs/test/202312201518348.png) 

 这里注意，如果有端口指定需求或者已经有端口冲突，请更改`jobmanager-rest-service.yaml`文件中的`nodePort`k8s  servcie端口号，本例采用默认的，*如果没有需求跳过此步骤，直接下一步*

![image-20230922162641149](http://pic.dinky.org.cn/dinky/docs/test/202312201518623.png) 

按照顺序执行文件即可，若已经创建命名空间可忽略第一条命令

```sh
kubectl create ns flink
kubectl apply -f flink-configuration-configmap.yaml -n flink
kubectl apply -f jobmanager-rest-service.yaml -n flink
kubectl apply -f jobmanager-service.yaml -n flink
kubectl apply -f taskmanager-session-deployment.yaml -n flink
kubectl apply -f jobmanager-session-deployment-non-ha.yaml -n flink
```

![image-20230922162234672](http://pic.dinky.org.cn/dinky/docs/test/202312201518718.png) 

等待镜像的拉取

```sh
kubectl  get pod,svc -n flink
```

![image-20230922162348324](http://pic.dinky.org.cn/dinky/docs/test/202312201518499.png) 

在镜像拉取完成后在浏览器输入主机ip和rest的端口，`主机ip:30081`即可查看Flink web Dashboard ，可通过submit提交flink-jar的任务。

![image-20230922162846944](http://pic.dinky.org.cn/dinky/docs/test/202312201517487.png)

到这里【方式一】k8s session安装完毕，下面开始集成Dinky

#### 集成dinky

由于本例测试是在虚拟机中测试，就只有一个主机ip，如果是在云服务器中部署，需要注意内外网的ip区分

记住这个jobmanager-rest服务对外暴露的端口，本例为`30081`

![image-20230922223242070](http://pic.dinky.org.cn/dinky/docs/test/202312201517276.png)

打开`dinky` - 进入`注册中心` - 点击左侧`集群管理` - 点击`Flink 实例管理` - 点击`新建` - 类型选择`Kubernetes Session`

如下图，填入任意名称与别名，填入`主机ip:端口`，并点击启用，点击完成即添加成功

- 如果是云服务器需要填内网的，填外网是不通的，*本地主机可忽略*

![image-20230922224304727](http://pic.dinky.org.cn/dinky/docs/test/202312201517180.png)

#### 提交任务测试

```sql
-- 创建数据生成器源表
CREATE TABLE IF NOT EXISTS test1 (
  id INT,
  vc INT,
  pt AS PROCTIME(), --处理时间
  et AS cast(CURRENT_TIMESTAMP as timestamp(3)), --事件时间
  WATERMARK FOR et AS et - INTERVAL '5' SECOND   --watermark
) WITH (
  'connector' = 'datagen',
  'rows-per-second' = '10',
  'fields.id.min' = '1',
  'fields.id.max' = '3',
  'fields.vc.min' = '1',
  'fields.vc.max' = '100'
);

SELECT *
FROM test1;
```

进入`数据开发`，在执行模式中选择`k8s session`模式,，在Flink 集群选择刚配置好的集群，点击上方的`执行任务`即可提交任务到该集群运行

![image-20230922230505421](http://pic.dinky.org.cn/dinky/docs/test/202312201517662.png)

进入Flink web Dashboard即可查看到该任务

![image-20230922230013915](http://pic.dinky.org.cn/dinky/docs/test/202312201521051.png)

第一种方式部署集成完毕，这里提前说明，如果使用到了三方Jar包，集群中是一套单独的环境，其中是没有相关Jar依赖的，需要通过Dockerfile将jar依赖打入`jobmanager-session-deployment-non-ha.yaml`和``taskmanager-session-deployment.yaml` 文件的镜像中的`/opt/flink/lib`下，并且日志会随着pod关闭而永久丢失，如果有**日志持久化**、**便携式添加三方Jar包**、**启用S3插件**等需求见下方的**注意事项**

------

### 【方式二】：FlinkDeployment部署Session集成Dinky

#### 部署Flink

这个部署需要通过**helm**包安装工具来进行部署，**必须使用helm3及其以上版本**进行部署，这里不演示 helm的安装方式，自行[helm官网](https://helm.sh/zh/)下载并且给予相关权限，自行安装即可。

![image-20230922164005629](http://pic.dinky.org.cn/dinky/docs/test/202312201517279.png)

另外提前安装`cert-manager`，国内由于网络原因会下载不下来，这里做了一份阿里云镜像的，可以通过下方的链接**任选一种**进行下载保存，如果不部署这个，helm部署flink会报错，下载后进入到`cert-manager.yaml`文件的当前目录执行 下面的命令即可。

- 百度网盘：https://pan.baidu.com/s/1tVt80DMtdoqfAL45AhXdTg?pwd=dink 提取码：`dink`
- 阿里网盘：https://www.aliyundrive.com/s/BwoGifQ478L 提取码：`41vr`
- 123网盘：https://www.123pan.com/s/PefzVv-q9pBd.html 提取码:`dink`

```sh
kubectl create ns cert-manager
kubectl apply -f cert-manager.yaml
```

![image-20230922181835107](http://pic.dinky.org.cn/dinky/docs/test/202312201517292.png)



截止目前，这种helm安装方式的最新版本是1.16.0，详情见官网：https://flink.apache.org/zh/downloads/#apache-flink-kubernetes-operator-2

请自行选择对应版本，【方式一】直接下载包并上传服务器指定位置，或者直接通过`wget`或`curl -O`的方式下载安装，【方式二】通过添加helm repo仓库的方式进行安装，本例使用【方式二】`helm repo`进行安装

```sh
#【方式一】保存helm charts包再使用helm进行安装
wget https://archive.apache.org/dist/flink/flink-kubernetes-operator-1.6.0//flink-kubernetes-operator-1.6.0-helm.tgz

#【方式二】添加helm仓库，更新，通过helm repo进行安装
helm repo add flink-operator https://downloads.apache.org/flink/flink-kubernetes-operator-1.6.0
helm repo update
```

![image-20230922165505016](http://pic.dinky.org.cn/dinky/docs/test/202312201521874.png)

导出保存一份`values.yaml`文件到本地，由于镜像访问比较慢，这里我上传了一份阿里云的镜像，如果镜像拉取不下来可以换下方的阿里云镜像

```sh
helm show values flink-operator/flink-kubernetes-operator > values.yaml
```

![image-20230923092502889](http://pic.dinky.org.cn/dinky/docs/test/202312201517342.png)

vim打开values.yaml文件在26行这将其替换为阿里云的地址

![image-20230923092102159](http://pic.dinky.org.cn/dinky/docs/test/202312201522396.png)

替换为以下镜像，并保存

```sh
registry.cn-beijing.aliyuncs.com/yilinwei/flink-kubernetes-operator
```

![image-20230923092108351](http://pic.dinky.org.cn/dinky/docs/test/202312201517514.png)

helm开始安装并指定values文件，指定命名空间

```sh
helm install flink-kubernetes-operator flink-operator/flink-kubernetes-operator --namespace flink --create-namespace -f ./values.yaml
```

等待镜像的拉取

![image-20230923093446576](http://pic.dinky.org.cn/dinky/docs/test/202312201522741.png)

![image-20230923093612409](http://pic.dinky.org.cn/dinky/docs/test/202312201517083.png) 

在拉取完成后，新建一个 `flink-session-operator.yaml`文件,将文件内容填入其中

**下方的【大版本号】及【小版本号】根据自己需求进行修改，默认也可**

**这里分配的资源是供整个集群中的任务分配调度的，后续自动创建是在这里已分配资源基础上再进行单个任务的分配的，适合测试环境使用**

```yaml
# Flink Session集群
apiVersion: flink.apache.org/v1beta1
kind: FlinkDeployment
metadata:
  namespace: flink
  name: session-operator
spec:
  image: flink:1.16.0 #【小版本号】  替换如: flink:1.16.1、flink:1.16.2、flink:1.16.3 ...
  flinkVersion: v1_16 #【大版本号】，替换如：V1_17、V1_16、V1_15 ...
  imagePullPolicy: IfNotPresent   # 镜像拉取策略，本地没有则从仓库拉取
  flinkConfiguration:
    taskmanager.numberOfTaskSlots: "2"   #可根据自己配置进行调整
  serviceAccount: flink #serviceaccount账号，需要确保该命名空间下的该账户有权限，会自动创建与删除taskmanager
  jobManager:
    replicas: 1  #非高可用不可更改此处
    resource:
      memory: "1024m"  #可根据自己配置进行调整
      cpu: 1    #可根据自己配置进行调整
  taskManager:
    replicas: 2    #可根据自己配置进行调整
    resource:
      memory: "1024m"    #可根据自己配置进行调整
      cpu: 1      #可根据自己配置进行调整

--- 

apiVersion: v1
kind: Service
metadata:
  namespace: flink
  name: session-jobmanager-rest-web
spec:
  selector:
    app: session-operator
    component: jobmanager
    type: flink-native-kubernetes
  ports:
    - name: session-rest-port
      port: 8081
      targetPort: 8081
      nodePort: 30082  #【对外暴露端口号】有需要可更改
  type: NodePort
```

执行该文件，等待创建完成

![image-20230923093937602](http://pic.dinky.org.cn/dinky/docs/test/202312201517128.png) 

查看端口，上方设置的NodePort暴露端口为`30082`

![image-20230923095022036](http://pic.dinky.org.cn/dinky/docs/test/202312201523277.png)

进入web端，输入`ip:30082`查看Flink Web Dashboardm，**这里没有显示资源，并且Pod只有一个，是因为该模式会在提交任务后根据提前设置好的资源进行创建，任务结束后会关闭对应的资源。**

![image-20230923095147413](http://pic.dinky.org.cn/dinky/docs/test/202312201523640.png)

#### 集成dinky

添加Dinky步骤同方式一，这里就不重复演示添加步骤了

![image-20230923095513543](http://pic.dinky.org.cn/dinky/docs/test/202312201517345.png)

#### 提交任务测试

选中对应的模式和集群，提交`【方式一】`中的测试任务，并进入web端查看

![image-20230923095757319](http://pic.dinky.org.cn/dinky/docs/test/202312201517647.png)



web端查看任务提交成功

![image-20230923095830861](http://pic.dinky.org.cn/dinky/docs/test/202312201517176.png)

这里有个特别的点，通过此种方式部署**web端没有Cancel Job按钮，需要在Dinky种点击结束任务**，然后稍等片刻，Taskmanager会自动退出，只留下一个Jobmanager

![image-20230923095915739](http://pic.dinky.org.cn/dinky/docs/test/202312201517922.png)

查看Pod也被自动创建了

![image-20230923100003258](http://pic.dinky.org.cn/dinky/docs/test/202312201517113.png)

如果需要关闭任务点击Dinky处的停止

![image-20230923100113971](http://pic.dinky.org.cn/dinky/docs/test/202312201517090.png)

web端也会被停止，并且Pod过一会也会自动退出

![image-20230923100149548](http://pic.dinky.org.cn/dinky/docs/test/202312201517363.png)

pod查看taskmanager过一会会自动退出

![](http://pic.dinky.org.cn/dinky/docs/test/202312201517498.png)

此种方式任务运行结束后日志也会丢失，如果想日志持久化以及便携式添加依赖见下方的注意事项

------

### 注意事项

> 在K8S集群中，会随机在各个节点创建Jobmanager和Taskmanager，所以需要节点共享所有的依赖、日志、插件、jar作业等，通过指定一个路径为nfs共享路径，其余从节点并挂载此路径，即可实现整个k8s集群共享此nfs文件夹，再**创建对应路径下的PVC映射，从而把容器中的日志、lib依赖挂载外部，实现不用重复打镜像即可动态添加依赖包，以及日志持久化**

#### 挂载nfs共享文件夹

- 所有节点安装

```sh
yum install -y nfs-utils
```

- 主节点上执行这个，本例选/data/nfs为共享文件夹

```sh
#自己选nfs共享文件夹
mkdir -p /data/nfs

#nfs主节点
echo "/data/nfs/ *(insecure,rw,sync,no_root_squash)" > /etc/exports

systemctl enable rpcbind --now
systemctl enable nfs-server --now
#配置生效
exportfs -r
```

- 每个从节点都执行，挂载主节点共享的路径，**不要在主节点执行这里的命令**

```sh
#查看主节点共享的
showmount -e 主节点ip

#在每个从节点上创建和主节点相同的文件夹
mkdir -p /data/nfs

mount -t nfs 主节点ip:/data/nfs /data/nfs
```

**注意，如果集群重启需要重新在主节点执行，并在从节点执行挂载nfs的命令，可以自行将其注册为一个服务，开机自启**

#### nfs持久存储类

- **必须修改下方 【修改1】、【修改2】处**，配置为上方nfs共享文件夹的路径，和主节点的ip
- 【可选】处自己判断是是否设置为默认存储类
- 创建一个`nfs-storage.yaml`的文件，`kubectl apply -f nfs-storage.yaml`

```sh
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-storage
  annotations:
    storageclass.kubernetes.io/is-default-class: "true"  #【可选】是否设置为默认存储类，这里设置为true，有需求可更改
provisioner: k8s-sigs.io/nfs-subdir-external-provisioner
parameters:
  archiveOnDelete: "true"  ## 删除pv的时候，pv的内容是否要备份

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: nfs-client-provisioner
  labels:
    app: nfs-client-provisioner
  namespace: default
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: nfs-client-provisioner
  template:
    metadata:
      labels:
        app: nfs-client-provisioner
    spec:
      serviceAccountName: nfs-client-provisioner
      containers:
        - name: nfs-client-provisioner
          image: registry.cn-beijing.aliyuncs.com/yilinwei/nfs-subdir-external-provisioner:v4.0.2
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: k8s-sigs.io/nfs-subdir-external-provisioner
            - name: NFS_SERVER
              value: 主节点ip # 【修改1】 指定自己nfs主服务器地址
            - name: NFS_PATH  
              value: /data/nfs  # 【修改2】 nfs服务器共享的目录
      volumes:
        - name: nfs-client-root
          nfs:
            server: 主节点ip # 【修改1】 指定自己nfs主服务器地址
            path: /data/nfs  # 【修改2】 nfs服务器共享的目录
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nfs-client-provisioner
  namespace: default
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-client-provisioner-runner
rules:
  - apiGroups: [""]
    resources: ["nodes"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-client-provisioner
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    namespace: default
roleRef:
  kind: ClusterRole
  name: nfs-client-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  namespace: default
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  namespace: default
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    namespace: default
roleRef:
  kind: Role
  name: leader-locking-nfs-client-provisioner
  apiGroup: rbac.authorization.k8s.io
```

#### 日志持久化

> 将容器内的文件路径通过pvc映射挂载到外部

创建pvc，日志的容量可以不用给很大，如果一个 pvc满了会保存，自动创建一个新的

```yaml
#Flink 日志 持久化存储pvc
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: session-log  # 日志 pvc名称
  namespace: flink
spec:
  storageClassName: nfs-storage   #sc名称，更改为实际的sc名称
  accessModes:
    - ReadWriteMany   #采用ReadWriteMany的访问模式
  resources:
    requests:
      storage: 5Gi    #存储容量，根据实际需要更改
```

然后可以在先前设置的`/data/nfs`路径下找到pvc映射到本地的路径，可以`ln -s`做个软连接也可以直接在此目录下查看日志

![image-20230923150553024](http://pic.dinky.org.cn/dinky/docs/test/202312201517704.png)

在nfs共享文件夹中找到pvc

![image-20230923150709955](http://pic.dinky.org.cn/dinky/docs/test/202312201517910.png)

【方式一】日志持久化更改配置文件后重新部署即可

- 映射容器内路径

  ![image-20230923150932341](http://pic.dinky.org.cn/dinky/docs/test/202312201517916.png)

- 指定pvc，日志的为session-log，重新部署后，再运行，即可将日志持久化到外部来

![image-20230923151012289](http://pic.dinky.org.cn/dinky/docs/test/202312201517975.png)



#### PVC外部挂载lib

思路同上方的日志持久化，自行更改下配置文件，以及pvc名称，以及调整pvc的大小，lib这种放依赖的可以将其调大些给`50Gi`或者更高，防止pvc满了新建一个，容器内路径为`/opt/flink/lib`，需要注意的是，lib不会从容器内映射出来，所以首次需要复制指定版本的lib文件夹到pvc下

#### PVC外部挂载Plugins

步骤思路同上，首次需要自己把依赖复制进pvc路径中，在路径映射中指定好pvc即可





## Dinky集成K8S Native模式

> 此模式需要下载Flink官方的二进制包，可在flink官网下载地址下载或者阿里云镜像站下载，本例使用flink1.16.2进行测试
>
> - Flink官网：https://flink.apache.org/downloads/
> - 阿里云镜像站：https://mirrors.aliyun.com/apache/flink/

Native模式Flink官网参考文档：https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/native_kubernetes/

### 前置条件（必要）

授予对应安装命名空间下`default`具备创建、删除Pod的RBAC权限，如本例是在`flink`这个namaspace下，所以授予`flink:default`具备RBAC权限

```sh
kubectl create rolebinding default-flink-rule --clusterrole=edit --serviceaccount=flink:default --namespace=flink
```

![image-20230923112138602](http://pic.dinky.org.cn/dinky/docs/test/202312201517056.png) 

### Native Session模式集成Dinky

#### 部署Flink

查看当前flink命名空间，是没有native的，这些是上方案例安装的，端口不冲突是可以共存的

![image-20230923113200476](http://pic.dinky.org.cn/dinky/docs/test/202312201517464.png)

进入flink解压后的根目录，执行下方命令创建native session集群

- 需要注意`kubernetes.cluster-id`这个要求是**唯一**的，即如果创建新的集群则不能与之前创建并在运行的集群id名称相同

```sh
./bin/kubernetes-session.sh \
    -Dkubernetes.namespace=flink \
    -Dkubernetes.cluster-id=my-first-flink-cluster
```

可以看到native session已经创建完成，并且和【方式二】一样，taskmanager是自动创建的

![image-20230923113321029](http://pic.dinky.org.cn/dinky/docs/test/202312201517695.png)

不过该方式的资源配置需要在命令中配置，这里举个例子，下方的classpaths是将本地的jar依赖添加进集群容器中，更多请在Flink官网了解

```sh
#例子，可跳过
./bin/kubernetes-session.sh \
    -Dkubernetes.namespace=flink \
    -Dkubernetes.cluster-id=my-first-flink-cluster \
    -Dtaskmanager.memory.process.size=1g \
    -Dtaskmanager.cpu.cores=2 \
    -Djobmanager.memory.process.size=2g \
    -Djobmanager.cpu.cores=1 \
    -Dpipeline.classpaths=/path/to/dependency.jar,/path/to/myjob.jar
```

通过命令创建的session集群svc服务端口没有对外暴露，可通过下方将其修改为NodePort将其暴露

```sh
kubectl edit service/my-first-flink-cluster-rest -n flink
```

移动到文件底部，将`ClusterIP`修改为`NodePort`，然后`wq`保存退出

![image-20230923115310306](http://pic.dinky.org.cn/dinky/docs/test/202312201517744.png) 

再查看svc，端口暴露为：`31057`，这里是随机的，如果需要指定可自行写个svc转发也可用再次edit修改svc中的`nodePort`

![image-20230923115447941](http://pic.dinky.org.cn/dinky/docs/test/202312201517790.png)

进入web端查看

![image-20230923121259953](http://pic.dinky.org.cn/dinky/docs/test/202312201517225.png)

如果不用了可通过下方命令删除session集群，这里还需要集成dinky，可跳过此步骤

```sh
kubectl delete deployment/my-first-flink-cluster -n flink
```

#### 集成Dinky

步骤同上方的Session模式，选择模式，添加`ip:31057`

![image-20230923121402696](http://pic.dinky.org.cn/dinky/docs/test/202312201517175.png)



#### 提交任务测试

测试命令在上方的k8s session 【方式一】的测试案例中复制，选择模式，选择对应的集群，保存，提交任务测试

![image-20230923121641084](http://pic.dinky.org.cn/dinky/docs/test/202312201517828.png)

web端

![image-20230923121615954](http://pic.dinky.org.cn/dinky/docs/test/202312201517527.png)

手动在dinky结束任务或者web端cancel job

------

### Native Application模式集成Dinky

#### 编译镜像

> 此模式需要打镜像，而且只能异步提交集群运行，所以需要一个私有仓库，如果自己有搭建harbor等私有仓库可用自建的，若没有可使用阿里云免费的镜像仓库，自行注册并创建命名空间，然后在本地通过dockerfile打镜像并将其上传至阿里云镜像仓库中，便于dinky中添加与拉取

- 阿里云容器仓库地址如下：https://cr.console.aliyun.com/cn-beijing/instance/repositories

在`Dinky_HOME/config/DinkyFlinkDockerfile`提供了以下文件，在测试的时候发现豆瓣源可能会卡住，所以pip镜像源替换为了阿里云源，并且在17行的`flink-python_*`会报错不存在，所以将下划线删除，**直接复制下方配置即可**

- `dlink-app-1.16-0.7.3-jar-with-dependencies.jar`文件下载dinky二进制包并解压在` dlink-release-版本号/jar/`下方即可找到对应版本，复制到dockerfile同级目录，以及把`dinky-release-版本号/plugins`也复制到dockerfile统计目录，进行编译打包
- 【可选】如果有额外需求可把相关依赖也放于dinky的plugins中，一同把依赖放入打包到`lib`下 

dockerfile文件

```sh
# 用来构建dinky环境
ARG FLINK_VERSION=1.16.2 #flink docker镜像版本，可按照官方镜像进行定制
FROM flink:${FLINK_VERSION}

ARG FLINK_VERSION
ENV PYTHON_HOME /opt/miniconda3

USER root
RUN wget "https://s3.jcloud.sjtu.edu.cn/899a892efef34b1b944a19981040f55b-oss01/anaconda/miniconda/Miniconda3-py38_4.9.2-Linux-x86_64.sh" -O "miniconda.sh" && chmod +x miniconda.sh
RUN ./miniconda.sh -b -p $PYTHON_HOME && chown -R flink $PYTHON_HOME && ls $PYTHON_HOME

USER flink

ENV PATH $PYTHON_HOME/bin:$PATH

#替换了阿里云pip镜像源
RUN pip install "apache-flink==${FLINK_VERSION}" -i http://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com

RUN cp /opt/flink/opt/flink-python* /opt/flink/lib/
# dinky依赖，1.16 为 flink对应的大版本号，请自行对应更改，并将其复制到此文件同级目录，以及dinky的plugins文件夹复制到此文件同级
COPY ./dlink-app-1.16-0.7.3-jar-with-dependencies.jar plugins/* $FLINK_HOME/lib/
```

开始进行build，本例以阿里云镜像为演示，提前在阿里云仓库中创建好仓库文件夹

![image-20230923132809846](http://pic.dinky.org.cn/dinky/docs/test/202312201517994.png)

选择本地仓库

![image-20230923132829801](http://pic.dinky.org.cn/dinky/docs/test/202312201517071.png) 



进入仓库即可看到相关的登录信息以及拉取和打标签的描述，在本机通过docker登录至阿里云镜像仓库

![image-20230923132915194](http://pic.dinky.org.cn/dinky/docs/test/202312201517114.png) 



开始build，**更换自己的镜像地址，别用本例的**

```sh
docker build -t registry.cn-beijing.aliyuncs.com/yilinwei/dinky-native-application:flink1.16.2 .
```

![image-20230923133050795](http://pic.dinky.org.cn/dinky/docs/test/202312201517177.png) 

等待build完成

![image-20230923133637433](http://pic.dinky.org.cn/dinky/docs/test/202312201517367.png)

然后将其push到阿里云仓库中,**更换自己的镜像地址，别用本例的**，镜像比较大，得等待一定的时间

```sh
docker push registry.cn-beijing.aliyuncs.com/yilinwei/dinky-native-application:flink1.16.2
```

![image-20230923133811521](http://pic.dinky.org.cn/dinky/docs/test/202312201517745.png) 

在push完成后可在阿里云 镜像仓库中查看到当前镜像

![image-20230923135314092](http://pic.dinky.org.cn/dinky/docs/test/202312201517651.png)

到目前位置镜像配置完成，下面开始与dinky集成

#### 集成dinky

进入此处选择`Flink Kubernates Native`类型

![image-20230923135415697](http://pic.dinky.org.cn/dinky/docs/test/202312201517740.png)



docker设置如下，**配置项填写自己的镜像地址、登录账户与密码，命名空间可自行更改，dinky地址填自己配置的**

- image-namespace：是阿里云命名空间

![image-20230923141208533](http://pic.dinky.org.cn/dinky/docs/test/202312201517830.png) 

其余配置如下
![image-20230923141332962](http://pic.dinky.org.cn/dinky/docs/test/202312201517929.png)

kubernetes配置项如下，**配置项填写自己的镜像地址，资源自定义，下方添加自定义项，`kubernetes.cluster-id`自行任意配置一个**

![image-20230923141612034](http://pic.dinky.org.cn/dinky/docs/test/202312201517322.png)

Flink配置，必填项只需要一个`flink/conf`下的配置文件，这里需要注意dinky部署的方式

- 如果dinky部署在**容器**内，那么需要通过`kubectl cp 的方式或者pvc外部挂载的方式将conf/flink-conf.yaml`移动到dinky容器的某个位置

- 如果dinky部署在**裸机**内，那么直接配置`flink-conf.yaml`的文件路径即可

  ![image-20230923142145971](http://pic.dinky.org.cn/dinky/docs/test/202312201518390.png)

基本配置配置完集群名称，点击测试链接，再启用完成即可

![image-20230923142328210](http://pic.dinky.org.cn/dinky/docs/test/202312201517422.png)

#### 提交任务测试

选中k8s application模式，并提交集群运行

![image-20230923143122068](http://pic.dinky.org.cn/dinky/docs/test/202312201517463.png)

自行找案例测试，这里我简单创个paimon表落盘alluxio上演示下

![image-20230923143422582](http://pic.dinky.org.cn/dinky/docs/test/202312201517558.png)

![image-20230923143838773](http://pic.dinky.org.cn/dinky/docs/test/202312201517627.png)

## 参考资料：

> 如果使用的版本非此版本，自行替换url中的版本号即可，如`1.16/zh/docs/`替换为`1.17/zh/docs/`

Flink官网-K8S Application模式&Session模式：https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/standalone/kubernetes/

Flink官网-K8S Native模式：https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/native_kubernetes/

Flink官网-Docker镜像：https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/resource-providers/standalone/docker/#advanced-customization

Flink官网-HA高可用配置：https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/ha/overview/

Flink官网-配置参数：https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/deployment/config/

Dinky官网-Dinky快速集成K8S：http://www.dlink.top/docs/0.7/k8s_guide/dinky_k8s_quick_start
