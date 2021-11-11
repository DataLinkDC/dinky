import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
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
    <PageContainer>
      <Card>
        <Alert
          message={intl.formatMessage({
            id: 'pages.welcome.alertMessage',
            defaultMessage: '实时计算平台 Dlink & Apache Flink 即将发布，目前为体验版，版本号为 0.3.2。',
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
        <Timeline pending={<><Text code>0.4.0</Text>
          <Text type="secondary">敬请期待</Text>
          <p> </p>
          <Paragraph>
            <ul>
              <li>
                <Link>FlinkSql Studio 时间调度</Link>
              </li>
              <li>
                <Link>FlinkSql Studio 依赖调度</Link>
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
          <Timeline.Item><Text code>0.4.0</Text> <Text type="secondary">2021-11-?</Text>
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
                  <Link>新增 yarn-application 的作业提交方式</Link>
                </li>
                <li>
                  <Link>新增 yarn-perjob 的作业提交方式</Link>
                </li>
                <li>
                  <Link>新增集群配置管理及维护页面</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
        </Timeline>
      </Card>
    </PageContainer>
  );
};
