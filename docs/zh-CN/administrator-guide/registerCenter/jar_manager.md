当用户使用 jar 包提交 Flink 应用时，可以在 j**ar管理**中对所需程序进行管理。

## Jar包配置

首先进入**注册中心** > **jar管理**，点击 **新建**，进入**创建Jar配置**界面。

![新建](http://www.aiwenmo.com/dinky/dev/docs/%E6%96%B0%E5%BB%BA.png)



![创建jar配置](http://www.aiwenmo.com/dinky/dev/docs/%E5%88%9B%E5%BB%BAjar%E9%85%8D%E7%BD%AE.png)

**参数配置说明：**

- **Jar配置:**
  - 默认:** User App
  - **文件路径:** 指定 HDFS 上的文件路径,即Flink提交的jar包程序
  - **启动类:** 指定可执行 Jar 的启动类，（可选）
  - **执行参数:** 指定可执行 Jar 的启动类入参,（可选）
- **基本配置:**
  - **标识:** 英文唯一标识(必选)
  - **名称:** 自定义
  - **注释:** 自定义
- **是否启用:** 默认启用

Jar包配置完成后，就可以正常提交运行。

如果可行性jar包出错，用户可以对其进行编辑修改。

## Jar包编辑

用户可以对所添加的可执行Jar包配置做编辑修改。

首先进入**注册中心** > **jar管理**，点击**编辑**，即可对可执行Jar配置进行修改。

![jar编辑](http://www.aiwenmo.com/dinky/dev/docs/jar%E7%BC%96%E8%BE%91.png)



![维护jar包配置](http://www.aiwenmo.com/dinky/dev/docs/%E7%BB%B4%E6%8A%A4jar%E5%8C%85%E9%85%8D%E7%BD%AE.png)



## Jar删除

用户可以对所添加的可执行Jar包配置做删除。

首先进入**注册中心** > **jar管理**，点击**删除**，即可对可执行Jar配置进行删除。

![jar配置删除](http://www.aiwenmo.com/dinky/dev/docs/jar%E9%85%8D%E7%BD%AE%E5%88%A0%E9%99%A4.png)



![删除jar](http://www.aiwenmo.com/dinky/dev/docs/%E5%88%A0%E9%99%A4jar.png)



**说明:** 目前jar包提交的方式还尚在开发阶段，不适合在生产使用。用户只能跑一些demo