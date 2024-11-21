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

import { CenterTab, DataStudioState } from '@/pages/DataStudio/model';
import { mapDispatchToProps } from '@/pages/DataStudio/DvaFunction';
import { Flex, Tabs, TabsProps, TreeDataNode } from 'antd';
import { Panel, PanelGroup } from 'react-resizable-panels';
import DirectoryTree from 'antd/es/tree/DirectoryTree';
import './index.less';
import React, { Key, useEffect, useMemo, useState } from 'react';
import {
  ApartmentOutlined,
  ArrowsAltOutlined,
  AuditOutlined,
  CodeOutlined,
  HistoryOutlined,
  MonitorOutlined,
  PartitionOutlined,
  ShrinkOutlined,
  TableOutlined
} from '@ant-design/icons';
import RunToolBarButton from '@/pages/DataStudio/components/RunToolBarButton';
import CusPanelResizeHandle from '@/pages/DataStudio/components/CusPanelResizeHandle';
import Output from '@/pages/DataStudio/Toolbar/Service/Output';
import ExecutionHistory from '@/pages/DataStudio/Toolbar/Service/ExecutionHistory';
import { KeepAlive } from 'react-activation';
import { DataStudioActionType } from '@/pages/DataStudio/data.d';
import Explain from '@/pages/DataStudio/Toolbar/Service/Explain';
import FlinkGraph from '@/pages/DataStudio/Toolbar/Service/FlinkGraph';
import Result from '@/pages/DataStudio/Toolbar/Service/Result';
import { getTabIcon } from '@/pages/DataStudio/function';
import { DIALECT } from '@/services/constants';
import { TableData } from '@/pages/DataStudio/Toolbar/Service/TableData';
import { isSql } from '@/pages/DataStudio/utils';
import { useAsyncEffect } from 'ahooks';
import { sleep } from '@antfu/utils';
import { l } from '@/utils/intl';
import { assert } from '@/pages/DataStudio/utils';
import { connect } from '@umijs/max';
import { Lineage } from '@/pages/DataStudio/Toolbar/Service/Lineage';

