---
sidebar_position: 3
id: remote_debug
title: 远程调试
---

# 开发者远程调试手册

远程调试适用与服务器已部署如下三种集群的场景

- Flink StandAlone
- Flink on yarn
- Flink on K8S

需要说明的是远程调试不需要在服务器部署相应的 Dinky 安装包，在对应的 IDE 启动 `dlink-admin/src/main/java/com/dlink/Dlink.java` 类，即可完成对作业的远程执行和Debug。到目前为止，远程调试已经打通yarn session模式，也希望有感兴趣的小伙伴贡献其他几种模式的远程调试模式。

**前提条件：** 在进行远程调试过程中，必须要进行一次 `install`，这样做的原因是为了生成 Dinky 各个模块或者各个包的jar包文件，为远程提交到集群做准备。此方式类似与在服务器部署 Dinky 后，Dinky 启动需要各个 Dinky 的包或者模块对应的 jar 包文件，方可正常启动。

对于开发者来说，如何在 IDEA 中对作业进行远程调试及提交。下面以 Yarn Session 模式为例。

:::tip 说明
环境准备及源码导入 IDEA，详见[本地调试](../developer_guide/local_debug)
:::

## 环境

| 名称    | 版本      | 
|-------|---------|
| Flink | 1.13.5  |
| Dinky | dev     |
| Java  | 1.8_291 |
| Node  | 14.17.0 |
| npm  | 7.9.0   |
| Lombok | IDEA 插件 |

# 下载 Dinky

```bash
git clone https://github.com/DataLinkDC/dlink.git

# 如需本地调试开发 请使用 dev 分支
# 进入到 dlink 源码目录
git checkout dev
```

## 编译

### 命令行编译

```
mvn clean package -Dmaven.test.skip=true
```

### 图形化编译

![install](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/local_package_install.jpg)
图形化编译，需要跳过 test ，并进行 clean ==> install。

**说明:**

​ 1.如果不想单独编译前端，在 dlink-web 模块的 pom 下有``frontend-maven-plugins``，可直接前后端编译；

​ 2.如果要分开编译，在后端编译完成后，需要在 dlink-web 下执行 ``npm i --force ``;

:::warning 注意事项
如果不执行 install 生成的 jar安装不到本地 别的依赖就识别不到本地仓库这些包 所以可能导依赖的时候会报错 CustomTableEnvironmentImpl 这个类未定义 。
:::

## 远程调试环境搭建

### 修改pom文件

需要修改 dlink 根目录下的 pom.xml 文件，将 **provied**  改为 **complie**，修改如下：

```
<properties>
        
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>${target.java.version}</maven.compiler.source>
        <maven.compiler.target>${target.java.version}</maven.compiler.target>
        <!--  `provided` for product environment ,`compile` for dev environment  -->
        <scope.runtime>compile</scope.runtime>
    </properties>
```

### 修改配置文件

修改 dlink 根目录下 **/dlink-admin/src/main/resources/application.yml** 文件

配置数据库连接信息：

```
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  application:
    name: dlink
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
  ##### mybatis-plus打印完整sql(只适用于开发环境)  # 此处如果想查看具体执行的SQL 本地调试的时候也可以打开  方便排错
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
```

### 初始化数据库

在MySQL数据库创建 dlink 用户并在 dlink 数据库中执行 dlink-doc/sql/dinky.sql 文件。此外 dlink-doc/sql/upgrade 目录下存放了了各版本的升级 sql 请依次按照版本号执行。

以上文件修改完成后，就可以启动 Dinky。

### 集群配置文件

- **hadoop配置文件:** core-site.xml hdfs-site.xml yarn-site.xml hive-site.xml;
- **Flink配置文件：** flink-conf.yaml；

:::warning 注意事项
hive-site.xml 需要使用到 Hive Catalog 时添加
:::

### 添加 plugins 插件依赖

根据 job 的场景自行选择插件依赖 jar, 选择需要的 jars , 右键添加为库,如下所示：

- 选中 jars 添加为库
  ![jars_add_to_repo](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/jars_add_to_repo.jpg)


- 弹框中选择信息如图:
    - 名称: 自定义
    - 级别: 项目库
    - 添加到模块: dlink-admin

![choose_addrepo_global](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/choose_addrepo_global.png)   
![create_repo](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/create_repo.png)

:::warning 注意事项
如果是整库同步场景下请将级别设置为全局库 ,模块选中所有
:::

### 启动 Yarn Session 集群

```
yarn-session.sh -n 2 -jm 1024 -tm 4096 -s 6 -d
```

### 启动 Dinky 服务

#### 方式一

