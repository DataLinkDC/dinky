---
position: 7
sidebar_position: 7
id: resource_setting
title: Resource 配置
---


当用户使用 **注册中心** > **[资源](../../register_center/resource)**，需要再本功能页面进行相关参数配置。配置保存后即生效

:::info 简介
从 Dinky v1.0.0 版本开始，提供了资源管理的功能，可以在 Dinky 中管理资源. 方便管理各个文件系统, 支持了 Local File System,
HDFS, OSS 三种文件系统.
:::

### Local File System 配置

![global_setting_resource_local](http://pic.dinky.org.cn/dinky/docs/test/global_setting_resource_local.png)

**参数配置说明:**

| 参数名称         | 参数说明                                                                                                                                                                                                                   | 默认值   |
|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------|
| 是否启用Resource | 启用资源管理功能，如果切换存储模式时，需关闭此开关，相关配置完成后，再开启。                                                                                                                                                                                 | true  |
| 存储模式         | 支持HDFS、S3(Minio、阿里云OSS、腾讯云COS等..)，切换选项后即可生效，同时并迁移资源文件。                                                                                                                                                                 | Local |
| 上传目录的根路径     | 资源存储在HDFS/OSS (S3)路径上，资源文件将存储到此基本路径，自行配置，请确保该目录存在于相关存储系统上并具有读写权限。<br/>如果是本地存储，则写本地文件存储路径，如 /User/xxx/data<br/>如果是 HDFS 存储则写 HDFS 文件访问路径，如 hdfs://localhost:9000/user/xxx<br/>如果是 OSS 存储则写 OSS 文件访问路径，如 oss://dinky/xxx | 无     |




### HDFS 配置

![global_setting_resource_hdfs](http://pic.dinky.org.cn/dinky/docs/test/global_setting_resource_hdfs.png)

| 参数名称           | 参数说明                                                                                                                                                                                                                   | 默认值      |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| 是否启用Resource   | 启用资源管理功能，如果切换存储模式时，需关闭此开关，相关配置完成后，再开启。                                                                                                                                                                                 | true     |
| 存储模式           | 支持HDFS、S3(Minio、阿里云OSS、腾讯云COS等..)，切换选项后即可生效，同时并迁移资源文件。                                                                                                                                                                 | Local    |
| 上传目录的根路径       | 资源存储在HDFS/OSS (S3)路径上，资源文件将存储到此基本路径，自行配置，请确保该目录存在于相关存储系统上并具有读写权限。<br/>如果是本地存储，则写本地文件存储路径，如 /User/xxx/data<br/>如果是 HDFS 存储则写 HDFS 文件访问路径，如 hdfs://localhost:9000/user/xxx<br/>如果是 OSS 存储则写 OSS 文件访问路径，如 oss://dinky/xxx | 无        |
| HDFS操作用户名      | hdfs用户名                                                                                                                                                                                                                | hdfs     |
| HDFS defaultFS | fs.defaultFS 配置项，例如: 远程 HDFS：hdfs://localhost:9000，本地：file:///                                                                                                                                                         | file:/// |

:::warning 注意
HDFS defaultFS 配置项，目前暂时不支持高可用 ,请填写可用节点(active)的 HDFS 地址
:::

### OSS 配置

![global_setting_resource_oss](http://pic.dinky.org.cn/dinky/docs/test/global_setting_resource_oss.png)

| 参数名称                  | 参数说明                                                                                                                                                                                                                   | 默认值                   |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| 是否启用Resource          | 启用资源管理功能，如果切换存储模式时，需关闭此开关，相关配置完成后，再开启。                                                                                                                                                                                 | true                  |
| 存储模式                  | 支持HDFS、S3(Minio、阿里云OSS、腾讯云COS等..)，切换选项后即可生效，同时并迁移资源文件。                                                                                                                                                                 | Local                 |
| 上传目录的根路径              | 资源存储在HDFS/OSS (S3)路径上，资源文件将存储到此基本路径，自行配置，请确保该目录存在于相关存储系统上并具有读写权限。<br/>如果是本地存储，则写本地文件存储路径，如 /User/xxx/data<br/>如果是 HDFS 存储则写 HDFS 文件访问路径，如 hdfs://localhost:9000/user/xxx<br/>如果是 OSS 存储则写 OSS 文件访问路径，如 oss://dinky/xxx | 无                     |
| 对象存储服务的 URL（Endpoint） | 例如：https://oss-cn-hangzhou.aliyuncs.com                                                                                                                                                                                | http://localhost:9000 |
| Access key            | Access key就像用户ID，可以唯一标识你的账户                                                                                                                                                                                            | minioadmin            |
| Secret key            | Secret key是你账户的密码，必须保管好，切勿泄露。                                                                                                                                                                                          | minioadmin            |
| 存储桶名称                 | 存储的Bucket名称                                                                                                                                                                                                            | dinky                 |
| 区域                    | 区域是oss的服务所在地域 如:oss-cn-hangzhou，无默认值，但是必须填写。                                                                                                                                                                           | 无                     |
| Path Style            | 是否开启 path style, 不同的提供方（如阿里云oss，腾讯云cos）支持情况不同，请阅读提供方文档说明进行填写                                                                                                                                                           | true                  |


