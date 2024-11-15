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

import { CenterTab, DataStudioState } from '@/pages/DataStudioNew/model';
import { Button, Col, Divider, Flex, Row, Skeleton, TabsProps } from 'antd';
import '../index.less';
import React, { memo, useCallback, useEffect, useRef, useState } from 'react';
import { registerEditorKeyBindingAndAction } from '@/utils/function';
import { Monaco } from '@monaco-editor/react';
import { Panel, PanelGroup } from 'react-resizable-panels';
import {
  ApartmentOutlined,
  AuditOutlined,
  BugOutlined,
  CaretRightOutlined,
  ClearOutlined,
  CloseOutlined,
  EnvironmentOutlined,
  FullscreenExitOutlined,
  FullscreenOutlined,
  FundOutlined,
  MergeCellsOutlined,
  PartitionOutlined,
  PauseOutlined,
  RocketOutlined,
  RotateRightOutlined,
  SafetyCertificateOutlined,
  SaveOutlined
} from '@ant-design/icons';
import RunToolBarButton from '@/pages/DataStudioNew/components/RunToolBarButton';
import { connect, useModel } from '@umijs/max';
import CusPanelResizeHandle from '@/pages/DataStudioNew/components/CusPanelResizeHandle';
import { ProForm, ProFormInstance } from '@ant-design/pro-components';
import { useAsyncEffect, useFullscreen } from 'ahooks';
import { SelectFlinkEnv } from '@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectFlinkEnv';
import { SelectFlinkRunMode } from '@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectFlinkRunMode';
import { mapDispatchToProps } from '@/pages/DataStudioNew/DvaFunction';
import { TaskInfo } from '@/pages/DataStudioNew/CenterTabContent/SqlTask/TaskInfo';
import { HistoryVersion } from '@/pages/DataStudioNew/CenterTabContent/SqlTask/HistoryVersion';
import { FlinkTaskRunType, StudioLineageParams, TaskState } from '@/pages/DataStudioNew/type';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { debounce } from 'lodash';
import {
  cancelTask,
  changeTaskLife,
  debugTask,
  executeSql,
  explainSql,
  getJobPlan,
  getTaskDetails
} from '@/pages/DataStudioNew/service';
import { l } from '@/utils/intl';
import { editor } from 'monaco-editor';
import { DataStudioActionType } from '@/pages/DataStudioNew/data.d';
import { getDataByParams, handlePutDataJson, queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs, LineageDetailInfo } from '@/types/DevOps/data';
import { isStatusDone, lockTask, matchLanguage } from '@/pages/DataStudioNew/function';
import { PushpinIcon } from '@/components/Icons/CustomIcons';
import { assert, isSql } from '@/pages/DataStudioNew/utils';
import { DIALECT } from '@/services/constants';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import CodeEdit from '@/components/CustomEditor/CodeEdit';
import DiffModal from '@/pages/DataStudioNew/CenterTabContent/SqlTask/DiffModal';
import TaskConfig from '@/pages/DataStudioNew/CenterTabContent/SqlTask/TaskConfig';
import SelectDb from '@/pages/DataStudioNew/CenterTabContent/RunToolbar/SelectDb';

export type FlinkSqlProps = {
  showDesc: boolean;
  tabData: CenterTab;
  activeTab?: string | undefined;
};
const toolbarSize = 40;
const dividerHeight = 24;

