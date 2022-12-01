## Dinky  Website

该项目保留了用于构建 Dinky 官方网站的所有资源，服务于 http://www.dlink.top/

## 条件

Dinky 网站基于 Docusaurus 。如果您的 Docusaurus 版本低于 2.2.0，请升级到 2.2.0。另请确保您的Node版本为 14.x。

### 安装

```shell
$ yarn / npm i
```

### 本地启动

```shell
$ yarn start / npm run start / npx docusaurus start [--port 3111 ] [--host localhost ] 默认端口为 3000
```

此命令启动本地开发服务器并打开浏览器窗口。大多数更改都会实时预览，而无需重新启动服务器。

### 编译

```
$ yarn build / npm run build
```


## 生成新版本
```shell
npm run docusaurus docs:version 0.7
```

## 生成国际化json
```shell
# 直接运行 docs/package.json 的 write-translations
# 或者直接运行 如下命令：
docusaurus write-translations
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

- 在当前路径下的 docs 下添加新的 `.md` 文件
- 在本地运行 serve 验证文章是否正确显示
- 发送拉取的请求包含 `*.md`

## 文档目录结构

```html
blog --博客分享 
docs --最新文档
download --下载
i18n -- 国际化
src --源码
  - components --组件
      - HomepageFeature --首页特性
  - css --css 样式
  - pages  -- 首页 
static --静态文件
  - img --官网图片
versioned_docs --历史版本文档
  - version-0.6 --版本0.6文档
  - version-0.7 --版本0.7文档
version_sidebars --历史版本文档侧边栏
  - version-0.6-sidebars.json --历史版本文档侧边栏
  - version-0.7-sidebars.json --历史版本文档侧边栏
babel.config.js --babel配置文件
docusaurus.config.js --docusaurus配置文件
package.json --项目配置文件
README.md --项目介绍
sidebars.js --侧边栏配置文件
sidebarsDevelopment.js --侧边栏配置文件
versions.json --版本配置文件

```
