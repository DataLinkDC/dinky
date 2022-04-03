当用户提交FlinkSQL作业时，首先要保证安装了Flink集群。Flink当前支持的集群模式包括：

- StandAlone集群
- Yarn集群
- Kubernetes集群

对于以上的三种集群而言，Dinky为用户提供了两种集群管理方式，一种是集群实例管理，一种是集群配置管理。

**需要说明的是:**

- StandAlone集群适用于既可以做为查询使用，又可以将作业异步提交到远程集群
- Yarn Session和Kubernetes Session适用于既可以做为查询使用，又可以将作业异步提交到远程集群
- Yarn Perjob,Yarn Application和Kubernetes Application适用于异步提交

## 集群实例管理

集群实例管理适用于StandAlone，Yarn Session和Kubernetes Session这三种类型配置。

### 集群配置

首先进入注册中心的集群管理中的集群实例管理，新建一个集群实例，点击新建，添加Flink集群，参数配置完成后，点击提交即可。

![cluster_manager_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/registerCenter/cluster_manager/cluster_manager_list.png)

![create_flink_cluster](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator-guide/registerCenter/cluster_manager/create_flink_cluster.png)

**参数配置说明：** 

- **名称:** 自定义(必填)
- **别名:** 自定义
- **类型：** 支持StandAlone，Yarn Session和Kubernetes Session三种类型
- **JobManager HA地址:** JobManager的RestAPI地址，当有多个RestAPI地址时。采用英文逗号隔开
- **注释:** 自定义

当集群实例配置完成后，点击心跳，会出现检测心跳

![心跳检查](http://www.aiwenmo.com/dinky/dev/docs/%E5%BF%83%E8%B7%B3%E6%A3%80%E6%9F%A5.png)

点击回收，会将过去的集群实例进行回收

![回收](http://www.aiwenmo.com/dinky/dev/docs/%E5%9B%9E%E6%94%B6.png)



### 集群实例编辑

当集群配置完成后，用户可以对集群实例做编辑修改。

首先进入注册中心集群实例管理，在列表中找到对应的集群实例，点击"编辑"后，编辑集群

![集群实例编辑](http://www.aiwenmo.com/dinky/dev/docs/%E9%9B%86%E7%BE%A4%E5%AE%9E%E4%BE%8B%E7%BC%96%E8%BE%91.png)

![编辑集群](http://www.aiwenmo.com/dinky/dev/docs/%E7%BC%96%E8%BE%91%E9%9B%86%E7%BE%A4.png)

### 集群实例删除

用户可以对所添加的集群实例进行删除。

首先进入注册中心中的集群实例管理，在列表中找到对应的集群实例，点击"删除"后，删除集群!

![集群实例删除](http://www.aiwenmo.com/dinky/dev/docs/%E9%9B%86%E7%BE%A4%E5%AE%9E%E4%BE%8B%E5%88%A0%E9%99%A4.png)

![集群删除](http://www.aiwenmo.com/dinky/dev/docs/%E9%9B%86%E7%BE%A4%E5%88%A0%E9%99%A4.png)

### 搜索

当用户集群实例配置完成后，配置的信息会显示在列表中，用户可以通过名称，别名，创建时间对配置的作业进行查找。

![搜索](http://www.aiwenmo.com/dinky/dev/docs/%E6%90%9C%E7%B4%A2.png)

## 集群配置管理

集群配置管理适用于Yarn Perjob，Yarn Application和Kubernetes Application这三种类型配置。

### 集群配置

首先进入注册中心的集群管理中的集群配置管理，点击"新建"后，可以创建集群配置，参数配置完成后，点击"测试"及"完成"即可。

![创建集群配置1](http://www.aiwenmo.com/dinky/dev/docs/%E5%88%9B%E5%BB%BA%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE1.png)

![创建集群配置2](http://www.aiwenmo.com/dinky/dev/docs/%E5%88%9B%E5%BB%BA%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE2.png)

**参数配置说明:**

- **类型：** 支持Flink on Yarn和Flink on Kubernetes
- **hadoop配置**
  - **配置文件路径:** hadoop配置文件路径，指定配置文件路径（末尾无/），需要包含以下文件：core-site.xml,hdfs-site.xml,yarn-site.xml
  - **自定义配置（高优先级）**
    - **ha.zookeeper.quorum:** zookeeper访问地址
    - **其他配置:** zookeeper的参数配置(默认不填写)
- **Flink配置**
  - **lib 路径:** 指定 lib 的 hdfs 路径（末尾无/），需要包含 Flink 运行时的依赖
  - **配置文件路径:**指定 flink-conf.yaml 的具体路径(末尾无/),必填
  - **自定义配置（高优先级）:** Flink参数配置
- **基本配置**
  - **标识:** 唯一英文标识(必填)
  - **名称:** 自定义
  - **注释:** 自定义
  - **是否启用:** 默认禁用，需要开启

参数配置完成后，点击"测试"，会显示测试连接成功。

![集群测试](http://www.aiwenmo.com/dinky/dev/docs/%E9%9B%86%E7%BE%A4%E6%B5%8B%E8%AF%95.png)

待测试连接成功后，选择完成即可。至此集群配置成功，可连接远程集群进行作业提交。

### 集群编辑

当集群配置完成后，用户可以对集群配置做编辑修改。

首先进入注册中心集群配置管理，在列表中找到对应的集群名称，点击"编辑"后，维护集群配置

![集群配置编辑](http://www.aiwenmo.com/dinky/dev/docs/%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE%E7%BC%96%E8%BE%91.png)

![维护集群配置](http://www.aiwenmo.com/dinky/dev/docs/%E7%BB%B4%E6%8A%A4%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE.png)

### 集群删除

用户可以对所添加的集群配置进行删除。

首先进入注册中心中的集群配置管理，，在列表中找到对应的集群，点击"删除"后，删除集群

![集群配置删除](http://www.aiwenmo.com/dinky/dev/docs/%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE%E5%88%A0%E9%99%A4.png)

![删除集群配置](http://www.aiwenmo.com/dinky/dev/docs/%E5%88%A0%E9%99%A4%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE.png)



### 搜索

当用户集群配置完成后，配置的信息会显示在列表中，用户可以通过名称，别名，创建时间对配置的作业进行查找。

![搜索](http://www.aiwenmo.com/dinky/dev/docs/%E6%90%9C%E7%B4%A2.png)



**说明:** 当集群配置的作业异步提交成功，可以看到所作业提交后的实例，并如果集群实例一样，可以对所在的集群实例进行回收，删除，编辑等操作。集群实例的操作步骤请查看集群实例管理部分。
