import { Descriptions, Badge } from 'antd';

const Tables = (props: any) => {

  const {table} = props;

  return (<Descriptions bordered>
    <Descriptions.Item label="Name">{table.name}</Descriptions.Item>
    <Descriptions.Item label="Schema">{table.schema}</Descriptions.Item>
    <Descriptions.Item label="Catalog">{table.catalog}</Descriptions.Item>
    <Descriptions.Item label="Rows">{table.rows}</Descriptions.Item>
    <Descriptions.Item label="Type">{table.type}</Descriptions.Item>
    <Descriptions.Item label="Engine">{table.engine}</Descriptions.Item>
    <Descriptions.Item label="Options" span={3}>
      {table.options}
    </Descriptions.Item>
    <Descriptions.Item label="Status"><Badge status="processing" text="Running" /></Descriptions.Item>
    <Descriptions.Item label="CreateTime">{table.createTime}</Descriptions.Item>
    <Descriptions.Item label="UpdateTime">{table.updateTime}</Descriptions.Item>
    <Descriptions.Item label="Comment" span={3}>{table.comment}</Descriptions.Item>
  </Descriptions>)
};

export default Tables;
