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

import { LoadingBtn } from '@/components/CallBackButton/LoadingBtn';
import CodeShow from '@/components/CustomEditor/CodeShow';
import { DataStudioTabsItemType, StateType, VIEW } from '@/pages/DataStudio/model';
import { handleDeleteOperation } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { JobStatus } from '@/types/Studio/data.d';
import { parseMilliSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { SplitPane } from '@andrewray/react-multi-split-pane';
import { Pane } from '@andrewray/react-multi-split-pane/dist/lib/Pane';
import { CheckOutlined, CloseCircleFilled, LoadingOutlined, XFilled } from '@ant-design/icons';
import { connect, useModel, useRequest } from '@umijs/max';
import { Button, Empty, Space, Typography } from 'antd';
import { DataNode } from 'antd/es/tree';
import DirectoryTree from 'antd/es/tree/DirectoryTree';
import { Key, useEffect, useRef, useState } from 'react';
import { SseData, Topic } from '@/models/UseWebSocketModel';
import WsErrorShow from '@/components/Modal/WsErrorShow/WsErrorShow';

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
  if (node?.children?.length > 0) {
    node.children.forEach((item: ProcessStep) => {
      keys.push(...buildExpandKeys(item));
    });
  }
  return keys;
};

const ConsoleContent = (props: ConsoleProps) => {
  const { tab } = props;
  const refObject = useRef<HTMLDivElement>(null);

  const [selectNode, setSelectNode] = useState<ProcessStep>();
  const [processNode, setProcessNode] = useState<ProcessStep>();
  const [expandedKeys, setExpandedKeys] = useState<Key[]>([]);

  const [showCacheData, setShowCacheData] = useState<boolean>(false);

  const process = `FlinkSubmit/${tab.params.taskId}`;
  const { subscribeTopic } = useModel('UseWebSocketModel', (model: any) => ({
    subscribeTopic: model?.subscribeTopic
  }));

  const { wsState } = useModel('UseWebSocketModel', (model: any) => ({
    wsState: model?.wsState
  }));

  const onUpdate = (data: ProcessStep) => {
    setProcessNode((prevState: any) => {
      //如果key不一致代表重新提交了任务，清空旧状态
      if ((prevState && prevState?.key != data?.key) || !data) {
        setSelectNode(undefined);
      }
      return data;
    });
    setSelectNode((prevState: any) => {
      if (prevState && data?.lastUpdateStep && prevState?.key === data?.lastUpdateStep?.key) {
        //更新当前节点
        return data?.lastUpdateStep;
      } else if (!prevState || prevState?.key === data?.key) {
        //未选择节点状态下选择根节点
        return data;
      }
      return prevState;
    });
  };

  const killProcess = useRequest(
    { url: API_CONSTANTS.KILL_PROCESS, params: { processName: process } },
    { onSuccess: async (res) => onUpdate(res) }
  );

  const refreshProcess = () => {
    subscribeTopic(Topic.PROCESS_CONSOLE, [process], (data: SseData) =>
      onUpdate(data?.data[process])
    );
  };

  useEffect(refreshProcess, []);
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
        {node.status === JobStatus.RUNNING && <LoadingOutlined />}
        {node.status === JobStatus.FINISHED && (
          <CheckOutlined style={{ color: 'green', fontWeight: 'bold' }} />
        )}
        {node.status === JobStatus.FAILED && (
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

  if (!wsState?.wsOnReady && !showCacheData) {
    return (
      <WsErrorShow
        state={wsState}
        extra={
          <Button onClick={() => setShowCacheData(true)}>
            {l('devops.jobinfo.recently.job.status')}
          </Button>
        }
      />
    );
  }

  return (
    <div style={{ height: props.height - VIEW.leftMargin }}>
      <SplitPane
        split={'vertical'}
        defaultSizes={[100, 500]}
        minSize={100}
        className={'split-pane'}
      >
        <Pane
          className={'split-pane'}
          forwardRef={refObject}
          minSize={100}
          size={100}
          split={'horizontal'}
        >
          {processNode ? (
            <DirectoryTree
              className={'treeList'}
              showIcon={false}
              titleRender={renderTitle}
              onSelect={onSelect}
              treeData={[processNode]}
              expandedKeys={expandedKeys}
              expandAction={'doubleClick'}
              onExpand={handleExpand}
            />
          ) : (
            <Empty />
          )}
        </Pane>

        <Pane
          className={'split-pane'}
          forwardRef={refObject}
          minSize={100}
          size={100}
          split={'horizontal'}
        >
          <CodeShow
            code={selectNode?.log ? selectNode.log : ''}
            height={props.height - VIEW.leftMargin}
            language={'javalog'}
            lineNumbers={'off'}
            enableMiniMap
            enableAutoScroll
            showFloatButton
            clearContent={async () => {
              const boolean = await handleDeleteOperation(
                API_CONSTANTS.PROCESS_LOG_CLEAR,
                { processName: process },
                l('rc.ds.detail.tag.console.clear.log')
              );
              if (boolean) refreshProcess();
            }}
            btnExtraContent={
              <LoadingBtn
                key={'kill-process'}
                click={async () => await killProcess.run()}
                icon={
                  <XFilled
                    style={{ color: processNode?.status === JobStatus.RUNNING ? 'red' : 'gray' }}
                  />
                }
              />
            }
          />
        </Pane>
      </SplitPane>
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console
}))(ConsoleContent);