export const SqlTask = memo((props: FlinkSqlProps & any) => {
  const {
    showDesc,
    tempData,
    updateAction,
    updateProject,
    updateCenterTab,
    activeTab,
    enabledDs,
    taskOwnerLockingStrategy,
    dsConfig,
    users,
    tabs
  } = props;
  const { params, title, id } = props.tabData as CenterTab;
  const containerRef = useRef<HTMLDivElement>(null);
  const editorInstance = useRef<editor.IStandaloneCodeEditor>(null);
  const [codeEditorWidth, setCodeEditorWidth] = useState(0);

  const [selectRightToolbar, setSelectRightToolbar] = useState<string | undefined>(undefined);

  const [loading, setLoading] = useState<boolean>(true);
  const [originStatementValue, setOriginStatementValue] = useState<string>('');
  const [currentState, setCurrentState] = useState<TaskState>({
    alertGroupId: -1,
    batchModel: false,
    configJson: {
      udfRefer: [],
      customConfig: {}
    },
    databaseId: 0,
    firstLevelOwner: 0,
    fragment: false,
    note: '',
    parallelism: 0,
    savePointPath: '',
    savePointStrategy: 0,
    secondLevelOwners: [],
    type: 'local',
    taskId: params.taskId,
    statement: '',
    name: '',
    dialect: '',
    step: 0,
    envId: -1,
    versionId: 0,
    createTime: new Date(),
    updateTime: new Date(),
    status: ''
  });
  // 代码恢复
  const [openDiffModal, setOpenDiffModal] = useState(false);
  const [diff, setDiff] = useState<any>([]);

  const formRef = useRef<ProFormInstance>();
  const [isFullscreen, { enterFullscreen, exitFullscreen }] = useFullscreen(containerRef);

  const { initialState, setInitialState } = useModel('@@initialState');

  useAsyncEffect(async () => {
    const taskDetail = await getTaskDetails(params.taskId);
    if (taskDetail) {
      const statement = params.statement ?? taskDetail.statement;
      const newParams = { ...taskDetail, taskId: params.taskId, statement };
      // @ts-ignore
      setCurrentState(newParams);
      updateCenterTab({ ...props.tabData, params: newParams });

      setOriginStatementValue(statement);
      if (params?.statement && params?.statement !== taskDetail.statement) {
        setDiff([{ key: 'statement', server: taskDetail.statement, cache: params.statement }]);
        setOpenDiffModal(true);
        updateCenterTab({
          ...props.tabData,
          isUpdate: true,
          params: { ...newParams }
        });
      }
    }
    setLoading(false);
  }, []);

  // 数据初始化
  useEffect(() => {
    if (!containerRef.current) {
      return () => {};
    }
    // 监控布局宽度高度变化，重新计算树的高度
    const element = containerRef.current!!;
    const observer = new ResizeObserver((entries) => {
      if (entries?.length === 1) {
        // 这里节点理应为一个，减去的高度是为搜索栏的高度
        setCodeEditorWidth(entries[0].contentRect.width);
      }
    });
    observer.observe(element);
    return () => {
      observer.unobserve(element);
    };
  }, [loading]);

  const editorDidMount = (editor: editor.IStandaloneCodeEditor, monaco: Monaco) => {
    editor.layout();
    editor.focus();
    // @ts-ignore
    editorInstance.current = editor;
    // @ts-ignore
    editor['id'] = currentState.taskId;
    editor.onDidChangeCursorPosition((e) => {});
    registerEditorKeyBindingAndAction(editor);
  };

  const updateTask = (useServerVersion: boolean) => {
    const statement = useServerVersion ? diff[0].server : diff[0].cache;
    if (useServerVersion) {
      updateCenterTab({
        ...props.tabData,
        isUpdate: false,
        params: { ...currentState, statement }
      });
    }
    setCurrentState((prevState) => ({ ...prevState, statement }));
    setOriginStatementValue(statement);

    setOpenDiffModal(false);
  };

  const getFlinkMode = () => {
    if (currentState.type === 'local') {
      return ['local'];
    }
    if (
      currentState.type === 'standalone' ||
      currentState.type === 'kubernetes-session' ||
      currentState.type === 'yarn-session'
    ) {
      return [currentState.type, currentState.clusterId];
    }
    return [currentState.type, currentState.clusterConfigurationId];
  };
  const onEditorChange = (value: string | undefined, ev: editor.IModelContentChangedEvent) => {
    updateCenterTab({
      ...props.tabData,
      isUpdate: originStatementValue !== value,
      params: { ...currentState, statement: value ?? '' }
    });
    setCurrentState((prevState) => ({ ...prevState, statement: value ?? '' }));
  };

  const onValuesChange = (changedValues: any, allValues: TaskState) => {
    if ('flinkMode' in allValues) {
      const mode = (allValues['flinkMode'] as [string, number])[0] as FlinkTaskRunType;
      if (mode === 'local') {
        allValues.clusterId = null;
        allValues.clusterConfigurationId = null;
      } else if (
        mode === 'standalone' ||
        mode === 'kubernetes-session' ||
        mode === 'yarn-session'
      ) {
        allValues.clusterId = (allValues['flinkMode'] as [string, number])[1];
        allValues.clusterConfigurationId = null;
      } else {
        const id = (allValues['flinkMode'] as [string, number])[1];
        allValues.clusterId = null;
        allValues.clusterConfigurationId = id;
      }
      allValues.type = mode;
    }
    setCurrentState({ ...currentState, ...allValues });
    updateCenterTab({ ...props.tabData, params: { ...currentState, ...allValues } });
  };
  const hotKeyConfig = { enable: activeTab === id };

  const getActiveTab = () => {
    return tabs.find((item: CenterTab) => {
      if (item.id === activeTab) {
        return item;
      }
    });
  };

  const isLockTask = lockTask(
    getActiveTab()?.params?.firstLevelOwner,
    getActiveTab()?.params?.secondLevelOwners,
    initialState?.currentUser?.user,
    taskOwnerLockingStrategy
  );

  const rightToolbarItem: TabsProps['items'] = [];
  if (
    isSql(currentState.dialect) ||
    assert(currentState.dialect, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes')
  ) {
    rightToolbarItem.push({
      label: l('button.config'),
      key: 'config',
      children: (
        <TaskConfig
          tempData={tempData}
          data={currentState}
          onValuesChange={debounce(onValuesChange, 500)}
          isLockTask={isLockTask}
        />
      )
    });
  }
  if (assert(currentState.dialect, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes')) {
    rightToolbarItem.push({
      label: l('menu.datastudio.historyVision'),
      key: 'historyVersion',
      children: (
        <HistoryVersion
          taskId={currentState.taskId}
          statement={currentState.statement}
          updateTime={currentState.updateTime}
        />
      )
    });
  }

  rightToolbarItem.push({
    label: l('global.info'),
    key: 'info',
    children: <TaskInfo params={{ ...getActiveTab()?.params }} users={users} />
  });

  const handleSave = useCallback(async () => {
    // await putTask(currentState)
    await handlePutDataJson(API_CONSTANTS.TASK, currentState);
    updateCenterTab({ ...props.tabData, isUpdate: false });
  }, [currentState, updateCenterTab, props.tabData]);

  const handleCheck = useCallback(async () => {
    const res = await explainSql(
      l('pages.datastudio.editor.checking', '', { jobName: currentState?.name }),
      { ...currentState }
    );
    updateAction({
      actionType: DataStudioActionType.TASK_RUN_CHECK,
      params: {
        taskId: params.taskId,
        data: res.data
      }
    });
  }, [currentState, updateAction]);
  const handleDAG = useCallback(async () => {
    const res = await getJobPlan(l('pages.datastudio.editor.explain.tip'), currentState);
    updateAction({
      actionType: DataStudioActionType.TASK_RUN_DAG,
      params: {
        taskId: params.taskId,
        data: res.data
      }
    });
  }, [currentState, updateAction]);

  const handleLineage = useCallback(async () => {
    const { type, dialect, databaseId, statement, envId, fragment, taskId } = currentState;
    const params: StudioLineageParams = {
      type: 1, // todo: 暂时写死 ,后续优化
      dialect: dialect,
      envId: envId ?? -1,
      fragment: fragment,
      statement: statement,
      statementSet: true,
      databaseId: databaseId ?? 0,
      variables: {},
      taskId: taskId
    };
    const data = (await getDataByParams(
      API_CONSTANTS.STUDIO_GET_LINEAGE,
      params
    )) as LineageDetailInfo;
    updateAction({
      actionType: DataStudioActionType.TASK_RUN_LINEAGE,
      params: {
        taskId: params.taskId,
        data: data
      }
    });
  }, [currentState, updateAction]);

  const handleSubmit = useCallback(async () => {
    await handleSave();
    updateAction({
      actionType: DataStudioActionType.TASK_RUN_SUBMIT,
      params: {
        taskId: currentState.taskId,
        envId: currentState.envId
      }
    });
    const result = await executeSql(
      l('pages.datastudio.editor.submitting', '', { jobName: title }),
      currentState.taskId
    );
    if (result.success) {
      setCurrentState((prevState) => {
        return {
          ...prevState,
          status: result.data.status === 'SUCCESS' ? 'RUNNING' : result.data.status
        };
      });
      if (isSql(currentState.dialect) && result?.data?.result?.success) {
        updateAction({
          actionType: DataStudioActionType.TASK_PREVIEW_RESULT,
          params: {
            taskId: currentState.taskId,
            dialect: currentState.dialect,
            columns: result.data.result.columns,
            rowData: result.data.result.rowData
          }
        });
      }
    }
  }, [updateAction, currentState.envId, handleSave, currentState.taskId, currentState.dialect]);

  const handleDebug = useCallback(async () => {
    const res = await debugTask(
      l('pages.datastudio.editor.debugging', '', { jobName: currentState.name }),
      { ...currentState }
    );
    if (res?.success && res?.data?.result?.success) {
      updateAction({
        actionType: DataStudioActionType.TASK_RUN_DEBUG,
        params: {
          taskId: params.taskId
        }
      });
      setCurrentState((prevState) => {
        return {
          ...prevState,
          status: res.data.status === 'SUCCESS' ? 'RUNNING' : res.data.status
        };
      });
    }
  }, [currentState, updateAction]);

  const handleStop = useCallback(async () => {
    const result = await cancelTask('', currentState.taskId, false);
    if (result.success) {
      setCurrentState((prevState) => {
        return {
          ...prevState,
          status: 'CANCEL'
        };
      });
    }
  }, [currentState.taskId]);

  const handleGotoDevOps = useCallback(async () => {
    const dataByParams = await queryDataByParams<Jobs.JobInstance>(
      API_CONSTANTS.GET_JOB_INSTANCE_BY_TASK_ID,
      { taskId: currentState.taskId }
    );
    if (dataByParams) {
      window.open(`/#/devops/job-detail?id=${dataByParams?.id}`);
    }
  }, [currentState.taskId]);

  const handleFormat = useCallback(async () => {
    editorInstance.current?.getAction('format')?.run();
  }, [editorInstance.current]);
  const handleLocation = useCallback(async () => {
    const key = Number(id.replace('project_', ''));
    updateAction({
      actionType: DataStudioActionType.TASK_RUN_LOCATION,
      params: {
        taskId: params.taskId,
        key: key
      }
    });
  }, [updateAction]);
  const handleChangeJobLife = useCallback(async () => {
    if (JOB_LIFE_CYCLE.PUBLISH == currentState.step) {
      await changeTaskLife(
        l('global.table.lifecycle.offline'),
        currentState.taskId,
        JOB_LIFE_CYCLE.DEVELOP
      );
      currentState.step = JOB_LIFE_CYCLE.DEVELOP;
    } else {
      await handleSave();
      await changeTaskLife(
        l('global.table.lifecycle.publishing'),
        currentState.taskId,
        JOB_LIFE_CYCLE.PUBLISH
      );
      currentState.step = JOB_LIFE_CYCLE.PUBLISH;
    }
    setCurrentState((prevState) => ({ ...prevState, step: currentState.step }));
  }, [handleSave, currentState.step, currentState.taskId]);

  return (
    <Skeleton
      loading={loading}
      active
      title={false}
      paragraph={{
        rows: 5,
        width: '100%'
      }}
    >
      <DiffModal
        diffs={diff}
        open={openDiffModal}
        language={matchLanguage(currentState.dialect)}
        fileName={currentState.name}
        onUse={updateTask}
      />
      <Flex vertical style={{ height: 'inherit', width: '100%' }} ref={containerRef}>
        <ProForm
          size={'middle'}
          initialValues={{
            flinkMode: getFlinkMode(),
            envId: currentState.envId,
            databaseId: currentState.databaseId
          }}
          formRef={formRef}
          submitter={false}
          layout='horizontal'
          variant={'filled'}
          disabled={currentState?.step === JOB_LIFE_CYCLE.PUBLISH || isLockTask} // 当该任务处于发布状态时 表单禁用 不允许修改 | when this job is publishing, the form is disabled , and it is not allowed to modify
          onValuesChange={debounce(onValuesChange, 500)}
          syncToInitialValues
        >
          <Flex className={'run-toolbar'} wrap gap={2}>
            {/* 运行工具栏*/}
            <RunToolBarButton
              showDesc={showDesc}
              desc={l('button.save')}
              icon={<SaveOutlined />}
              onClick={handleSave}
              disabled={currentState?.step === JOB_LIFE_CYCLE.PUBLISH || isLockTask}
              hotKey={{
                ...hotKeyConfig,
                hotKeyDesc: 'Ctrl/Command +S',
                hotKeyHandle: (e: KeyboardEvent) =>
                  (e.ctrlKey && e.key === 's') || (e.metaKey && e.key === 's')
              }}
            />
            <RunToolBarButton
              isShow={!isFullscreen}
              showDesc={showDesc}
              desc={l('global.fullScreen')}
              icon={<FullscreenOutlined />}
              onClick={async () => {
                enterFullscreen();
              }}
            />
            <RunToolBarButton
              isShow={isFullscreen}
              showDesc={showDesc}
              desc={l('global.fullScreen.exit')}
              icon={<FullscreenExitOutlined />}
              onClick={async () => {
                exitFullscreen();
              }}
            />
            <RunToolBarButton
              showDesc={showDesc}
              desc={l('pages.datastudio.editor.check')}
              icon={<SafetyCertificateOutlined />}
              onClick={handleCheck}
              disabled={isLockTask}
              isShow={
                assert(
                  currentState.dialect,
                  [DIALECT.JAVA, DIALECT.SCALA, DIALECT.PYTHON_LONG],
                  true,
                  'notIncludes'
                ) && !isSql(currentState.dialect)
              }
              hotKey={{
                ...hotKeyConfig,
                hotKeyDesc: 'Alt+2/@',
                hotKeyHandle: (e: KeyboardEvent) =>
                  (e.altKey && e.code === 'Digit2') || (e.altKey && e.key === '@')
              }}
            />
            <RunToolBarButton
              showDesc={showDesc}
              desc={l('button.graph')}
              disabled={isLockTask}
              isShow={assert(
                currentState.dialect,
                [DIALECT.FLINK_SQL, DIALECT.FLINKJAR],
                true,
                'includes'
              )}
              icon={<ApartmentOutlined />}
              onClick={handleDAG}
            />
            <RunToolBarButton
              showDesc={showDesc}
              disabled={isLockTask}
              desc={l('menu.datastudio.lineage')}
              icon={<PartitionOutlined />}
              onClick={handleLineage}
              isShow={assert(currentState.dialect, [DIALECT.FLINK_SQL], true, 'includes')}
            />

            {assert(
              currentState.dialect,
              [DIALECT.FLINK_SQL, DIALECT.FLINKJAR],
              true,
              'includes'
            ) && (
              <>
                <Divider type={'vertical'} style={{ height: dividerHeight }} />
                <SelectFlinkEnv flinkEnv={tempData.flinkEnv} />
                <SelectFlinkRunMode data={tempData.flinkCluster} />
              </>
            )}
            {isSql(currentState.dialect) && (
              <>
                <Divider type={'vertical'} style={{ height: dividerHeight }} />
                <SelectDb databaseDataList={tempData.dataSourceDataList} data={currentState} />
              </>
            )}

            {assert(
              currentState.dialect,
              [DIALECT.JAVA, DIALECT.SCALA, DIALECT.PYTHON_LONG, DIALECT.FLINKSQLENV],
              true,
              'notIncludes'
            ) && <Divider type={'vertical'} style={{ height: dividerHeight }} />}

            <RunToolBarButton
              isShow={
                isStatusDone(currentState.status) &&
                assert(
                  currentState.dialect,
                  [DIALECT.JAVA, DIALECT.SCALA, DIALECT.PYTHON_LONG, DIALECT.FLINKSQLENV],
                  true,
                  'notIncludes'
                )
              }
              showDesc={showDesc}
              disabled={isLockTask}
              color={'green'}
              desc={l('pages.datastudio.editor.exec')}
              icon={<CaretRightOutlined />}
              onClick={handleSubmit}
              hotKey={{
                ...hotKeyConfig,
                hotKeyDesc: 'Shift+F10',
                hotKeyHandle: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10'
              }}
            />
            <RunToolBarButton
              isShow={
                isStatusDone(currentState.status) &&
                assert(currentState.dialect, [DIALECT.FLINK_SQL], true, 'includes')
              }
              showDesc={showDesc}
              disabled={isLockTask}
              color={'red'}
              desc={l('pages.datastudio.editor.debug')}
              icon={<BugOutlined />}
              onClick={handleDebug}
              hotKey={{
                ...hotKeyConfig,
                hotKeyDesc: 'Shift+F9',
                hotKeyHandle: (e: KeyboardEvent) => e.shiftKey && e.key === 'F9'
              }}
            />

            <RunToolBarButton
              isShow={!isStatusDone(currentState.status)}
              disabled={isLockTask}
              showDesc={showDesc}
              color={'red'}
              desc={l('pages.datastudio.editor.stop')}
              icon={<PauseOutlined />}
              onClick={handleStop}
              hotKey={{
                ...hotKeyConfig,
                hotKeyDesc: 'Ctrl+F2',
                hotKeyHandle: (e: KeyboardEvent) => e.ctrlKey && e.key === 'F2'
              }}
            />

            <RunToolBarButton
              isShow={
                !isStatusDone(currentState.status) &&
                assert(
                  currentState.dialect,
                  [DIALECT.FLINK_SQL, DIALECT.FLINKJAR],
                  true,
                  'includes'
                )
              }
              disabled={isLockTask}
              showDesc={showDesc}
              desc={l('pages.datastudio.to.jobDetail')}
              icon={<RotateRightOutlined />}
              onClick={handleGotoDevOps}
            />

            <Divider type={'vertical'} style={{ height: dividerHeight }} />
            <RunToolBarButton
              showDesc={showDesc}
              disabled={isLockTask}
              desc={l('shortcut.key.format')}
              icon={<ClearOutlined />}
              onClick={handleFormat}
            />
            <RunToolBarButton
              showDesc={showDesc}
              disabled={isLockTask}
              desc={l('button.position')}
              icon={<EnvironmentOutlined />}
              onClick={handleLocation}
            />

            <Divider type={'vertical'} style={{ height: dividerHeight }} />

            <RunToolBarButton
              isShow={JOB_LIFE_CYCLE.PUBLISH !== currentState.step}
              showDesc={showDesc}
              disabled={isLockTask}
              desc={l('button.publish')}
              icon={<RocketOutlined />}
              onClick={handleChangeJobLife}
            />
            <RunToolBarButton
              isShow={JOB_LIFE_CYCLE.PUBLISH === currentState.step}
              showDesc={showDesc}
              disabled={isLockTask}
              desc={l('button.offline')}
              icon={<MergeCellsOutlined />}
              onClick={handleChangeJobLife}
            />
            <RunToolBarButton
              showDesc={showDesc}
              disabled={isLockTask}
              desc={l('button.push')}
              icon={<PushpinIcon className={'blue-icon'} />}
              isShow={
                enabledDs &&
                JOB_LIFE_CYCLE.PUBLISH === currentState.step &&
                assert(
                  currentState.dialect,
                  [DIALECT.FLINKSQLENV, DIALECT.SCALA, DIALECT.JAVA, DIALECT.PYTHON_LONG],
                  true,
                  'notIncludes'
                )
              }
              hotKey={{
                ...hotKeyConfig,
                hotKeyDesc: 'Ctrl+E',
                hotKeyHandle: (e: KeyboardEvent) => e.ctrlKey && e.key === 'E'
              }}
            />
          </Flex>
        </ProForm>
        <Flex flex={1} style={{ height: 0 }}>
          <Row style={{ width: '100%', height: '100%' }}>
            <Col style={{ width: codeEditorWidth - toolbarSize, height: '100%' }}>
              <PanelGroup direction={'horizontal'}>
                <Panel>
                  <CodeEdit
                    monacoRef={editorInstance}
                    code={originStatementValue}
                    language={matchLanguage(currentState.dialect)}
                    editorDidMount={editorDidMount}
                    onChange={debounce(onEditorChange, 50)}
                    enableSuggestions={true}
                    options={{
                      readOnlyMessage: {
                        value: isLockTask
                          ? l('pages.datastudio.editor.onlyread.lock')
                          : l('pages.datastudio.editor.onlyread')
                      },
                      readOnly: currentState?.step == JOB_LIFE_CYCLE.PUBLISH || isLockTask,
                      scrollBeyondLastLine: false,
                      wordWrap: 'on'
                    }}
                  />
                </Panel>
                {selectRightToolbar && (
                  <>
                    <CusPanelResizeHandle />
                    <Panel
                      className={'right-toolbar-container'}
                      style={{ overflowY: 'auto' }}
                      defaultSize={30}
                    >
                      <Flex gap={5} vertical>
                        <Flex justify={'right'}>
                          <Button
                            key='close'
                            icon={<CloseOutlined />}
                            type={'text'}
                            onClick={() => setSelectRightToolbar(undefined)}
                          />
                        </Flex>

                        {
                          rightToolbarItem.find((item) => item.label === selectRightToolbar)
                            ?.children
                        }
                      </Flex>
                    </Panel>
                  </>
                )}
              </PanelGroup>
            </Col>

            {/*渲染右边更多扩展栏*/}
            <Flex wrap vertical className={'right-toolbar'} style={{ width: toolbarSize }}>
              {rightToolbarItem
                .map((item) => item.label?.toString())
                .map((item) => (
                  <div
                    key={item}
                    className={
                      'right-toolbar-item ' +
                      (selectRightToolbar === item ? 'right-toolbar-item-active' : '')
                    }
                    onClick={() => setSelectRightToolbar(item)}
                  >
                    {item}
                  </div>
                ))}
            </Flex>
          </Row>
        </Flex>
      </Flex>
    </Skeleton>
  );
});

export default connect(
  ({ DataStudio, SysConfig }: { DataStudio: DataStudioState; SysConfig: SysConfigStateType }) => ({
    showDesc: DataStudio.toolbar.showDesc,
    tempData: DataStudio.tempData,
    activeTab: DataStudio.centerContent.activeTab,
    dsConfig: SysConfig.dsConfig,
    enabledDs: SysConfig.enabledDs,
    taskOwnerLockingStrategy: SysConfig.taskOwnerLockingStrategy,
    users: DataStudio.users,
    tabs: DataStudio.centerContent.tabs
  }),
  mapDispatchToProps
)(SqlTask);
