/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
import { LoadingBtn } from '@/components/CallBackButton/LoadingBtn';
import { FlexCenterDiv } from '@/components/StyledComponents';
import { getCurrentData, getCurrentTab, mapDispatchToProps } from '@/pages/DataStudio/function';
import Explain from '@/pages/DataStudio/HeaderContainer/Explain';
import FlinkGraph from '@/pages/DataStudio/HeaderContainer/FlinkGraph';
import {
  buildBreadcrumbItems,
  isOnline,
  isRunning,
  projectCommonShow
} from '@/pages/DataStudio/HeaderContainer/function';
import {
  cancelTask,
  executeSql,
  getJobPlan,
  onLineTask
} from '@/pages/DataStudio/HeaderContainer/service';
import {
  DataStudioTabsItemType,
  MetadataTabsItemType,
  StateType,
  TabsPageSubType,
  TabsPageType,
  TaskDataType,
  VIEW
} from '@/pages/DataStudio/model';
import { JOB_LIFE_CYCLE, JOB_STATUS } from '@/pages/DevOps/constants';
import { ConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { handlePutDataJson } from '@/services/BusinessCrud';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import {
  ApartmentOutlined,
  CaretRightFilled,
  EnvironmentOutlined,
  FundOutlined,
  MergeCellsOutlined,
  MoreOutlined,
  PauseOutlined,
  RotateRightOutlined,
  SaveOutlined,
  ScheduleOutlined,
  SendOutlined
} from '@ant-design/icons';
import { Breadcrumb, Descriptions, message, Modal, Space } from 'antd';
import { ButtonProps } from 'antd/es/button/button';
import React, { useEffect, useState } from 'react';

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

const HeaderContainer = (props: any) => {
  const {
    size,
    activeBreadcrumbTitle,
    tabs: { panes, activeKey },
    saveTabs,
    updateJobRunningMsg,
    queryDsConfig,
    dsConfig
  } = props;

  const [modal, contextHolder] = Modal.useModal();
  const [messageApi, messageContextHolder] = message.useMessage();
  const [enableDs, setEnableDs] = useState<boolean>(false);
  const [currentData, setCurrentData] = useState<TaskDataType | undefined>(undefined);
  const [currentTab, setCurrentTab] = useState<
    DataStudioTabsItemType | MetadataTabsItemType | undefined
  >(undefined);

  useEffect(() => {
    queryDsConfig(SettingConfigKeyEnum.DOLPHIN_SCHEDULER.toLowerCase());
  }, []);

  useEffect(() => {
    setCurrentTab(getCurrentTab(panes, activeKey));
    setCurrentData(getCurrentData(panes, activeKey));
  }, [panes, activeKey]);

  useEffect(() => {
    // 检查是否开启 ds 配置 & 如果
    if (!dsConfig) {
      dsConfig.foreach((item: BaseConfigProperties) => {
        if (item.key === 'dolphinscheduler.settings.enable') {
          setEnableDs(item.value === 'true');
        }
      });
    }
  }, [dsConfig]);

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

  const handlerSubmit = async () => {
    if (!currentData) return;
    const saved = currentData.step == JOB_LIFE_CYCLE.ONLINE ? true : await handleSave();
    if (saved) {
      const res = await executeSql(
        l('pages.datastudio.editor.submitting', '', { jobName: currentData.name }),
        currentData.id
      );
      if (!res) return;

      updateJobRunningMsg({
        taskId: currentData.id,
        jobName: currentData.name,
        jobState: res.datas.status,
        runningLog: res.msg
      });
      messageApi.success(l('pages.datastudio.editor.exec.success'));
      currentData.status = JOB_STATUS.RUNNING;
      saveTabs({ ...props.tabs });
    }
  };

  const handleChangeJobLife = async () => {
    if (!currentData) return;
    if (isOnline(currentData)) {
      await cancelTask('', currentData.id);
      currentData.step = JOB_LIFE_CYCLE.DEVELOP;
    } else {
      const saved = await handleSave();
      if (saved) {
        await onLineTask(currentData.id);
        currentData.step = JOB_LIFE_CYCLE.ONLINE;
      }
    }
    saveTabs({ ...props.tabs });
  };

  const showDagGraph = async () => {
    const result = await getJobPlan(l('pages.datastudio.editor.explan.tip'), currentData);
    if (result) {
      modal.confirm({
        title: l('pages.datastudio.editor.explan.tip'),
        width: '100%',
        icon: null,
        content: <FlinkGraph data={result.datas} />,
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
      hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's',
      hotKeyDesc: 'Ctrl+S',
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
      isShow: projectCommonShow(currentTab?.type),
      click: async () => showDagGraph()
    },
    {
      // 检查 sql按钮
      icon: <ScheduleOutlined />,
      title: l('pages.datastudio.editor.check'),
      click: () => showExplain(),
      isShow: projectCommonShow(currentTab?.type)
    },
    {
      // 推送海豚, 此处需要将系统设置中的 ds 的配置拿出来做判断 启用才展示
      icon: <SendOutlined className={'blue-icon'} />,
      title: l('button.push'),
      hotKey: (e: KeyboardEvent) => e.ctrlKey && e.key === 's',
      isShow: enableDs
    },
    {
      // 发布按钮
      icon: isOnline(currentData) ? <MergeCellsOutlined /> : <FundOutlined />,
      title: isOnline(currentData) ? l('button.offline') : l('button.publish'),
      isShow: currentTab?.type == TabsPageType.project,
      click: () => handleChangeJobLife()
    },
    {
      // flink jobdetail跳转
      icon: <RotateRightOutlined />,
      title: l('pages.datastudio.to.jobDetail'),
      isShow:
        currentTab?.type == TabsPageType.project &&
        currentData?.jobInstanceId &&
        currentTab.subType == TabsPageSubType.flinkSql,
      props: {
        href: `/#/devops/job-detail?id=${currentData?.jobInstanceId}`,
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
      isShow: currentTab?.type == TabsPageType.project && !isRunning(currentData),
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
        <Breadcrumb separator={'>'} items={buildBreadcrumbItems(activeBreadcrumbTitle)} />
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
        {messageContextHolder}
      </div>
    );
  };

  /**
   * render
   */
  return (
    <Descriptions column={2} size={'middle'} layout={'horizontal'} key={'h'} style={headerStyle}>
      <Descriptions.Item>{renderBreadcrumbItems()}</Descriptions.Item>
      <Descriptions.Item contentStyle={{ display: 'flex', flexDirection: 'row-reverse' }}>
        {renderRightButtons()}
      </Descriptions.Item>
    </Descriptions>
  );
};

export default connect(
  ({ Studio, Config }: { Studio: StateType; Config: ConfigStateType }) => ({
    tabs: Studio.tabs,
    dsConfig: Config.dsConfig
  }),
  mapDispatchToProps
)(HeaderContainer);
