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

import { JobDetailInfoModel } from '@/pages/DataStudio/BottomContainer/JobExecHistory/components/JobDetailInfoModel';
import { getCurrentTab } from '@/pages/DataStudio/function';
import { DataStudioTabsItemType, StateType } from '@/pages/DataStudio/model';
import { queryList } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { JobExecutionHistory } from '@/types/Studio/data';
import { formatDateToYYYYMMDDHHMMSS } from '@/utils/function';
import { l } from '@/utils/intl';
import { ErrorMessage } from '@/utils/messages';
import { ClusterOutlined, FireOutlined, RocketOutlined } from '@ant-design/icons';
import { ActionType, ProList } from '@ant-design/pro-components';
import { ProListMetas } from '@ant-design/pro-list';
import { useUpdateEffect } from 'ahooks';
import { Badge, Divider, Empty, Space, Tag, Typography } from 'antd';
import React, { useRef, useState } from 'react';
import { connect } from 'umi';

const { Link, Paragraph, Text } = Typography;

const JobExecHistory: React.FC<connect> = (props) => {
  const { tabs } = props;

  const currentTab = getCurrentTab(tabs.panes, tabs.activeKey) as DataStudioTabsItemType;

  const refAction = useRef<ActionType>();

  useUpdateEffect(() => {
    refAction.current?.reload();
  }, [tabs.activeKey]);

  const [modalVisit, setModalVisit] = useState(false);
  const [historyData, setHistoryData] = useState<JobExecutionHistory>();
  const [type, setType] = useState<number>(1);
  const showDetail = (row: JobExecutionHistory, type: number) => {
    setHistoryData(row);
    setModalVisit(true);
    setType(type);
    if (type === 3) {
      // todo: 实现预览数据逻辑并展示
      // showJobData(historyData.jobId,dispatch)
      // const res = getJobData(historyData.jobId);
      // res.then((resd) => {
      //   setResult(resd.datas);
      // });
    }
  };

  const handleCancel = () => {
    setModalVisit(false);
  };

  const renderMetaDescription = (item: JobExecutionHistory) => {
    return (
      <>
        <Paragraph>
          <>
            <Link href={`http://${item.jobManagerAddress}`} target='_blank'>
              [{item.jobManagerAddress}]
            </Link>
            <Divider type='vertical' />
            {l('global.table.startTime')}:{formatDateToYYYYMMDDHHMMSS(item.startTime)}
            <Divider type='vertical' />
            {l('global.table.finishTime')}:{formatDateToYYYYMMDDHHMMSS(item.endTime)}
          </>
        </Paragraph>
      </>
    );
  };

  const renderSubTitle = (item: JobExecutionHistory) => {
    return (
      <Space size={0}>
        {item.jobName ? (
          <Tag color={item.jobName ? 'blue' : 'red'} key={item.jobName}>
            {item.jobName}
          </Tag>
        ) : (
          l('global.job.status.failed-tip')
        )}

        {item.clusterName ? (
          <Tag color='green' key={item.clusterName}>
            <ClusterOutlined /> {item.clusterName}
          </Tag>
        ) : (
          <Tag color='green' key={'local'}>
            <ClusterOutlined /> {l('pages.devops.jobinfo.localenv')}
          </Tag>
        )}
        {item.type && (
          <Tag color='blue' key={item.type}>
            <RocketOutlined /> {item.type}
          </Tag>
        )}
        {item.status == 2 ? (
          <Space>
            <Badge status='success' />
            <Text type='success'>{l('global.job.status.success')}</Text>
          </Space>
        ) : item.status == 1 ? (
          <Space>
            <Badge status='success' />
            <Text type='secondary'>{l('global.job.status.running')}</Text>
          </Space>
        ) : item.status == 3 ? (
          <Space>
            <Badge status='error' />
            <Text type='danger'>{l('global.job.status.failed')}</Text>
          </Space>
        ) : item.status == 4 ? (
          <Space>
            <Badge status='error' />
            <Text type='warning'>{l('global.job.status.canceled')}</Text>
          </Space>
        ) : item.status == 0 ? (
          <Space>
            <Badge status='error' />
            <Text type='warning'>{l('global.job.status.initiating')}</Text>
          </Space>
        ) : (
          <Space>
            <Badge status='success' />
            <Text type='danger'>{l('global.job.status.unknown')}</Text>
          </Space>
        )}
      </Space>
    );
  };

  const renderActions = (item: JobExecutionHistory) => {
    return [
      <a key='config' onClick={() => showDetail(item, 1)}>
        {l('pages.datastudio.label.history.execConfig')}
      </a>,
      <a key='statement' onClick={() => showDetail(item, 2)}>
        {l('pages.datastudio.label.history.statement')}
      </a>,
      <a
        key='result'
        onClick={async () => {
          if (item.status != 2) {
            await ErrorMessage(l('pages.datastudio.label.history.notSuccess'));
            return;
          }
          showDetail(item, 3);
        }}
      >
        {l('pages.datastudio.label.history.result')}
      </a>,
      <a key='error' onClick={() => showDetail(item, 4)}>
        {l('pages.datastudio.label.history.error')}
      </a>
    ];
  };

  const buildMetaInfo = (): ProListMetas<JobExecutionHistory> => {
    return {
      title: {
        dataIndex: 'jobId',
        title: 'JobId',
        render: (_, row) => {
          return (
            <Space size={0}>
              <Tag color={row.jobId ? 'blue' : 'red'} key={row.jobId}>
                <FireOutlined /> {row.jobId ?? l('global.job.status.failed-tip')}
              </Tag>
            </Space>
          );
        }
      },
      description: {
        search: false,
        render: (_, row) => renderMetaDescription(row)
      },
      subTitle: {
        render: (_, row) => renderSubTitle(row),
        search: false
      },
      actions: {
        render: (text, row) => renderActions(row),
        search: false
      },
      jobName: {
        dataIndex: 'jobName',
        // title: l('pages.datastudio.label.history.jobName'),
        title: 'JobName'
      },
      clusterId: {
        dataIndex: 'clusterId',
        // title: l('pages.datastudio.label.history.runningCluster'),
        title: 'ClusterId'
      },
      status: {
        // 自己扩展的字段，主要用于筛选，不在列表中显示
        title: l('global.table.status'),
        valueType: 'select',
        valueEnum: {
          '': { text: '全部', status: 'ALL' },
          0: {
            text: '初始化中',
            status: 'INITIALIZE'
          },
          1: {
            text: '运行中',
            status: 'RUNNING'
          },
          2: {
            text: '成功',
            status: 'SUCCESS'
          },
          3: {
            text: '失败',
            status: 'FAILED'
          },
          4: {
            text: '取消',
            status: 'CANCEL'
          }
        }
      },
      startTime: {
        dataIndex: 'startTime',
        title: l('global.table.startTime'),
        valueType: 'dateTimeRange'
      },
      endTime: {
        dataIndex: 'endTime',
        title: l('global.table.finishTime'),
        valueType: 'dateTimeRange'
      }
    };
  };

  return (
    <>
      {tabs.panes.length === 0 ? (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={l('pages.datastudio.label.history.noData')}
        />
      ) : (
        <ProList<JobExecutionHistory>
          actionRef={refAction}
          search={{
            filterType: 'light'
          }}
          size={'small'}
          rowKey='id'
          params={{ taskId: currentTab?.task?.id }}
          dateFormatter={'string'}
          headerTitle={l('pages.datastudio.label.history.title', '', { name: currentTab?.label })}
          request={(params, sorter, filter: any) =>
            queryList(API_CONSTANTS.HISTORY_LIST, {
              ...{ ...params, taskId: currentTab?.params?.taskId },
              sorter: { id: 'descend' },
              filter
            })
          }
          pagination={{
            defaultPageSize: 5,
            showSizeChanger: true
          }}
          metas={buildMetaInfo()}
          options={{
            search: false,
            setting: false,
            density: false
          }}
        />
      )}

      <JobDetailInfoModel
        modalVisit={modalVisit}
        handleCancel={handleCancel}
        row={historyData}
        type={type}
      />
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(JobExecHistory);
