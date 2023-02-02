import {VERSION} from '@/components/Version/Version';
import {l} from '@/utils/intl';
import {PageContainer} from '@ant-design/pro-components';
import {Alert, Card, Image, Typography} from 'antd';
import React from 'react';

const { Paragraph} = Typography;

const Welcome: React.FC = () => {
  return (
    <PageContainer title={false} >
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
          {l('pages.welcome.Community')}
        </Typography.Text>
        <br/><br/>
        <Paragraph style={{marginRight: 30}}>
          <Typography.Text title={l('pages.welcome.QQcode')} style={{marginRight: 80}} strong ellipsis={true}>
            <Image title={l('pages.welcome.QQcode')} height={300} width={250} src="community/qq.png"/>
          </Typography.Text>

          <Typography.Text style={{marginRight: 80}} strong ellipsis={true}>
            <Image title={l('pages.welcome.wechatCode')} height={300} width={250} src="community/wechat.jpg"/>
          </Typography.Text>

          <Typography.Text strong ellipsis={true}>
            <Image title={l('pages.welcome.dingTalkCode')} height={300} width={250} src="community/dingtalk.jpg"/>
          </Typography.Text>

        </Paragraph>

      </Card>
    </PageContainer>
  );
};

export default Welcome;



