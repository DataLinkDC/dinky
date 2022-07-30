---
sidebar_position: 4
id: alert
title: 扩展报警插件
---




Dinky 告警机制遵循 SPI,可随意扩展所需要的告警机制。如需扩展可在 dlink-alert 模块中进行可插拔式扩展。现已经支持的告警机制包括如下：

- DingDingTalk
- 企业微信: 同时支持**APP** 和 **WeChat 群聊** 方式
- Email
- 飞书

Dinky 学习了 ``Apache Dolphinscheduler`` 的插件扩展机制，可以在 Dinky 中添加自定义的告警机制。

## 准备工作
- 本地开发环境搭建
    - 详见 [开发者本地调试手册](../../developer_guide/local_debug)

## 后端开发
- 在 dlink-alert 新建子模块 , 命名规则为 `dlink-alert-{报警类型}` 在子模块中实现 `dlink-alert-{报警类型}` 的报警机制

## 后端开发
- 在 **dlink-alert** 模块中， 右键**新建子模块**, 命名规则: **dlink-alert-{报警类型}**
- **代码层面** 根据告警场景自行实现报警逻辑 
    - 注意事项:
        - 不可在父类的基础上修改代码，可以在子类中进行扩展 ,或者重写父类方法
        - 扩展告警类型需要同时提交**测试用例**
        - 在每个告警类型的 Constants 常量类中 必须声明 ``static final String TYPE = "Email";`` 否则会报错
- 需要在此模块的 **resource** 下 新建包 ``META-INF.services`` , 在此包中新建文件 ``com.dlink.alert.Alert`` 内容如下:
    - ``com.dlink.alert.{报警类型}.{报警类型Alert}`` 参考其他告警类型的此文件
- 打包相关配置 修改如下:
    - 在 **dlink-core** 模块的 **pom.xml** 下 , 找到扩展告警相关的依赖 `放在一起方便管理` 并且新增如下内容:
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
            <directory>${project.parent.basedir}/dlink-alert/模块名称/target
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
- 扩展告警插件相关表单所在路径: `dlink-web/src/pages/AlertInstance`
  - 修改 `dlink-web/src/pages/AlertInstance/conf.ts` 

  **ALERT_TYPE** 添加如下 eg:
  ```
  EMAIL:'Email', 
  ```
  **ALERT_CONFIG_LIST** 添加如下 eg: 
  ```
   {
      type: ALERT_TYPE.EMAIL,
    } 
  ```
 **注意:** 此处属性值需要与后端 `static final String TYPE = "Email";`变量值保持一致

如下图:
![extend_alert_conf](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/function_expansion/alert/extend_alert_conf.png)


  修改 `dlink-web/src/pages/AlertInstance/icon.tsx` 的 **getAlertIcon** 中 
  
  添加如下 eg:
```
  case ALERT_TYPE.EMAIL:
      return (<Icon component={EmailSvg}/>);
```
同时在下方定义 SVG :  `如不定义将使用默认 SVG`

svg 获取: [https://www.iconfont.cn](https://www.iconfont.cn)
```
export const EmailSvg = () => (
      {svg 相关代码}
);
```
**注意:** svg 相关代码中需要将 **width**  **height** 统一更换为 **width={svgSize} height={svgSize}**

如下图:
![extened_alert_icon](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/function_expansion/alert/extened_alert_icon.png)



  - 修改 `dlink-web/src/pages/AlertInstance/components/AlertInstanceChooseForm.tsx` 
 
  追加如下  eg: 
```
      {(values?.type == ALERT_TYPE.EMAIL || alertType == ALERT_TYPE.EMAIL)?
        <EmailForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type == ALERT_TYPE.EMAIL || alertType == ALERT_TYPE.EMAIL}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        />:undefined
      }
```
其中需要修改的地方为
-  `EMAIL` 替换为上述 **dlink-web/src/pages/AlertInstance/conf.ts** 中 **ALERT_TYPE** 的新增类型
-  `EmailForm` 为新建告警表单文件 **dlink-web/src/pages/AlertInstance/components/EmailForm.tsx** 中的 **EmailForm** .

如下图:
![extened_alert_choose_form](http://www.aiwenmo.com/dinky/docs/zh-CN/extend/function_expansion/alert/extened_alert_choose_form.png)

 - 需要新建表单文件 , 命名规则: ``{告警类型}Form``
   - 该文件中除 **表单属性** 外 , 其余可参照其他类型告警 , 建议复制其他类型告警的表单 , 修改其中的表单属性即可 
   - 注意: 
     - 部分表单属性保存为 Json 格式
     - 需要修改 如下的表单配置
 

 ```shell
    找到如下相关代码: 
    const [formVals, setFormVals] = useState<Partial<AlertInstanceTableListItem>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.EMAIL,  # 此处需要修改为新增的告警类型
    params: props.values?.params,
    enabled: props.values?.enabled,
    });

```


---- 
:::tip 说明
至此 , 基于 Dinky 扩展告警完成 , 如您也有扩展需求 ,请参照如何 [[Issuse]](../../developer_guide/contribution/issue)  和如何[[提交 PR]](../../developer_guide/contribution/pull_request)
:::