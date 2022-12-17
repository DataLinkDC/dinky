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


import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {Badge, Divider, Modal, Space, Tag, Typography} from 'antd';
import {ClusterOutlined, FireOutlined, MessageOutlined, RocketOutlined} from "@ant-design/icons";
import ProList from '@ant-design/pro-list';
import {handleRemove, queryData} from "@/components/Common/crud";
import ProDescriptions from '@ant-design/pro-descriptions';
import React, {useState} from "react";
import StudioPreview from "../StudioPreview";
import {getJobData} from "@/pages/DataStudio/service";
import {HistoryItem} from "@/components/Studio/StudioConsole/StudioHistory/data";
import CodeShow from "@/components/Common/CodeShow";
import {l} from "@/utils/intl";

const {Paragraph, Text, Link} = Typography;

type HistoryConfig = {
  useSession: boolean;
  session: string;
  useRemote: boolean;
  type: string;
  clusterId: number;
  clusterConfigurationId: number;
  host: string;
  useResult: boolean;
  useChangeLog: boolean;
  maxRowNum: number;
  useAutoCancel: boolean;
  taskId: number;
  jobName: string;
  useSqlFragment: boolean;
  useStatementSet: boolean;
  useBatchModel: boolean;
  checkpoint: number;
  parallelism: number;
  savePointPath: string;
};

