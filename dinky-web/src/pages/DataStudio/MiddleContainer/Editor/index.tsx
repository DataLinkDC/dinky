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
import { TASK_VAR_FILTER } from '@/pages/DataStudio/MiddleContainer/Editor/constants';
import DiffModal from '@/pages/DataStudio/MiddleContainer/Editor/DiffModal';
import {
  DataStudioTabsItemType,
  StateType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC,
  TaskDataType
} from '@/pages/DataStudio/model';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { DIALECT } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { connect, useRequest } from '@@/exports';
import { FullscreenExitOutlined, FullscreenOutlined } from '@ant-design/icons';
import { Monaco } from '@monaco-editor/react';
import { Button, Spin } from 'antd';
import { editor, KeyCode, KeyMod } from 'monaco-editor';
import React, { useEffect, useRef, useState } from 'react';
import { format } from 'sql-formatter';

export type EditorProps = {
  tabsItem: DataStudioTabsItemType
  height?: number;
};

const CodeEditor: React.FC<EditorProps & any> = (props) => {
  const {
    tabsItem,
    dispatch,
    height
  } = props;

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
  const editorInstance = useRef<editor.IStandaloneCodeEditor | any>();
  const monacoInstance = useRef<Monaco | any>();

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
    monacoInstance.current = monaco;
    editorInstance.current = editor;

    editor.onDidChangeCursorPosition((e) => {
      props.footContainer.codePosition = [e.position.lineNumber, e.position.column];
      dispatch({
        type: STUDIO_MODEL.saveFooterValue,
        payload: { ...props.footContainer }
      });
    });

    editor.addCommand(KeyMod.Alt | KeyCode.Digit3, () => {
      editor?.trigger('anyString', 'editor.action.formatDocument', '');
      editor.setValue(format(editor.getValue()));
    });
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
          language={
            tabsItem?.params?.taskData?.dialect?.toLowerCase() === DIALECT.FLINK_SQL
              ? 'flinksql'
              : 'sql'
          }
          fileName={currentData?.name}
          onUse={upDateTask}
        />
        <CodeEdit
          monacoRef={monacoInstance}
          editorRef={editorInstance}
          code={tabsItem?.params?.taskData?.statement}
          language={
            tabsItem?.params?.taskData?.dialect?.toLowerCase() === DIALECT.FLINK_SQL
              ? 'flinksql'
              : 'sql'
          }
          // language={'sql'}
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
}))(CodeEditor);
