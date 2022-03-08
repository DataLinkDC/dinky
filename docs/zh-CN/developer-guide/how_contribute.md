# 如何贡献

​       Dinky0.6基本功能已经全部开发完成，已经达到生产可用级别。为了Dinky社区生态更加完善，社区希望有更多的人参与进来。不管是小白还是大佬，Dinky社区欢迎你的加入，未来共同建设Dinky生态体系。

​       Dinky目前围绕Flink已经初步建设完成，其他数据源也在不断扩展中，当前支持的MySQL，Doris等数据库，更多数据源支持请查阅[扩展数据源](/zh-CN/extend/datasource.md)。后续还会有更多的功能，如数据集成，数据治理等。

  那对于参与进来的伙伴如何贡献呢？那么贡献的方式有如下3种方式：

- 代码贡献；
- 文档类贡献；
- 问题解答；

为了方便更多人参与进社区，不管你是代码贡献者还是文档贡献者，Dinky社区方便大家开发，准备了文档贡献和代码贡献的开发者指南

## 文档贡献

首先要下载[Dinky源码](https://github.com/DataLinkDC/dlink/tree/dev)，下载完成后，需要安装nodejs和npm。详细安装步骤可以查阅本地调试。由于Dinky文档是有[docsify](https://docsify.js.org/#/zh-cn/quickstart)维护。因此需要在dinky根目录执行如下命令，在本地方可启动：

```
#推荐全局安装 docsify-cli 工具
npm i docsify-cli -g
#运行 docsify serve 启动一个本地服务器
docsify serve docs
```

可以方便地实时预览效果。默认访问地址 [http://localhost:3000](http://localhost:3000/)

##  代码贡献

请查阅[本地调试](/zh-CN/developer-guide/local_debug.md)或[远程调试](/zh-CN/developer-guide/remote_debug.md)

## 开发者须知

开发不同的代码需要不同的分支

- 如果要基于二进制包进行开发，需要切换到对应的分支代码，如0.5.1；
- 如果想要开发新代码，切换到dev分支即可；
