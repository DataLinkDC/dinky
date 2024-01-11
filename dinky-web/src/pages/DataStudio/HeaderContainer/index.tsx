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
import {getCurrentData, getCurrentTab, isDataStudioTabsItemType, mapDispatchToProps} from '@/pages/DataStudio/function';
import Explain from '@/pages/DataStudio/HeaderContainer/Explain';
import FlinkGraph from '@/pages/DataStudio/HeaderContainer/FlinkGraph';
import {
  buildBreadcrumbItems,
  isCanPushDolphin,
  isOnline,
  isRunning,
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
  StateType, STUDIO_MODEL,
  TabsPageType,
  TaskDataType,
  VIEW
} from '@/pages/DataStudio/model';
import { JOB_LIFE_CYCLE, JOB_STATUS } from '@/pages/DevOps/constants';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { handleOption, handlePutDataJson, queryDataByParams } from '@/services/BusinessCrud';
import {DATETIME_FORMAT, DIALECT, RUN_MODE} from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data.d';
import { DolphinTaskDefinition, DolphinTaskMinInfo } from '@/types/Studio/data.d';
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
import {
  ProForm, ProFormDateRangePicker, ProFormDateTimePicker,
  ProFormRadio,
  ProFormSelect,
  ProFormTimePicker
} from '@ant-design/pro-components';
import { Breadcrumb, Descriptions, Modal, Space, Select, Radio, RadioChangeEvent,DatePicker, TimePicker} from 'antd';
import { ButtonProps } from 'antd/es/button/button';
import React, { memo, useEffect, useState ,useRef} from 'react';
import type { ProFormInstance } from '@ant-design/pro-components';
import {useForm} from "antd/es/form/Form";
import {MONTH_CONTEXT_MENU, WEEK_CONTEXT_MENU} from "@/pages/DataStudio/HeaderContainer/constants";
import {Moment} from "moment";
import {debounce} from "lodash";
import dayjs from "dayjs";

const headerStyle: React.CSSProperties = {
  display: 'inline-flex',
  lineHeight: VIEW.headerHeight + 'px',
  height: VIEW.headerHeight,
  fontStyle: 'normal',
  fontWeight: 'bold',
  fontSize: '16px',
  padding: '4px 10px'
};

type ButtonRoute = {
  icon?: React.ReactNode;
  title?: string;
  click?: () => void;
  hotKey?: (e: KeyboardEvent) => boolean;
  hotKeyDesc?: string;
  isShow: boolean;
  props?: ButtonProps;
};

