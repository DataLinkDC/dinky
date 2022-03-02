import {Descriptions, Typography, Tag} from 'antd';
import {
  RocketOutlined
} from '@ant-design/icons';

const {Link} = Typography;

const Config = (props: any) => {

  const {job} = props;

  return (<>
    <Descriptions bordered size="small">
      <Descriptions.Item label="执行模式">{job?.history?.type ? (
        <Tag color="blue" key={job?.history?.type}>
          <RocketOutlined/> {job?.history?.type}
        </Tag>
      ) : undefined}
      </Descriptions.Item>
      <Descriptions.Item label="集群实例">
        {job?.cluster?.alias?<Link>{job?.cluster?.alias}</Link>:'-'}
      </Descriptions.Item>
      <Descriptions.Item label="集群配置">
        {job?.clusterConfiguration?.alias?<Link>{job?.clusterConfiguration?.alias}</Link>:'-'}
      </Descriptions.Item>
      <Descriptions.Item label="共享会话">
        {job?.history?.session?<Link>{job?.history?.session}</Link>:'禁用'}
      </Descriptions.Item>
      <Descriptions.Item label="片段机制">{job?.history?.config.useSqlFragment?'启用':'禁用'}</Descriptions.Item>
      <Descriptions.Item label="语句集">{job?.history?.config.useStatementSet?'启用':'禁用'}</Descriptions.Item>
      <Descriptions.Item label="批模式">{job?.history?.config.useBatchModel?'启用':'禁用'}</Descriptions.Item>
      <Descriptions.Item label="SavePoint机制">
        {job?.history?.config.savePointStrategy?'启用':'禁用'}
      </Descriptions.Item>
    </Descriptions>
  </>)
};

export default Config;
