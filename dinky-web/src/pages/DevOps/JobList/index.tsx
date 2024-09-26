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

import { CircleBtn } from '@/components/CallBackButton/CircleBtn';
import JobLifeCycleTag from '@/components/JobTags/JobLifeCycleTag';
import StatusTag from '@/components/JobTags/StatusTag';
import {
  mapDispatchToProps,
  showFirstLevelOwner,
  showSecondLevelOwners
} from '@/pages/DataStudio/function';
import {
  buildProjectTree,
  generateList,
  getLeafKeyList,
  searchInTree
} from '@/pages/DataStudio/LeftContainer/Project/function';
import { StateType } from '@/pages/DataStudio/model';
import { DevopsContext } from '@/pages/DevOps';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { getJobDuration } from '@/pages/DevOps/function';
import JobHistoryList from '@/pages/DevOps/JobList/components/JobHistoryList/JobHistoryList';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { queryList } from '@/services/api';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { getTenantByLocalStorage } from '@/utils/function';
import { l } from '@/utils/intl';
import {
  ArrowsAltOutlined,
  ClearOutlined,
  ClockCircleTwoTone,
  EyeTwoTone,
  RedoOutlined,
  ShrinkOutlined
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProCard, ProTable } from '@ant-design/pro-components';
import { connect, useModel } from '@umijs/max';
import { Button, Col, Empty, Flex, Radio, Splitter, Table, Tree } from 'antd';
import Search from 'antd/es/input/Search';
import { Key, useContext, useEffect, useRef, useState } from 'react';
import { history } from 'umi';
import EllipsisMiddle from '@/components/Typography/EllipsisMiddle';

const { DirectoryTree } = Tree;