const url = '/api/history';
const StudioHistory = (props: any) => {

  const {current, refs, dispatch} = props;
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<HistoryItem>();
  const [config, setConfig] = useState<HistoryConfig>();
  const [type, setType] = useState<number>();
  const [result, setResult] = useState<{}>();

  const showDetail = (row: HistoryItem, type: number) => {
    setRow(row);
    setModalVisit(true);
    setType(type);
    setConfig(JSON.parse(row.configJson));
    if (type === 3) {
      // showJobData(row.jobId,dispatch)
      const res = getJobData(row.jobId);
      res.then((resd) => {
        setResult(resd.datas);
      });
    }
  };

  const removeHistory = (row: HistoryItem) => {
    Modal.confirm({
      title: l('pages.datastudio.label.history.delete'),
      content: l('pages.datastudio.label.history.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await handleRemove(url, [row]);
        refs.history?.current?.reload();
      }
    });
  };

  const handleCancel = () => {
    setModalVisit(false);
  };

  return (
    <>
      <ProList<HistoryItem>
        actionRef={refs.history}
        toolBarRender={() => {
          return [
            // <Button key="3" type="primary"  icon={<ReloadOutlined />}/>,
          ];
        }}
        search={{
          filterType: 'light',
        }}
        rowKey="id"
        headerTitle={l('pages.datastudio.label.history.exec')}
        request={(params, sorter, filter) => queryData(url, {...params, sorter: {id: 'descend'}, filter})}
        pagination={{
          defaultPageSize: 5,
          showSizeChanger: true,
        }}
        showActions="hover"
        metas={{
          title: {
            dataIndex: 'jobId',
            title: 'JobId',
            render: (_, row) => {
              return (
                <Space size={0}>
                  <Tag color="blue" key={row.jobId}>
                    <FireOutlined/> {row.jobId}
                  </Tag>
                </Space>
              );
            },
          },
          description: {
            search: false,
            render: (_, row) => {
              return (<Paragraph>
                <blockquote>
                  <Link href={`http://${row.jobManagerAddress}`} target="_blank">
                    [{row.jobManagerAddress}]
                  </Link>
                  <Divider type="vertical"/>{l('global.table.startTime')}:{row.startTime}
                  <Divider type="vertical"/>{l('global.table.finishTime')}:{row.endTime}
                </blockquote>
              </Paragraph>)
            }
          },
          subTitle: {
            render: (_, row) => {
              return (
                <Space size={0}>
                  {row.jobName ? (
                    <Tag color="gray" key={row.jobName}>
                      {row.jobName}
                    </Tag>
                  ) : ''}
                  {row.session ? (
                    <Tag color="orange" key={row.session}>
                      <MessageOutlined/> {row.session}
                    </Tag>
                  ) : ''}
                  {row.clusterAlias ? (
                    <Tag color="green" key={row.clusterAlias}>
                      <ClusterOutlined/> {row.clusterAlias}
                    </Tag>
                  ) : (<Tag color="green" key={row.clusterAlias}>
                    <ClusterOutlined/> {l('pages.devops.jobinfo.localenv')}
                  </Tag>)}
                  {row.type ? (
                    <Tag color="blue" key={row.type}>
                      <RocketOutlined/> {row.type}
                    </Tag>
                  ) : ''}
                  {(row.status == 2) ?
                    (<><Badge status="success"/><Text type="success">{l('pages.devops.jobstatus.SUCCESS')}</Text></>) :
                    (row.status == 1) ?
                      <><Badge status="success"/><Text type="secondary">{l('pages.devops.jobstatus.RUNNING')}</Text></> :
                      (row.status == 3) ?
                        <><Badge status="error"/><Text type="danger">{l('pages.devops.jobstatus.FAILED')}</Text></> :
                        (row.status == 4) ?
                          <><Badge status="error"/><Text type="warning">{l('pages.devops.jobstatus.CANCELED')}</Text></> :
                          (row.status == 0) ?
                            <><Badge status="error"/><Text type="warning">{l('pages.devops.jobstatus.INITIALIZING')}</Text></> :
                            <><Badge status="success"/><Text type="danger">{l('pages.devops.jobstatus.UNKNOWN')}</Text></>}
                </Space>
              );
            },
            search: false,
          },
          actions: {
            render: (text, row) => [
              <a key="config" onClick={() => {
                showDetail(row, 1)
              }}>
                {l('pages.datastudio.label.history.execConfig')}
              </a>,
              <a key="statement" onClick={() => {
                showDetail(row, 2)
              }}>
                {l('pages.datastudio.label.history.statement')}
              </a>,
              <a key="result" onClick={() => {
                showDetail(row, 3)
              }}>
                {l('pages.datastudio.label.history.result')}
              </a>,
              <a key="error" onClick={() => {
                showDetail(row, 4)
              }}>
                {l('pages.datastudio.label.history.error')}
              </a>,
              <a key="delete" onClick={() => {
                removeHistory(row)
              }}>
                {l('button.delete')}
              </a>,
            ],
            search: false,
          },
          jobName: {
            dataIndex: 'jobName',
            title: l('pages.datastudio.label.history.jobName'),
          },
          clusterId: {
            dataIndex: 'clusterId',
            title: l('pages.datastudio.label.history.runningCluster'),
          },
          session: {
            dataIndex: 'session',
            title: l('pages.datastudio.label.history.session'),
          },
          status: {
            // 自己扩展的字段，主要用于筛选，不在列表中显示
            title: l('global.table.status'),
            valueType: 'select',
            valueEnum: {
              '': {text: l('pages.devops.jobstatus.ALL'), status: 'ALL'},
              0: {
                text: l('pages.devops.jobstatus.INITIALIZING'),
                status: 'INITIALIZE',
              },
              1: {
                text: l('pages.devops.jobstatus.RUNNING'),
                status: 'RUNNING',
              },
              2: {
                text: l('pages.devops.jobstatus.SUCCESS'),
                status: 'SUCCESS',
              },
              3: {
                text: l('pages.devops.jobstatus.FAILED'),
                status: 'FAILED',
              },
              4: {
                text: l('pages.devops.jobstatus.CANCELED'),
                status: 'CANCEL',
              },
            },
          },
          startTime: {
            dataIndex: 'startTime',
            title: l('global.table.startTime'),
            valueType: 'dateTimeRange',
          },
          endTime: {
            dataIndex: 'endTime',
            title: l('global.table.finishTime'),
            valueType: 'dateTimeRange',
          },
        }}
        options={{
          search: false,
          setting: false
        }}
      />
      <Modal
        width={'80%'}
        visible={modalVisit}
        destroyOnClose
        centered
        footer={false}
        onCancel={handleCancel}
      >
        {type == 1 && (
          <ProDescriptions
            column={2}
            title={l('pages.datastudio.label.history.execConfig')}
          >
            <ProDescriptions.Item span={2} label="JobId">
              <Tag color="blue" key={row.jobId}>
                <FireOutlined/> {row.jobId}
              </Tag>
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.session')}>
              {config.useSession ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.session')}>
              {config.session}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('global.table.runmode')}>
              {config.useRemote ? l('global.table.runmode.remote') : l('global.table.runmode.local')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.taskType')}>
              {config.type}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.clusterId')}>
              {config.clusterId}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.clusterConfigId')}>
              {config.clusterConfigurationId}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.result')}>
              {config.useResult ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.changelog')}>
              {config.useChangeLog ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.maxRow')}>
              {config.maxRowNum}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.autoStop')}>
              {config.useAutoCancel ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item span={2} label="JobManagerAddress">
              {row.jobManagerAddress}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.jobId')}>
              {config.taskId}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.jobName')}>
              {config.jobName}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.useStatementSet')}>
              {config.useSqlFragment ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.useSqlFragment')}>
              {config.useStatementSet ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.parallelism')}>
              {config.parallelism}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.checkpoint')}>
              {config.checkpoint}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.savePointStrategy')}>
              {config.savePointStrategy}
            </ProDescriptions.Item>
            <ProDescriptions.Item label={l('pages.datastudio.label.history.savePointPath')}>
              {config.savePointPath}
            </ProDescriptions.Item>
          </ProDescriptions>
        )}
        {type == 2 && (
          <ProDescriptions
            column={1}
            title={l('pages.datastudio.label.history.statement')}
          >
            <ProDescriptions.Item label="JobId">
              <Tag color="blue" key={row.jobId}>
                <FireOutlined/> {row.jobId}
              </Tag>
            </ProDescriptions.Item>
            <ProDescriptions.Item>
              <CodeShow height={"80vh"} language={"sql"} code={row.statement} theme={"vs-dark"}/>
            </ProDescriptions.Item>
          </ProDescriptions>
        )}
        {type == 3 && (
          <ProDescriptions
            column={2}
            title={l('pages.datastudio.label.history.result')}
          >
            <ProDescriptions.Item span={2} label="JobId">
              <Tag color="blue" key={row.jobId}>
                <FireOutlined/> {row.jobId}
              </Tag>
            </ProDescriptions.Item>
            <ProDescriptions.Item span={2}>
              <StudioPreview result={result} style={{width: '100%'}}/>
            </ProDescriptions.Item>
          </ProDescriptions>
        )}
        {type == 4 && (
          <ProDescriptions
            column={1}
            title={l('pages.datastudio.label.history.error')}
          >
            <ProDescriptions.Item label="JobId">
              <Tag color="blue" key={row.jobId}>
                <FireOutlined/> {row.jobId}
              </Tag>
            </ProDescriptions.Item>
            <ProDescriptions.Item>
              <CodeShow height={"80vh"} language={"java"} code={row.error} theme={"vs-dark"}/>
            </ProDescriptions.Item>
          </ProDescriptions>
        )}
      </Modal>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  refs: Studio.refs,
}))(StudioHistory);
