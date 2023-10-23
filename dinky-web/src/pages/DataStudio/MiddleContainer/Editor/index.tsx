import { getCurrentTab } from '@/pages/DataStudio/function';
import { TASK_VAR_FILTER } from '@/pages/DataStudio/MiddleContainer/Editor/constants';
import DiffModal from '@/pages/DataStudio/MiddleContainer/Editor/DiffModal';
import {
  DataStudioTabsItemType,
  StateType,
  STUDIO_MODEL,
  TaskDataType
} from '@/pages/DataStudio/model';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { connect, useRequest } from '@@/exports';
import { Editor } from '@monaco-editor/react';
import { Spin } from 'antd';
import { editor } from 'monaco-editor';
import React, { useState } from 'react';

export type EditorProps = {
  taskId: number;
};

const CodeEditor: React.FC<EditorProps & any> = (props) => {
  const {
    taskId,
    tabs: { panes, activeKey },
    dispatch
  } = props;

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [diff, setDiff] = useState<any>([]);

  const currentTab = getCurrentTab(panes, activeKey) as DataStudioTabsItemType;
  const currentData = currentTab.params.taskData;

  const loadTask = (cache: TaskDataType, serverParams: TaskDataType) => {
    if (!cache) {
      currentTab.params.taskData = { ...serverParams, useResult: true, maxRowNum: 100 };
      dispatch({ type: STUDIO_MODEL.saveTabs, payload: { ...props.tabs } });
      return;
    }
    const diff: any[] = [];
    Object.keys(serverParams).forEach((key) => {
      if (TASK_VAR_FILTER.includes(key)) {
        cache[key] = serverParams[key];
      } else if (JSON.stringify(serverParams[key]) !== JSON.stringify(cache[key])) {
        diff.push({ key: key, server: serverParams[key], cache: cache[key] });
      }
    });
    if (diff.length > 0) {
      setDiff(diff);
      setIsModalOpen(true);
    }
  };

  const { loading, data } = useRequest(
    { url: API_CONSTANTS.TASK, params: { id: taskId } },
    { onSuccess: (data: any) => loadTask(currentTab.params.taskData, data) }
  );

  const upDateTask = (useServerVersion: boolean) => {
    if (useServerVersion) {
      currentTab.params.taskData = { ...data, useResult: true, maxRowNum: 100 };
      currentTab.isModified = false;
    } else {
      currentTab.isModified = true;
    }
    dispatch({ type: STUDIO_MODEL.saveTabs, payload: { ...props.tabs } });
    setIsModalOpen(false);
  };

  return (
    <Spin spinning={loading}>
      <DiffModal diffs={diff} open={isModalOpen} fileName={currentData?.name} onUse={upDateTask} />
      <Editor
        width={'100%'}
        height={'84vh'}
        value={currentTab?.params?.taskData?.statement}
        language={'sql'}
        options={{
          readOnlyMessage: { value: l('pages.datastudio.editor.onlyread') },
          readOnly: currentData?.step == JOB_LIFE_CYCLE.ONLINE,
          scrollBeyondLastLine: false,
          wordWrap: 'on',
          autoDetectHighContrast: true,
          scrollbar: {
            // Subtle shadows to the left & top. Defaults to true.
            useShadows: false,
            // Defaults to 'auto'
            vertical: 'visible',
            // Defaults to 'auto'
            horizontal: 'visible',
            verticalScrollbarSize: 8,
            horizontalScrollbarSize: 8,
            arrowSize: 30
          }
        }}
        className={'editor-develop'}
        onMount={(editor: editor.IStandaloneCodeEditor) => {
          editor.layout();
          editor.focus();

          editor.onDidChangeCursorPosition((e) => {
            props.footContainer.codePosition = [e.position.lineNumber, e.position.column];
            dispatch({
              type: STUDIO_MODEL.saveFooterValue,
              payload: { ...props.footContainer }
            });
          });
        }}
        onChange={(v) => {
          if (!currentData || !currentTab) {
            return;
          }

          if (typeof v === 'string') {
            currentData.statement = v;
          }
          currentTab.isModified = true;
          dispatch({
            type: STUDIO_MODEL.saveTabs,
            payload: { ...props.tabs }
          });
        }}
        theme={'vs-dark'}
      />
    </Spin>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  footContainer: Studio.footContainer
}))(CodeEditor);