const JobList = (props: connect) => {
  const {
    users,
    queryUserData,
    taskOwnerLockingStrategy,
    queryTaskOwnerLockingStrategy,
    projectData
  } = props;
  const tableRef = useRef<ActionType>();
  const { statusFilter, setStatusFilter } = useContext<any>(DevopsContext);
  const [stepFilter, setStepFilter] = useState<number | undefined>();
  const [taskFilter, setTaskFilter] = useState<string | undefined>();
  const [taskId, setTaskId] = useState<number>();
  const [searchValue, setSearchValueValue] = useState('');
  const [expandedKeys, setExpandedKeys] = useState<Key[]>([]);
  const [selectedKey, setSelectedKey] = useState<Key[]>([]);
  const { initialState, setInitialState } = useModel('@@initialState');

  const [data, setData] = useState<any[]>(
    buildProjectTree(
      projectData,
      searchValue,
      [],
      initialState?.currentUser?.user,
      taskOwnerLockingStrategy,
      users
    )
  );

  useEffect(() => {
    setData(
      buildProjectTree(
        projectData,
        searchValue,
        [],
        initialState?.currentUser?.user,
        taskOwnerLockingStrategy,
        users
      )
    );
    if (searchValue === '' || searchValue === undefined) {
      setExpandedKeys([]);
    }
  }, [searchValue, projectData, taskOwnerLockingStrategy]);

  const jobListColumns: ProColumns<Jobs.JobInstance>[] = [
    {
      title: l('global.table.jobname'),
      dataIndex: 'name'
    },
    {
      title: l('global.table.lifecycle'),
      dataIndex: 'step',
      render: (_: any, row: { step: number }) => <JobLifeCycleTag status={row.step} />
    },
    {
      title: l('global.table.runmode'),
      dataIndex: 'type',
      hideInSearch: true
    },
    {
      title: l('devops.jobinfo.config.JobId'),
      dataIndex: 'jid',
      width: '20%',
      copyable: true
    },
    {
      title: l('global.table.firstLevelOwner'),
      width: '5%',
      hideInSearch: true,
      render: (_: any, row: Jobs.JobInstance) => showFirstLevelOwner(row?.firstLevelOwner, users)
    },
    {
      width: '8%',
      title: l('global.table.secondLevelOwners'),
      hideInSearch: true,
      render: (_: any, row: Jobs.JobInstance) => (
        <EllipsisMiddle maxCount={15} copyable={false}>
          {showSecondLevelOwners(row?.secondLevelOwners, users)}
        </EllipsisMiddle>
      )
    },
    {
      title: l('global.table.createTime'),
      hideInSearch: true,
      dataIndex: 'createTime',
      valueType: 'dateTime',
      sorter: true,
      defaultSortOrder: 'descend'
    },
    {
      title: l('global.table.useTime'),
      hideInSearch: true,
      render: (_: any, row: Jobs.JobInstance) => getJobDuration(row)
    },
    {
      title: l('global.table.status'),
      dataIndex: 'status',
      render: (_: any, row: Jobs.JobInstance) => <StatusTag status={row.status} />
    },
    Table.EXPAND_COLUMN,
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '5%',
      fixed: 'right',
      render: (text: any, record: Jobs.JobInstance) => [
        <Button
          className={'options-button'}
          key={`${record.id}_history`}
          title={l('devops.joblist.detail')}
          icon={<EyeTwoTone />}
          onClick={() => history.push(`/devops/job-detail?id=${record.id}`)}
        />
      ]
    }
  ];

  // 重置选中的 key 和 taskId | reset the selected key and taskId
  const resetValue = () => {
    setSelectedKey([]);
    setTaskId(undefined);
    tableRef.current?.reload();
  };

  //  监听 statusFilter 的变化，如果为 undefined 则重置选中的 key 和 taskId | listen for changes in statusFilter, if it is undefined, reset the selected key and taskId
  useEffect(() => {
    if (statusFilter === undefined) {
      resetValue();
    }
  }, [statusFilter]);

  useEffect(() => {
    setInterval(() => tableRef.current?.reload(false), 5 * 1000);
    queryUserData({ id: getTenantByLocalStorage() });
    queryTaskOwnerLockingStrategy(SettingConfigKeyEnum.ENV.toLowerCase());
  }, []);

  const onChangeSearch = (e: any) => {
    let { value } = e.target;
    if (!value) {
      setSearchValueValue(value);
      return;
    }
    value = String(value).trim();
    const expandedKeys: string[] = searchInTree(generateList(data, []), data, value, 'contain');
    setExpandedKeys(expandedKeys);
    setSearchValueValue(value);
  };

  function onNodeClick(info: any) {
    const {
      node: { isLeaf, name, type, parentId, path, key, taskId, fullInfo }
    } = info;
    if (isLeaf) {
      // 如果是 leaf 节点 则设置选中的 key 和 taskId | if it is a leaf node, set the selected key and taskId
      setSelectedKey([key]);
      setTaskId(taskId);
    } else {
      // 如果不是 leaf 节点 则设置选中的 key 和 taskId 为 undefined | if it is not a leaf node, set the selected key and taskId to undefined
      setTaskId(undefined);
      setSelectedKey([]);
    }
  }

  return (
    <ProCard
      boxShadow
      size={'small'}
      bodyStyle={{
        height: parent.innerHeight - 235,
        overflow: 'auto',
        width: '99vw'
      }}
    >
      <Splitter>
        <Splitter.Panel collapsible={{ end: true }}>
          <Col>
            <Flex
              align={'center'}
              justify={'flex-start'}
              style={{ margin: '4px 0px', padding: '0 5px' }}
            >
              <Search
                placeholder={l('global.search.text')}
                onChange={onChangeSearch}
                allowClear={true}
                autoFocus
                addonAfter={
                  // 如果选中的 key 长度大于 0 则显示清除按钮 | if the length of the selected key is greater than 0, the clear button is displayed
                  selectedKey.length > 0 && (
                    <Button
                      title={l('devops.joblist.clear.filtertips')}
                      icon={<ClearOutlined />}
                      onClick={() => resetValue()}
                    >
                      {l('devops.joblist.clear.filter')}
                    </Button>
                  )
                }
              />

              <Button.Group>
                <CircleBtn
                  title={l('button.expand-all')}
                  icon={<ArrowsAltOutlined />}
                  onClick={() => setExpandedKeys(getLeafKeyList(data))}
                />
              </Button.Group>
              <Button.Group>
                <CircleBtn
                  title={l('button.collapse-all')}
                  icon={<ShrinkOutlined />}
                  onClick={() => setExpandedKeys([])}
                />
              </Button.Group>
            </Flex>

            {data.length ? (
              <DirectoryTree
                style={{ padding: '0 10px' }}
                className={'treeList'}
                onSelect={(_, info) => onNodeClick(info)}
                expandedKeys={expandedKeys}
                selectedKeys={selectedKey}
                onExpand={(expandedKeys: Key[]) => setExpandedKeys(expandedKeys)}
                treeData={data}
              />
            ) : (
              <Empty className={'code-content-empty'} />
            )}
          </Col>
        </Splitter.Panel>

        <Splitter.Panel defaultSize={'80%'} max={'90%'} min={'20%'}>
          <Flex align={'center'} justify={'center'}>
            <Col style={{ width: '98%' }}>
              <ProTable<Jobs.JobInstance>
                {...PROTABLE_OPTIONS_PUBLIC}
                search={false}
                loading={{ delay: 1000 }}
                rowKey={(record) => record.id}
                columns={jobListColumns}
                params={{
                  isHistory: false,
                  status: statusFilter,
                  step: stepFilter,
                  name: taskFilter,
                  taskId: taskId
                }}
                actionRef={tableRef}
                toolbar={{
                  settings: false,
                  search: { onSearch: (value: string) => setTaskFilter(value) },
                  filter: (
                    <>
                      <Radio.Group
                        defaultValue={undefined}
                        onChange={(e) => setStepFilter(e.target.value)}
                      >
                        <Radio.Button value={undefined} defaultChecked={true}>
                          {l('global.table.lifecycle.all')}
                        </Radio.Button>
                        <Radio.Button value={JOB_LIFE_CYCLE.PUBLISH}>
                          {l('global.table.lifecycle.publish')}
                        </Radio.Button>
                        <Radio.Button value={JOB_LIFE_CYCLE.DEVELOP}>
                          {l('global.table.lifecycle.dev')}
                        </Radio.Button>
                      </Radio.Group>
                    </>
                  ),
                  actions: [
                    <Button icon={<RedoOutlined />} onClick={() => tableRef.current?.reload()} />
                  ]
                }}
                request={async (params, sorter, filter: any) =>
                  queryList(API_CONSTANTS.JOB_INSTANCE, {
                    ...params,
                    sorter,
                    filter
                  })
                }
                expandable={{
                  expandedRowRender: (record) => (
                    <JobHistoryList taskId={record.taskId} key={record.id} />
                  ),
                  expandIcon: ({ expanded, onExpand, record }) => (
                    <Button
                      className={'options-button'}
                      key={`${record.id}_history`}
                      onClick={(e) => onExpand(record, e)}
                      title={l('devops.joblist.history')}
                      icon={<ClockCircleTwoTone twoToneColor={expanded ? '#52c41a' : '#4096ff'} />}
                    />
                  )
                }}
                // scroll={{y: parent.innerHeight - 245 - 150}}
              />
            </Col>
          </Flex>
        </Splitter.Panel>
      </Splitter>
    </ProCard>
  );
};
export default connect(
  ({ Studio, SysConfig }: { Studio: StateType; SysConfig: SysConfigStateType }) => ({
    users: Studio.users,
    projectData: Studio.project.data,
    taskOwnerLockingStrategy: SysConfig.taskOwnerLockingStrategy
  }),
  mapDispatchToProps
)(JobList);
