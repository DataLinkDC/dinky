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
import {MinusSquareOutlined, RocketOutlined, SyncOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Col, Drawer, Modal, Row, Tag, Tooltip} from 'antd';
import ProDescriptions from '@ant-design/pro-descriptions';
import {handleOption, queryData} from "@/components/Common/crud";
import {Scrollbars} from "react-custom-scrollbars";
import {TaskHistoryTableListItem} from "@/components/Studio/StudioRightTool/StudioHistory/data";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import moment from "moment";
import {MonacoDiffEditor} from "react-monaco-editor";
import {l} from "@/utils/intl";


const url = '/api/task/version';


const StudioHistory = (props: any) => {

  const {current, toolHeight} = props;
  const [row, setRow] = useState<TaskHistoryTableListItem>();
  const actionRef = useRef<ActionType>();

  const [versionDiffVisible, setVersionDiffVisible] = useState<boolean>(false);
  const [versionDiffRow, setVersionDiffRow] = useState<TaskHistoryTableListItem>();

  if (current.key) {
    actionRef.current?.reloadAndRest?.();
  }

  const cancelHandle = () => {
    setVersionDiffVisible(false);
  }

  const VersionDiffForm = () => {

    let leftTitle = "Version：【" + versionDiffRow?.versionId + "】   创建时间: 【" + (moment(versionDiffRow?.createTime).format('YYYY-MM-DD HH:mm:ss')) + "】";
    let rightTitle = "Version：【当前编辑版本】 创建时间: 【" + (moment(current?.task?.createTime).format('YYYY-MM-DD HH:mm:ss')) + "】 最后更新时间: 【" + (moment(current?.task?.updateTime).format('YYYY-MM-DD HH:mm:ss')) + "】"

    let originalValue = versionDiffRow?.statement;
    let currentValue = current?.task?.statement;

    return (
      <>
        <Modal title={"Version Diff"} visible={versionDiffVisible} destroyOnClose={true} width={"85%"}
               bodyStyle={{height: "700px"}}
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
          <div style={{display: "flex", flexDirection: "row", justifyContent: "space-between"}}>
            <Tag color="green" style={{height: "20px"}}>
              <RocketOutlined/> {leftTitle}
            </Tag>
            <Tag color="blue" style={{height: "20px"}}>
              <SyncOutlined spin/> {rightTitle}
            </Tag>
          </div>
          <br/>
          <Scrollbars style={{height: "98%"}}>
            <React.StrictMode>
              <MonacoDiffEditor options={{
                readOnly: true,
                selectOnLineNumbers: true,
                lineDecorationsWidth: 20,
                mouseWheelZoom: true,
                automaticLayout:true,
              }} language={"sql"} theme={"vs-dark"} original={originalValue} value={currentValue}/>
            </React.StrictMode>
          </Scrollbars>
        </Modal>
      </>
    )
  }

  const columns: ProColumns<TaskHistoryTableListItem>[] = [
    // {
    //   title: 'id',
    //   dataIndex: 'id',
    //   hideInForm: false,
    //   hideInSearch: false,
    // },
    {
      title: '版本ID',
      dataIndex: 'versionId',
      sorter: true,
      hideInForm: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      align: "center",
      render: (text, record, index) => (
        <>
          <Button type="link" onClick={() => onRollBackVersion(record)}>回滚</Button>
          <Button type="link" title={"只和当前编辑器内的内容对比"} onClick={() => {
            setVersionDiffRow(record)
            setVersionDiffVisible(true)
          }}>版本对比</Button>
        </>

      )
    },
  ];


  const onRollBackVersion = (row: TaskHistoryTableListItem) => {
    Modal.confirm({
      title: '回滚Flink SQL版本',
      content: `确定回滚Flink SQL版本至【${row.versionId}】吗？`,
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const TaskHistoryRollbackItem = {
          id: current.key, versionId: row.versionId
        }
        await handleOption('api/task/rollbackTask', "回滚Flink SQL版本", TaskHistoryRollbackItem);
        actionRef.current?.reloadAndRest?.();
      }
    });
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined/>}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        <ProTable<TaskHistoryTableListItem>
          actionRef={actionRef}
          rowKey="id"
          request={(params, sorter, filter) => queryData(url, {taskId: current.key, ...params, sorter, filter})}
          columns={columns}
          search={false}
        />
        <Drawer
          width={600}
          visible={!!row}
          onClose={() => {
            setRow(undefined);
          }}
          closable={false}
        >
          {row?.versionId && (
            <ProDescriptions<TaskHistoryTableListItem>
              column={2}
              title={row?.versionId}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.versionId,
              }}
              columns={columns}
            />
          )}
        </Drawer>
      </Scrollbars>
      {VersionDiffForm()}
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  toolHeight: Studio.toolHeight,
}))(StudioHistory);
