---
sidebar_position: 2
id: document
title: 文档贡献
---




良好的使用文档对任何类型的软件都是至关重要的。欢迎任何可以改进 Dinky 文档的贡献。

### 获取文档项目

Dinky 项目的文档维护在 [dinky-website](https://github.com/DataLinkDC/dinky-website) 。

首先你需要先将源项目 fork 到自己的 github 仓库中，然后将 fork 的文档克隆到本地计算机中。

```shell
git clone https://github.com/<your-github-user-name>/dinky-website
```
### 文档环境

Docusaurus 网站由 [Docusaurus](https://docusaurus.io/docs/category/getting-started) 提供支持。

请确保你已经安装了 nodejs 和 npm 。详细安装步骤可以查阅本地调试。

### 文档构建

在 Dinky 的根目录下执行：

```shell
# 推荐
npm install
# 运行 Docusaurus serve 启动一个本地服务器
npm start 
```

可以方便地实时预览效果。默认访问地址 [http://localhost:3000](http://localhost:3000/)

### 文档规范

汉字与英文、数字之间需空格，中文标点符号与英文、数字之间不需空格，以增强中英文混排的美观性和可读性。

建议在一般情况下使用 “你” 即可。当然必要的时候可以使用 “您” 来称呼，比如有 warning 提示的时候。

### 怎样提交文档 Pull Request

不要使用“git add.”提交所有更改。只需推送更改的文件，例如：*.md。

向 dev 分支提交 Pull Request。

### 参考文档

更多规范请参考 [Apache Flink 中文文档规范](https://cwiki.apache.org/confluence/display/FLINK/Flink+Translation+Specifications)