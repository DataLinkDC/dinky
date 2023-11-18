---
position: 7
id: resource_setting
title: Resource 配置
---
当用户使用 **注册中心** > **资源**，需要上传并管理资源时，需要先在 **配置中心** > **全局配置** > **Resource 配置** 页面进行相关参数配置。

**参数配置说明:**
- **是否启用Resource：** 启用资源管理功能，如果切换存储模式时，需关闭此开关，相关配置完成后，再开启.
- **上传目录的根路径：** 资源存储在HDFS/OSS上的文件目录，请确保该目录存在并具有读写权限。
- **存储模式：** 目前支持三种存储模式：本地存储，HDFS 存储和 OSS 存储。

![metrics_setting](http://www.aiwenmo.com/dinky/docs/test/hdfs_setting.png)

- **HDFS操作用户名：** hdfs用户名
- **HDFS defaultFS：** dinky自定义了 rs 协议，读取 fs.defaultFS 配置项，自动根据参数路径解析是本地文件存储还是 HDFS 存储。如果是本地存储则写本地文件存储路径，如 file:/// 。
如果是 HDFS 存储则写 HDFS 文件访问路径，如 hdfs://localhost:9000/。

![metrics_setting](http://www.aiwenmo.com/dinky/docs/test/oss_setting.png)

- **对象存储服务的URL：** oss文件存储地址
- **Access key：** oss账号AccessKey
- **Secret key：** oss账号AccessKey对应的秘钥
- **存储桶名称：** 存储的Bucket名称
- **区域：** 区域