- 直接启动 dlink-admin 下的 Dlink 启动类，可见 8888 端口。
- **访问:** 127.0.0.1:8888
- **账户密码:** admin/admin

#### 方式二

- 启动 dlink-admin 下的 Dlink 启动类
- 进入到 **dlink-web** 执行 **npm start**
- **访问:** 127.0.0.1:8000
- **账户密码:** admin/admin

在 Dinky-0.6 版本后，不需要额外启动前端，如需进行前后端联调,详见[方式二](./remote_debug#方式二)

## 远程调试作业示例

以上远程调试环境搭建完成后，就如同在服务器部署上类似，可以对作业远程提交到 Flink 集群。下面以 Flink CDC ==》 Hudi 做为远程调试的作业

### 脚本准备

```
------------- '付款单表'  order_mysql_goods_order_pay -----------------
CREATE TABLE source_order_mysql_goods_order_pay (
     `id` bigint COMMENT '自增主键id'
    , `uid` string COMMENT 'uid'
    , `goods_order_uid` string COMMENT '订单uid'
    , `trans_goods_order_uid` string COMMENT '转单时老订单uid'
    , `trans_goods_order_change_uid` string COMMENT '转单时老订单变更单uid'
    , `customer_uid` string COMMENT '客户uid'
    , `customer_name` string COMMENT '客户姓名'
    , `pay_order_no` string COMMENT '付款支付订单uid'
    , `pay_status` bigint COMMENT '支付状态 1:待付款 2:付款审核 3:付款成功 4:付款取消'
    , `pay_type` bigint COMMENT '付款单类型 1:支付 2:资金池抵扣 3:转单抵扣'
    , `pay_type_detail` bigint COMMENT '付款方式'
    , `pay_amount` bigint COMMENT '应付金额'
    , `real_amount` bigint COMMENT '实际付款金额'
    , `pay_time` timestamp(3) COMMENT '支付时间'
    , `pay_sucs_time` timestamp(3) COMMENT '支付成功时间'
    , `pay_audit_time` timestamp(3) COMMENT '支付审核时间'
    , `pay_fail_time` timestamp(3) COMMENT '支付失败时间'
    , `apply_uid` string COMMENT '申请人'
    , `apply_name` string COMMENT '申请人'
    , `examine_uid` string COMMENT '审核人'
    , `examine_name` string COMMENT '审核人'
    , `examine_time` timestamp(3) COMMENT '审核时间'
    , `voucher_pics` string COMMENT '付款凭证图片地址'
    , `remark` string COMMENT '备注'
    , `status` bigint COMMENT '是否删除（1.否，2.是）'
    , `create_time` timestamp(3) COMMENT '创建时间'
    , `update_time` timestamp(3) COMMENT '更新时间'
    , `belongs_bank` string COMMENT '所属银行'
    , `bank_no` string COMMENT '银行卡号'
    , `company_pay_time` timestamp(3) COMMENT '公对公转账时间'
    
    ,PRIMARY KEY(id) NOT ENFORCED
) COMMENT '付款单表'
WITH (
     'connector' = 'mysql-cdc'
    ,'hostname' = 'rm-bsssssssssssssssncs.com'
    ,'port' = '3306'
    ,'username' = 'read-bigdata'
    ,'password' = 'aaVxxxxx0E40'
    ,'server-time-zone' = 'UTC'
    ,'scan.incremental.snapshot.enabled' = 'true'
    ,'debezium.snapshot.mode'='latest-offset'  ---或者key是scan.startup.mode，initial表示要历史数据，latest-offset表示不要历史数据
    ,'debezium.datetime.format.timestamp.zone'='UTC+8'
    ,'database-name' = 'order'
    ,'table-name' = 'goods_order_pay'
);
CREATE TABLE sink_order_mysql_goods_order_pay(
     `id` bigint COMMENT '自增主键id'
    , `uid` string COMMENT 'uid'
    , `goods_order_uid` string COMMENT '订单uid'
    , `trans_goods_order_uid` string COMMENT '转单时老订单uid'
    , `trans_goods_order_change_uid` string COMMENT '转单时老订单变更单uid'
    , `customer_uid` string COMMENT '客户uid'
    , `customer_name` string COMMENT '客户姓名'
    , `pay_order_no` string COMMENT '付款支付订单uid'
    , `pay_status` bigint COMMENT '支付状态 1:待付款 2:付款审核 3:付款成功 4:付款取消'
    , `pay_type` bigint COMMENT '付款单类型 1:支付 2:资金池抵扣 3:转单抵扣'
    , `pay_type_detail` bigint COMMENT '付款方式'
    , `pay_amount` bigint COMMENT '应付金额'
    , `real_amount` bigint COMMENT '实际付款金额'
    , `pay_time` timestamp(3) COMMENT '支付时间'
    , `pay_sucs_time` timestamp(3) COMMENT '支付成功时间'
    , `pay_audit_time` timestamp(3) COMMENT '支付审核时间'
    , `pay_fail_time` timestamp(3) COMMENT '支付失败时间'
    , `apply_uid` string COMMENT '申请人'
    , `apply_name` string COMMENT '申请人'
    , `examine_uid` string COMMENT '审核人'
    , `examine_name` string COMMENT '审核人'
    , `examine_time` timestamp(3) COMMENT '审核时间'
    , `voucher_pics` string COMMENT '付款凭证图片地址'
    , `remark` string COMMENT '备注'
    , `status` bigint COMMENT '是否删除（1.否，2.是）'
    , `create_time` timestamp(3) COMMENT '创建时间'
    , `update_time` timestamp(3) COMMENT '更新时间'
    , `belongs_bank` string COMMENT '所属银行'
    , `bank_no` string COMMENT '银行卡号'
    , `company_pay_time` timestamp(3) COMMENT '公对公转账时间'
    
    ,PRIMARY KEY (id) NOT ENFORCED
) COMMENT '付款单表'
WITH (
      'connector' = 'hudi'
    , 'path' = 'hdfs://cluster1/data/bizdata/cdc/mysql/order/goods_order_pay' ---路径会自动创建
    , 'hoodie.datasource.write.recordkey.field' = 'id'  -- 主键
    , 'write.precombine.field' = 'update_time'             -- 相同的键值时，取此字段最大值，默认ts字段
    , 'read.streaming.skip_compaction' = 'true'            -- 避免重复消费问题
    , 'write.bucket_assign.tasks' = '2'              --并发写的 bucekt 数
    , 'write.tasks' = '2'
    , 'compaction.tasks' = '1'
    , 'write.operation' = 'upsert'                          --UPSERT（插入更新）\INSERT（插入）\BULK_INSERT（批插入）（upsert性能会低些，不适合埋点上报）
    , 'write.rate.limit' = '20000'                          -- 限制每秒多少条
    , 'table.type' = 'COPY_ON_WRITE'                       -- 默认COPY_ON_WRITE ，经测试MERGE_ON_READ模式的压缩有问题，不会在指定次数和时间压缩，容易在执行离线任务时漏数据（在埋点上报环节可以用，因为上报的数据是连续的）。
    , 'compaction.async.enabled' = 'true'                  -- 在线压缩
    , 'compaction.trigger.strategy' = 'num_or_time'        -- 按次数压缩
    , 'compaction.delta_commits' = '20'               -- 默认为5
    , 'compaction.delta_seconds' = '60'                   -- 默认为1小时
    , 'hive_sync.enable' = 'true'                          -- 启用hive同步
    , 'hive_sync.mode' = 'hms'                             -- 启用hive hms同步，默认jdbc
    , 'hive_sync.metastore.uris' = 'thrift://cdh2.vision.com:9083'    -- required, metastore的端口
    , 'hive_sync.jdbc_url' = 'jdbc:hive2://cdh1.vision.com:10000'     -- required, hiveServer地址
    , 'hive_sync.table' = 'order_mysql_goods_order_pay'                            -- required, hive 新建的表名 会自动同步hudi的表结构和数据到hive
    , 'hive_sync.db' = 'cdc_ods'                              -- required, hive 新建的数据库名
    , 'hive_sync.username' = 'hive'                        -- required, HMS 用户名
    , 'hive_sync.password' = '123456'                            -- required, HMS 密码
    , 'hive_sync.skip_ro_suffix' = 'true'                  -- 去除ro后缀
);
---------- source_order_mysql_goods_order_pay=== TO ==>> sink_order_mysql_goods_order_pay ------------
insert into sink_order_mysql_goods_order_pay select * from source_order_mysql_goods_order_pay;
```

### 作业提交检查

**SQL 逻辑语法校验**

![check_sql](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/check_sql.jpg)

**获取JobPlan**

![check_sql](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/job_plan.jpg)

**Flink Web UI 查看作业**

![job_flinkwebui](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/job_flinkwebui.png)

**查看是否同步到 Hive**

![is_sync_hive_table](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/is_sync_hive_table.png)

**运维中心查看 JOB 提交状态**

![job_davops_center](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/job_davops_center.png)

**运维中心查看 JOB 详情**
![job_davops_center](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/remote_debug/devops_job_detail.png)

:::warning 注意事项
如果拉取了新代码，远程调试环境一定要检查一遍，以防各种报错。
:::







