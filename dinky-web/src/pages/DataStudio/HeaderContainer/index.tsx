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
import { PushpinIcon } from '@/components/Icons/CustomIcons';
import { FlexCenterDiv } from '@/components/StyledComponents';
import { LeftBottomKey } from '@/pages/DataStudio/data.d';
import { getCurrentData, getCurrentTab, mapDispatchToProps } from '@/pages/DataStudio/function';
import Explain from '@/pages/DataStudio/HeaderContainer/Explain';
import FlinkGraph from '@/pages/DataStudio/HeaderContainer/FlinkGraph';
import {
  buildBreadcrumbItems,
  isCanPushDolphin,
  isOnline,
  isSql,
  projectCommonShow
} from '@/pages/DataStudio/HeaderContainer/function';
import PushDolphin from '@/pages/DataStudio/HeaderContainer/PushDolphin';
import {
  cancelTask,
  changeTaskLife,
  debugTask,
  executeSql,
  getJobPlan
} from '@/pages/DataStudio/HeaderContainer/service';
import {
  DataStudioTabsItemType,
  StateType,
  TabsPageType,
  TaskDataType,
  VIEW
} from '@/pages/DataStudio/model';
import { JOB_LIFE_CYCLE, JOB_STATUS } from '@/pages/DevOps/constants';
import { isStatusDone } from '@/pages/DevOps/function';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { handleOption, handlePutDataJson, queryDataByParams } from '@/services/BusinessCrud';
import { DIALECT } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data.d';
import { ButtonRoute, DolphinTaskDefinition, DolphinTaskMinInfo } from '@/types/Studio/data.d';
import { l } from '@/utils/intl';
import {
  ApartmentOutlined,
  BugOutlined,
  CaretRightFilled,
  EnvironmentOutlined,
  FundOutlined,
  MergeCellsOutlined,
  MoreOutlined,
  PauseOutlined,
  RotateRightOutlined,
  SaveOutlined,
  ScheduleOutlined
} from '@ant-design/icons';
import { connect } from '@umijs/max';
import { Breadcrumb, Descriptions, Modal, Space } from 'antd';
import React, { memo, useEffect, useState } from 'react';

const headerStyle: React.CSSProperties = {
  display: 'inline-flex',
  lineHeight: VIEW.headerHeight + 'px',
  height: VIEW.headerHeight,
  fontStyle: 'normal',
  fontWeight: 'bold',
  fontSize: '16px',
  padding: '4px 10px'
};

