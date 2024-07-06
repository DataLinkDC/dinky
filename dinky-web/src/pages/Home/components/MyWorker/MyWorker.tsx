import { Card, Space, Typography } from 'antd';
import useHookRequest from '@/hooks/useHookRequest';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskInfo } from '@/types/Studio/data';
import { getTabIcon } from '@/pages/DataStudio/MiddleContainer/function';
import { ProCard } from '@ant-design/pro-components';
import JobLifeCycleTag from '@/components/JobTags/JobLifeCycleTag';
import StatusTag from '@/components/JobTags/StatusTag';
import EllipsisMiddle from '@/components/Typography/EllipsisMiddle';
import { l } from '@/utils/intl';

const MyWorker = () => {
  const { loading, data } = useHookRequest<any, any>(getData, {
    defaultParams: [API_CONSTANTS.MY_TASK]
  });

  const renderTitle = (item: TaskInfo) => {
    return (
      <Space>
        {getTabIcon(item.dialect, 20)}
        <EllipsisMiddle copyable={false} maxCount={15}>
          {item.name}
        </EllipsisMiddle>
      </Space>
    );
  };

  return (
    <Card
      style={{
        marginBottom: 24
      }}
      title={l('home.mywork')}
      bordered={false}
      extra={<a href='/'>{l('home.allwork')}</a>}
      loading={loading}
      bodyStyle={{
        padding: 0
      }}
    >
      <Card
        style={{
          height: '30vh',
          overflowY: 'auto'
        }}
      >
        {data?.map((item: TaskInfo) => (
          <Card.Grid key={item.id} style={{ padding: 5 }}>
            <ProCard
              bordered={false}
              title={renderTitle(item)}
              extra={
                <Space>
                  <JobLifeCycleTag animation={false} bordered={false} status={item.step} />
                </Space>
              }
            >
              <div style={{ marginBottom: 10 }}>{item.note ?? l('home.task.not.desc')}</div>
              <Space style={{ fontSize: 10 }}>
                <Typography.Text type='secondary'>{item.updateTime.toString()}</Typography.Text>
                <StatusTag animation={false} bordered={false} status={item.status} />
              </Space>
            </ProCard>
          </Card.Grid>
        ))}
      </Card>
    </Card>
  );
};
export default MyWorker;
