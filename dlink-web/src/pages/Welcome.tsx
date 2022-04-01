import React from 'react';
import { Card, Alert, Typography,Timeline } from 'antd';
import { useIntl, FormattedMessage } from 'umi';
import styles from './Welcome.less';
const { Text, Link,Paragraph } = Typography;
const CodePreview: React.FC = ({ children }) => (
  <pre className={styles.pre}>
    <code>
      <Typography.Text copyable>{children}</Typography.Text>
    </code>
  </pre>
);

export default (): React.ReactNode => {
  const intl = useIntl();
  return (
    <>
      <Card>
        <Alert
          message={intl.formatMessage({
            id: 'pages.welcome.alertMessage',
            defaultMessage: '实时计算平台 Dinky 即将发布，目前为体验版，版本号为 0.6.1。',
          })}
          type="success"
          showIcon
          banner
          style={{
            margin: -12,
            marginBottom: 24,
          }}
        />
        <Typography.Text strong>
          <FormattedMessage id="pages.welcome.Community" defaultMessage="官方社区" />{' '}
          <FormattedMessage id="pages.welcome.link" defaultMessage="欢迎加入" />
        </Typography.Text>
        <Paragraph>
        <Typography.Text strong>
          <FormattedMessage id="pages.welcome.QQ" defaultMessage="QQ官方社区群" />{' '}
          <FormattedMessage id="pages.welcome.QQcode" defaultMessage="543709668" />
        </Typography.Text>
        </Paragraph>
        <CodePreview>微信公众号：Datalink数据中台</CodePreview>
        <Typography.Text
          strong
          style={{
            marginBottom: 12,
          }}
        >
          <FormattedMessage id="pages.welcome.advancedLayout" defaultMessage="Github" />{' '}
          <a
            href="https://github.com/DataLinkDC/dlink"
            rel="noopener noreferrer"
            target="__blank"
          >
            <FormattedMessage id="pages.welcome.star" defaultMessage="欢迎 Star " />
          </a>
        </Typography.Text>
        <Paragraph>
        <Typography.Text strong>
          <FormattedMessage id="pages.welcome.upgrade" defaultMessage="更新日志" />
        </Typography.Text>
        </Paragraph>
        <p> </p>
        <Timeline pending={<><Text code>0.7.0</Text>
          <Text type="secondary">敬请期待</Text>
          <p> </p>
          <Paragraph>
            <ul>
              <li>
                <Link>多租户及命名空间</Link>
              </li>
              <li>
                <Link>全局血缘与影响分析</Link>
              </li>
              <li>
                <Link>统一元数据管理</Link>
              </li>
              <li>
                <Link>Flink 元数据持久化</Link>
              </li>
              <li>
                <Link>多版本 Flink-Client Server</Link>
              </li>
              <li>
                <Link>整库千表同步</Link>
              </li>
            </ul>
          </Paragraph></>} reverse={true}>
          <Timeline.Item><Text code>0.1.0</Text> <Text type="secondary">2021-06-06</Text>
            <p> </p>
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
            <p> </p>
            <Paragraph>
              <ul>
                <li>
                  <Link>FlinkSql Studio 代码底层架构进行大优化</Link>
                </li>
                <li>
                  <Link>支持以 SPI 的方式扩展任意 Connector，同 Flink 官网</Link>
                </li>
                <li>
                  <Link>提供了 dlink-connector-jdbc，额外支持 Oracle 和 ClickHouse 读写，该扩展包可直接上传 Flink 集群的 lib 进行远程使用，无需重新编译</Link>
                </li>
                <li>
                  <Link>提供了 dlink-client-1.12，支持 Flink 1.12.0+ 多集群的远程使用与本地隔离使用，1.10、1.11 和 1.13 集群可能存在问题</Link>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
            <p> </p>
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
        </Timeline>
      </Card>
    </>
  );
};
