import {Descriptions, Typography, Tag} from 'antd';
import {
  RocketOutlined
} from '@ant-design/icons';

const {Text, Link} = Typography;

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
      <Descriptions.Item label="任务类型">{job?.history?.config.isJarTask?'Jar':'FlinkSQL'}</Descriptions.Item>
      <Descriptions.Item label="批模式">{job?.history?.config.useBatchModel?'启用':'禁用'}</Descriptions.Item>
      <Descriptions.Item label="CheckPoint">{job?.history?.config.checkpoint}</Descriptions.Item>
      <Descriptions.Item label="SavePoint机制">
        {job?.history?.config.savePointStrategy=='NONE'?'禁用':
          job?.history?.config.savePointStrategy=='LATEST'?'最近一次':
            job?.history?.config.savePointStrategy=='EARLIEST'?'最早一次':
              job?.history?.config.savePointStrategy=='CUSTOM'?'指定一次':'禁用'}
      </Descriptions.Item>
      <Descriptions.Item label="SavePoint" span={2}>{job?.history?.config.savePointPath}</Descriptions.Item>
      <Descriptions.Item label="Flink Configuration" span={3}><Text code>{JSON.stringify(job?.history?.config.config)}</Text></Descriptions.Item>
      {job?.jar?<>
        <Descriptions.Item label="Jar 路径">{job?.jar?.path}</Descriptions.Item>
        <Descriptions.Item label="Jar 主类">{job?.jar?.mainClass}</Descriptions.Item>
        <Descriptions.Item label="Jar 入参">{job?.jar?.paras}</Descriptions.Item>
      </>:undefined}
    </Descriptions>
  </>)
};

export default Config;
