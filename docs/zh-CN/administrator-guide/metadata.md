当用户查看或者使用数据源中的DDL或者DML，可通过元数据中心获取数据源中的相关DDL或者DML。目前元数据中心包含：

- **表信息**

- **字段信息**

- **SQL生成**

其中在SQL生成中又包括：

- **FlinkDDL语句**
- **SELECT语句**
- **SQLDDL语句**

首先进入FlinkSQL Studio中的元数据，选择已经配置好的数据源，会出现数据源对应的schema。

![进入元数据](http://www.aiwenmo.com/dinky/dev/docs/%E8%BF%9B%E5%85%A5%E5%85%83%E6%95%B0%E6%8D%AE.png)



![选择源数据](http://www.aiwenmo.com/dinky/dev/docs/%E9%80%89%E6%8B%A9%E6%BA%90%E6%95%B0%E6%8D%AE.png)



![schema信息](http://www.aiwenmo.com/dinky/dev/docs/schema%E4%BF%A1%E6%81%AF.png)



出现以上schema后，查看schema下的表，右键单击schema下的表即可看到表信息，字段信息及SQL生成。

![表信息](http://www.aiwenmo.com/dinky/dev/docs/%E8%A1%A8%E4%BF%A1%E6%81%AF.png)



![字段信息](http://www.aiwenmo.com/dinky/dev/docs/%E5%AD%97%E6%AE%B5%E4%BF%A1%E6%81%AF.png)

![SQL生成](http://www.aiwenmo.com/dinky/dev/docs/SQL%E7%94%9F%E6%88%90.png)

如何配置数据源请参考[数据源管理](/zh-CN/administrator-guide/registerCenter/datasource_manage.md)。



