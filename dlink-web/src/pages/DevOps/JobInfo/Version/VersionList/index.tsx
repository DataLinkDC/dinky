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


import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {TaskVersion} from "@/pages/DevOps/data";
import {useRef, useState,} from "react";
import {queryData} from "@/components/Common/crud";
import {getIcon} from "@/components/Studio/icon";
import {Button, Modal, Tag} from "antd";
import {FullscreenOutlined} from "@ant-design/icons";
import CodeShow from "@/components/Common/CodeShow";
import {useIntl} from "umi";

const url = '/api/task/version';
const VersionList = (props: any) => {
  const {job} = props;

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<TaskVersion>();
  const [modalVisible, setModalVisible] = useState<boolean>(false);


  const cancelHandle = () => {
    setRow(undefined);
    setModalVisible(false);
  }

  const handleShowStatement = (item: any) => {
    return (
      <div style={{width: "1100px"}}>
        <Modal title="作业执行 SQL" visible={modalVisible} destroyOnClose={true} width={"60%"}
               onCancel={() => {
                 cancelHandle();
               }}
               footer={[
                 <Button key="back" onClick={() => {
                   cancelHandle();
                 }}>
                   {l('button.close')}
                 </Button>,
               ]}>
          <CodeShow language={"sql"} code={item?.statement} height={'600px'}/>
        </Modal>
      </div>
    )

  }


  const columns: ProColumns<TaskVersion>[] = [
    {
      title: '作业ID',
      align: 'center',
      dataIndex: 'taskId',
      hideInSearch: true,
    },
    {
      title: '作业名称',
      align: 'center',
      sorter: true,
      dataIndex: 'name',
    },
    {
      title: '作业别名',
      align: 'center',
      sorter: true,
      dataIndex: 'alias',
    },
    {
      title: '作业方言',
      align: 'center',
      render: (dom, entity) => {
        return <>
          {getIcon(entity.dialect)}
          {
            <Tag color="blue">
              {entity.dialect}
            </Tag>
          }
        </>;
      },
    },
    {
      title: '作业类型',
      align: 'center',
      render: (dom, entity) => {
        return <>
          {
            <Tag color="blue">
              {entity.type}
            </Tag>
          }
        </>;
      },
    },
    {
      title: '版本号',
      align: 'center',
      sorter: true,
      dataIndex: 'versionId',
    },
    {
      title: '作业内容',
      align: 'center',
      ellipsis: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <>
          {<>
            <a onClick={() => {
              setRow(entity)
              setModalVisible(true);
            }}>
              <Tag color="green">
                <FullscreenOutlined title={"查看作业详情"}/>
              </Tag> 查看作业详情
            </a>

          </>
          }
        </>
          ;
      },
    },
    {
      title: l('global.table.createTime'),
      align: 'center',
      sorter: true,
      valueType: 'dateTime',
      dataIndex: 'createTime',
    },
    // {
    //   title: l('global.table.operate'),
    //   align: 'center',
    //   render: (dom, entity) => {
    //     return <>
    //       {<>
    //         <Button type={"link"} onClick={()=>{
    //           setRow(entity)
    //           setModalVisible(true);
    //         }}>
    //           版本对比
    //         </Button>
    //       </>
    //       }
    //     </>
    //       ;
    //   },
    // },
  ];

  return (
    <>
      <ProTable<TaskVersion>
        columns={columns}
        style={{width: '100%'}}
        request={(params, sorter, filter) => queryData(url, {taskId: job?.instance.taskId, ...params, sorter, filter})}
        actionRef={actionRef}
        rowKey="id"
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
        }}
        bordered
        search={false}
        size="small"
      />
      {handleShowStatement(row)}
    </>
  )
};

export default VersionList;
