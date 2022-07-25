## Dinky  Website

该项目保留了用于构建 Dinky 官方网站的所有资源，服务于 http://www.dlink.top/

## 条件

Dinky 网站基于 Docusaurus 。如果您的 Docusaurus 版本低于 2.0.0，请升级到 2.0.0。另请确保您的Node版本为 14.x。

### 安装

```
$ yarn / npm i
```

### 本地启动

```
$ yarn start / npm run start / npx docusaurus start [--port 3111 ] [--host localhost ] 默认端口为 3000
```

此命令启动本地开发服务器并打开浏览器窗口。大多数更改都会实时预览，而无需重新启动服务器。

### 编译

```
$ yarn build / npm run build
```

此命令将静态内容生成到 build 目录中，并且可以使用任何静态内容托管服务提供服务。

## 如何提交一个  PR

确保您有提交问题以跟踪 PR：https://github.com/DataLinkDC/dinky-website/issues

- 不要使用 `git add .` 提交所有更改；
- 只需推送您更改的文件，例如：
  - `*.md`
  - `config.js`
- 提交 PR 到 **dev** 分支；

## 添加新文档

### 为文档添加新文章

- 在当前路径下的 docs 下添加新的 `.md` 文件
- 在本地运行 serve 验证文章是否正确显示
- 发送拉取的请求包含 `*.md`

### 为文档添加新版本

- 修改 docs 中的文档，然后在本地运行 `yarn start`或者 `npm run start`
- 将新添加的文档从 docs 复制到 versioned_docs 的新版本
- 修改最后一个所有版本在 `/src/pages/version/index.js`

### 添加博客/分享/实践

  - 只需在根目录下的 `blog` 文件夹下添加新的 `.md` 文件 , 需要注意点如下:

  ```text
     1. 文件命名格式: `年-月-日-title.md`  切记不可以中文命名 命名尽量见名知意 言简意赅
     2. 例如: `2022-01-01-hello-world.md`
     3. 文件名称中的日期将自动解析为改文章的创建日期
  ```
 - 博客作者的作者信息可以添加到 `authors.yml` 中 ,格式如下:

  ```text
  aiwenmo: # 博客作者
      name: aiwenmo # 博客作者名称
      title: Dinky 项目发起人 # 博客作者标题
      url: https://github.com/DataLinkDC/dlink # 博客作者主页
      image_url: http://www.aiwenmo.com/dinky/docs/dinky_logo.svg # 博客作者头像
  ```

  - 作者信息定义完成后 即可在 `md` 文件顶部引用 eg: `authors: [aiwenmo]` 
  - 文档头部信息引用模板:

  ```text
  # 注意 此处定义信息 必须从文档的顶部(第一行)开始
  --- 
  slug: first-blog-post  # 博客 slug 等于 文件名中自动解析的 title
  title: First Blog Post # 标题 可覆盖文件名称中的自动解析的 title
  authors: [dinky] # 可引用  authors.yml 定义的作者名称 可以多个作者 逗号隔开
  tags: [Hudi, Flink]  # 文档所属标签 可选项(可多个)
  ---  

  注意 : 如果不像在 authors.yml 中定义作者信息 模板如下:
  --- 
  slug: first-blog-post  # 博客 slug 等于文件名
  title: First Blog Post # 博客 标题 
  authors: 
       name: aiwenmo # 博客作者名称
       title: Dinky 项目发起人 # 博客作者标题
       url: https://github.com/DataLinkDC/dlink # 博客作者主页
       image_url: http://www.aiwenmo.com/dinky/docs/dinky_logo.svg # 博客作者头像
  tags: [Hudi, Flink]  # 文档所属标签 可选项(可多个且无需定义)
  --- 
  ```
  为了更方便得提交您的文章 博客相关的格式如下:
```text
---
slug: long-blog-post 
title: Blog Title
authors: [aiwenmo]
tags: [hello, docusaurus]
---

输入简短的该博客描述信息

<!--truncate--> # 注意:此处用于主页截断 上述的描述信息 以达到首页的博客列表limit 此行必须写

正文

```

**注意:** 如您的博客文章中含有图片 请遵守以下规范:
- 文章内图片统一放置在 `blog/blog_img/{文件的title名称 命名新的文件夹}/`
- 图片需要更名: 不要使用默认 `img.png` 根据其用途命名, 不能有中文 , 尽量见名知意
- 文档内引用图片 请使用相对路径 eg: `![图片名称](./blog_img/{文件的title名称 命名新的文件夹}/demo.png)`



## 文档目录结构

```html
blog --博客分享 
docs --最新文档
download --下载
i18n -- 国际化
src --源码
  - components --组件
      - HomepageFeature --首页特性
  - css --css
  - pages  -- all version 页面管理 
     - versions --版本管理   
static --静态文件
  - img --官网图片
versioned_docs --历史版本文档
  - version-0.6.0 --版本0.6.0文档
  - version-0.6.1 --版本0.6.1文档
version_sidebars --历史版本文档侧边栏
  - version-0.6.0-sidebars.json --历史版本文档侧边栏
  - version-0.6.1-sidebars.json --历史版本文档侧边栏
babel.config.js --babel配置文件
docusaurus.config.js --docusaurus配置文件
package.json --项目配置文件
README.md --项目介绍
sidebars.js --侧边栏配置文件
sidebarsDevelopment.js --侧边栏配置文件
versions.json --版本配置文件

```
