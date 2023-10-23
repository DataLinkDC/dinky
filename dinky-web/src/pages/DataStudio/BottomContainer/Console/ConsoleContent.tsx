import CodeShow from '@/components/CustomEditor/CodeShow';
import MovableSidebar from '@/components/Sidebar/MovableSidebar';
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
}
const ConsoleContent = (props: ConsoleProps) => {
  const { tab } = props;

  const [selectNode, setSelectNode] = useState<ProcessStep>();
  const [processNode, setProcessNode] = useState<ProcessStep>();

  const process = `FlinkSubmit/${tab.params.taskId}`;
  const topic = `${SSE_TOPIC.PROCESS_CONSOLE}/${process}`;
  const { subscribeTopic } = useModel('Sse', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));

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
  const onSelect = (_selectedKeys: Key[], info: { node: ProcessStep }) => setSelectNode(info.node);

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
        style={{ borderInlineStart: `1px`, float: 'left' }}
      >
        {processNode ? (
          <DirectoryTree
            className={'treeList'}
            showIcon={false}
            titleRender={renderTitle}
            onSelect={onSelect}
            treeData={[processNode]}
            expandAction={'doubleClick'}
            defaultExpandParent
            defaultExpandAll
          />
        ) : (
          <Empty />
        )}
      </MovableSidebar>
      <div>
        <CodeShow
          code={selectNode?.log ? selectNode.log : ''}
          height={props.height - 53}
          language={'kotlin'}
          lineNumbers={'off'}
          showFloatButton
        />
      </div>
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console
}))(ConsoleContent);
