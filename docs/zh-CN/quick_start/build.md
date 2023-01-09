## 环境准备

| **环境** | **版本**  | 备注                              |
| -------- | --------- | --------------------------------- |
| npm      | 7.19.0    |                                   |
| node.js  | 14.19.0   |                                   |
| jdk      | 1.8.0_201 |                                   |
| maven    | 3.6.3     |                                   |
| lombok   | 1.18.16   | 如果在idea编译，需要自行安装 插件 |
| mysql    | 5.6+      |                                   |

## NodeJS 安装部署

### Windows版本

[下载地址]( <https://registry.npmmirror.com/-/binary/node/v14.19.0/node-v14.19.0-x64.msi>)

下载完成后，双击傻瓜式安装即可

由于Dinky编译NPM最低版本为7.19.0，因此需要把NPM版本升级到7.19.0。具体操作如下

```
npm install -g npm@7.19.0
```

### Linux 版本

[下载地址](https://nodejs.org/download/release/v14.19.1/node-v14.19.1-linux-x64.tar.gz)

下载完成后，安装配置即可，操作如下：

```
tar xf node-v14.19.0.tar.gz -C /opt/module/
vim /etc/profile  
export NODEJS_HOME=/opt/module/node-v14.19.0
export PATH=$NODEJS_HOME/bin:$PATH
#保存退出
source /etc/profile #刷新环境变量
```

由于Dinky编译NPM最低版本为7.19.0，因此需要把NPM版本升级到7.19.0。具体操作如下：

```
npm install -g npm@7.19.0
```

## MySQL部署

MySQL版本选择5.6+

### Windows版本

[下载地址](<https://dev.mysql.com/downloads/file/?id=510038>)

下载完成后，双击傻瓜式安装即可

### Linux 版本

[下载地址]( [http://dev.mysql.com/get/mysql57-community-release-el7-11.noarch.rpm](http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm))

下载完成后，安装配置即可，操作如下：

```
#如果没有wget命令
    yum install wget
#下载mysql 5.7
    wget  http://dev.mysql.com/get/mysql57-community-release-el7-11.noarch.rpm
#安装yum repository
    yum -y install mysql57-community-release-el7-11.noarch.rpm
#在线安装
    yum -y install mysql-community-server
#修改配置文件
    vi /etc/my.cnf
    skip-grant-tables     #末尾添加这句话，这时候登入mysql就不需要密码
#开启mysql服务
    service mysqld start
#空密码登录mysql
    mysql -u root -p
#设置root密码
    flush privileges;
    set password for root@localhost = password('123456');
#恢复mysql配置
    service mysqld stop #停止mysql服务
    vi /etc/my.cnf     #修改配置文件
    # skip-grant-tables # 注释掉这句话
service mysqld start # 启动mysql服务
#设置开机自启动
systemctl enable mysqld
-------------------------------修改密码策略-------------------------
vi /etc/my.cnf
在 [mysqld]下面添加
validate_password=off
collation_server = utf8mb4_general_ci
character_set_server = utf8mb4
#重启服务
service mysqld restart 或者 systemctl restart mysqld.service

#卸载自动更新
yum -y remove mysql57-community-release-el7-11.noarch

------------------------------------远程连接----------------------------
#输入mysql -uroot -p123456 进入到mysql命令行
#授权
grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;
#刷新权限
flush privileges;

---------------------------------修改默认编码-------------------------------------------
#root用户
vi /etc/my.cnf
#添加如下内容
[client]
default_character_set=utf8mb4
#重启mysql服务
service mysqld restart
#查看修改结果
mysql -u root -p
SHOW VARIABLES LIKE 'char%'; 字符编码
SHOW VARIABLES LIKE 'collation_%'; 排序规则
#显示为utf-8
#已存在的表编码不变
#改变的是新建的表
```

## Maven 安装部署

### Windows版本

[下载地址](<https://dlcdn.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip>)

下载完成后后，解压到对应目录

```
1.将安装包解压到某目录，这里解压到C:\Program Files\apache-maven-3.6.3
3.设置系统变量MAVEN_HOME，值为C:\Program Files\apache-maven-3.6.3
4. 更新 PATH 变量，添加 Maven bin 文件夹到 PATH 的最后，%MAVEN_HOME%\bin
完成，以验证它，执行 mvn –v 在命令提示符
```

### Linux 版本

[下载地址](<https://dlcdn.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz>)

下载完成后后，解压到对应目录

解压安装 

```
tar xf apache-maven-3.6.3-bin.tar.gz -C /opt/module/
cd /opt/module/
mv apache-maven-3.6.3-bin maven-3.6.3

#添加环境变量
vim /etc/profile  
export MAVEN_HOME=/opt/module/maven-3.6.3
export PATH=$MAVEN_HOME/bin:$PATH

#保存退出，刷新环境变量
source /etc/profile 
```

以上环境准备就绪后，接下来就可以开始Dinky的编译。

## Dinky编译

### Windows编译

#### 直接编译

如果在window直接编译，首先将源码包解压到相应目录下，其次切换到Dinky根目录,编译命令如下：

```
mvn clean install -Dmaven.test.skip=true
```

切换到Dinky根目录下得build文件夹下，即可出现编译后的安装包

#### IDEA编译

```
创建 远程克隆项目 
maven ->> dlink->> 生命周期->> 跳过测试 ->> 双击install
打包完成后 安装包见项目根下  build 文件夹下
```

说明：如果要对dinky做二次开发，请参考[开发调试](/zh-CN/developer_guide/debug.md)

### Linux编译

```
yum -y install git
git clone https://github.com/DataLinkDC/dlink.git
cd dlink 
mvn clean install -Dmaven.test.skip=true
```

切换到Dinky根目录下得build文件夹下，即可出现编译后的安装包。

以上就是Dinky源码编译的详细步骤，Dinky如何安装部署，请查看下一章节[Dinky部署](/zh-CN/quick_start/deploy.md)





