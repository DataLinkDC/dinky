---
sidebar_position: 13
id: upgrade
title: 升级指南
---

## 后端编译包升级

1. 下载最新的编译包
2. 对比一下 安装目录/config 下的文件，主要是 `application.yml` ，如果没用到最新特性，在最新版中修改一下mysql连接配置即可
3. 如果需要使用新特性，相关配置看相关文档描述即可

---

## 数据库 `SQL` 升级

```sql
-- 注意: 按照版本号依次升级 切不可跨版本升级 ${version} 代表的是你目前的 dinky版本+1 依次往下执行
-- 其中 /opt/dinky 是你dinky安装的目录 
mysql> source /opt/dinky/sql/upgrade/${version}_schema/mysql/dinky_ddl.sql -- 表的ddl
mysql> source /opt/dinky/sql/upgrade/${version}_schema/mysql/dinky_dml.sql  -- 表初始化数据 (部分版本无)
```

---

## 前后端分离升级

### 前端升级

重新编译 `dinky-web` 模块 移到 nginx 对应目录即可

### 后端升级

[查看如上](./upgrade#后端编译包升级)

---