const HeaderContainer = (props: connect) => {
  const {
    size,
    activeBreadcrumbTitle,
    tabs: { panes, activeKey },
    saveTabs,
    updateJobRunningMsg,
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

  const currentData = getCurrentData(panes, activeKey);

  useEffect(() => {
    queryDsConfig(SettingConfigKeyEnum.DOLPHIN_SCHEDULER.toLowerCase());
    form.setFieldsValue({ ...currentData?.scheduleConfig });
  }, [currentData]);

  const currentTab = getCurrentTab(panes, activeKey) as DataStudioTabsItemType;

  const handlePushDolphinOpen = async () => {
    const dinkyTaskId = currentData?.id;
    const dolphinTaskList: DolphinTaskMinInfo[] | undefined = await queryDataByParams<
      DolphinTaskMinInfo[]
    >('/api/scheduler/queryUpstreamTasks', { dinkyTaskId });
    const dolphinTaskDefinition: DolphinTaskDefinition | undefined =
      await queryDataByParams<DolphinTaskDefinition>('/api/scheduler/queryTaskDefinition', {
        dinkyTaskId
      });
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
    const saved = await handlePutDataJson('/api/task', currentData);
    saveTabs({ ...props.tabs });
    if (currentTab) currentTab.isModified = false;
    return saved;
  };

  const handlerStop = () => {
    if (!currentData) return;

    modal.confirm({
      title: l('pages.datastudio.editor.stop.job'),
      content: l('pages.datastudio.editor.stop.jobConfirm', '', {
        jobName: currentData.name
      }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        cancelTask(l('pages.datastudio.editor.stop.job'), currentData.id).then(() => {
          currentData.status = JOB_STATUS.CANCELED;
          saveTabs({ ...props.tabs });
        });
      }
    });
  };

  const handlerDebug = async () => {
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
      isShow:
        ((currentTab?.type == TabsPageType.project &&
          currentTab?.subType?.toLowerCase() === DIALECT.FLINK_SQL) ||
          currentTab?.subType?.toLowerCase() === DIALECT.FLINKJAR) &&
          currentTab?.params?.taskData?.type !== "local",
      // click: () => handleChangeJobLife()
      click: () => currentTab?.params?.taskData?.batchModel && !isOnline(currentData) ?showModal():handleChangeJobLife()
    },
    {
      // flink jobdetail跳转 运维
      icon: <RotateRightOutlined />,
      title: l('pages.datastudio.to.jobDetail'),
      isShow:
        // currentTab?.type == TabsPageType.project &&
        // currentData?.jobInstanceId &&
        // (currentTab?.subType?.toLowerCase() == DIALECT.FLINK_SQL ||
        //   currentTab?.subType?.toLowerCase() == DIALECT.FLINKJAR),
        false,
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
        !isRunning(currentData) &&
        currentTab?.subType?.toLowerCase() !== DIALECT.JAVA &&
        currentTab?.subType?.toLowerCase() !== DIALECT.SCALA &&
        currentTab?.subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
        currentTab?.subType?.toLowerCase() !== DIALECT.FLINKSQLENV &&
        currentTab?.params?.taskData?.type === "local",
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
        !isRunning(currentData) &&
        (currentTab?.subType?.toLowerCase() === DIALECT.FLINK_SQL ||
          isSql(currentTab?.subType?.toLowerCase() ?? '')) &&
        currentTab?.params?.taskData?.type === "local",
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
      isShow: currentTab?.type == TabsPageType.project && isRunning(currentData),
      hotKey: (e: KeyboardEvent) => e.shiftKey && e.key === 'F10',
      hotKeyDesc: 'Shift+F10',
      props: {
        type: 'primary',
        danger: true
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
  // const renderBreadcrumbItems = () => {
  //   if (!activeBreadcrumbTitle) {
  //     return (
  //       <Space>
  //         <EnvironmentOutlined />
  //         <span>Guide Page</span>
  //       </Space>
  //     );
  //   }
  //
  //   return (
  //     <FlexCenterDiv style={{ width: (size.width - 2 * VIEW.paddingInline) / 2 }}>
  //       {/*<Breadcrumb itemRender={(item, params, items, paths)=><span>{item.title}</span>} items={buildBreadcrumbItems(activeBreadcrumbTitle)} />*/}
  //       <EnvironmentOutlined style={{ paddingRight: 20 }} />
  //       <Breadcrumb
  //         style={{ fontSize: 12, lineHeight: VIEW.headerHeight + 'px' }}
  //         separator={'/'}
  //         items={buildBreadcrumbItems(activeBreadcrumbTitle)}
  //       />
  //     </FlexCenterDiv>
  //   );
  // };

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
              return (
                <LoadingBtn
                  key={route.title}
                  size={'small'}
                  type={'text'}
                  icon={route.icon}
                  onClick={route.click}
                  title={route.hotKeyDesc}
                  {...route.props}
                >
                  {route.title}
                </LoadingBtn>
              );
            })}
        </Space>
        {contextHolder}
      </div>
    );
  };

  const handlePushDolphinSubmit = async (value: DolphinTaskDefinition) => {
    setPushDolphinState((prevState) => ({ ...prevState, loading: true }));
    await handleOption(
      '/api/scheduler/createOrUpdateTaskDefinition',
      `推送任务[${currentData?.name}]至 DolphinScheduler`,
      value
    );
    await handlePushDolphinCancel();
  };

  // 调度类型
  const [value, setValue] = useState(0)

  // 天
  const [period, setPeriod] = useState('')

  const [form] = useForm();

  const formRef = useRef<ProFormInstance>();

  const [isModalOpen, setIsModalOpen] = useState(false);

  //表单点击确认事件
  const handleOk = () => {
    console.log(currentData)
      form.validateFields().then((value) => {
        const arr = value.effectiveDate;
        if(arr != null){
          value.effectiveDateStart = dayjs(arr[0]).format(DATETIME_FORMAT);
          value.effectiveDateEnd = dayjs(arr[1]).format(DATETIME_FORMAT);
        }
        currentData!.scheduleConfig = value;
        value.periodTime = dayjs(value.periodTime).format(DATETIME_FORMAT);
        handleChangeJobLife()
        setIsModalOpen(false);
      })
  };
  const handleCancel = () => {
      setIsModalOpen(false);
  };
  const showModal = () => {
      setIsModalOpen(true);
  };

  const onValuesChange = (change: { [key in string]: any }, all: any) => {
    const pane = getCurrentTab(panes, activeKey);
    if (!isDataStudioTabsItemType(pane)) {
      return;
    }


    Object.keys(change).forEach((key) => {
      if(!pane.params.taskData.scheduleConfig || pane.params.taskData.scheduleConfig=="null"){
        pane.params.taskData.scheduleConfig = {}
      }
      if(key === "effectiveDate"){
        pane.params.taskData.scheduleConfig.effectiveDateEnd = dayjs(all[key][0]).format(DATETIME_FORMAT);
        pane.params.taskData.scheduleConfig.effectiveDateStart = dayjs(all[key][0]).format(DATETIME_FORMAT);
      } else if(key === "periodTime"){
        pane.params.taskData.scheduleConfig.periodTime = dayjs(all[key][0]).format(DATETIME_FORMAT);
      } else{
        pane.params.taskData.scheduleConfig[key] = all[key];
      }

    });
    pane.isModified = true;
  };

  /**
   * render
   */
  return (
    <Descriptions column={2} size={'middle'} layout={'horizontal'} key={'h'} style={headerStyle}>
      {/*<Descriptions.Item>{renderBreadcrumbItems()}</Descriptions.Item>*/}
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
        <Modal title="发布设置" open={isModalOpen} onOk={handleOk} okText={"发布"} onCancel={handleCancel}>
          <ProForm
            size={'middle'}
            initialValues={{
              schedulingType: "0",
            }}
            form={form}
            formRef={formRef}
            submitter={false}
            onValuesChange={debounce(onValuesChange, 500)}
          >
            {currentTab?.params?.taskData?.batchModel && (
              <ProFormRadio.Group
                label='调度类型'
                name={'schedulingType'}
                radioType='button'
                rules={[{ required: true, message: l('menu.typePlaceholder') }]}
                options={[{title:'周期调度',value:'0',label:'周期调度'},{title:'一次性调度',value:'1',label:'一次性调度'}]}
                fieldProps={{
                  onChange: (e: RadioChangeEvent) => {
                    console.log('radio checked', e.target.value);
                    setValue(e.target.value);
                  }
                }}
              />
            )}
            {currentTab?.params?.taskData?.batchModel && value==0 && (
              <ProFormDateRangePicker
                label='生效日期'
                name={'effectiveDate'}
                rules={[{ required: true, message: l('menu.typePlaceholder') }]}
              />
            )}
            {currentTab?.params?.taskData?.batchModel && value==0 && (
              <ProFormSelect
                label='调度周期'
                name={'periodType'}
                rules={[{ required: true, message: l('menu.typePlaceholder') }]}
                options={[{title:'天',value:'1',label:'天'},{title:'周',value:'2',label:'周'},{title:'月',value:'3',label:'月'}]}
                fieldProps={{
                  onChange: (value: string) => {
                    setPeriod(value)
                  }
                }}
              />
            )}
            {currentTab?.params?.taskData?.batchModel && value==0 && (period==="2" || period==="3") && (
              <ProFormSelect
                label='天'
                mode={"multiple"}
                name={period=='2'?"days":'weeks'}
                placeholder={"具体天数据"}
                options={period=='2'?WEEK_CONTEXT_MENU:MONTH_CONTEXT_MENU}
              />
            )}
            {currentTab?.params?.taskData?.batchModel && value==0 && (
              <ProFormTimePicker
                label='时间'
                name={'periodTime'}
                rules={[{ required: true, message: l('menu.typePlaceholder') }]}
              />
            )}
            {value==1 && (
              <ProFormDateTimePicker
                label='指定执行时间'
                name={'periodTime'}
                rules={[{ required: true, message: l('menu.typePlaceholder') }]}
              />
            )}
          </ProForm>
        </Modal>
      </Descriptions.Item>
    </Descriptions>
  );
}



export default connect(
  ({ Studio, SysConfig }: { Studio: StateType; SysConfig: SysConfigStateType }) => ({
    tabs: Studio.tabs,
    dsConfig: SysConfig.dsConfig,
    enabledDs: SysConfig.enabledDs
  }),
  mapDispatchToProps
)(memo(HeaderContainer));
