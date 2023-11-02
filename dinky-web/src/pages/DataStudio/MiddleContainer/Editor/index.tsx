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

import { useEditor } from '@/hooks/useEditor';
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
import { convertCodeEditTheme } from '@/utils/function';
import { l } from '@/utils/intl';
import { connect, useRequest } from '@@/exports';
import { FullscreenExitOutlined, FullscreenOutlined } from '@ant-design/icons';
import { Editor } from '@monaco-editor/react';
import { Button, Spin } from 'antd';
import { editor, KeyCode, KeyMod } from 'monaco-editor';
import React, { useState } from 'react';
import { format } from 'sql-formatter';

export type EditorProps = {
  taskId: number;
  height?: number;
};

const CodeEditor: React.FC<EditorProps & any> = (props) => {
  const {
    taskId,
    tabs: { panes, activeKey },
    dispatch,
    height
  } = props;

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [diff, setDiff] = useState<any>([]);
  const { fullscreen, setFullscreen } = useEditor();
  const [editorIns, setEditorIns] = useState<editor.IStandaloneCodeEditor>(null);

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
    <Spin spinning={loading} delay={600}>
      <div style={{ width: '100%', height: fullscreen ? 'calc(100vh - 50px)' : height }}>
        <DiffModal
          diffs={diff}
          open={isModalOpen}
          fileName={currentData?.name}
          onUse={upDateTask}
        />
        <Editor
          value={currentTab?.params?.taskData?.statement}
          language={'sql'}
          options={{
            readOnlyMessage: { value: l('pages.datastudio.editor.onlyread') },
            readOnly: currentData?.step == JOB_LIFE_CYCLE.PUBLISH,
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
            },
            automaticLayout: true
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

            editor.addCommand(KeyMod.Alt | KeyCode.Digit3, () => {
              editor?.trigger('anyString', 'editor.action.formatDocument');
              editor.setValue(format(editor.getValue()));
            });
            setEditorIns(editor);
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
          theme={convertCodeEditTheme()}
        ></Editor>
        <div
          style={{
            position: 'absolute',
            top: 20,
            right: '7%'
          }}
        >
          {fullscreen ? (
            <Button
              type='text'
              style={{
                color: '#fff'
              }}
              icon={<FullscreenExitOutlined />}
              onClick={() => {
                editorIns.layout();
                setFullscreen(false);
              }}
            />
          ) : (
            <Button
              type='text'
              style={{
                color: '#fff'
              }}
              icon={<FullscreenOutlined />}
              onClick={() => {
                editorIns.layout();
                setFullscreen(true);
              }}
            />
          )}
        </div>
      </div>
    </Spin>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  footContainer: Studio.footContainer
}))(CodeEditor);
