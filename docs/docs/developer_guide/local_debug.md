---
sidebar_position: 2
id: local_debug
title: 本地调试
---

# 开发者本地调试手册

:::tip
前置知识:

- Flink, Java, Maven, Node, React, Ant Design Pro
- MySQL/PostgreSQL/H2
- IntelliJ IDEA
:::

## 前置条件

在搭建Dinky开发环境之前请确保你已经安装如下软件

- Git:版本控制软件
- JDK环境：后端开发环境,支持 Java8 和 Java11
- Maven：Java包管理
- Node:前端开发;
- MySQL/PostgreSQL/H2:数据库
- IntelliJ IDEA:IDEA开发工具(建议使用 2023.2 版本以上,旧版本对 Maven Profile 支持不友好)
- Lombok:IDEA插件,用于简化代码

### 环境要求

|        环境        |            版本            |
|:----------------:|:------------------------:|
|       npm        |         7.19.0+          |
|     node.js      |         14.17.0+         |
|       jdk        |     Java8 或者 Java11      |
|      maven       |          3.8.0+          |
|      lombok      |         IDEA插件安装         |
| MySQL/PostgreSQL | MySQL5.7+ / PostgreSQL15 |

### 代码克隆

请通过 git 管理工具/IDEA 进行代码克隆，从 GitHub 中拉取 Dinky 源码

```shell
mkdir workspace
cd workspace
git clone https://github.com/DataLinkDC/dinky.git
#或者
git clone git://github.com/DataLinkDC/dlink.git
```

### 导入 Dinky

1. 启动 IDEA 并选择 Open。
2. 选择已克隆的 Dinky 存储库的根文件夹。
3. 等待项目加载完成。
4. 设置 JDK 1.8 和 Maven 3.6.0。

## 前端环境

推荐使用 nvm 管理 node 版本，安装 nvm 后，执行以下命令安装 node

```bash 
# linux/mac
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
```

