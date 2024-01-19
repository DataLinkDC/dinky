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

import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { ShowLogBtn } from '@/components/CallBackButton/ShowLogBtn';
import CodeShow from '@/components/CustomEditor/CodeShow';
import { JobProps } from '@/pages/DevOps/JobDetail/data.d';
import { handleRemoveById, queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { AlertHistory } from '@/types/DevOps/data.d';
import { InitAlertHistoryState } from '@/types/DevOps/init.d';
import { AlertHistoryState } from '@/types/DevOps/state.d';
import { l } from '@/utils/intl';
import { ProColumns } from '@ant-design/pro-components';
import ProTable from '@ant-design/pro-table';
import { Modal, Tag } from 'antd';
import { useEffect, useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

const AlertHistoryList = (props: JobProps) => {
  const { jobDetail } = props;

  const [alertHistory, setAlertHistory] = useState<AlertHistoryState>(InitAlertHistoryState);

  const getAlertHistory = () => {
    setAlertHistory((prevState) => ({ ...prevState, loading: true }));
    queryDataByParams(API_CONSTANTS.ALERT_HISTORY_LIST, {
      jobInstanceId: jobDetail.instance.id
    }).then((res) => {
      setAlertHistory((prevState) => ({ ...prevState, alertHistory: res as AlertHistory[] }));
    });
    setAlertHistory((prevState) => ({ ...prevState, loading: false }));
  };

  useEffect(() => {
    getAlertHistory();
  }, []);

  const handleDelete = async (record: AlertHistory) => {
    setAlertHistory((prevState) => ({ ...prevState, loading: true }));
    await handleRemoveById(API_CONSTANTS.ALERT_HISTORY_DELETE, record.id);
    getAlertHistory();
    setAlertHistory((prevState) => ({ ...prevState, loading: false }));
  };

  const handleShowContent = (record: AlertHistory) => {
    Modal.info({
      title: l('devops.jobinfo.config.JobAlert.history.content'),
      bodyStyle: { width: '100%', overflow: 'auto' },
      width: '30vw',
      content: (
        <ReactMarkdown remarkPlugins={[remarkGfm]} key={record.id}>
          {record.content}
        </ReactMarkdown>
      )
    });
  };

  const handleShowLog = (record: AlertHistory) => {
    Modal.info({
      title: l('devops.jobinfo.config.JobAlert.history.log'),
      bodyStyle: { width: '100%', overflow: 'auto' },
      width: '30vw',
      content: <CodeShow code={record.log} language={'java'} />
    });
  };

  const columns: ProColumns<AlertHistory>[] = [
    {
      title: 'ID',
      dataIndex: 'id'
    },
    {
      title: l('devops.jobinfo.config.JobAlert.history.group'),
      render: (text: any, record: AlertHistory) => {
        return <Tag color={'processing'}>{record.alertGroup.name}</Tag>;
      }
    },
    {
      title: l('devops.jobinfo.config.JobAlert.history.title'),
      dataIndex: 'title'
    },
    {
      title: l('devops.jobinfo.config.JobAlert.history.content'),
      render: (text: any, record: AlertHistory) => {
        return <ShowLogBtn onClick={() => handleShowContent(record)} />;
      }
    },
    {
      title: l('devops.jobinfo.config.JobAlert.history.status'),
      dataIndex: 'status',
      render: (text: any, record: AlertHistory) => {
        return (
          <Tag color={record.status === 1 ? 'success' : 'error'}>
            {record.status === 1 ? 'Success' : 'Failed'}{' '}
          </Tag>
        );
      }
    },
    {
      title: l('devops.jobinfo.config.JobAlert.history.log'),
      render: (text: any, record: AlertHistory) => {
        return <ShowLogBtn onClick={() => handleShowLog(record)} />;
      }
    },
    {
      title: l('devops.jobinfo.config.JobAlert.history.time'),
      dataIndex: 'createTime',
      valueType: 'dateTime'
    },
    {
      title: l('global.table.operate'),
      width: '2vw',
      fixed: 'right',
      render: (t: any, record: AlertHistory) => [
        <PopconfirmDeleteBtn
          key={`${record.id}_${t}_delete`}
          onClick={() => handleDelete(record)}
          description={l('devops.jobinfo.config.JobAlert.history.delete')}
        />
      ]
    }
  ];
  return (
    <>
      <ProTable<AlertHistory>
        rowKey={'id'}
        style={{ height: 'calc(100vh - 240px)' }}
        dataSource={alertHistory.alertHistory}
        loading={alertHistory.loading}
        pagination={{
          hideOnSinglePage: true,
          pageSize: 15
        }}
        size={'small'}
        virtual
        search={false}
        options={false}
        columns={columns}
      />
    </>
  );
};

export default AlertHistoryList;
