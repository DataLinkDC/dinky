## K8S快速安装dinky教程

### 前置条件:

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
      #如果需要添加拓展镜像，可通过dinky注册中心添加，或者通过Dockerfile将docker镜像打包上传至私有仓库再替换此处的镜像
      - image: dinkydocker/dinky-standalone-server:0.7.3-flink16  #0.7.3版本的镜像
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

![image-20230909105508605](https://ylw-typora-img.oss-cn-chengdu.aliyuncs.com/img/202309091056114.png)

等待pod初始化完成

![](https://ylw-typora-img.oss-cn-chengdu.aliyuncs.com/img/202309091055530.png)

在安装完毕后查看pod，svc是否有问题

```sh
kubectl get pods,svc -o wide -n dinky
```

![image-20230909105740008](https://ylw-typora-img.oss-cn-chengdu.aliyuncs.com/img/202309091057080.png)

进入浏览器，访问配置文件中设置的`【对外暴露端口号】`，这里配置文件设置的为`32323`

输入主机`ip:端口`访问页面

```
http://主机ip:端口
```

默认登录账号密码为`admin/admin`

![image-20230909105927963](https://ylw-typora-img.oss-cn-chengdu.aliyuncs.com/img/202309091059029.png)

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

![image-20230909110331424](https://ylw-typora-img.oss-cn-chengdu.aliyuncs.com/img/202309091103543.png)

点击查看bi

![image-20230909110437935](https://ylw-typora-img.oss-cn-chengdu.aliyuncs.com/img/202309091104042.png)
