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
            defaultMessage: '实时计算平台 Dlink & Apache Flink 即将发布，目前为体验版，版本号为 0.2.1。',
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
            href="https://github.com/aiwenmo/dlink"
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
        <Timeline pending={<><Text code>0.3.0</Text>
          <Text type="secondary">敬请期待</Text>
          <p> </p>
          <Paragraph>
            <ul>
              <li>
                <Link href="">FlinkSql Studio 进程监控</Link>
              </li>
              <li>
                <Link href="">FlinkSql Studio 集群总览</Link>
              </li>
              <li>
                <Link href="">FlinkSql Studio 集群任务</Link>
              </li>
              <li>
                <Link href="">FlinkSql Studio 任务详情</Link>
              </li>
              <li>
                <Link href="">FlinkSql Studio 连接器详情</Link>
              </li>
              <li>
                <Link href="">FlinkSql Studio 指标展示</Link>
              </li>
              <li>
                <Link href="">FlinkSql Studio 自动完成函数</Link>
              </li>
              <li>
                <Link href="">用户与授权</Link>
              </li>
            </ul>
          </Paragraph></>} reverse={true}>
          <Timeline.Item><Text code>0.1.0</Text> <Text type="secondary">2021-06-06</Text>
            <p> </p>
            <Paragraph>
              <ul>
                <li>
                  <Link href="">FlinkSql Studio 基本功能</Link>
                </li>
                <li>
                  <Link href="">Flink 集群管理</Link>
                </li>
                <li>
                  <Link href="">FlinkSql 任务管理</Link>
                </li>
                <li>
                  <Link href="">FlinkSql 文档管理</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.2.0</Text> <Text type="secondary">2021-06-08</Text>
            <p> </p>
            <Paragraph>
              <ul>
                <li>
                  <Link href="">FlinkSql Studio 代码底层架构进行大优化</Link>
                </li>
                <li>
                  <Link href="">支持以 SPI 的方式扩展任意 Connector，同 Flink 官网</Link>
                </li>
                <li>
                  <Link href="">提供了 dlink-connector-jdbc，额外支持 Oracle 和 ClickHouse 读写，该扩展包可直接上传 Flink 集群的 lib 进行远程使用，无需重新编译</Link>
                </li>
                <li>
                  <Link href="">提供了 dlink-client-1.12，支持 Flink 1.12.0+ 多集群的远程使用与本地隔离使用，1.10、1.11 和 1.13 集群可能存在问题</Link>
                </li>
                <li>
                  <Link href="">优化了 FlinkSQL 执行与提交到远程集群的任务名，默认为作业的中文别名</Link>
                </li>
                <li>
                  <Link href="">优化了目录的操作，点击节点即可打开作业，无须右键打开</Link>
                </li>
                <li>
                  <Link href="">优化了执行结果信息，添加了任务名的展示</Link>
                </li>
                <li>
                  <Link href="">对 Studio 界面进行了一定的提示优化</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
          <Timeline.Item><Text code>0.2.1</Text> <Text type="secondary">2021-06-11</Text>
            <p> </p>
            <Paragraph>
              <ul>
                <li>
                  <Link href="">FlinkSql Studio 页面仿IDE紧凑型设计改进</Link>
                </li>
                <li>
                  <Link href="">解决了目录树右键菜单的不能任意点关闭问题</Link>
                </li>
                <li>
                  <Link href="">解决了选项卡关闭不能正确刷新编辑器的问题</Link>
                </li>
                <li>
                  <Link href="">解决了当前位置不根据选项卡刷新的问题</Link>
                </li>
                <li>
                  <Link href="">增加了目录树非空文件夹的灰色删除按钮</Link>
                </li>
                <li>
                  <Link href="">增加了目录树创建根目录按钮以及折叠按钮</Link>
                </li>
                <li>
                  <Link href="">优化了连接器刷新与清空按钮</Link>
                </li>
                <li>
                  <Link href="">优化了作业异步提交的提示</Link>
                </li>
                <li>
                  <Link href="">增加了简易的使用帮助</Link>
                </li>
              </ul>
            </Paragraph>
          </Timeline.Item>
        </Timeline>
      </Card>
    </PageContainer>
  );
};
