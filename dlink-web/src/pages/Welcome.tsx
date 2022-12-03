/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import React from 'react';
import {Alert, Card, Timeline, Typography} from 'antd';
import {FormattedMessage} from 'umi';
import styles from './Welcome.less';
import {VERSION} from "@/components/Common/Version";
import {l} from "@/utils/intl";

const {Text, Link, Paragraph} = Typography;
const CodePreview: React.FC = ({children}) => (
  <pre className={styles.pre}>
    <code>
      <Typography.Text copyable>{children}</Typography.Text>
    </code>
  </pre>
);

export default (): React.ReactNode => {

  return (
    <>
      <Card>
        <Alert
          message={l('pages.welcome.alertMessage', '', {version: VERSION})}
          type="success"
          showIcon
          banner
          style={{
            margin: -12,
            marginBottom: 24,
          }}
        />
        <Typography.Text strong>
          <FormattedMessage id="pages.welcome.Community" defaultMessage="官方社区"/>{' '}
          <FormattedMessage id="pages.welcome.link" defaultMessage="欢迎加入"/>
        </Typography.Text>
        <Paragraph>
          <Typography.Text strong>
            <FormattedMessage id="pages.welcome.QQ" defaultMessage="QQ官方社区群"/>{' '}
            <FormattedMessage id="pages.welcome.QQcode" defaultMessage="543709668"/>
          </Typography.Text>
        </Paragraph>
        <CodePreview>微信公众号：Dinky 开源</CodePreview>
        <Typography.Text
          strong
          style={{
            marginBottom: 12,
          }}
        >
          <FormattedMessage id="pages.welcome.advancedLayout" defaultMessage="Github"/>{' '}
          <a
            href="https://github.com/DataLinkDC/dlink"
            rel="noopener noreferrer"
            target="__blank"
          >
            <FormattedMessage id="pages.welcome.star" defaultMessage="欢迎 Star "/>
          </a>
        </Typography.Text>
        <Paragraph>
          <Typography.Text strong>
            <FormattedMessage id="pages.welcome.upgrade" defaultMessage="更新日志"/>
          </Typography.Text>
        </Paragraph>
        <p></p>
        <Timeline pending={<><Text code>0.8.0</Text>
          <Text type="secondary">{l('global.stay.tuned')}</Text>
          <p></p>
          <Paragraph>
            <ul>
              <li>
                <Link>代码重构</Link>
              </li>
              <li>
                <Link>数据链路分析</Link>
              </li>
              <li>
                <Link>CDAS & CTAS 语法支持</Link>
              </li>
            </ul>
          </Paragraph></>} reverse={true}>
          <Timeline.Item><Text code>0.1.0</Text> <Text type="secondary">2021-06-06</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>FlinkSql Studio 基本功能</Link>
                </li>
                <li>
                  <Link>Flink 集群管理</Link>
                </li>
                <li>
                  <Link>FlinkSql 任务管理</Link>
                </li>
                <li>
                  <Link>FlinkSql 文档管理</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.2.0</Text> <Text type="secondary">2021-06-08</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>FlinkSql Studio 代码底层架构进行大优化</Link>
                </li>
                <li>
                  <Link>支持以 SPI 的方式扩展任意 Connector，同 Flink 官网</Link>
                </li>
                <li>
                  <Link>提供了 dlink-connector-jdbc，额外支持 Oracle 和 ClickHouse 读写，该扩展包可直接上传 Flink 集群的
                    lib
                    进行远程使用，无需重新编译</Link>
                </li>
                <li>
                  <Link>提供了 dlink-client-1.12，支持 Flink 1.12.0+ 多集群的远程使用与本地隔离使用，1.10、1.11 和 1.13
                    集群可能存在问题</Link>
                </li>
                <li>
                  <Link>优化了 FlinkSQL 执行与提交到远程集群的任务名，默认为作业的中文别名</Link>
                </li>
                <li>
                  <Link>优化了目录的操作，点击节点即可打开作业，无须右键打开</Link>
                </li>
                <li>
                  <Link>优化了执行结果信息，添加了任务名的展示</Link>
                </li>
                <li>
                  <Link>对 Studio 界面进行了一定的提示优化</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.2.1</Text> <Text type="secondary">2021-06-11</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>FlinkSql Studio 页面仿IDE紧凑型设计改进</Link>
                </li>
                <li>
                  <Link>解决了目录树右键菜单的不能任意点关闭问题</Link>
                </li>
                <li>
                  <Link>解决了选项卡关闭不能正确刷新编辑器的问题</Link>
                </li>
                <li>
                  <Link>解决了当前位置不根据选项卡刷新的问题</Link>
                </li>
                <li>
                  <Link>增加了目录树非空文件夹的灰色删除按钮</Link>
                </li>
                <li>
                  <Link>增加了目录树创建根目录按钮以及折叠按钮</Link>
                </li>
                <li>
                  <Link>优化了连接器刷新与清空按钮</Link>
                </li>
                <li>
                  <Link>优化了作业异步提交的提示</Link>
                </li>
                <li>
                  <Link>增加了简易的使用帮助</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.2.2</Text> <Text type="secondary">2021-06-17</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>AGGTABLE 语法实现</Link>
                </li>
                <li>
                  <Link>增加了 dlink-function 模块用来管理 UDF 等，其可直接上传至集群lib</Link>
                </li>
                <li>
                  <Link>解决了表单无法正确提交 Fragment 的问题</Link>
                </li>
                <li>
                  <Link>开启Hash路由解决了历史路由集成Springboot的问题</Link>
                </li>
                <li>
                  <Link>解决了 FlinkSQL 编辑器的 CTRL+C 撤销乱窜问题</Link>
                </li>
                <li>
                  <Link>解决了右键删除目录树的作业时对应选项卡不关闭的问题</Link>
                </li>
                <li>
                  <Link>解决了新增作业其配置无法正常初始化的问题</Link>
                </li>
                <li>
                  <Link>解决了新增作业集群下拉框值为0的问题</Link>
                </li>
                <li>
                  <Link>增加了新增作业自动定位及打开选项卡的功能</Link>
                </li>
                <li>
                  <Link>增加了在不选择会话值时自动禁用会话的功能</Link>
                </li>
                <li>
                  <Link>解决了在修改配置后异步提交任务会将作业识别为草稿的问题</Link>
                </li>
                <li>
                  <Link>扩展了 Flink Client 1.13</Link>
                </li>
                <li>
                  <Link>解决了编辑器初始化高度5px的问题</Link>
                </li>
                <li>
                  <Link>解决了首页更新日志点击会打开新连接的问题</Link>
                </li>
                <li>
                  <Link>解决了首次加载草稿无法执行 Sql 的问题</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.2.3</Text> <Text type="secondary">2021-06-21</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>优化了任务配置集群下拉框的展示</Link>
                </li>
                <li>
                  <Link>解决了新增会话后输入框的值仍保留的问题</Link>
                </li>
                <li>
                  <Link>修改了历史下拉框语句为全部展示</Link>
                </li>
                <li>
                  <Link>增加了作业同步执行时的任务名配置</Link>
                </li>
                <li>
                  <Link>增加了连接器当前的集群与会话显示</Link>
                </li>
                <li>
                  <Link>增加了选择集群或会话以及同步执行时的连接器自动刷新</Link>
                </li>
                <li>
                  <Link>解决了在修改作业配置后异步提交作业名未定义的问题</Link>
                </li>
                <li>
                  <Link>增加了历史记录下拉列表鼠标悬浮显示全部SQL的功能</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.3.0</Text> <Text type="secondary">2021-07-27</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>批流任务实时查看 SELECT 数据</Link>
                </li>
                <li>
                  <Link>共享会话持久管理</Link>
                </li>
                <li>
                  <Link>数据源注册与连接</Link>
                </li>
                <li>
                  <Link>元数据查询</Link>
                </li>
                <li>
                  <Link>历史任务监控与管理</Link>
                </li>
                <li>
                  <Link>集群作业监控与管理</Link>
                </li>
                <li>
                  <Link>单任务血缘分析</Link>
                </li>
                <li>
                  <Link>语句语法与逻辑检查</Link>
                </li>
                <li>
                  <Link>界面交互升级</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.3.1</Text> <Text type="secondary">2021-08-25</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>解决日志无法正确输出的bug</Link>
                </li>
                <li>
                  <Link>解决数据源注册可能失败的bug</Link>
                </li>
                <li>
                  <Link>扩展了对Flink 1.11的支持，并更新了其他的最新版本</Link>
                </li>
                <li>
                  <Link>Flink集群添加了版本号的自动获取及展示</Link>
                </li>
                <li>
                  <Link>修复了本地环境+远程执行导致集群查询未果的bug</Link>
                </li>
                <li>
                  <Link>增加了 Flink StreamGraph 预览功能</Link>
                </li>
                <li>
                  <Link>修复了 Studio 编辑器的自动补全无法正常提示的bug</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.3.2</Text> <Text type="secondary">2021-10-22</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增了SQL编辑器自动补全文档的功能</Link>
                </li>
                <li>
                  <Link>优化了 Flink 多版本间的切换，下沉 Flink 获取表字段的逻辑</Link>
                </li>
                <li>
                  <Link>增加了 plugins 类加载路径用于加载 Flink 的有关依赖</Link>
                </li>
                <li>
                  <Link>增加了 dlink-extends 模块用于扩展依赖打包</Link>
                </li>
                <li>
                  <Link>增加了更加稳定 Nginx 前后端分离部署方式</Link>
                </li>
                <li>
                  <Link>优化所有的新增功能别名未填则默认为名称</Link>
                </li>
                <li>
                  <Link>优化在注册 Flink 集群时的链接检测与异常输出</Link>
                </li>
                <li>
                  <Link>支持set语法来设置执行环境参数</Link>
                </li>
                <li>
                  <Link>升级了 Flink 1.13 的版本支持为 1.13.3</Link>
                </li>
                <li>
                  <Link>扩展了 Flink 1.14 的支持</Link>
                </li>
                <li>
                  <Link>修复血缘分析缩进树图渲染bug</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.4.0</Text> <Text type="secondary">2021-12-02</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>修复 FlinkSQL 代码提示会因为打开多个选项卡而重复的问题</Link>
                </li>
                <li>
                  <Link>新增 FlinkSQL 自动补全可以根据 sql 上下文来提示已注册的元数据的功能</Link>
                </li>
                <li>
                  <Link>修复 Flink 1.14.0 远程提交无法正确提交任务至集群的问题</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 与 yarn-application 的任务提交方式</Link>
                </li>
                <li>
                  <Link>更新 dlink 的 flink 主版本号为 1.13.3</Link>
                </li>
                <li>
                  <Link>新增 yarn-application 的sql作业提交方式</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 的作业提交方式</Link>
                </li>
                <li>
                  <Link>新增集群配置管理及维护页面</Link>
                </li>
                <li>
                  <Link>新增 Jar 管理及维护页面</Link>
                </li>
                <li>
                  <Link>新增 FlinkSQL 语句集提交</Link>
                </li>
                <li>
                  <Link>executor 模块独立并优化增强逻辑</Link>
                </li>
                <li>
                  <Link>新增系统配置管理</Link>
                </li>
                <li>
                  <Link>新增 yarn-application 的sql作业提交方式</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 和 yarn-application 集群的作业停止</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 和 yarn-application 集群的自动注册</Link>
                </li>
                <li>
                  <Link>新增 savepoint 各种机制触发</Link>
                </li>
                <li>
                  <Link>新增 savepoint 的归档管理</Link>
                </li>
                <li>
                  <Link>新增任务启动 savepoint 多种启动策略</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 和 yarn-application 的从 savepoint 启动</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 和 yarn-application 的启动时多样化集群配置生效</Link>
                </li>
                <li>
                  <Link>优化项目结构与打包结构</Link>
                </li>
                <li>
                  <Link>新增yarn-application 的自定义Jar提交</Link>
                </li>
                <li>
                  <Link>优化 Studio 页面布局与拖动</Link>
                </li>
                <li>
                  <Link>新增全模块实时的布局拖动与滚动条联动</Link>
                </li>
                <li>
                  <Link>新增用户管理模块</Link>
                </li>
                <li>
                  <Link>优化 SET 语法</Link>
                </li>
                <li>
                  <Link>新增作业配置的其他配置低优先级加载实现</Link>
                </li>
                <li>
                  <Link>新增一键回收过期集群</Link>
                </li>
                <li>
                  <Link>优化打包及扩展方式</Link>
                </li>
                <li>
                  <Link>解决血缘分析与执行图分析异常</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.5.0</Text> <Text type="secondary">2022-01-16</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增 JobPlanGraph 来替代 StreamGraph </Link>
                </li>
                <li>
                  <Link>新增 SQLServer Jdbc Connector 的实现</Link>
                </li>
                <li>
                  <Link>修复编辑集群配置测试后保存会新建的bug</Link>
                </li>
                <li>
                  <Link>新增 Local 的运行模式选择并优化 JobManager</Link>
                </li>
                <li>
                  <Link>修复登录页报错弹框</Link>
                </li>
                <li>
                  <Link>优化所有模式的所有功能的执行逻辑</Link>
                </li>
                <li>
                  <Link>新增 SavePoint 的 restAPI 实现</Link>
                </li>
                <li>
                  <Link>新增 OpenAPI 的执行sql、校验sql、获取计划图、获取StreamGraph、获取预览数据接口</Link>
                </li>
                <li>
                  <Link>新增 OpenAPI 的执行Jar、停止、SavePoint接口</Link>
                </li>
                <li>
                  <Link>新增数据源的 Sql 作业语法校验</Link>
                </li>
                <li>
                  <Link>新增数据源的 Sql 作业语句执行</Link>
                </li>
                <li>
                  <Link>优化 ClickHouse SQL 校验逻辑</Link>
                </li>
                <li>
                  <Link>建立官网文档</Link>
                </li>
                <li>
                  <Link>解决 Yarn Application 解析数组异常问题</Link>
                </li>
                <li>
                  <Link>解决自定义Jar配置为空会导致异常的bug</Link>
                </li>
                <li>
                  <Link>解决任务提交失败时注册集群报错的bug</Link>
                </li>
                <li>
                  <Link>解决set在perjob和application模式不生效的问题</Link>
                </li>
                <li>
                  <Link>解决perjob和application模式的任务名无法自定义的问题</Link>
                </li>
                <li>
                  <Link>支持 Kubernetes Session 和 Application 模式提交任务</Link>
                </li>
                <li>
                  <Link>新增 FlinkSQL 执行环境方言及其应用功能</Link>
                </li>
                <li>
                  <Link>新增 Mysql,Oracle,PostGreSql,ClickHouse,Doris,Java 方言及图标</Link>
                </li>
                <li>
                  <Link>新增 元数据查看表和字段信息</Link>
                </li>
                <li>
                  <Link>新增 FlinkSQL 及 SQL 导出</Link>
                </li>
                <li>
                  <Link>新增 集群与数据源的 Studio 管理交互</Link>
                </li>
                <li>
                  <Link>修复 set 语法在1.11和1.12的兼容问题</Link>
                </li>
                <li>
                  <Link>升级 各版本 Flink 依赖至最新版本以解决核弹问题</Link>
                </li>
                <li>
                  <Link>新增 Yarn 的 Kerboros 验证</Link>
                </li>
                <li>
                  <Link>新增 ChangLog 和 Table 的查询及自动停止实现</Link>
                </li>
                <li>
                  <Link>修改 项目名为 Dinky 以及图标</Link>
                </li>
                <li>
                  <Link>优化 血缘分析图</Link>
                </li>
                <li>
                  <Link>新增 快捷键保存、校验、美化</Link>
                </li>
                <li>
                  <Link>新增 UDF Java方言的Local模式的在线编写、调试、动态加载</Link>
                </li>
                <li>
                  <Link>新增 编辑器选项卡右键关闭其他和关闭所有</Link>
                </li>
                <li>
                  <Link>新增 BI选项卡的折线图、条形图、饼图</Link>
                </li>
                <li>
                  <Link>修改 草稿为引导页</Link>
                </li>
                <li>
                  <Link>修复 批量启用禁用集群、关闭tab无法更新位置及无法取消FlinkSQLEnv的bug</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.5.1</Text> <Text type="secondary">2022-01-24</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>修复 SHOW 和 DESC 的查询预览失效</Link>
                </li>
                <li>
                  <Link>修复 作业非remote作业进行remote语法校验的问题</Link>
                </li>
                <li>
                  <Link>增加 dlink-client-hadoop 版本定制依赖</Link>
                </li>
                <li>
                  <Link>优化 菜单</Link>
                </li>
                <li>
                  <Link>优化 pom及升级log4j至最新</Link>
                </li>
                <li>
                  <Link>修复 前端多处bug</Link>
                </li>
                <li>
                  <Link>新增 F2 全屏开发</Link>
                </li>
                <li>
                  <Link>升级 SpringBoot 至 2.6.3</Link>
                </li>
                <li>
                  <Link>优化 日志依赖</Link>
                </li>
                <li>
                  <Link>修复 前端 state 赋值 bug</Link>
                </li>
                <li>
                  <Link>修复 异常预览内容溢出 bug</Link>
                </li>
                <li>
                  <Link>修复 数据预览特殊条件下无法获取数据的 bug</Link>
                </li>
                <li>
                  <Link>优化 SQL编辑器性能</Link>
                </li>
                <li>
                  <Link>修复 全屏开发退出后 sql 不同步</Link>
                </li>
                <li>
                  <Link>升级 Flink 1.14.2 到 1.14.3</Link>
                </li>
                <li>
                  <Link>修复 Flink 1.14 提交任务报错缺类 bug</Link>
                </li>
                <li>
                  <Link>优化 作业配置查看及全屏开发按钮</Link>
                </li>
                <li>
                  <Link>新增 K8S集群配置</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.0</Text> <Text type="secondary">2022-03-20</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>优化 sql美化</Link>
                </li>
                <li>
                  <Link>扩展 Doris、SqlServer、Oracle 数据源注册、元数据、查询和执行</Link>
                </li>
                <li>
                  <Link>新增 元数据生成 FlinkSQL 和 SQL</Link>
                </li>
                <li>
                  <Link>新增 CDCSOURCE 多源合并任务语法支持</Link>
                </li>
                <li>
                  <Link>新增 作业生命周期管理</Link>
                </li>
                <li>
                  <Link>新增 FlinkJar Dialect 的管理</Link>
                </li>
                <li>
                  <Link>新增 Batch 引擎</Link>
                </li>
                <li>
                  <Link>优化 异常日志的捕获、反馈与持久化</Link>
                </li>
                <li>
                  <Link>优化 默认启用数据预览等</Link>
                </li>
                <li>
                  <Link>新增 作业目录树关键字搜索框</Link>
                </li>
                <li>
                  <Link>扩展 Phoenix 数据源注册、元数据、查询和执行</Link>
                </li>
                <li>
                  <Link>优化 元数据数据源的的类型转换和连接管理</Link>
                </li>
                <li>
                  <Link>新增 数据源的连接信息片段机制自动注入</Link>
                </li>
                <li>
                  <Link>升级 Flink 1.13.5 至 1.13.6</Link>
                </li>
                <li>
                  <Link>新增 密码修改</Link>
                </li>
                <li>
                  <Link>新增 报警模块（实例和组）</Link>
                </li>
                <li>
                  <Link>新增 钉钉报警</Link>
                </li>
                <li>
                  <Link>新增 微信企业号报警</Link>
                </li>
                <li>
                  <Link>新增 运维中心任务实例功能</Link>
                </li>
                <li>
                  <Link>新增 运维中心任务监控功能</Link>
                </li>
                <li>
                  <Link>新增 运维中心任务监控的FlinkWebUI、智能停止、SavePoint等操作</Link>
                </li>
                <li>
                  <Link>新增 运维中心任务监控的作业总览</Link>
                </li>
                <li>
                  <Link>新增 运维中心任务监控的配置信息</Link>
                </li>
                <li>
                  <Link>新增 运维中心任务监控的智能重启和报警推送</Link>
                </li>
                <li>
                  <Link>新增 实时监控 Flink Job</Link>
                </li>
                <li>
                  <Link>新增 实时自动告警</Link>
                </li>
                <li>
                  <Link>优化 K8S Application 提交配置</Link>
                </li>
                <li>
                  <Link>优化 PerJob 和 Application 作业的JID提交检测</Link>
                </li>
                <li>
                  <Link>修复 集群配置参数项为空时无法正常提交perjob任务的bug</Link>
                </li>
                <li>
                  <Link>优化 语法检测建议的结果提示</Link>
                </li>
                <li>
                  <Link>修复 Oracle无法正确获取元数据的bug</Link>
                </li>
                <li>
                  <Link>新增 Application 模式自增修正checkpoint和savepoint存储路径</Link>
                </li>
                <li>
                  <Link>修复 报警组刷新当前页面时无法正常显示下拉报警实例</Link>
                </li>
                <li>
                  <Link>优化 当提交作业无法获取JID时变为提交失败</Link>
                </li>
                <li>
                  <Link>新增 作业发布时进行语法校验和逻辑检查</Link>
                </li>
                <li>
                  <Link>优化 IDEA调试时的依赖配置</Link>
                </li>
                <li>
                  <Link>新增 作业上下线自动提交和停止任务</Link>
                </li>
                <li>
                  <Link>修复 用户未登录时后台报错及鉴权问题</Link>
                </li>
                <li>
                  <Link>新增 作业生命周期与任务实例同步联动</Link>
                </li>
                <li>
                  <Link>修复 用户逻辑删除bug</Link>
                </li>
                <li>
                  <Link>新增 运维中心的作业实例与历史切换</Link>
                </li>
                <li>
                  <Link>新增 运维中心的异常信息实现</Link>
                </li>
                <li>
                  <Link>新增 运维中心的FlinkSQL实现</Link>
                </li>
                <li>
                  <Link>新增 运维中心的报警记录实现</Link>
                </li>
                <li>
                  <Link>修复 kubernetes集群配置相关显示bug</Link>
                </li>
                <li>
                  <Link>新增 运维中心血缘分析——字段级</Link>
                </li>
                <li>
                  <Link>优化 Studio血缘分析为字段级</Link>
                </li>
                <li>
                  <Link>新增 Hive 数据源注册、元数据、查询和执行</Link>
                </li>
                <li>
                  <Link>新增 作业剪切和粘贴</Link>
                </li>
                <li>
                  <Link>新增 实时任务监控容错机制</Link>
                </li>
                <li>
                  <Link>修复 Doris无法获取到列的主键信息</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.1</Text> <Text type="secondary">2022-04-01</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增 issue 模板</Link>
                </li>
                <li>
                  <Link>修复 Jar 任务存在空配置时提交失败的 bug</Link>
                </li>
                <li>
                  <Link>修复 Mysql 字段类型转换的 bug</Link>
                </li>
                <li>
                  <Link>修复 Hive 多语句查询失败的 bug</Link>
                </li>
                <li>
                  <Link>修复 Jar 任务无法被运维中心监控的 bug</Link>
                </li>
                <li>
                  <Link>新增 Phoenix 的 Flink Connector</Link>
                </li>
                <li>
                  <Link>升级 mybatis-plus-boot-starter 至最新版本 3.5.1</Link>
                </li>
                <li>
                  <Link>新增 savepointTask 的 Open API</Link>
                </li>
                <li>
                  <Link>新增 WeChat WebHook 报警方式</Link>
                </li>
                <li>
                  <Link>修复 报警实例表单联动的 bug</Link>
                </li>
                <li>
                  <Link>修复 数据源元数据表信息切换无效的 bug</Link>
                </li>
                <li>
                  <Link>修复 用户信息修改导致密码被二次加密的 bug</Link>
                </li>
                <li>
                  <Link>修复 启动文件的编码格式为 LF</Link>
                </li>
                <li>
                  <Link>新增 数据开发全屏的退出按钮</Link>
                </li>
                <li>
                  <Link>修复 FlinkSQL 美化时出现空格的 bug</Link>
                </li>
                <li>
                  <Link>修复 停止 per-job 任务无法销毁集群实例的 bug</Link>
                </li>
                <li>
                  <Link>优化 文档管理表单</Link>
                </li>
                <li>
                  <Link>修复 字段级血缘的无法解析语句集的 bug</Link>
                </li>
                <li>
                  <Link>修复 字段级血缘的无法解析语句集的 bug</Link>
                </li>
                <li>
                  <Link>修复 钉钉报警表单无法正确展示的 bug</Link>
                </li>
                <li>
                  <Link>修复 FlinkSQL 执行或提交由于空配置导致的 bug</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.2</Text> <Text type="secondary">2022-04-17</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增 飞书报警</Link>
                </li>
                <li>
                  <Link>新增 邮件报警</Link>
                </li>
                <li>
                  <Link>新增 报警实例测试功能</Link>
                </li>
                <li>
                  <Link>新增 docker 镜像文件</Link>
                </li>
                <li>
                  <Link>新增 Remote mode（Standalone、Yarn Session、Kubernetes Session）从 SavePoint 恢复作业</Link>
                </li>
                <li>
                  <Link>新增 Oracle CDC 多源合并</Link>
                </li>
                <li>
                  <Link>新增 版本为 0.6.2</Link>
                </li>
                <li>
                  <Link>优化 检查 FlinkSQL 不通过时返回全部的异常信息</Link>
                </li>
                <li>
                  <Link>优化 Hive 的 pom</Link>
                </li>
                <li>
                  <Link>优化 httpclient 的依赖</Link>
                </li>
                <li>
                  <Link>优化 报警、数据源、线程任务的 SPI 机制</Link>
                </li>
                <li>
                  <Link>优化 CDC 多源合并</Link>
                </li>
                <li>
                  <Link>优化 运维中心始终显示 FlinkWebUI 按钮</Link>
                </li>
                <li>
                  <Link>优化 集群实例显示 FlinkWebUI 按钮</Link>
                </li>
                <li>
                  <Link>修复 Integer 类型判断方式从 “==” 改为 “equals”</Link>
                </li>
                <li>
                  <Link>修复 Flink 的 hadoop_conf 配置未生效</Link>
                </li>
                <li>
                  <Link>修复 报警测试的最新配置未生效</Link>
                </li>
                <li>
                  <Link>修复 飞书报警使用 “@all” 时会触发异常</Link>
                </li>
                <li>
                  <Link>修复 install 项目失败</Link>
                </li>
                <li>
                  <Link>修复 Hive 构建 sql 的异常</Link>
                </li>
                <li>
                  <Link>修复 特殊字符导致全局变量替换失败</Link>
                </li>
                <li>
                  <Link>修复 不支持查看执行图 sql 的展示异常</Link>
                </li>
                <li>
                  <Link>修复 CDC 多源合并 Remote 提交的任务同步问题</Link>
                </li>
                <li>
                  <Link>修复 修改集群配置后显示 “新增成功”</Link>
                </li>
                <li>
                  <Link>修复 Flink Oracle Connector 读取 Date 和 Timestamp 类型的异常</Link>
                </li>
                <li>
                  <Link>修复 MybatsPlus 实体类 boolean 默认为 false 导致业务错误的异常</Link>
                </li>
                <li>
                  <Link>修复 修复系统配置的 sql 分隔符默认为 “;\r\n”</Link>
                </li>
                <li>
                  <Link>修复 任务失败导致的循环问题</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.3</Text> <Text type="secondary">2022-05-09</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增 CDC 整库实时同步至 kafka 的一个 topic</Link>
                </li>
                <li>
                  <Link>新增 CDC 整库实时同步至 kafka 对应 topic</Link>
                </li>
                <li>
                  <Link>新增 CDC 整库实时入仓 doris</Link>
                </li>
                <li>
                  <Link>新增 CDC 整库实时入湖 hudi</Link>
                </li>
                <li>
                  <Link>新增 CDC 整库同步表名规则</Link>
                </li>
                <li>
                  <Link>新增 CDC 整库实时 sql sink</Link>
                </li>
                <li>
                  <Link>新增 MysqlCDC 整库同步配置扩展 </Link>
                </li>
                <li>
                  <Link>新增 CDC 整库同步主键注入</Link>
                </li>
                <li>
                  <Link>新增 Flink 1.15.0 的支持</Link>
                </li>
                <li>
                  <Link>修复当作业停止时作业状态错误的 bug</Link>
                </li>
                <li>
                  <Link>修复 Oracle 不支持的编码</Link>
                </li>
                <li>
                  <Link>修复 Clickhouse 元数据无法展示的 bug</Link>
                </li>
                <li>
                  <Link>修复元数据查询切换 bug</Link>
                </li>
                <li>
                  <Link>修复整库同步 Hudi 无法构建 Schema 的 bug</Link>
                </li>
                <li>
                  <Link>修复数据源单元测试的 name 缺失 bug</Link>
                </li>
                <li>
                  <Link>修复默认分隔符为 ';\r\n|;\n' 来解决 windows 和 mac 同时兼容</Link>
                </li>
                <li>
                  <Link>修复批任务无法正确提交 yarn application 的 bug</Link>
                </li>
                <li>
                  <Link>修复修改作业名失败的 bug</Link>
                </li>
                <li>
                  <Link>修复一些错误文档连接</Link>
                </li>
                <li>
                  <Link>修复获取作业执行计划被执行两次的 bug</Link>
                </li>
                <li>
                  <Link>优化 Java 流的释放</Link>
                </li>
                <li>
                  <Link>优化 CDC 整库实时入库 doris</Link>
                </li>
                <li>
                  <Link>优化同一节点下无法启动多个进程实例的问题</Link>
                </li>
                <li>
                  <Link>优化微信告警的发送标题</Link>
                </li>
                <li>
                  <Link>优化启动文件编码及禁用环境变量</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.4</Text> <Text type="secondary">2022-06-05</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增整库同步表名参数支持换行和列支持按主键优先排序</Link>
                </li>
                <li>
                  <Link>新增整库同步日志输出</Link>
                </li>
                <li>
                  <Link>新增钉钉报警的 @mobile 配置</Link>
                </li>
                <li>
                  <Link>新增任务的 StreamGraph 导出为 JSON</Link>
                </li>
                <li>
                  <Link>新增任务的 API 接口示例页面</Link>
                </li>
                <li>
                  <Link>新增数据开发帮助页面</Link>
                </li>
                <li>
                  <Link>新增普通 SQL 的字段血缘</Link>
                </li>
                <li>
                  <Link>新增作业监听池来解决频繁写库的问题</Link>
                </li>
                <li>
                  <Link>新增非 FlinkSQL 作业的导出 StreamGraphPlan 的按钮隐藏</Link>
                </li>
                <li>
                  <Link>新增数据源的删除按钮</Link>
                </li>
                <li>
                  <Link>新增整库同步的 jdbc 配置和升级 flinkcdc 版本</Link>
                </li>
                <li>
                  <Link>修复刷新作业监控页面时的抖动问题</Link>
                </li>
                <li>
                  <Link>修复 Flink Oracle Connector 不能转换 CLOB 到 String 的问题</Link>
                </li>
                <li>
                  <Link>修复切换任务时保存点未同步刷新的问题</Link>
                </li>
                <li>
                  <Link>修复 ClusterClient 接口不通版本的兼容性问题</Link>
                </li>
                <li>
                  <Link>修复 MySQL 类型转换精度信息是空的问题</Link>
                </li>
                <li>
                  <Link>修复初始化函数的冗余操作</Link>
                </li>
                <li>
                  <Link>修复整库同步的 decimal 问题</Link>
                </li>
                <li>
                  <Link>修复获取作业计划失败的问题</Link>
                </li>
                <li>
                  <Link>修复整库同步 OracleCDC number 不能被转换为 Long 的问题</Link>
                </li>
                <li>
                  <Link>修复微信企业号报警测试按钮后台错误的问题</Link>
                </li>
                <li>
                  <Link>修复当切换作业 tab 时无法正确保存修改的作业配置的问题</Link>
                </li>
                <li>
                  <Link>修复数据源和元数据不能展示别名的问题</Link>
                </li>
                <li>
                  <Link>修复作业重命名后 tab 未更新的问题</Link>
                </li>
                <li>
                  <Link>修复 K8S 集群配置的 FlinkLibPath 是空的问题</Link>
                </li>
                <li>
                  <Link>优化初始化 sql</Link>
                </li>
                <li>
                  <Link>优化打包</Link>
                </li>
                <li>
                  <Link>优化移除 preset-ui</Link>
                </li>
                <li>
                  <Link>优化 MySQL 字段类型转换</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.5</Text> <Text type="secondary">2022-07-03</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增 phoenix 的 1.14 版本的 Flink 连接器</Link>
                </li>
                <li>
                  <Link>新增生成 FlinkSQL 时从元数据中获取空和非空</Link>
                </li>
                <li>
                  <Link>新增作业实例信息接口</Link>
                </li>
                <li>
                  <Link>新增运维中心 Flink 快照信息、JobManager 配置信息</Link>
                </li>
                <li>
                  <Link>新增运维中心 TaskManager 信息</Link>
                </li>
                <li>
                  <Link>新增作业信息选项卡</Link>
                </li>
                <li>
                  <Link>新增运维中心 TaskManager 的表和表单详情</Link>
                </li>
                <li>
                  <Link>新增作业版本管理</Link>
                </li>
                <li>
                  <Link>新增作业检查点历史信息</Link>
                </li>
                <li>
                  <Link>新增捕获 CDCSOURCE 中的列值转换异常</Link>
                </li>
                <li>
                  <Link>新增 TaskManager 信息</Link>
                </li>
                <li>
                  <Link>新增作业复制</Link>
                </li>
                <li>
                  <Link>修复数据开发数据源别名是""</Link>
                </li>
                <li>
                  <Link>修复数据开发关闭其他页面无法关闭第一个页面</Link>
                </li>
                <li>
                  <Link>修复切换元数据时发生异常</Link>
                </li>
                <li>
                  <Link>修复 flinkLibPath 和 cluster-id 在 K8S 配置中是空</Link>
                </li>
                <li>
                  <Link>修复 FlinkSql 语句末尾的分号异常</Link>
                </li>
                <li>
                  <Link>修复 K8S 集群配置不能获取自定义配置</Link>
                </li>
                <li>
                  <Link>修复 yarn per-job 运行时的空指针异常</Link>
                </li>
                <li>
                  <Link>修复任务实例信息在强制刷新且失去连接时会清空记录的问题</Link>
                </li>
                <li>
                  <Link>修复 'table.local-time-zone' 参数不生效的问题</Link>
                </li>
                <li>
                  <Link>修复邮箱报警不能找到类 javax.mail.Address</Link>
                </li>
                <li>
                  <Link>修复不能校验 'show datatbases' 的问题</Link>
                </li>
                <li>
                  <Link>修复 setParentId 方法判空错误</Link>
                </li>
                <li>
                  <Link>修复 CDCSOURCE 里当 FlinkSql 字段名为关键字时不能创建 Flink 表</Link>
                </li>
                <li>
                  <Link>修复字段血缘分析不能处理相同字段名</Link>
                </li>
                <li>
                  <Link>修复集群配置页面的问题</Link>
                </li>
                <li>
                  <Link>修复邮件报警消息不能自定义昵称</Link>
                </li>
                <li>
                  <Link>修复 dlink-connector-phoenix-1.14 编译异常</Link>
                </li>
                <li>
                  <Link>修复 Oracle 字段可为空的识别错误</Link>
                </li>
                <li>
                  <Link>修复 CDCSOURCE 中 MySQL varbinary 和 binary 类型的支持</Link>
                </li>
                <li>
                  <Link>优化作业树搜索结果高亮与选中背景色</Link>
                </li>
                <li>
                  <Link>优化多处别名是空的显示</Link>
                </li>
                <li>
                  <Link>优化 explainSqlRecord 代码逻辑</Link>
                </li>
                <li>
                  <Link>优化集群实例页面</Link>
                </li>
                <li>
                  <Link>优化血缘分析的刷新</Link>
                </li>
                <li>
                  <Link>优化作业实例信息接口</Link>
                </li>
                <li>
                  <Link>优化所有报警的发送信息</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.6</Text> <Text type="secondary">2022-07-23</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>新增运维中心的作业历史版本列表</Link>
                </li>
                <li>
                  <Link>新增数据开发的历史版本对比功能</Link>
                </li>
                <li>
                  <Link>新增 Flink MySql Catalog</Link>
                </li>
                <li>
                  <Link>新增 FlinkSQLEnv 默认的 Flink Mysql Catalog</Link>
                </li>
                <li>
                  <Link>新增 1.13 版本 Doris 连接默认隐藏 __DORIS_DELETE_</Link>
                </li>
                <li>
                  <Link>新增 dlink-connector-pulsar</Link>
                </li>
                <li>
                  <Link>新增选择 Checkpoint 重启任务</Link>
                </li>
                <li>
                  <Link>升级 Flink 1.15.0 到 1.15.1</Link>
                </li>
                <li>
                  <Link>新增数据开发的元存储查看</Link>
                </li>
                <li>
                  <Link>新增数据开发的 Flink 元存储信息和列详情</Link>
                </li>
                <li>
                  <Link>添加和更新开源协议头</Link>
                </li>
                <li>
                  <Link>新增作业批量导出导入 Json 文件</Link>
                </li>
                <li>
                  <Link>修复 Flink-connector-phoenix 问题</Link>
                </li>
                <li>
                  <Link>修复作业实例导致的内存溢出</Link>
                </li>
                <li>
                  <Link>升级 Flink 版本和修复 CDC 的问题</Link>
                </li>
                <li>
                  <Link>修复任务实例耗时解析错误的问题</Link>
                </li>
                <li>
                  <Link>修复 Catalog SPI 和 sql 的问题</Link>
                </li>
                <li>
                  <Link>修复运维中心的 Checkpoint 错误以及添加 Savepoint 信息</Link>
                </li>
                <li>
                  <Link>捕获 SQLSinkBuilder 中 translateToPlan 的异常</Link>
                </li>
                <li>
                  <Link>修复打包时发生的异常</Link>
                </li>
                <li>
                  <Link>修复 CopyrightUtil 问题</Link>
                </li>
                <li>
                  <Link>修复 Flink Yarn Application 提交失败的问题</Link>
                </li>
                <li>
                  <Link>修复报警实例删除的问题</Link>
                </li>
                <li>
                  <Link>允许依赖环路</Link>
                </li>
                <li>
                  <Link>删除未被引用的类</Link>
                </li>
                <li>
                  <Link>修复作业历史字段 null 的问题</Link>
                </li>
                <li>
                  <Link>优化 Flinksql 执行图获取失败时的返回提示</Link>
                </li>
                <li>
                  <Link>从 API 中移除敏感信息如密码</Link>
                </li>
                <li>
                  <Link>修复已被删除的用户登录，显示信息不正确问题</Link>
                </li>
                <li>
                  <Link>优化运维中心的 Checkpoint 页面</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.6.7</Text> <Text type="secondary">2022-09-06</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>添加多租户的实现</Link>
                </li>
                <li>
                  <Link>一键上线和下线作业</Link>
                </li>
                <li>
                  <Link>添加全局变量管理</Link>
                </li>
                <li>
                  <Link>添加命名空间与密码的表单</Link>
                </li>
                <li>
                  <Link>登录时选择多租户</Link>
                </li>
                <li>
                  <Link>多租户前端业务管理实现</Link>
                </li>
                <li>
                  <Link>添加 github 工作流来检查代码风格、测试类和打包</Link>
                </li>
                <li>
                  <Link>添加 druid 连接池来解决 jdbc 多连接问题</Link>
                </li>
                <li>
                  <Link>修改 Flink 默认版本为 1.14</Link>
                </li>
                <li>
                  <Link>新增全局变量管理实现</Link>
                </li>
                <li>
                  <Link>新增 SqlServer 整库同步</Link>
                </li>
                <li>
                  <Link>新增全局变量在 Flinksql 中生效</Link>
                </li>
                <li>
                  <Link>新增字段血缘分析从 Flink 逻辑计划获取</Link>
                </li>
                <li>
                  <Link>新增 postgresql 整库同步</Link>
                </li>
                <li>
                  <Link>修改 checkstyle 为必须的工作</Link>
                </li>
                <li>
                  <Link>新增 swagger api 文档</Link>
                </li>
                <li>
                  <Link>cdcsource 增加多目标库同步功能</Link>
                </li>
                <li>
                  <Link>新增文件上传</Link>
                </li>
                <li>
                  <Link>Jar 和集群配置管理新增文件上传</Link>
                </li>
                <li>
                  <Link>新增 StarRocks 数据源</Link>
                </li>
                <li>
                  <Link>新增任务监控失败重复的容错时间</Link>
                </li>
                <li>
                  <Link>修改任务监控代码重复判断的问题</Link>
                </li>
                <li>
                  <Link>修复邮件报警参数问题</Link>
                </li>
                <li>
                  <Link>修复获取作业实例信息可能获取到错误的结果问题</Link>
                </li>
                <li>
                  <Link>修复 doris 连接器批量写入时发生异常导致写入失败</Link>
                </li>
                <li>
                  <Link>修复 SQLSinkBuilder.buildRow 的错误</Link>
                </li>
                <li>
                  <Link>修复 Flink1.14 执行缺失依赖的问题</Link>
                </li>
                <li>
                  <Link>修复 savepoint 接口获取前端集群表单的 taskId 为空的问题</Link>
                </li>
                <li>
                  <Link>修复 yarn per-job 无法自动释放资源的问题</Link>
                </li>
                <li>
                  <Link>修复多租户新增角色和删除角色的问题</Link>
                </li>
                <li>
                  <Link>修复 dlink-conector-pulsar-1.14 找不到 SubscriptionType 的报错</Link>
                </li>
                <li>
                  <Link>修复 flink1.14 savepoint 时的 jackjson 问题</Link>
                </li>
                <li>
                  <Link>修复元数据字段类型转换的问题</Link>
                </li>
                <li>
                  <Link>修复整库同步 KafkaSinkBuilder 未序列化导致报错</Link>
                </li>
                <li>
                  <Link>修复注册中心文档管理的查询条件错误</Link>
                </li>
                <li>
                  <Link>修复 yarn perjob/application 和 k8s application 集群配置未生效</Link>
                </li>
                <li>
                  <Link>修复 k8s application 模式提交失败，优化增加获取 JobId 等待时间</Link>
                </li>
                <li>
                  <Link>修复日志 banner 的错误</Link>
                </li>
                <li>
                  <Link>修复 UDF 和 UDTAF 在 Flink 1.14 的错误</Link>
                </li>
                <li>
                  <Link>优化前端和文档</Link>
                </li>
                <li>
                  <Link>优化作业被删除后作业版本未被删除</Link>
                </li>
                <li>
                  <Link>优化作业树在导入作业后溢出的问题</Link>
                </li>
                <li>
                  <Link>优化数据开发的进程列表</Link>
                </li>
                <li>
                  <Link>优化整库同步分流逻辑</Link>
                </li>
                <li>
                  <Link>优化git提交忽略的文件类型</Link>
                </li>
                <li>
                  <Link>优化中文和英文 Readme</Link>
                </li>
                <li>
                  <Link>移除一些接口的敏感信息</Link>
                </li>
                <li>
                  <Link>优化多租户</Link>
                </li>
                <li>
                  <Link>添加 Maven Wrapper</Link>
                </li>
                <li>
                  <Link>优化整库同步的时区问题</Link>
                </li>
                <li>
                  <Link>优化 sql 默认分隔符统一为 ;\n</Link>
                </li>
                <li>
                  <Link>优化代码风格的错误</Link>
                </li>
                <li>
                  <Link>添加.DS_Store到git的忽略文件类型</Link>
                </li>
                <li>
                  <Link>优化多租户角色穿梭框和前端回显</Link>
                </li>
                <li>
                  <Link>优化用户关联角色渲染</Link>
                </li>
                <li>
                  <Link>优化 dlink-admin 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-alert 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-common 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-catalog 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-client 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-app 的代码风格</Link>
                </li>
                <li>
                  <Link>优化数据源连接池和链接创建</Link>
                </li>
                <li>
                  <Link>优化 dlink-connectors 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-core 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-daemon 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-executor 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-function 和 dlink-gateway 的代码风格</Link>
                </li>
                <li>
                  <Link>优化 dlink-metadata 的代码风格</Link>
                </li>
                <li>
                  <Link>添加协议头到pom文件</Link>
                </li>
                <li>
                  <Link>优化项目打包和启动文件</Link>
                </li>
                <li>
                  <Link>dlink-client-hadoop 打包增加 ServicesResourceTransformer</Link>
                </li>
                <li>
                  <Link>优化配置文件和静态资源目录打包</Link>
                </li>
                <li>
                  <Link>配置全局 checkstyle 验证</Link>
                </li>
                <li>
                  <Link>添加 sqlserver 的 date 类型转换</Link>
                </li>
                <li>
                  <Link>优化 PG 数据库 schema_name 查询 sql </Link>
                </li>
                <li>
                  <Link>Doris 支持更多语法</Link>
                </li>
                <li>
                  <Link>优化整库同步 DorisSink</Link>
                </li>
                <li>
                  <Link>优化前端的展示与提示</Link>
                </li>
                <li>
                  <Link>优化数据开发作业目录默认折叠</Link>
                </li>
                <li>
                  <Link>合并官网文档仓库源码至主仓库的 docs 目录下</Link>
                </li>
                <li>
                  <Link>添加 Flink 1.15 文档</Link>
                </li>
                <li>
                  <Link>整库同步文档修复</Link>
                </li>
                <li>
                  <Link>添加导入导出作业的文档</Link>
                </li>
                <li>
                  <Link>优化多个文档</Link>
                </li>
                <li>
                  <Link>更新主页和基础信息的文档</Link>
                </li>
                <li>
                  <Link>新增flink扩展redis的实践分享</Link>
                </li>
                <li>
                  <Link>优化部署文档</Link>
                </li>
                <li>
                  <Link>修复 yarn-application 任务分隔符错误</Link>
                </li>
                <li>
                  <Link>升级 Flink 1.15 版本为 1.15.2</Link>
                </li>
                <li>
                  <Link>优化 SqlServer 字段类型查询</Link>
                </li>
                <li>
                  <Link>修复重命名作业后保存作业失败</Link>
                </li>
                <li>
                  <Link>修复提交历史的第二次弹框时无内容</Link>
                </li>
                <li>
                  <Link>新增数据开发任务信息日志详情按钮</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.7.0</Text> <Text type="secondary">2022-11-24</Text>
            <p></p>
            <Paragraph>
              <ul>
                <li>
                  <Link>支持 Apache Flink 1.16.0</Link>
                </li>
                <li>
                  <Link>新增 Java udf 打包</Link>
                </li>
                <li>
                  <Link>支持 Flink Session 模式自动加载 udf</Link>
                </li>
                <li>
                  <Link>支持 Flink Per-Job 模式自动加载 udf</Link>
                </li>
                <li>
                  <Link>支持 Flink Application 模式自动加载 udf</Link>
                </li>
                <li>
                  <Link>支持 Python udf 在线开发</Link>
                </li>
                <li>
                  <Link>支持 Scala udf 在线开发</Link>
                </li>
                <li>
                  <Link>支持自定义的 K8S Application 提交</Link>
                </li>
                <li>
                  <Link>新增 FlinkJar 文件上传</Link>
                </li>
                <li>
                  <Link>从逻辑计划分析字段血缘支持 Flink 所有版本</Link>
                </li>
                <li>
                  <Link>Flink JDBC 支持数据过滤</Link>
                </li>
                <li>
                  <Link>Flink JDBC 分区查询支持 datetime</Link>
                </li>
                <li>
                  <Link>新增多租户管理</Link>
                </li>
                <li>
                  <Link>新增在登录时选择租户</Link>
                </li>
                <li>
                  <Link>新增海豚调度自动创建任务</Link>
                </li>
                <li>
                  <Link>新增系统日志控制台</Link>
                </li>
                <li>
                  <Link>新增执行进度控制台</Link>
                </li>
                <li>
                  <Link>新增 Flink udf 模板</Link>
                </li>
                <li>
                  <Link>新增 Presto 数据源</Link>
                </li>
                <li>
                  <Link>新增作业树目录级删除功能</Link>
                </li>
                <li>
                  <Link>新增 CDCSOURCE 的 datastream-doris-ext 支持元数据写入</Link>
                </li>
                <li>
                  <Link>数据开发元数据添加刷新按钮</Link>
                </li>
                <li>
                  <Link>新增数据源复制</Link>
                </li>
                <li>
                  <Link>新增前端国际化</Link>
                </li>
                <li>
                  <Link>新增后端国际化</Link>
                </li>
                <li>
                  <Link>新增 kerberos 验证</Link>
                </li>
                <li>
                  <Link>新增 K8S 自动部署应用</Link>
                </li>
                <li>
                  <Link>新增 Local 模式的 FlinkWebUI</Link>
                </li>
                <li>
                  <Link>修复从指定的 savepoint 恢复任务时未设置 savepint 文件路径导致的问题</Link>
                </li>
                <li>
                  <Link>修复 StarRocks 数据源不可见</Link>
                </li>
                <li>
                  <Link>修复数据源查询数据后切换数据源导致报错</Link>
                </li>
                <li>
                  <Link>修复数据源查询视图元数据报错</Link>
                </li>
                <li>
                  <Link>修复 Oracle 验证查询的错误</Link>
                </li>
                <li>
                  <Link>修复 PG 数据库元数据获取 schema 失败 </Link>
                </li>
                <li>
                  <Link>修复 kafka properties 没有启用</Link>
                </li>
                <li>
                  <Link>修复 jobConfig useAutoCancel 参数传递错误</Link>
                </li>
                <li>
                  <Link>修复由于多租户导致的作业监控报错</Link>
                </li>
                <li>
                  <Link>修复集群实例删除导致已存在的任务无法停止</Link>
                </li>
                <li>
                  <Link>修复 Application 模式缺失数据源变量</Link>
                </li>
                <li>
                  <Link>修复连续单击任务项将打开多个选项卡问题</Link>
                </li>
                <li>
                  <Link>修复 cdcsource KafkaSink 不支持添加 transactionalIdPrefix 导致 kafka product 发送消息失败</Link>
                </li>
                <li>
                  <Link>修复当集群别名为空时集群无法展示</Link>
                </li>
                <li>
                  <Link>修复作用版本查询错误</Link>
                </li>
                <li>
                  <Link>修复作业完成时 Per-Job 和 Application 的状态始终未知</Link>
                </li>
                <li>
                  <Link>修复引导页的连接错误</Link>
                </li>
                <li>
                  <Link>修复 Open API 没有租户</Link>
                </li>
                <li>
                  <Link>增加 MysqlCDC 的参数配置</Link>
                </li>
                <li>
                  <Link>优化 datastream kafka-json 和 datastream starrocks</Link>
                </li>
                <li>
                  <Link>支持元数据缓存到 Redis</Link>
                </li>
                <li>
                  <Link>自动在 doris label prefix 后追加 uuid</Link>
                </li>
                <li>
                  <Link>优化租户切换</Link>
                </li>
                <li>
                  <Link>修改资源中心为认证中心</Link>
                </li>
                <li>
                  <Link>添加 spotless 插件</Link>
                </li>
                <li>
                  <Link>优化不同版本的 SQL 文件</Link>
                </li>
                <li>
                  <Link>改进 MySQL 表的自动创建</Link>
                </li>
                <li>
                  <Link>优化 postgres 元数据信息</Link>
                </li>
                <li>
                  <Link>优化 postgre 建表语句</Link>
                </li>
                <li>
                  <Link>优化 Flink Oracle Connector </Link>
                </li>
                <li>
                  <Link>优化 maven assembly 和 profile</Link>
                </li>
                <li>
                  <Link>兼容 Java 11</Link>
                </li>
                <li>
                  <Link>删除数据源的重复初始化</Link>
                </li>
                <li>
                  <Link>升级 mysql 驱动版本至 8.0.28</Link>
                </li>
                <li>
                  <Link>升级 Flink 1.14.5 到 1.14.6</Link>
                </li>
                <li>
                  <Link>升级 Guava 和 Lombok 版本</Link>
                </li>
                <li>
                  <Link>升级jackson 和 sa-token 版本</Link>
                </li>
                <li>
                  <Link>优化 ReadMe</Link>
                </li>
                <li>
                  <Link>更新官网</Link>
                </li>
                <li>
                  <Link>优化部署文档</Link>
                </li>
                <li>
                  <Link>添加 Flink Metrics 监控和优化的文档</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
        </Timeline>
      </Card>
    </>
  );
};