const Service = (props: { showDesc: boolean; tabs: CenterTab[]; action: any }) => {
  const {
    showDesc,
    tabs,
    action: { actionType, params }
  } = props;
  const [selectedKey, setSelectedKey] = useState<Key[]>([]);
  const [taskItems, setTaskItems] = useState<Record<string, TabsProps['items']>>({});
  const [tabActiveKey, setTabActiveKey] = useState<Record<string, string>>({});
  const [treeData, setTreeData] = useState<TreeDataNode[]>([]);
  const [expandKeys, setExpandKeys] = useState<Key[]>([]);

  const getAllNodeKeys = (data: TreeDataNode[], keys: Key[] = []) => {
    data.forEach((item) => {
      keys.push(item.key);
      if (item.children) {
        getAllNodeKeys(item.children, keys);
      }
    });
    return keys;
  };
  const router = useMemo(() => {
    if (!params?.data) {
      return {};
    }
    return {
      [DataStudioActionType.TASK_RUN_CHECK]: {
        key: actionType,
        label: l('button.check'),
        icon: <AuditOutlined />,
        children: <Explain data={params.data} />
      },
      [DataStudioActionType.TASK_RUN_DAG]: {
        key: actionType,
        label: l('button.graph'),
        icon: <ApartmentOutlined />,
        children: <FlinkGraph data={params.data} />
      },
      [DataStudioActionType.TASK_RUN_LINEAGE]: {
        key: actionType,
        label: l('menu.datastudio.lineage'),
        icon: <PartitionOutlined />,
        children: <Lineage data={params.data} />
      }
    };
  }, [actionType, params?.data]);
  const expandAll = () => {
    setExpandKeys(getAllNodeKeys(treeData));
  };
  useAsyncEffect(async () => {
    if (!actionType || !params || !params.taskId) {
      return;
    }
    const taskItem = taskItems[params.taskId] ?? [];
    setSelectedKey([params.taskId]);

    if (router.hasOwnProperty(actionType)) {
      // 这里是防止tab没被展开，导致图获取尺寸出现的bug
      if (
        !tabActiveKey[params.taskId] &&
        (actionType === DataStudioActionType.TASK_RUN_DAG || DataStudioActionType.TASK_RUN_LINEAGE)
      ) {
        setTabActiveKey((prevState) => ({
          ...prevState,
          [params.taskId]: DataStudioActionType.TASK_RUN_SUBMIT
        }));
        await sleep(50);
      }
      // @ts-ignore
      const route = router[actionType];
      if (!taskItem.some((item) => item.key === actionType)) {
        setTaskItems((prevState) => ({
          ...prevState,
          [params.taskId]: [...taskItem, route]
        }));
      } else {
        setTaskItems((prevState) => {
          const item = prevState[params.taskId]!!;
          item.find((item) => item.key === actionType)!!.children = route.children;
          return { ...prevState };
        });
      }
      setTabActiveKey((prevState) => ({
        ...prevState,
        [params.taskId]: actionType
      }));
    } else {
      if (
        actionType === DataStudioActionType.TASK_RUN_SUBMIT ||
        actionType === DataStudioActionType.TASK_RUN_DEBUG
      ) {
        setTabActiveKey((prevState) => ({
          ...prevState,
          [params.taskId]: DataStudioActionType.TASK_RUN_SUBMIT
        }));
      } else if (actionType === DataStudioActionType.TASK_PREVIEW_RESULT) {
        setTabActiveKey((prevState) => ({
          ...prevState,
          [params.taskId]: DataStudioActionType.TASK_PREVIEW_RESULT
        }));
      }
    }
  }, [props.action]);

  useEffect(() => {
    const treeData: TreeDataNode[] = [
      {
        title: 'Task',
        key: 'Task',
        children: []
      }
    ];
    tabs.forEach((tab) => {
      if (tab.tabType === 'task') {
        // 查找到对应的task，并添加，不存在的节点，添加
        // 1. 查找到对应的Task
        // 2. 查找到对应的FlinkSql
        // 3. 查找到对应的task

        treeData.forEach((node) => {
          const dialect = tab.params.dialect;
          if (
            assert(dialect, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes') ||
            isSql(dialect)
          ) {
            const icon = getTabIcon(dialect, 20);
            if (node.key === 'Task') {
              let currentDialectTree = node.children!!.find(
                (child) => child.key === dialect
              ) as TreeDataNode;
              if (!currentDialectTree) {
                node.children!!.push({ title: dialect, key: dialect, icon: icon, children: [] });
                currentDialectTree = node.children!!.find(
                  (child) => child.key === dialect
                ) as TreeDataNode;
              }
              currentDialectTree.children!!.push({
                title: tab.title,
                key: tab.params.taskId,
                icon: icon,
                isLeaf: true
              });
            }
          }
        });
      }
    });
    setTreeData(treeData);
    setExpandKeys(getAllNodeKeys(treeData));
  }, [tabs]);

  const renderContent = () => {
    if (selectedKey.length === 1) {
      const taskId = selectedKey[0] as number;
      const taskParams = tabs.find((tab) => tab.params.taskId === taskId)?.params;
      if (!taskParams) {
        setSelectedKey([]);
        setTaskItems((prevState) => {
          const newState = { ...prevState };
          delete newState[taskId];
          return newState;
        });
        setTabActiveKey((prevState) => {
          const newState = { ...prevState };
          delete newState[taskId];
          return newState;
        });
      }
      const items: TabsProps['items'] = [
        {
          key: DataStudioActionType.TASK_RUN_SUBMIT,
          label: l('button.output'),
          icon: <CodeOutlined />,
          children: <Output taskId={taskId} />
        },
        {
          key: DataStudioActionType.TASK_PREVIEW_RESULT,
          label: l('menu.datastudio.result'),
          icon: <MonitorOutlined />,
          children: <Result taskId={taskId} action={props.action} dialect={taskParams?.dialect} />
        }
      ];
      if (assert(taskParams?.dialect, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes')) {
        items.push({
          key: 'history',
          label: l('menu.datastudio.history'),
          icon: <HistoryOutlined />,
          children: <ExecutionHistory taskId={taskId} dialect={taskParams?.dialect}/>
        });
      }
      if (assert(taskParams?.dialect, [DIALECT.FLINK_SQL], true, 'includes')) {
        items.push({
          key: 'tableData',
          label: l('menu.datastudio.table-data'),
          icon: <TableOutlined />,
          children: <TableData statement={taskParams?.statement} />
        });
      }

      const taskItem = taskItems[taskId] ?? [];
      return (
        <Tabs
          activeKey={tabActiveKey[taskId]}
          items={[...items, ...taskItem]}
          size={'small'}
          onChange={(activeKey) => {
            setTabActiveKey((prevState) => ({
              ...prevState,
              [taskId]: activeKey
            }));
          }}
          style={{ height: '100%' }}
        />
      );
    }
  };
  return (
    <PanelGroup direction={'horizontal'}>
      <Panel defaultSize={20} style={{ display: 'flex', flexDirection: 'column', padding: 10 }}>
        <Flex justify={'right'}>
          <RunToolBarButton
            showDesc={showDesc}
            desc={l('button.collapse-all')}
            icon={<ShrinkOutlined />}
            sleepTime={100}
            onClick={async () => {
              setExpandKeys([]);
            }}
          />
          <RunToolBarButton
            showDesc={showDesc}
            desc={l('button.expand-all')}
            icon={<ArrowsAltOutlined />}
            sleepTime={100}
            onClick={async () => {
              expandAll();
            }}
          />
        </Flex>
        <DirectoryTree
          selectedKeys={selectedKey}
          expandedKeys={expandKeys}
          treeData={treeData}
          onExpand={setExpandKeys}
          onSelect={(selectedKeys, { node }) => {
            node.isLeaf && setSelectedKey(selectedKeys);
          }}
          blockNode
        />
      </Panel>

      {selectedKey && selectedKey.length > 0 && selectedKey[0] !== undefined && (
        <>
          <CusPanelResizeHandle />
          <Panel style={{ paddingInline: 10 }}>
            <KeepAlive cacheKey={'service:' + selectedKey[0]} autoFreeze={true}>
              {renderContent()}
            </KeepAlive>
          </Panel>
        </>
      )}
    </PanelGroup>
  );
};

export default connect(
  ({ DataStudio }: { DataStudio: DataStudioState }) => ({
    project: DataStudio.toolbar.project,
    action: DataStudio.action,
    showDesc: DataStudio.toolbar.showDesc,
    tabs: DataStudio.centerContent.tabs
  }),
  mapDispatchToProps
)(Service);