const HeaderContainer = (props: connect) => {
  const {
    size,
    activeBreadcrumbTitle,
    tabs: { panes, activeKey },
    saveTabs,
    updateJobRunningMsg,
    updateSelectBottomKey,
    queryDsConfig,
    queryTaskData,
    enabledDs
  } = props;

  const [modal, contextHolder] = Modal.useModal();

  const [pushDolphinState, setPushDolphinState] = useState<{
    modalVisible: boolean;
    buttonLoading: boolean;
    confirmLoading: boolean;
    dolphinTaskList: DolphinTaskMinInfo[];
    dolphinDefinitionTask: Partial<DolphinTaskDefinition>;
    currentDinkyTaskValue: Partial<TaskDataType>;
  }>({
    modalVisible: false,
    buttonLoading: false,
    confirmLoading: false,
    dolphinTaskList: [],
    dolphinDefinitionTask: {},
    currentDinkyTaskValue: {}
  });

  useEffect(() => {
    queryDsConfig(SettingConfigKeyEnum.DOLPHIN_SCHEDULER.toLowerCase());
  }, []);

  const currentData = getCurrentData(panes, activeKey);
  const currentTab = getCurrentTab(panes, activeKey) as DataStudioTabsItemType;

  const handlePushDolphinOpen = async () => {
    const dinkyTaskId = currentData?.id;
    const dolphinTaskList: DolphinTaskMinInfo[] | undefined = await queryDataByParams<
      DolphinTaskMinInfo[]
    >(API_CONSTANTS.SCHEDULER_QUERY_UPSTREAM_TASKS, { dinkyTaskId });
    const dolphinTaskDefinition: DolphinTaskDefinition | undefined =
      await queryDataByParams<DolphinTaskDefinition>(
        API_CONSTANTS.SCHEDULER_QUERY_TASK_DEFINITION,
        {
          dinkyTaskId
        }
      );
    setPushDolphinState((prevState) => ({
      ...prevState,
      buttonLoading: true,
      confirmLoading: false,
      modalVisible: true,
      dolphinTaskList: dolphinTaskList ?? [],
      dolphinDefinitionTask: dolphinTaskDefinition ?? {},
      currentDinkyTaskValue: currentData as TaskDataType
    }));
  };

  const handlePushDolphinCancel = async () => {
    setPushDolphinState((prevState) => ({
      ...prevState,
      modalVisible: false,
      buttonLoading: false,
      dolphinTaskList: [],
      confirmLoading: false,
      dolphinDefinitionTask: {},
      currentDinkyTaskValue: {}
    }));
  };

  const handleSave = async () => {
    const saved = await handlePutDataJson(API_CONSTANTS.TASK, currentData);
    saveTabs({ ...props.tabs });
    if (currentTab) currentTab.isModified = false;
    return saved;
  };

  const handlerStop = () => {
    if (!currentData) return new Promise((resolve) => resolve(false));
    //RECONNECTING状态无法停止需要改变提示内容
    const isReconnect = currentData.status == JOB_STATUS.RECONNECTING;
    const content = isReconnect
      ? 'pages.datastudio.editor.stop.force.jobConfirm'
      : 'pages.datastudio.editor.stop.job';
    return new Promise((resolve) => {
      modal.confirm({
        title: l('pages.datastudio.editor.stop.job'),
        content: l(content, '', {
          jobName: currentData.name
        }),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await cancelTask(l('pages.datastudio.editor.stop.job'), currentData.id).then(() => {
            currentData.status = JOB_STATUS.CANCELED;
            saveTabs({ ...props.tabs });
          });
          resolve(true);
        },
        onCancel: () => {
          resolve(false);
        }
      });
    });
  };

  const handlerDebug = async () => {
    updateSelectBottomKey(LeftBottomKey.CONSOLE_KEY);
    if (!currentData) return;
    // @ts-ignore
    const editor = currentTab.monacoInstance.editor
      .getEditors()
      .find((x: any) => x['id'] === currentData.id);

    let selectSql = '';
    if (editor) {
      selectSql = editor.getModel().getValueInRange(editor.getSelection());
    }
    if (selectSql == null || selectSql == '') {
      selectSql = currentData.statement;
    }

    const res = await debugTask(
      l('pages.datastudio.editor.debugging', '', { jobName: currentData.name }),
      { ...currentData, statement: selectSql }
    );

    if (!res) {
      return;
    } else {
      updateSelectBottomKey(LeftBottomKey.RESULT_KEY);
    }

    updateJobRunningMsg({
      taskId: currentData.id,
      jobName: currentData.name,
      jobState: res.data.status,
      runningLog: res.msg
    });
    currentData.status = JOB_STATUS.RUNNING;
    // Common sql task is synchronized, so it needs to automatically update the status to finished.
    if (isSql(currentData.dialect)) {
      currentData.status = JOB_STATUS.FINISHED;
      if (currentTab) currentTab.console.results = res.data.results;
    } else {
      if (currentTab) currentTab.console.result = res.data.result;
    }
    // Common sql task is synchronized, so it needs to automatically update the status to finished.
    if (isSql(currentData.dialect)) {
      currentData.status = JOB_STATUS.FINISHED;
    }
    if (currentTab) currentTab.console.result = res.data.result;
    saveTabs({ ...props.tabs });
  };

  const handlerSubmit = async () => {
    updateSelectBottomKey(LeftBottomKey.CONSOLE_KEY);

    if (!currentData) return;
    const saved = currentData.step == JOB_LIFE_CYCLE.PUBLISH ? true : await handleSave();
    if (!saved) return;

    const res = await executeSql(
      l('pages.datastudio.editor.submitting', '', { jobName: currentData.name }),
      currentData.id
    );

    if (!res) return;
    updateJobRunningMsg({
      taskId: currentData.id,
      jobName: currentData.name,
      jobState: res.data.status,
      runningLog: res.msg
    });
    currentData.status = JOB_STATUS.RUNNING;
    // Common sql task is synchronized, so it needs to automatically update the status to finished.
    if (isSql(currentData.dialect)) {
      currentData.status = JOB_STATUS.FINISHED;
    }
    if (currentTab) currentTab.console.result = res.data.result;
    if (isSql(currentData.dialect)) {
      updateSelectBottomKey(LeftBottomKey.RESULT_KEY);
    }
    saveTabs({ ...props.tabs });
  };

  const handleChangeJobLife = async () => {
    if (!currentData) return;
    if (isOnline(currentData)) {
      await changeTaskLife(
        l('global.table.lifecycle.offline'),
        currentData.id,
        JOB_LIFE_CYCLE.DEVELOP
      );
      currentData.step = JOB_LIFE_CYCLE.DEVELOP;
    } else {
      const saved = await handleSave();
      if (saved) {
        await changeTaskLife(
          l('global.table.lifecycle.publishing'),
          currentData.id,
          JOB_LIFE_CYCLE.PUBLISH
        );
        currentData.step = JOB_LIFE_CYCLE.PUBLISH;
      }
    }
    saveTabs({ ...props.tabs });
    await queryTaskData();
  };

  const showDagGraph = async () => {
    const result = await getJobPlan(l('pages.datastudio.editor.explan.tip'), currentData);
    if (result) {
      modal.confirm({
        title: l('pages.datastudio.editor.explan.tip'),
        width: '100%',
        icon: null,
        content: <FlinkGraph data={result.data} />,
        cancelButtonProps: { style: { display: 'none' } }
      });
    }
  };

  const showExplain = async () => {
    modal.confirm({
      title: l('pages.datastudio.explain.validate.msg'),
      width: '100%',
      icon: null,
      content: <Explain />,
      cancelButtonProps: { style: { display: 'none' } }
    });
  };

  const routes: ButtonRoute[] = [
    // 保存按钮 icon
    {
      hotKey: (e: KeyboardEvent) => (e.ctrlKey && e.key === 's') || (e.metaKey && e.key === 's'),
      hotKeyDesc: 'Ctrl/Command +S',
      isShow: projectCommonShow(currentTab?.type),
      icon: <SaveOutlined />,
      title: l('button.save'),
      click: () => handleSave(),
      props: {
        disabled: isOnline(currentData)
      }
    },
    {
      // 执行图按钮
      icon: <ApartmentOutlined />,
      title: l('button.graph'),
      isShow:
        (projectCommonShow(currentTab?.type) &&
          currentTab?.subType?.toLowerCase() === DIALECT.FLINK_SQL) ||
        currentTab?.subType?.toLowerCase() === DIALECT.FLINKJAR,
      click: async () => showDagGraph()
    },
    {
      // 检查 sql按钮
      icon: <ScheduleOutlined />,
      hotKey: (e: KeyboardEvent) =>
        (e.altKey && e.code === 'Digit2') || (e.altKey && e.key === '@'),
      hotKeyDesc: 'Alt+2/@',
      title: l('pages.datastudio.editor.check'),
      click: () => showExplain(),
      isShow:
        projectCommonShow(currentTab?.type) &&
        currentTab?.subType?.toLowerCase() !== DIALECT.JAVA &&
        currentTab?.subType?.toLowerCase() !== DIALECT.SCALA &&
        currentTab?.subType?.toLowerCase() !== DIALECT.PYTHON_LONG
    },
    {
      // 推送海豚, 此处需要将系统设置中的 ds 的配置拿出来做判断 启用才展示
      icon: <PushpinIcon loading={pushDolphinState.buttonLoading} className={'blue-icon'} />,
      title: l('button.push'),
      hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 'e',
      hotKeyDesc: 'Ctrl+E',
      isShow: enabledDs && isCanPushDolphin(currentData),
      click: () => handlePushDolphinOpen()
    },
    {
      // 发布按钮
      icon: isOnline(currentData) ? <MergeCellsOutlined /> : <FundOutlined />,
      title: isOnline(currentData) ? l('button.offline') : l('button.publish'),
      isShow: currentTab?.type == TabsPageType.project,
      click: () => handleChangeJobLife()
    },
    {
      // flink jobdetail跳转 运维
      icon: <RotateRightOutlined />,
      title: l('pages.datastudio.to.jobDetail'),
      isShow:
        currentTab?.type == TabsPageType.project &&
        currentData?.jobInstanceId &&
        (currentTab?.subType?.toLowerCase() == DIALECT.FLINK_SQL ||
          currentTab?.subType?.toLowerCase() == DIALECT.FLINKJAR),
      props: {
        onClick: async () => {
          const dataByParams = await queryDataByParams<Jobs.JobInstance>(
            API_CONSTANTS.GET_JOB_INSTANCE_BY_TASK_ID,
            { taskId: currentData?.id }
          );
          if (dataByParams) {
            window.open(`/#/devops/job-detail?id=${dataByParams?.id}`);
          }
        },
        target: '_blank'
      }
    },
    {
      // 执行按钮
      icon: <CaretRightFilled />,
      title: l('pages.datastudio.editor.exec'),
      click: handlerSubmit,
      hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10',
      hotKeyDesc: 'Shift+F10',
      isShow:
        currentTab?.type == TabsPageType.project &&
        isStatusDone(currentData?.status) &&
        currentTab?.subType?.toLowerCase() !== DIALECT.JAVA &&
        currentTab?.subType?.toLowerCase() !== DIALECT.SCALA &&
        currentTab?.subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
        currentTab?.subType?.toLowerCase() !== DIALECT.FLINKSQLENV,
      props: {
        style: { background: '#52c41a' },
        type: 'primary'
      }
    },
    {
      // Debug button
      icon: <BugOutlined />,
      title: l('pages.datastudio.editor.debug'),
      click: handlerDebug,
      hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F9',
      hotKeyDesc: 'Shift+F9',
      isShow:
        currentTab?.type == TabsPageType.project &&
        isStatusDone(currentData?.status) &&
        (currentTab?.subType?.toLowerCase() === DIALECT.FLINK_SQL ||
          isSql(currentTab?.subType?.toLowerCase() ?? '')),
      props: {
        style: { background: '#52c41a' },
        type: 'primary'
      }
    },
    {
      // 停止按钮
      icon: <PauseOutlined />,
      title: l('pages.datastudio.editor.stop'),
      click: handlerStop,
      isShow: currentTab?.type == TabsPageType.project && !isStatusDone(currentData?.status),
      hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10',
      hotKeyDesc: 'Shift+F10',
      props: {
        type: 'primary',
        style: { background: '#FF4D4F' }
      }
    },
    {
      icon: <MoreOutlined />,
      title: '',
      click: () => {},
      isShow: true
      // hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's'
    }
  ];

  /**
   * @description: 生成面包屑
   */
  const renderBreadcrumbItems = () => {
    if (!activeBreadcrumbTitle) {
      return (
        <Space>
          <EnvironmentOutlined />
          <span>Guide Page</span>
        </Space>
      );
    }

    return (
      <FlexCenterDiv style={{ width: (size.width - 2 * VIEW.paddingInline) / 2 }}>
        {/*<Breadcrumb itemRender={(item, params, items, paths)=><span>{item.title}</span>} items={buildBreadcrumbItems(activeBreadcrumbTitle)} />*/}
        <EnvironmentOutlined style={{ paddingRight: 20 }} />
        <Breadcrumb
          style={{ fontSize: 12, lineHeight: VIEW.headerHeight + 'px' }}
          separator={'/'}
          items={buildBreadcrumbItems(activeBreadcrumbTitle)}
        />
      </FlexCenterDiv>
    );
  };

  document.onkeydown = (e) => {
    routes
      .filter((r) => r.hotKey?.(e))
      .forEach((r) => {
        r.click?.();
        e.preventDefault();
      });
  };

  /**
   * @description: 渲染右侧按钮
   */
  const renderRightButtons = () => {
    return (
      <div style={{ padding: '4px' }}>
        <Space size={'small'} align={'center'} direction={'horizontal'} wrap>
          {routes
            .filter((x) => x.isShow)
            .map((route) => {
              return <LoadingBtn {...route} />;
            })}
        </Space>
        {contextHolder}
      </div>
    );
  };

  const handlePushDolphinSubmit = async (value: DolphinTaskDefinition) => {
    setPushDolphinState((prevState) => ({ ...prevState, loading: true }));
    await handleOption(
      API_CONSTANTS.SCHEDULER_CREATE_OR_UPDATE_TASK_DEFINITION,
      `推送任务[${currentData?.name}]至 DolphinScheduler`,
      value
    );
    await handlePushDolphinCancel();
  };

  /**
   * render
   */
  return (
    <Descriptions column={2} size={'middle'} layout={'horizontal'} key={'h'} style={headerStyle}>
      <Descriptions.Item>{renderBreadcrumbItems()}</Descriptions.Item>
      <Descriptions.Item contentStyle={{ display: 'flex', flexDirection: 'row-reverse' }}>
        {renderRightButtons()}
        {pushDolphinState.modalVisible && (
          <PushDolphin
            onCancel={() => handlePushDolphinCancel()}
            currentDinkyTaskValue={pushDolphinState.currentDinkyTaskValue}
            modalVisible={pushDolphinState.modalVisible}
            loading={pushDolphinState.confirmLoading}
            dolphinDefinitionTask={pushDolphinState.dolphinDefinitionTask}
            dolphinTaskList={pushDolphinState.dolphinTaskList}
            onSubmit={(values) => handlePushDolphinSubmit(values)}
          />
        )}
      </Descriptions.Item>
    </Descriptions>
  );
};

export default connect(
  ({ Studio, SysConfig }: { Studio: StateType; SysConfig: SysConfigStateType }) => ({
    tabs: Studio.tabs,
    dsConfig: SysConfig.dsConfig,
    enabledDs: SysConfig.enabledDs
  }),
  mapDispatchToProps
)(memo(HeaderContainer));
