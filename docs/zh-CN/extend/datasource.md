 
 Dinky 数据源遵循 SPI,可随意扩展所需要的数据源。数据源扩展可在 dlink-metadata 模块中进行可插拔式扩展。现已经支持的数据源包括如下：

   - MySQL
   - Oracle
   - SQLServer
   - PostGreSQL
   - Phoenix
   - Doris(Starrocks)
   - ClickHouse 
   - Hive ``需要的jar包: hive-jdbc-2.1.1.jar && hive-service-2.1.1.jar``

使用以上数据源,请查阅注册中心[数据源管理](/zh-CN/administrator-guide/registerCenter/datasource_manage.md),配置数据源连接

**说明：** Dinky 不在对 Starorcks 进行额外扩展，Doris 和 Starorcks 底层并无差别，原则上只是功能区分。经社区测试验证，可采用 Doris 扩展连接 Starrocks。

----

## 准备工作
- 本地开发环境搭建
  - 参考 [开发者本地调试手册](/zh-CN/developer_guide/local_debug.md)

## 后端开发
- 在 **dlink-metadata** 模块中， 右键**新建子模块**, 命名规则: **dlink-metadata-{数据源名称}**
- **代码层面** 参照其他类型数据源扩展，按照相应的模板进行修改 
  - 注意事项: 
    - 不可在父类的基础上修改代码，可以在子类中进行扩展 ,或者重写父类方法
    - 扩展数据源需要同时提交**测试用例**
- 需要在此模块的 **resource** 下 新建包 ``META-INF.services`` , 在此包中新建文件 ``com.dlink.metadata.driver.Driver`` 内容如下:
  - ``com.dlink.metadata.driver.数据源类型Driver`` 
- 在此模块的 **pom.xml** 中，添加所需依赖, 需要注意的是 : 数据源本身的 ``JDBC``的 jar 不要包含在打包中 , 而是后续部署时,添加在 ``plugins`` 下
- 打包相关配置 修改如下:
  - 在 **dlink-core** 模块的 **pom.xml** 下 , 找到扩展数据源相关的依赖 `放在一起方便管理` 并且新增如下内容:
```xml
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>模块名称</artifactId>
            <scope>${scope.runtime}</scope>
        </dependency>
``` 
  - 在 **dlink** 根下 **pom.xml** 中 ,新增如下内容:
```xml
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>模块名称</artifactId>
            <version>${project.version}</version>
        </dependency>
```

  - 在 **dlink-assembly** 模块中 , 找到 ``package.xml`` 路径: **/dlink-assembly/src/main/assembly/package.xml** , 新增如下内容:
```xml
        <fileSet>
            <directory>${project.parent.basedir}/dlink-metadata/模块名称/target
            </directory>
            <outputDirectory>lib</outputDirectory>
            <includes>
                <include>模块名称-${project.version}.jar</include>
            </includes>
        </fileSet>
  ```

----

## 前端开发
- **dlink-web** 为 Dinky 的前端模块
- 扩展数据源相关表单所在路径: `dlink-web/src/pages/DataBase/`
  - 修改 `dlink-web/src/pages/DataBase/components/DBForm.tsx` 的 **const data** 中 添加如下:
eg:
```shell
         {
           type: 'Hive', 
         },
```
如下图:
![](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/datasource/extened_datasource_dbform.jpg)

注意: ``此处数据源类型遵照大驼峰命名规则``

  - 添加数据源logo图片
    - 路径: `dlink-web/public/database/`
    - logo图下载参考: [https://www.iconfont.cn](https://www.iconfont.cn) 

  - 修改 `dlink-web/src/pages/DataBase/DB.ts` , 添加如下:
eg:
```shell
    case 'hive':  
      imageUrl += 'hive.png'; # 需要和添加的图片名称保持一致
      break;
```
如下图:
![](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/datasource/extened_datasource_datasourceform.jpg)
   - 创建数据源相关表单属性在: `dlink-web/src/pages/DataBase/components/DataBaseForm.tsx`

----
至此 , 基于 Dinky 扩展数据源完成 , 如您也有扩展需求 ,请参照 [如何 Issuse](/zh-CN/developer_guide/issue.md)    [如何提交 PR](/zh-CN/developer_guide/pull_request.md)

