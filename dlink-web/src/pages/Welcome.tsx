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
            defaultMessage: '更快更强的重型组件，已经发布。',
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
        <Timeline pending={<><Text code>0.2.0</Text>
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
        </Timeline>
      </Card>
    </PageContainer>
  );
};
