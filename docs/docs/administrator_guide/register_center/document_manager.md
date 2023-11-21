---
position: 3
id: document_manager
title: 文档管理
---


## 文档管理列表

![document_manager_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/document_manager/document_manager_list.png)

## 查看文档描述

![document_manager_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/document_manager/document_show_desc.png)

## 创建文档

![create_document](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/document_manager/create_document.png)

## 维护文档

![update_document](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/document_manager/update_document.png)

**参数配置说明:**

- **名称:** 文档名称 (必填) 此处定义可在数据开发中使用触发快捷提示 ;
- **文档类型:** 文档类型 (必填) ;
- **类型:** 定义函数类型 (必填) ;
- **子类型:** 定义函数子类型 (必填) ;
- **描述:** 该文档描述  ;
- **填充值:**
  - 如果希望在函数 _`LTRIM(parms)`_ 中输入参数 则此处填充值语法为: _`LTRIM(${1:})`_  此时的1代表第一个光标 ; 如需多个 数字+1即可 tab键切换光标 ;
  - 如不需要参数则直接输入期望填充值 ;
- **版本:** 文档版本 (必填) ;

**填充值提示效果:**
![show_hotkey_tips](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/document_manager/show_hotkey_tips.png)
