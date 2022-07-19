/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import React, {useRef, useState} from "react";
import {PageContainer, FooterToolbar} from '@ant-design/pro-layout';
import {
  DownOutlined,
  HeartOutlined,
  PlusOutlined,
  UserOutlined,
  SettingOutlined,
  EditOutlined,
  EllipsisOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined, DeleteOutlined
} from '@ant-design/icons';
import {Progress, Tag, Button, Space, Badge, Typography, Image, Row, Col, Card, Avatar, Modal} from 'antd';
import ProList from '@ant-design/pro-list';
import {handleRemove, queryData} from "@/components/Common/crud";
import {getDBImage} from "@/pages/DataBase/DB";
import DBForm from "./components/DBForm";
import {ActionType} from "@ant-design/pro-table";

import styles from './index.less';
import {DataBaseItem} from "@/pages/DataBase/data";
import {checkHeartBeat} from "@/pages/DataBase/service";
import {showDataBase} from "@/components/Studio/StudioEvent/DDL";

const {Text} = Typography;

const url = '/api/database';
const cardBodyStyle = {
  backgroundColor: 'rgba(0, 0, 0, 0.08)',
  position: 'relative',
  zIndex: 999
};

const DataBaseTableList: React.FC<{}> = (props: any) => {
  const { dispatch} = props;
  const [chooseDBModalVisible, handleDBFormModalVisible] = useState<boolean>(false);
  const [values, setValues] = useState<any>({});
  const actionRef = useRef<ActionType>();


  const onRefreshDataBase = () => {
    showDataBase(dispatch);
  };

  const onEdit = (row: DataBaseItem) => {
    setValues(row);
    handleDBFormModalVisible(true);
  };

  const onCheckHeartBeat = (row: DataBaseItem) => {
    checkHeartBeat(row.id);
    actionRef.current?.reloadAndRest?.();
  };

  const onDeleteDataBase = (row: DataBaseItem) => {
    Modal.confirm({
      title: '删除数据源',
      content: `确定删除该数据源【${row.alias === "" ? row.name : row.alias}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        await handleRemove('api/database', [row]);
        onRefreshDataBase();
        actionRef.current?.reloadAndRest?.();
      }
    });
  };

  return (
    <PageContainer>
      <ProList
        actionRef={actionRef}
        toolBarRender={() => {
          return [
            <Button type="primary" onClick={() => {
              setValues({});
              handleDBFormModalVisible(true);}}>
              <PlusOutlined/> 新建
            </Button>,
          ];
        }}
        pagination={{
          defaultPageSize: 8,
          showSizeChanger: false,
        }}
        grid={{gutter: 16, column: 4}}
        request={(params, sorter, filter) => queryData(url, {...params, sorter: {id: 'descend'}, filter})}
        renderItem={(row) => {
          return (
            <Card

              style={{width: 300}}
              hoverable={true}
              bodyStyle={cardBodyStyle}
              cover={
                <div className={styles.cardImage}>
                  <Image
                    preview={false}
                    style={{padding: '20px'}}
                    src={getDBImage(row.type)}
                    fallback="data:image/png;base64,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"
                  />
                </div>
              }
              actions={[
                <HeartOutlined key="setting" onClick={()=>{
                  onCheckHeartBeat(row);
                }}/>,
                <EditOutlined key="edit" onClick={() => {
                  onEdit(row);
                }}/>,
                <DeleteOutlined key="delete" onClick={() => {
                  onDeleteDataBase(row);
                }}/>,
                <EllipsisOutlined key="ellipsis"/>,
              ]}
            >
              <Card.Meta
                title={<Tag color="gray" key={row.alias}>
                  {row.alias}
                </Tag>}
                description={(
                  <Space size={0}>
                    <Tag key={row.name}>
                      {row.name}
                    </Tag>
                    <Tag color="blue" key={row.groupName}>
                      {row.groupName}
                    </Tag>
                    {(row.status) ?
                      (<Tag icon={<CheckCircleOutlined/>} color="success">
                        正常
                      </Tag>) :
                      <Tag icon={<ExclamationCircleOutlined/>} color="warning">
                        异常
                      </Tag>}
                  </Space>
                )}
              />
            </Card>)
        }}
        headerTitle="数据源"
      />
      <DBForm onCancel={() => {
        handleDBFormModalVisible(false);
        setValues({});
      }}
              modalVisible={chooseDBModalVisible}
              onSubmit={() => {
                actionRef.current?.reloadAndRest?.();
              }}
              values={values}
      />
    </PageContainer>
  );
};


export default DataBaseTableList;
