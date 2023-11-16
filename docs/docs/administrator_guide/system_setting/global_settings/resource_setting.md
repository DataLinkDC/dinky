---
position: 7
id: resource_setting
title: Resource 配置
---
当用户使用 **注册中心** > **资源**，需要上传并管理资源时，需要先在 **配置中心** > **全局配置** > **Resource 配置** 页面进行相关参数配置。

**参数配置说明:**
- **是否启用Resource：** 启用资源管理功能，如果切换存储模式时，需关闭此开关，相关配置完成后，再开启.
- **上传目录的根路径：** 资源存储在HDFS/OSS上的文件目录，请确保该目录存在并具有读写权限。
- **存储模式：** 目前提供两种：HDFS和OSS上传设置。


![metrics_setting](http://www.aiwenmo.com/dinky/docs/test/hdfs_setting.png)
- **HDFS操作用户名：** hdfs用户名
- **HDFS defaultFS：** fs.defaultFS 配置项，即hdfs文件访问路径

![metrics_setting](http://www.aiwenmo.com/dinky/docs/test/oss_setting.png)


- **对象存储服务的URL：** oss文件存储地址
- **Access key：** oss账号AccessKey
- **Secret key：** oss账号AccessKey对应的秘钥
- **存储桶名称：** 存储的Bucket名称
- **区域：** 区域

