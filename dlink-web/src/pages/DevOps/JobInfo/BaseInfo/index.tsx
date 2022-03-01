import {Descriptions} from 'antd';
import StatusCounts from "@/components/Common/StatusCounts";

const BaseInfo = (props: any) => {

  const {job} = props;

  return (<Descriptions bordered>
    <Descriptions.Item label="作业状态"><StatusCounts statusCounts={job?.instance.statusCounts}/></Descriptions.Item>
    <Descriptions.Item label="重启次数">{job?.instance.failedRestartCount}</Descriptions.Item>
    <Descriptions.Item label="耗时">{job?.instance.duration}</Descriptions.Item>
    <Descriptions.Item label="启动时间">{job?.instance.createTime}</Descriptions.Item>
    <Descriptions.Item label="更新时间">{job?.instance.updateTime}</Descriptions.Item>
    <Descriptions.Item label="完成时间">{job?.instance.finishTime}</Descriptions.Item>
    <Descriptions.Item span={3}>{}</Descriptions.Item>
  </Descriptions>)
};

export default BaseInfo;
