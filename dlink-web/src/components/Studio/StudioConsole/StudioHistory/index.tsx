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

const { Paragraph, Text, Link} = Typography;

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
      title: '删除执行记录',
      content: '确定删除该执行记录吗？',
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
        headerTitle="执行历史"
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
                  <Divider type="vertical"/>开始于：{row.startTime}
                  <Divider type="vertical"/>完成于：{row.endTime}
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
                    <ClusterOutlined/> 本地环境
                  </Tag>)}
                  {row.type ? (
                    <Tag color="blue" key={row.type}>
                      <RocketOutlined/> {row.type}
                    </Tag>
                  ) : ''}
                  {(row.status == 2) ?
                    (<><Badge status="success"/><Text type="success">SUCCESS</Text></>) :
                    (row.status == 1) ?
                      <><Badge status="success"/><Text type="secondary">RUNNING</Text></> :
                      (row.status == 3) ?
                        <><Badge status="error"/><Text type="danger">FAILED</Text></> :
                        (row.status == 4) ?
                          <><Badge status="error"/><Text type="warning">CANCEL</Text></> :
                          (row.status == 0) ?
                            <><Badge status="error"/><Text type="warning">INITIALIZE</Text></> :
                            <><Badge status="success"/><Text type="danger">UNKNOWEN</Text></>}
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
                执行配置
              </a>,
              <a key="statement" onClick={() => {
                showDetail(row, 2)
              }}>
                FlinkSql语句
              </a>,
              <a key="result" onClick={() => {
                showDetail(row, 3)
              }}>
                预览数据
              </a>,
              <a key="error" onClick={() => {
                showDetail(row, 4)
              }}>
                异常信息
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
            title: '作业名',
          },
          clusterId: {
            dataIndex: 'clusterId',
            title: '运行集群',
          },
          session: {
            dataIndex: 'session',
            title: '共享会话',
          },
          status: {
            // 自己扩展的字段，主要用于筛选，不在列表中显示
            title: '状态',
            valueType: 'select',
            valueEnum: {
              '': {text: '全部', status: 'ALL'},
              0: {
                text: '初始化',
                status: 'INITIALIZE',
              },
              1: {
                text: '运行中',
                status: 'RUNNING',
              },
              2: {
                text: '成功',
                status: 'SUCCESS',
              },
              3: {
                text: '失败',
                status: 'FAILED',
              },
              4: {
                text: '停止',
                status: 'CANCEL',
              },
            },
          },
          startTime: {
            dataIndex: 'startTime',
            title: '开始时间',
            valueType: 'dateTimeRange',
          },
          endTime: {
            dataIndex: 'endTime',
            title: '完成时间',
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
            title='执行配置'
          >
            <ProDescriptions.Item span={2} label="JobId">
              <Tag color="blue" key={row.jobId}>
                <FireOutlined/> {row.jobId}
              </Tag>
            </ProDescriptions.Item>
            <ProDescriptions.Item label="共享会话">
              {config.useSession ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="会话 Key">
              {config.session}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="执行方式">
              {config.useRemote ? '远程' : '本地'}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="任务类型">
              {config.type}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="集群ID">
              {config.clusterId}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="集群配置ID">
              {config.clusterConfigurationId}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="预览结果">
              {config.useResult ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="打印流">
              {config.useChangeLog ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="最大行数">
              {config.maxRowNum}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="自动停止">
              {config.useAutoCancel ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item span={2} label="JobManagerAddress">
              {row.jobManagerAddress}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="作业ID">
              {config.taskId}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="作业名">
              {config.jobName}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="片段机制">
              {config.useSqlFragment ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="语句集">
              {config.useStatementSet ? l('button.enable') : l('button.disable')}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="并行度">
              {config.parallelism}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="CheckPoint">
              {config.checkpoint}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="savePoint 机制">
              {config.savePointStrategy}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="SavePointPath">
              {config.savePointPath}
            </ProDescriptions.Item>
          </ProDescriptions>
        )}
        {type == 2 && (
          <ProDescriptions
            column={1}
            title='FlinkSql 语句'
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
            title='数据预览'
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
            title='异常信息'
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
