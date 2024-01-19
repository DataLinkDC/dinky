---
sidebar_position: 2
id: dinky_k8s_quick_install
title: K8S快速安装dinky教程
---
## K8S快速安装dinky教程

### 前置条件

- MySQL5.7+
- K8S 1.20.1+
- Docker 1.13.1+

### 安装步骤

#### 1.初始化MySQL数据库

- 如果需要指定用户并给予权限使用以下命令，这里以`dinky`用户举例子

  ```sql
  -- 创建名称为dinky的用户，密码也为dinky，有需要自行更改
  create user 'dinky'@'%' IDENTIFIED WITH mysql_native_password by 'dinky';
  -- 授权dinky数据库给用户dinky
  grant ALL PRIVILEGES ON dinky.* to 'dinky'@'%';
  -- 刷新权限
  flush privileges;
  ```

- 下载对应的版本的dinky二进制包，将`dlink-release-版本号.tar\dlink-release-版本号\sql`下的`dinky.sql`文件单独取出，创建好`dinky数据库`

    - 通过Navicat等可视化工具执行`dinky.sql`文件
    - 或者进入MySQL命令通过`source /路径/dinky.sql`执行初始化

#### 2.编写配置文件

- 首先创建dinky的命名空间

```sh
kubectl create ns dinky
```

- 然后创建一个任意名称的yaml文件，用来放下面的配置文件，这里以`deploy.yaml`为例
- 下方配置文件请自行对应修改
    - MySQL账号、密码、连接IP、端口
    - 如果需要 dolphinscheduler 配置好后需要重启Dinky的Pod生效

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: flink-dinky
  name: dinky
  namespace: dinky
spec:
  selector:
    matchLabels:
      app: flink-dinky
  template:
    metadata:
      labels:
        app: flink-dinky
    spec:
      containers:
      #dinky镜像，如果需要更新请前往【https://hub.docker.com/r/dinkydocker/dinky-standalone-server/tags】选择合适镜像版本
      #如果需要添加拓展jar包，可通过dinky注册中心【jar包管理】处添加，或者通过Dockerfile将其打包进docker镜像，再上传至私有仓库再替换此处的镜像
      - image: dinkydocker/dinky-standalone-server:0.7.3-flink16  #此处为 0.7.3版本的镜像，新版本替换此处即可
        imagePullPolicy: IfNotPresent
        name: dinky
        #将下方configMap配置映射到容器内部，修改配置需要重启pod生效
        volumeMounts: 
        - mountPath: /opt/dinky/config/application.yml
          name: admin-config
          subPath: application.yml
      volumes:
      - name: admin-config
        configMap:
          name: dinky-config
          
---

apiVersion: v1
kind: ConfigMap
metadata:
  name: dinky-config
  namespace: dinky
