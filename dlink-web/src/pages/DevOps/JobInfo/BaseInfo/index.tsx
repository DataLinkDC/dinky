import { Descriptions, Badge } from 'antd';

const BaseInfo = (props: any) => {

  const {job} = props;

  return (<Descriptions bordered>
    <Descriptions.Item label="实例状态">{job?.instance.status}</Descriptions.Item>
    <Descriptions.Item label="重启次数">{job?.instance.schema}</Descriptions.Item>
    <Descriptions.Item label="耗时">{job?.instance.duration}</Descriptions.Item>
    <Descriptions.Item label="启动时间">{job?.instance.createTime}</Descriptions.Item>
    <Descriptions.Item label="更新时间">{job?.instance.updateTime}</Descriptions.Item>
    <Descriptions.Item label="完成时间">{job?.instance.finishTime}</Descriptions.Item>
    <Descriptions.Item label="Task" span={3}>
      {}
    </Descriptions.Item>
    <Descriptions.Item >{}</Descriptions.Item>
  </Descriptions>)
};

export default BaseInfo;
