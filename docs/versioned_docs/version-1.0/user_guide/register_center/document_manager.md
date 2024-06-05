---
position: 4
sidebar_position: 4
id: document_manager
title: 文档管理
---

:::tip 简介
文档管理是 Dinky 系统中重要的功能之一，主要用于数据开发编辑器内的函数快捷提示,自动补全,以及常用 SQL 模版语句的快捷输入等功能.

Dinky 管理系统中的文档管理功能，可以对文档进行新增、编辑、删除等操作。新增/更新完成后,即可在数据开发中使用.

在系统初始化时,默认插入了一些常用的函数文档, 以供参考.
:::

## 文档管理列表

![document_manager_list](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/document_manager/document_manager_list.png)

## 查看文档描述

![doc_desc](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/document_manager/doc_desc.png)

## 创建文档

![doc_create](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/document_manager/doc_create.png)

## 参数解读

| 参数名称 | 参数说明                                                                                                                                                                                                    | 是否必填 | 默认值     |
|:-----|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----|:--------|
| 名称   | 文档名称,用于触发快捷提示                                                                                                                                                                                           | 是    | 无       |
| 文档类型 | 文档类型,主要用于管理文档分类,从而在编辑器内实现不同的提示/补全效果 ,可选值:<br/> 代码片段/模版<br/>Flink 参数<br/>函数/UDF<br/>其他                                                                                                                   | 是    | 代码片段/模板 |
| 注册类型 | 注册类型, 该类型决定了在编辑器内的提示/补全效果渲染各种类型                                                                                                                                                                         | 是    | 无       |
| 子类型  | 定义函数子类型/语言                                                                                                                                                                                              | 是    | 无       |
| 描述   | 该文档描述                                                                                                                                                                                                   | 否    | 无       |
| 填充值  | 填充值 ,由 名称触发快捷提示时,回车后将自动补全该值<br/> 如: _`SELECT * FROM table WHERE name = ${1:}`_ ; <br/> 如果希望在函数 _`LTRIM(parms)`_ 中输入参数 则此处填充值语法为: _`LTRIM(${1:})`_  此时的1代表第一个光标 ; 如需多个数字+1即可 tab键切换光标,如不需要参数则直接输入期望填充值即可 | 是    | 无       |
| 版本   | 文档版本<br/>Flink1.14<br/> Flink1.15<br/> Flink1.16<br/> Flink1.17<br/> Flink1.18<br/> All Version                                                                                                         | 是    | 无       |
| 是否启用 | 是否启用 , 只有启用的文档才会在数据开发中使用触发快捷提示                                                                                                                                                                          | 是    | 不启用     |

**填充值提示效果:**
![ducument_fill_style](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/document/ducument_fill_style.png)