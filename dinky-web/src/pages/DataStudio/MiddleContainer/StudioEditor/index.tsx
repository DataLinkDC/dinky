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

import CodeEdit from '@/components/CustomEditor/CodeEdit';
import { useEditor } from '@/hooks/useEditor';
import { getCurrentTab } from '@/pages/DataStudio/function';
import { matchLanguage } from '@/pages/DataStudio/MiddleContainer/function';
import { TASK_VAR_FILTER } from '@/pages/DataStudio/MiddleContainer/StudioEditor/constants';
import DiffModal from '@/pages/DataStudio/MiddleContainer/StudioEditor/DiffModal';
import {
  DataStudioTabsItemType,
  StateType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC,
  TaskDataType
} from '@/pages/DataStudio/model';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { registerEditorKeyBindingAndAction } from '@/utils/function';
import { l } from '@/utils/intl';
import { connect, useRequest } from '@@/exports';
import { FullscreenExitOutlined, FullscreenOutlined } from '@ant-design/icons';
import { Monaco } from '@monaco-editor/react';
import { Button, Spin } from 'antd';
import { editor } from 'monaco-editor';
import React, { memo, useEffect, useRef, useState } from 'react';

export type EditorProps = {
  tabsItem: DataStudioTabsItemType;
  height?: number;
};

const StudioEditor: React.FC<EditorProps & connect> = (props) => {
  const { tabsItem, dispatch, height, tabs } = props;

  const editorInstance = useRef<editor.IStandaloneCodeEditor | undefined>();

  useEffect(() => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.querySuggestions,
      payload: {
        enableSchemaSuggestions: false
      }
    });
  }, []);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [diff, setDiff] = useState<any>([]);
  const { fullscreen, setFullscreen } = useEditor();

  const currentData = tabsItem.params.taskData;

  const loadTask = (cache: TaskDataType, serverParams: TaskDataType) => {
    if (!cache) {
      tabsItem.params.taskData = { ...serverParams, useResult: true, maxRowNum: 100 };
      dispatch({ type: STUDIO_MODEL.saveTabs, payload: { ...props.tabs } });
      return;
    }
    const diff: any[] = [];
    Object.keys(serverParams).forEach((key) => {
      if (TASK_VAR_FILTER.includes(key)) {
        cache[key] = serverParams[key];
      } else if (key == 'configJson') {
        const cacheCj = JSON.stringify(cache[key]);
        const serverCj = JSON.stringify(serverParams[key]);
        if (cacheCj != serverCj) {
          diff.push({ key: key, server: serverCj, cache: cacheCj });
        }
      } else if (serverParams[key] != cache[key]) {
        diff.push({ key: key, server: serverParams[key], cache: cache[key] });
      }
    });
    if (diff.length > 0) {
      setDiff(diff);
      setIsModalOpen(true);
    }
  };

  const { loading, data } = useRequest(
    { url: API_CONSTANTS.TASK, params: { id: tabsItem.params.taskId } },
    { onSuccess: (data: any) => loadTask(tabsItem.params.taskData, data) }
  );

  const upDateTask = (useServerVersion: boolean) => {
    if (useServerVersion) {
      tabsItem.params.taskData = { ...data, useResult: true, maxRowNum: 100 };
      tabsItem.isModified = false;
    } else {
      tabsItem.isModified = true;
    }
    dispatch({ type: STUDIO_MODEL.saveTabs, payload: { ...props.tabs } });
    setIsModalOpen(false);
  };

  const editorDidMount = (editor: editor.IStandaloneCodeEditor, monaco: Monaco) => {
    editor.layout();
    editor.focus();
    editorInstance.current = editor;
    // @ts-ignore
    editor['id'] = getCurrentTab(tabs.panes, tabs.activeKey)?.params.taskId;
    tabsItem.monacoInstance = monaco;

    editor.onDidChangeCursorPosition((e) => {
      props.footContainer.codePosition = [e.position.lineNumber, e.position.column];
      dispatch({
        type: STUDIO_MODEL.saveFooterValue,
        payload: { ...props.footContainer }
      });
    });
    registerEditorKeyBindingAndAction(editor);
  };

  const handleEditChange = (v: string | undefined) => {
    if (!currentData || !tabsItem) {
      return;
    }
    if (typeof v === 'string') {
      currentData.statement = v;
    }
    tabsItem.isModified = true;
    dispatch({
      type: STUDIO_MODEL.saveTabs,
      payload: { ...props.tabs }
    });
  };

  return (
    <Spin spinning={loading} delay={600}>
      <div style={{ width: '100%', height: fullscreen ? 'calc(100vh - 50px)' : height }}>
        <DiffModal
          diffs={diff}
          open={isModalOpen}
          language={matchLanguage(tabsItem?.subType)}
          fileName={currentData?.name}
          onUse={upDateTask}
        />
        <CodeEdit
          monacoRef={tabsItem?.monacoInstance}
          code={tabsItem?.params?.taskData?.statement}
          language={matchLanguage(tabsItem?.subType)}
          editorDidMount={editorDidMount}
          onChange={handleEditChange}
          enableSuggestions={true}
          options={{
            readOnlyMessage: { value: l('pages.datastudio.editor.onlyread') },
            readOnly: currentData?.step == JOB_LIFE_CYCLE.PUBLISH,
            scrollBeyondLastLine: false,
            wordWrap: 'on'
          }}
        />
        <div
          style={{
            position: 'absolute',
            top: 15,
            right: '10%',
            boxShadow: '0 0 10px #ccc'
          }}
        >
          {fullscreen ? (
            <Button
              type='text'
              style={{
                zIndex: 999
              }}
              title={l('global.fullScreen.exit')}
              icon={<FullscreenExitOutlined />}
              onClick={() => {
                editorInstance?.current?.layout();
                setFullscreen(false);
              }}
            />
          ) : (
            <Button
              type='text'
              style={{
                zIndex: 999
              }}
              title={l('global.fullScreen')}
              icon={<FullscreenOutlined />}
              onClick={() => {
                editorInstance?.current?.layout();
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
}))(memo(StudioEditor));