data:
  #配置项均为【conf】目录下的application.yaml文件内容，如果有更新，把下面整段替换即可
  application.yml: |-
    spring:
      datasource:
      	#修改为自己的mysql地址与端口，以及dinky数据库名称
      	#例：jdbc:mysql://${MYSQL_ADDR:192.168.50.100:3306}/${MYSQL_DATABASE:dinky}
        url: jdbc:mysql://${MYSQL_ADDR:【mysql地址】:【端口】}/${MYSQL_DATABASE:【dinky元数据库名称】}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
        username: ${MYSQL_USERNAME:【mysql用户名】}	#具有访问dinky数据库的账号, 例：${MYSQL_USERNAME:dinky}
        password: ${MYSQL_PASSWORD:【mysql密码】} #具有访问dinky数据库权限的密码, 例：${MYSQL_PASSWORD:dinky} 
        driver-class-name: com.mysql.cj.jdbc.Driver
      application:
        name: dinky
      mvc:
        pathmatch:
          matching-strategy: ant_path_matcher
        format:
          date: yyyy-MM-dd HH:mm:ss
        #json格式化全局配置
      jackson:
        time-zone: GMT+8
        date-format: yyyy-MM-dd HH:mm:ss

      main:
        allow-circular-references: true

      #  默认使用内存缓存元数据信息，
      #  dlink支持redis缓存，如有需要请把simple改为redis，并打开下面的redis连接配置
      #  子配置项可以按需要打开或自定义配置
      cache:
        type: simple
      ##    如果type配置为redis，则该项可按需配置
      #    redis:
      ##      是否缓存空值，保存默认即可
      #      cache-null-values: false
      ##      缓存过期时间，24小时
      #      time-to-live: 86400


      #  flyway:
      #    enabled: false
      #    clean-disabled: true
      ##    baseline-on-migrate: true
      #    table: dlink_schema_history
      # Redis配置
      #sa-token如需依赖redis，请打开redis配置和pom.xml、dlink-admin/pom.xml中依赖
      # redis:
      #   host: localhost
      #   port: 6379
      #   password:
      #   database: 10
      #   jedis:
      #     pool:
      #       # 连接池最大连接数（使用负值表示没有限制）
      #       max-active: 50
      #       # 连接池最大阻塞等待时间（使用负值表示没有限制）
      #       max-wait: 3000
      #       # 连接池中的最大空闲连接数
      #       max-idle: 20
      #       # 连接池中的最小空闲连接数
      #       min-idle: 5
      #   # 连接超时时间（毫秒）
      #   timeout: 5000
      servlet:
        multipart:
          max-file-size: 524288000
          max-request-size: 524288000
          enabled: true
    server:
      port: 8888
    mybatis-plus:
      mapper-locations: classpath:/mapper/*Mapper.xml
      #实体扫描，多个package用逗号或者分号分隔
      typeAliasesPackage: com.dlink.model
      global-config:
        db-config:
          id-type: auto
      configuration:
        ##### mybatis-plus打印完整sql(只适用于开发环境)
        #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
        log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
    # Sa-Token 配置
    sa-token:
      # token名称 (同时也是cookie名称)
      token-name: satoken
      # token有效期，单位s 默认10小时, -1代表永不过期
      timeout: 36000
      # token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
      activity-timeout: -1
      # 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
      is-concurrent: false
      # 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
      is-share: true
      # token风格
      token-style: uuid
      # 是否输出操作日志
      is-log: false
    knife4j:
      enable: true
    dinky:
      #dolphinscheduler配置，如果开启后需要重启pod，并且添加对应的ip地址，并在dolphinscheduler的【安全中心】- 【令牌管理】中添加令牌添加至下方发token
      dolphinscheduler:
        enabled: false
        # dolphinscheduler 地址
        url: http://127.0.0.1:5173/dolphinscheduler
        # dolphinscheduler 生成的token
        token: ad54eb8f57fadea95f52763517978b26
        # dolphinscheduler 中指定的项目名不区分大小写
        project-name: Dinky
        # Dolphinscheduler DinkyTask Address
        address: http://127.0.0.1:8888
      # python udf 需要用到的 python 执行环境
      python:
        path: python
        
---

apiVersion: v1
kind: Service
metadata:
  name: flink-dinky
  namespace: dinky
spec:
  ports:
  - name: web-flink-dinky
    port: 8888 	#dinky pod 的端口号
    protocol: TCP
    nodePort: 32323 #【对外暴露端口号】，如果出现端口冲突，可自行更改
  selector:
    app: flink-dinky
  type:  NodePort 
```

#### 3. 执行安装

在执行配置文件后等待镜像的拉取

```sh
kubectl apply -f deploy.yaml
```

![image-20230909105508605](http://pic.dinky.org.cn/dinky/docs/test/202312201511694.png)

等待pod初始化完成

![](http://pic.dinky.org.cn/dinky/docs/test/202312201511677.png)

在安装完毕后查看pod，svc是否有问题

```sh
kubectl get pods,svc -o wide -n dinky
```

![image-20230909105740008](http://pic.dinky.org.cn/dinky/docs/test/202312201511321.png)

进入浏览器，访问配置文件中设置的`【对外暴露端口号】`，这里配置文件设置的为`32323`

输入主机`ip:端口`访问页面

```
http://主机ip:端口
```

默认登录账号密码为`admin/admin`

![image-20230909105927963](http://pic.dinky.org.cn/dinky/docs/test/202312201511332.png)

#### 4.测试

创建一个作业`ctrl+s`保存并点击执行

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

点击获取数据测试

![image-20230909110331424](http://pic.dinky.org.cn/dinky/docs/test/202312201511356.png)

点击查看bi

![image-20230909110437935](http://pic.dinky.org.cn/dinky/docs/test/202312201511369.png)

### 注意事项

- 本例中的`Pod`会自动启动`Flink1.16`版本，如若需要指定版本的Flink，有两种方式

  - 方式一：`kubectl exec -it -n 命名空间 pod名称 -- /bin/bash`手动进入Pod，关闭`sh auto.sh stop`再启动对应版本,如1.17`sh auto.sh start 1.17`

  - 方式二：是通过Dokcerfile重新打包，重新指定启动参数，默认镜像是指定了`ENV FLINK_BIG_VERSION=1.16`,重写打包即可，上面的镜像自行替换最新版

    ![image-20230911102325172](http://pic.dinky.org.cn/dinky/docs/test/202312201511494.png)

    在此基础上添加参数重启即可，用下面例子，或者自行编写，完整参数可见：[docker hub](https://hub.docker.com/layers/dinkydocker/dinky-standalone-server/0.7.3-flink16/images/sha256-b1ac433950a004c899d4fe930d5996acbf9d09dc2eaca5973ff84d1d4c12f5b0?context=explore)

    ```sh
    FROM dinkydocker/dinky-standalone-server:0.7.3-flink16
    
    # 指定启动版本号为 1.17
    ENV FLINK_BIG_VERSION=1.17
    
    # 更改启动命令为新命令
    ENTRYPOINT ["/bin/sh","-c","rm -rf /opt/dinky/run/dinky.pid && /opt/dinky/auto.sh restart ${FLINK_BIG_VERSION} && tail -f /opt/dinky/logs/dlink.log"]
    ```

- 若MySQL是部署在同一个K8S集群中，k8s集群如果重启，dinky可能会无法自动重新启动

  - 原因是：dinky先于MySQL由于连接不上MySQL会内部报错，但是k8s的pod会显示Running状态，并且通过`kubectl logs pod名`也不会出现报错日志，只有进入容器查看`logs`下的`dinky.log`才可以看到报错信息）
  - 解决方案：需要重新部署一次(元数据存于MySQL)，或者通过`kubectl exec -it -n 命名空间 pod名称 -- /bin/bash`进入`Pod`内部手动`auto.sh start 【对应版本flink】`启动dinky

- 若添加第三方Jar包是通过dinky-web端添加的，如果不是指定存储与`HDFS`的某个位置而是存储于`本地`，那么当重新部署Pod的时候Jar包会丢失

- dinky安装位置在镜像中的：`/opt/dinky`，其相关jar包依赖存放于该目录下的`plugins`下，推荐通过`Dockerfile`方式修改镜像，将常用`Jar`，如`Paimon`、`CDC`等相关依赖，复制进该目录或该目录下的版本目录，再编译打包提交私有仓库，添加于上方配置文件中

  - Dockerfile打包文件进镜像请自行搜索相关教程

- 如果想实现日志持久化以及动态添加jar依赖，可通过`nfs-storage`创建pvc，也可以直接在 nfs共享文件夹下创建个文件夹，只要能保证k8s集群中各个主机都能访问到该路径及其文件中的内容即可，然后将pvc映射到dinky pod容器内，实现外部挂载，nfs-storage安装部署教程可见`dinky官网/k8s集成手册/Dinky集成Flink on K8S文档中的注意事项`，如果是通过非pvc的方式，那么每次添加依赖后需要重启下pod容器

  - 两种方式首次映射后pvc的文件夹都是空的，需要手动将二进制包下的plugins下的内容移动到路径或者 pvc的路径下，之后只在此路径下添加或更改依赖即可

  - 日志创建pvc后可直接持久化到外部路径进行查看

  - pvc创建

    ```SH
    apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
      name: dinky-plugins  # pvc名称
      namespace: dinky
    spec:
      storageClassName: nfs-storage   #sc名称
      accessModes:
        - ReadOnlyMany   #采用ReadOnlyMany的访问模式
      resources:
        requests:
          storage: 50Gi    #存储容量，根据实际需要更改
    ```

  - 映射

    ```sh
    ---
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      labels:
        app: flink-dinky
      name: dinky
      namespace: dinky
    spec:
      selector:
        matchLabels:
          app: flink-dinky
      template:
        metadata:
          labels:
            app: flink-dinky
        spec:
          containers:
          - image: dinkydocker/dinky-standalone-server:0.7.3-flink16
            imagePullPolicy: IfNotPresent
            name: dinky
            volumeMounts: 
            - mountPath: /opt/dinky/config/application.yml
              name: admin-config
              subPath: application.yml
              #把plugins文件夹映射到本地来，方便添加jar
            - name: plugins-data
              mountPath: /opt/dinky/plugins   #映射的容器内的监控路径
          volumes:
          - name: admin-config
            configMap:
              name: dinky-config
          - name: plugins-data
            persistentVolumeClaim:
              claimName: dinky-plugins  #【方式一】指定nfs共享的pvc，可动态添加依赖，无序重启,首次需要手动复制文件到此目录
            # hostPath:
            #   path: /data/nfs/dinky/plugins #【方式二】映射到nfs共享目录下，每次添加依赖需重启pod生效，首次需要手动复制文件到此目录
    ```

![image-20230923155056343](http://pic.dinky.org.cn/dinky/docs/test/202312201511589.png)
