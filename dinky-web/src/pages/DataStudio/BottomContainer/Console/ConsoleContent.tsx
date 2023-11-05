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

import CodeShow from '@/components/CustomEditor/CodeShow';
import MovableSidebar from '@/components/Sidebar/MovableSidebar';
import useThemeValue from '@/hooks/useThemeValue';
import { SseData } from '@/models/Sse';
import { DataStudioTabsItemType, StateType } from '@/pages/DataStudio/model';
import { SSE_TOPIC } from '@/pages/DevOps/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { parseMilliSecondStr } from '@/utils/function';
import { connect, useModel, useRequest } from '@@/exports';
import { CheckOutlined, CloseCircleFilled, LoadingOutlined } from '@ant-design/icons';
import { Empty, Space, Typography } from 'antd';
import { DataNode } from 'antd/es/tree';
import DirectoryTree from 'antd/es/tree/DirectoryTree';
import { Key, useEffect, useState } from 'react';

const { Text } = Typography;

export type ConsoleProps = {
  tab: DataStudioTabsItemType;
  height: number;
};

export interface ProcessStep extends DataNode {
  status: string;
  type: string;
  startTime: string;
  endTime: string;
  time: number;
  log: string;
  lastUpdateStep: ProcessStep;
  children: ProcessStep[];
}

const buildExpandKeys = (node: ProcessStep) => {
  const keys: Key[] = [];
  keys.push(node.key);
  if (node.children.length > 0) {
    node.children.forEach((item: ProcessStep) => {
      keys.push(...buildExpandKeys(item));
    });
  }
  return keys;
};

const ConsoleContent = (props: ConsoleProps) => {
  const { tab } = props;

  const [selectNode, setSelectNode] = useState<ProcessStep>();
  const [processNode, setProcessNode] = useState<ProcessStep>();
  const [expandedKeys, setExpandedKeys] = useState<Key[]>([]);

  const process = `FlinkSubmit/${tab.params.taskId}`;
  const topic = `${SSE_TOPIC.PROCESS_CONSOLE}/${process}`;
  const { subscribeTopic } = useModel('Sse', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));
  const themeValue = useThemeValue();

  const onUpdate = (data: ProcessStep) => {
    setProcessNode((prevState: any) => {
      //如果key不一致代表重新提交了任务，清空旧状态
      if (prevState && prevState.key != data.key) {
        setSelectNode(undefined);
      }
      return data;
    });
    setSelectNode((prevState: any) => {
      if (prevState && prevState.key == data.lastUpdateStep.key) {
        //更新当前节点
        return data.lastUpdateStep;
      } else if (!prevState || prevState.key == data.key) {
        //未选择节点状态下选择根节点
        return data;
      }
      return prevState;
    });
  };

  useRequest(
    { url: API_CONSTANTS.PROCESS_LOG, params: { processName: process } },
    { onSuccess: async (res) => onUpdate(res) }
  );
  useEffect(() => subscribeTopic([topic], (data: SseData) => onUpdate(data.data)), []);
  const onSelect = (
    _selectedKeys: Key[],
    info: {
      node: ProcessStep;
    }
  ) => setSelectNode(info.node);

  const renderTitle = (node: any) => {
    const startDate = new Date(node.startTime);
    const endDate = new Date();
    const duration = node.time ? node.time : endDate.getTime() - startDate.getTime();
    return (
      <Space size={5}>
        {node.status == 'RUNNING' && <LoadingOutlined />}
        {node.status == 'FINISHED' && (
          <CheckOutlined style={{ color: 'green', fontWeight: 'bold' }} />
        )}
        {node.status == 'FAILED' && (
          <CloseCircleFilled style={{ color: 'red', fontWeight: 'bold' }} />
        )}
        <Text>{node.title}</Text>
        <Text type={'secondary'} style={{ marginLeft: 'auto' }}>
          {parseMilliSecondStr(duration)}
        </Text>
      </Space>
    );
  };

  useEffect(() => {
    if (processNode) {
      setExpandedKeys(buildExpandKeys(processNode));
    }
  }, [processNode]);

  const handleExpand = (expandedKeys: Key[]) => {
    setExpandedKeys(expandedKeys);
  };

  return (
    <div style={{ overflow: 'hidden' }}>
      <MovableSidebar
        defaultSize={{
          width: 300,
          height: props.height - 53
        }}
        minWidth={20}
        visible={true}
        enable={{ right: true }}
        headerVisible={false}
        style={{
          float: 'left',
          borderInlineEnd: `1px solid ${themeValue.borderColor}`,
          paddingInline: 10
        }}
      >
        {processNode ? (
          <DirectoryTree
            className={'treeList'}
            showIcon={false}
            titleRender={renderTitle}
            onSelect={onSelect}
            treeData={[processNode]}
            expandedKeys={expandedKeys}
            expandAction={"doubleClick"}
            onExpand={handleExpand}
          />
        ) : (
          <Empty />
        )}
      </MovableSidebar>
      <div style={{ display: 'inline', width: 1500 }}>
        <CodeShow
          code={selectNode?.log ? selectNode.log : ''}
          height={props.height - 53}
          language={'javalog'}
          lineNumbers={'off'}
          autoWrap={'off'}
          //TODO 按钮显示有问题，先注释
          // showFloatButton
        />
      </div>
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console
}))(ConsoleContent);
