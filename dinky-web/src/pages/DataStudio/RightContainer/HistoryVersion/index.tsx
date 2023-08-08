/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {ActionType, ProColumns, ProTable} from "@ant-design/pro-components";
import {Button, Modal, Tag} from "antd";
import {l} from "@/utils/intl";
import {handleOption} from "@/services/BusinessCrud";
import {DiffEditor } from "@monaco-editor/react";
import React, {useRef, useState} from "react";
import {RocketOutlined, SyncOutlined} from "@ant-design/icons";
import {postAll} from "@/services/api";
import moment from "moment";
import {getCurrentData} from "@/pages/DataStudio/function";

const url = '/api/task/version';

export type TaskHistoryTableListItem = {
  id: number,
  versionId: number,
  statement: string,
  createTime: Date,
};

const HistoryVersion = (props: any) => {
  const {  tabs: {panes, activeKey}} = props;
  const current = getCurrentData(panes, activeKey);
  const actionRef = useRef<ActionType>();

  const [versionDiffVisible, setVersionDiffVisible] = useState<boolean>(false);
  const [versionDiffRow, setVersionDiffRow] = useState<TaskHistoryTableListItem>();
  actionRef.current?.reloadAndRest?.();


  const cancelHandle = () => {
    setVersionDiffVisible(false);
  }

  const VersionDiffForm = () => {

    let leftTitle = l('pages.datastudio.label.version.leftTitle','',{
      versionId: versionDiffRow?.versionId,
      createTime: (moment(versionDiffRow?.createTime).format('YYYY-MM-DD HH:mm:ss')),
    })

    let rightTitle = l('pages.datastudio.label.version.rightTitle','',{
      createTime: (moment(current?.createTime).format('YYYY-MM-DD HH:mm:ss')),
      updateTime: (moment(current?.updateTime).format('YYYY-MM-DD HH:mm:ss')),
    })
    let originalValue = versionDiffRow?.statement;
    let currentValue = current?.statement;

    return (
      <>
        <Modal title={l('pages.datastudio.label.version.diff')} open={versionDiffVisible} destroyOnClose={true} width={"85%"}
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
          {/*<Scrollbars style={{height: "98%"}}>*/}
            <React.StrictMode>
              <DiffEditor height={"95%"} options={{
                readOnly: true,
                selectOnLineNumbers: true,
                lineDecorationsWidth: 20,
                mouseWheelZoom: true,
                automaticLayout:true,
              }} language={"sql"} theme={"vs-dark"} original={originalValue} modified={currentValue}/>
            </React.StrictMode>
          {/*</Scrollbars>*/}
        </Modal>
      </>
    )
  }

  const columns: ProColumns<TaskHistoryTableListItem>[] = [
    {
      title: l('pages.datastudio.label.version.id'),
      dataIndex: 'versionId',
      hideInForm: true,
      hideInSearch: true
    },
    {
      sorter:true,
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      align:"center",
      title: l('global.table.operate'),
      valueType: 'option',
      render: (text, record) => (
        <>
          <Button type="link" onClick={() => onRollBackVersion(record)}>{l('pages.datastudio.label.version.rollback')}</Button>
          <Button type="link" title={l('pages.datastudio.label.version.diff.tip')} onClick={() => {
            setVersionDiffRow(record)
            setVersionDiffVisible(true)
          }}>{l('pages.datastudio.label.version.diff')}</Button>
        </>

      )
    },
  ];


  const onRollBackVersion = (row: TaskHistoryTableListItem) => {
    Modal.confirm({
      title: l('pages.datastudio.label.version.rollback.flinksql'),
      content: l('pages.datastudio.label.version.rollback.flinksqlConfirm','',{versionId: row.versionId }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const TaskHistoryRollbackItem = {
          id: current.key, versionId: row.versionId
        }
        await handleOption('api/task/rollbackTask', l('pages.datastudio.label.version.rollback.flinksql'), TaskHistoryRollbackItem);
        actionRef.current?.reloadAndRest?.();
      }
    });
  };

  return (
    <>
        <ProTable<TaskHistoryTableListItem>
          actionRef={actionRef}
          rowKey="id"
          request={(params, sorter, filter) => postAll(url, {taskId: current.key, ...params, sorter, filter})}
          columns={columns}
          search={false}
        />
      {VersionDiffForm()}
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
}))(HistoryVersion);