> 在 Windows 上推荐使用 [nvm-desktop](https://github.com/1111mp/nvm-desktop) 管理软件进行安装,安装完成后在界面中选择合适版本的
> node 版本安装即可

### 初始化依赖

代码 Clone 完成后，进入 `dinky-web` 目录，执行以下命令安装依赖

```bash
npm install --force
```

### 启动前端

在 `dinky-web` 目录下执行以下命令启动前端,也可在 IDEA 中打开`dinky-web`下的`package.json`文件,点击`dev`左侧启动按钮启动前端

```bash
npm run dev
```

## 后端环境

:::warning 注意
在此默认将使用 IDEA 进行后端代码调试. 所有操作都基于 IDEA 进行界面化操作.不再进行命令行操作.

由于目前 Dinky 各个模块未发布到 Maven 中央仓库，所以需要先进行 Install 编译。从而在本地仓库中生成相应的依赖。

如果你是第一次编译 Dinky，那么请勾选以下 Maven Profile,然后双击下图中的`生命周期 -> Install`进行编译。如果在
Install/Package 过程中报错代码格式化问题,请参考 [代码格式化](contribution/code_format)章节
:::

![localdebug_profile](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/local_debug/localdebug_profile.png)

### Profile 说明

|    Profile    |                                         说明                                         |
|:-------------:|:----------------------------------------------------------------------------------:|
|      dev      |                     开发环境/本地调试，默认不选中,此功能主要用于本地调试或者二次开发,用于加载相关依赖                     |
|    aliyun     |                                       加速依赖下载                                       |
|     fast      | 主要用于跳过代码检查和代码格式化<br/>注意:如果 JDK 环境为 jdk8 需要勾选此 profile,否则会报错<br/>如果 JDK 环境为 11 无需勾选 |
|  flink-1.14   |                 用于指定 Flink 版本为 1.14,只能单选,需要勾选 flink-single-version                 |
|  flink-1.15   |                 用于指定 Flink 版本为 1.15,只能单选,需要勾选 flink-single-version                 |
|  flink-1.16   |                 用于指定 Flink 版本为 1.16,只能单选,需要勾选 flink-single-version                 |
|  flink-1.17   |                 用于指定 Flink 版本为 1.17,只能单选,需要勾选 flink-single-version                 |
|  flink-1.18   |                 用于指定 Flink 版本为 1.18,只能单选,需要勾选 flink-single-version                 |
|    jdk 11     |            用于指定 JDK 版本为 11,前提是本地已经安装了 JDK 11,如果没有安装 jdk11,则默认使用本地的 jdk8            |            
|      mac      |                                 用于适配在 mac 系统上进行调试                                  |            
| maven-central |                                 用于指定 maven 仓库为中央仓库                                 |  
|     prod      |             生产环境，默认选中,此功能主要用于编译打包,此 profile 会将部分依赖排除掉,不会打进最终 tar.gz 包内             |
|  scala-2.11   |                              用于指定 Scala 版本为 2.11,只能单选                              |
|  scala-2.12   |                              用于指定 Scala 版本为 2.12,只能单选                              |
|      web      |                                    打包前端资源,需要勾选                                     |

:::warning 注意

- 其他差异化配置为自己的 Maven 的 settings.xml 文件中的 profile 配置,请忽略
- 如果无JDK11环境, 默认使用JDK8,如果需要使用JDK11,请在IDEA中配置JDK11环境
- 注意 Profile 之间的冲突,如 flink-1.14 和 flink-1.15 不能同时勾选,否则会报错
- 注意 Profile 是否是灰色,如果是灰色,说明此 Profile 被默认选中了,请根据自己的需求进行勾选或者取消勾选

:::

### 开发者须知

Dinky开发环境配置有两种模式，分别是 provided 环境和 compile 环境

- provided：即上述 Profile 中的 `prod` ,此环境适合打包部署，或者二次开发后打包部署
- compile： 即上述 Profile 中的 `dev`,此环境适合二次开发或者熟悉源码，此模式不需要额外安装Flink,通过Flink自带的local模式便可调试开发

### 本地调试 Profile

![localdebug_dev_profile](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/local_debug/localdebug_dev_profile.png)

### 打包部署 Profile

![locadenug_single_package_profile](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/local_debug/locadenug_single_package_profile.png)

## Dinky 本地开发环境搭建

### 分支选择

开发不同的代码需要不同的分支

- 如果要基于二进制包进行开发，需要切换到对应的分支代码，如 `realease-1.0.0` 分支；
- 如果想要开发新代码，切换到dev分支即可；

下面说明在启动前如何修改相应的代码，为本地启动做准备。修改

### 修改配置文件

> 以 MySQL 为例, 如果使用 PostgreSQL/H2 请自行修改,注意: 使用 H2
> 数据库时无需修改如下配置,直接进行 [启动后端服务](#启动后端服务)

- 修改 dinky-admin/src/main/resources/application-mysql.yml 文件,配置相应的数据库连接信息

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_ADDR:192.168.1.22:3306}/${MYSQL_DATABASE:dinky}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
``` 

- 修改 dinky-admin/src/main/resources/application.yml 文件，将`spring.profiles.active` 设置为 mysql

```yaml
spring:
  # Dinky application name
  application:
    name: Dinky
  profiles:
    # The h2 database is used by default. If you need to use other databases, please set the configuration active to: mysql, currently supports [mysql, pgsql, h2]
    # If you use mysql database, please configure mysql database connection information in application-mysql.yml
    # If you use pgsql database, please configure pgsql database connection information in application-pgsql.yml
    # If you use the h2 database, please configure the h2 database connection information in application-h2.yml,
    # note: the h2 database is only for experience use, and the related data that has been created cannot be migrated, please use it with caution
    active: mysql #[h2,mysql,pgsql]
    include: jmx
```

### 初始化数据库

在 MySQL 数据库自行创建用户/使用默认 root 用户,并创建 dinky 数据库,在 dinky 数据库中执行 script/sql/dinky-mysql.sql
文件。此外 script/sql/upgrade 目录下存放了了各版本的升级 sql ,如需执行:请依次按照版本号执行。

以上文件修改完成后，就可以启动Dinky。

### 启动后端服务

启动 dinky-admin/src/main/java/org/dinky/ 下的 Dinky 启动类，可见如下信息:

![localdebug_idea_console_log](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/local_debug/localdebug_idea_console_log.png)

:::warning 注意

上述地址全为后端端口,前端端口为 8000, 如果在 [启动前端](#启动前端) 已经成功启动, 则直接访问 127.0.0.1:8000 即可

如果在 [启动前端](#启动前端) 未成功启动, 则需要先启动前端, 再访问 127.0.0.1:8000

注意: 默认用户名/密码为 admin/admin
:::

:::tip 说明
以上内容是 Dinky 在 IDEA 本地环境搭建步骤，并简单介绍了如何在本地配置/启动 Dinky。在了解上述步骤后，可以动手改造 Dinky。

如果你在 Dinky 的基础上进行了二次开发,并有意向将代码贡献给 Dinky,请参考 [代码贡献](./contribution/how_contribute) 文档。
:::

## 常见问题

请参考 [本地调试 FAQ](../faq#本地调试FAQ) 文档。