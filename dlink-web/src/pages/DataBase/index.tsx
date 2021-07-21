import React from "react";
import {PageContainer, FooterToolbar} from '@ant-design/pro-layout';
import {DownOutlined, HeartOutlined, PlusOutlined, UserOutlined} from '@ant-design/icons';
import { Progress, Tag, Button,Space,Badge,Typography } from 'antd';
import ProList from '@ant-design/pro-list';
import {queryData} from "@/components/Common/crud";

const { Text } = Typography;

const url = '/api/database';

const DataBaseTableList: React.FC<{}> = (props: any) => {

  return (
    <PageContainer>
      <ProList
        toolBarRender={() => {
          return [
            <Button key="3" type="primary">
              <PlusOutlined/> 新建
            </Button>,
          ];
        }}
        pagination={{
        defaultPageSize: 8,
        showSizeChanger: false,
      }}
        grid={{ gutter: 16, column: 4 }}
        request={(params, sorter, filter) => queryData(url,{...params, sorter:{id:'descend'}, filter})}
        metas={{
          title: {
            dataIndex: 'alias',
            title: 'alias',
          },
        subTitle: {
          render: (_, row) => {
            return (
              <Space size={0}>
                <Tag color="blue" key={row.name}>
                  {row.name}
                </Tag>
                <Tag color="gray" key={row.groupName}>
                  {row.groupName}
                </Tag>
                {(row.status) ?
                  (<><Badge status="success"/><Text type="success">正常</Text></>):
                  <><Badge status="error"/><Text type="danger">异常</Text></>}
              </Space>
            );
          },
        },
        type: {},
        avatar: {},
        content: {},
        actions: {},
      }}
        headerTitle="数据源"
        // dataSource={data}
        />

    </PageContainer>
  );
};


export default DataBaseTableList;
